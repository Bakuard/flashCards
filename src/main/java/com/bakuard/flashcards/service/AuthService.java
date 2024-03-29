package com.bakuard.flashcards.service;

import com.bakuard.flashcards.config.configData.ConfigData;
import com.bakuard.flashcards.dal.IntervalRepository;
import com.bakuard.flashcards.dal.UserRepository;
import com.bakuard.flashcards.dal.fragment.UserSaver;
import com.bakuard.flashcards.model.auth.JwsWithUser;
import com.bakuard.flashcards.model.auth.credential.Credential;
import com.bakuard.flashcards.model.auth.credential.User;
import com.bakuard.flashcards.validation.*;
import com.bakuard.flashcards.validation.exception.FailToSendMailException;
import com.bakuard.flashcards.validation.exception.IncorrectCredentials;
import com.bakuard.flashcards.validation.exception.NotUniqueEntityException;
import com.bakuard.flashcards.validation.exception.UnknownEntityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.validation.ConstraintViolationException;
import java.util.UUID;

/**
 * Сервис отвечающий за управление учетными данными пользователей. Каждый метод этого класса выполняется
 * в отдельной транзакции.
 */
@Transactional
public class AuthService {

    private UserRepository userRepository;
    private IntervalRepository intervalRepository;
    private JwsService jwsService;
    private EmailService emailService;
    private ConfigData conf;
    private ValidatorUtil validator;
    private TransactionTemplate transactionTemplate;

    /**
     * Создает новый сервис управления учетными данными.
     * @param userRepository репозиторий учетных данных пользователей
     * @param intervalRepository репозиторий интервалов повторений
     * @param jwsService сервис jws токенов
     * @param emailService сервис рассылки на почту писем подтверждения
     * @param conf общие данные конфигурации приложения
     * @param validator объект отвечающий за валидация входных данных пользователя
     */
    public AuthService(UserRepository userRepository,
                       IntervalRepository intervalRepository,
                       JwsService jwsService,
                       EmailService emailService,
                       ConfigData conf,
                       ValidatorUtil validator,
                       TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.intervalRepository = intervalRepository;
        this.jwsService = jwsService;
        this.emailService = emailService;
        this.conf = conf;
        this.validator = validator;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Если в постоянном хранилище отсутствует учетная запись супер-администратора, то создает её. <br/>
     * Иначе, если в конфигурации приложения задано пересоздание этой учетной записи - пересоздает её. <br/>
     * Иначе, ничего не делает.
     */
    public void initialize() {
        long countUserWithRole = userRepository.countForRole(conf.superAdmin().roleName());
        if(countUserWithRole < 1) {
            Credential credential = new Credential(conf.superAdmin().mail(), conf.superAdmin().password());

            transactionTemplate.execute(status -> {
                User superAdmin = save(
                        new User(validator.assertValid(credential)).
                                addRole(conf.superAdmin().roleName())
                );

                //add default repeat intervals
                intervalRepository.addAll(superAdmin.getId(), 1, 3, 5, 11);

                return superAdmin;
            });
        } else if(conf.superAdmin().recreate()) {
            transactionTemplate.execute(status -> {
                User superAdmin = userRepository.findByRole(conf.superAdmin().roleName(), 1, 0).get(0);
                Credential credential = new Credential(conf.superAdmin().mail(), conf.superAdmin().password());
                superAdmin.setCredential(validator.assertValid(credential));
                save(superAdmin);
                return superAdmin;
            });
        }
    }

    /**
     * Выполняет вход для указанных учетных данных и в случае успеха - возвращает подробную
     * информацию об учетных данных пользователя и JWS токен общего доступа. Если среди существующих
     * пользователей нет пользователя с такой почтой или указан неверный пароль - генерирует исключение.
     * @param credential учетные данные пользователя совершающего вход в приложение.
     * @return подробная информация об учетных данных пользователя и JWS токен общего доступ
     * @throws ConstraintViolationException если нарушен хотя бы один из инвариантов {@link Credential}
     * @throws UnknownEntityException если пользователя с указанной почтой не существует
     * @throws IncorrectCredentials см. {@link User#assertCurrentPassword(String)}
     * @see Credential
     * @see JwsWithUser
     */
    public JwsWithUser enter(Credential credential) {
        validator.assertValid(credential);
        User user = tryFindByEmail(credential.email());
        user.assertCurrentPassword(credential.password());
        String jws = jwsService.generateJws(user.getId(), "common", conf.jws().commonTokenLifeTime());
        return new JwsWithUser(user, jws);
    }

    /**
     * Первый из двух шагов регистрации. На этом шаге проверяется корректность создаваемых учетных
     * данных (требования к учетным данным см. {@link Credential}) и в случае успеха - на почту пользователя
     * отправляется письмо с подтверждением. В обратной ссылке указанной в письме будет содержаться JWS
     * токен для подтверждения регистрации, который требуется для выполнения второго шага регистрации.
     * @param credential учетные данные пользователя
     * @throws ConstraintViolationException если нарушен хотя бы один из инвариантов {@link Credential}
     * @throws FailToSendMailException см. {@link EmailService#confirmEmailForRegistration} вернет User.email.unique
     * @throws NotUniqueEntityException если уже есть пользователь с такой почтой.
     *                                  {@link NotUniqueEntityException#getMessageKey()} вернет User.email.unique
     * @see Credential
     */
    public void registerFirstStep(Credential credential) {
        validator.assertValid(credential);
        if(userRepository.existsByEmail(credential.email())) {
            throw new NotUniqueEntityException(
                    "Use with email '" + credential.email() + "' already exists",
                    "User.email.unique");
        }
        String jws = jwsService.generateJws(credential, "register", conf.jws().registrationTokenLifeTime());
        emailService.confirmEmailForRegistration(jws, credential.email());
    }

    /**
     * Последний из двух шагов регистрации. На этом шаге создается и сохраняется новый пользователь
     * с указанными на первом шаге (см {@link #registerFirstStep(Credential)}) учетными данными, а
     * затем возвращается вместе с JWS токеном общего доступа.
     * @param jwsBody учетные данные создаваемого пользователя
     * @return данные нового пользователя вместе с JWS токеном общего доступа.
     * @see JwsWithUser
     */
    public JwsWithUser registerFinalStep(Credential jwsBody) {
        User user = save(new User(jwsBody));

        //add default repeat intervals
        intervalRepository.addAll(user.getId(), 1, 3, 5, 11);

        String jws = jwsService.generateJws(user.getId(), "common", conf.jws().commonTokenLifeTime());
        return new JwsWithUser(user, jws);
    }

    /**
     * Первый из двух шагов восстановления пароля. На этом шаге проверяется корректность введенных учетных
     * данных (требования к учетным данным см. {@link Credential}) и в случае успеха - на почту пользователя
     * отправляется письмо с подтверждением. Новый пароль задается в указанных учетных данных. В обратной
     * ссылке указанной в письме будет содержаться JWS токен для подтверждения смены пароля, который требуется
     * для выполнения второго шага смены пароля.
     * @param credential учетные данные пользователя с новым паролем
     * @throws ConstraintViolationException если нарушен хотя бы один из инвариантов {@link Credential}
     * @throws FailToSendMailException см. {@link EmailService#confirmEmailForRestorePass}
     * @throws UnknownEntityException если пользователя с указанной почтой не существует.
     *                                {@link UnknownEntityException#getMessageKey()} вернет User.email.exists
     * @see Credential
     */
    public void restorePasswordFirstStep(Credential credential) {
        validator.assertValid(credential);
        if(!userRepository.existsByEmail(credential.email())) {
            throw new UnknownEntityException(
                    "Unknown user with email ='" + credential.email() + '\'', "User.email.exists");
        }
        String jws = jwsService.generateJws(credential, "restorePassword", conf.jws().restorePassTokenLifeTime());
        emailService.confirmEmailForRestorePass(jws, credential.email());
    }

    /**
     * Последний из двух шагов восстановления пароля. На этом шаге пользователю с указанной на первом шаге почтой
     * задается указанный пароль. Затем метод возвращает обновленные данные пользователя вместе с JWS токеном
     * общего доступа.
     * @param jwsBody учетные данные пользователя, у которого обновили пароль
     * @return данные пользователя вместе с JWS токеном общего доступа.
     * @see JwsWithUser
     */
    public JwsWithUser restorePasswordFinalStep(Credential jwsBody) {
        User user = tryFindByEmail(jwsBody.email());
        user.setCredential(jwsBody);
        user = save(user);
        String jws = jwsService.generateJws(user.getId(), "common", conf.jws().commonTokenLifeTime());
        return new JwsWithUser(user, jws);
    }

    /**
     * Делегирует вызов методу {@link com.bakuard.flashcards.dal.fragment.UserSaver#save(User)} добавляя
     * предварительную валидацию данных пользователя.
     * @throws NotUniqueEntityException см. {@link UserSaver#save(User)}
     * @throws ConstraintViolationException если нарушен хотя бы один из инвариантов {@link User}
     * @see com.bakuard.flashcards.dal.fragment.UserSaver
     */
    public User save(User user) {
        validator.assertValid(user);
        return userRepository.save(user);
    }

    /**
     * Первый из двух шагов удаления учетных данных пользователя и всех данных связанных с ними. На этом шаге
     * проверяется - существует ли пользователь с указанными данными и если да, то на почту пользователя
     * отправляется письмо с подтверждением удаления всех его данных. В обратной ссылке указанной в письме будет
     * содержаться JWS токен для подтверждения удаления пользователя, который требуется для выполнения второго
     * шага удаления пользователя.
     * @param userId идентификатор пользователя
     * @param email почта пользователя
     * @throws UnknownEntityException если пользователя с идентификатором userId и почтой email не существует.
     * @throws FailToSendMailException см. {@link EmailService#confirmEmailForDeletion}
     */
    public void deletionFirstStep(UUID userId, String email) {
        assertExists(userId, email);
        String jws = jwsService.generateJws(userId, "delete", conf.jws().deleteUserTokenLifeTime());
        emailService.confirmEmailForDeletion(jws, email);
    }

    /**
     * Последний из двух шагов удаления учетных данных пользователя и всех данных связанных с ним. На этом шаге
     * происходит фактическое удаление всех данных пользователя с указанным идентификатор. Если пользователь
     * userId уже был удален - метод ничего не делает.
     * @param userId идентификатор удаляемого пользователя
     */
    public void deletionFinalStep(UUID userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Проверяет - существует ли пользователь с таким идентификатором. Если это не так - выбрасывает исключение,
     * иначе - ничего не делает.
     * @param userId идентификатор искомого пользователя
     * @throws UnknownEntityException если пользователя с указанным идентификатором не существует.
     *                                {@link UnknownEntityException#getMessageKey()} вернет User.unknownId
     */
    public void assertExists(UUID userId) {
        if(!userRepository.existsById(userId)) {
            throw new UnknownEntityException(
                    "Unknown user with id=" + userId,
                    "User.unknownId"
            );
        }
    }

    /**
     * Проверяет - существует ли пользователь с таким идентификатором и почтой. Если это не так - выбрасывает
     * исключение, иначе - ничего не делает.
     * @param userId идентификатор искомого пользователя
     * @param email почта искомого пользователя
     * @throws UnknownEntityException если пользователя с указанным идентификатором и почтой не существует.
     *                                {@link UnknownEntityException#getMessageKey()} вернет User.unknownIdAndEmail
     */
    public void assertExists(UUID userId, String email) {
        if(userRepository.findById(userId).
                map(user -> !user.getEmail().equals(email)).
                orElse(true)) {
            throw new UnknownEntityException(
                    "Unknown user with id=" + userId + " and email='" + email + '\'',
                    "User.unknownIdAndEmail"
            );
        }
    }

    /**
     * Возвращает пользователя по его идентификатору. Если такого пользователя нет - выбрасывает исключение.
     * @param userId идентификатор искомого пользователя
     * @return пользователя по его идентификатору
     * @throws UnknownEntityException если пользователя с таким идентификатором не существует.
     *                                {@link UnknownEntityException#getMessageKey()} вернет User.unknownId
     */
    public User tryFindById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UnknownEntityException(
                        "Unknown user with id=" + userId,
                        "User.unknownId"
                )
        );
    }

    /**
     * Возвращает пользователя по его почте. Если такого пользователя нет - выбрасывает исключение.
     * @param email почта искомого пользователя
     * @return пользователя по его почте
     * @throws UnknownEntityException если пользователя с такой почтой не существует.
     *                                {@link UnknownEntityException#getMessageKey()} вернет User.unknownEmail
     */
    public User tryFindByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UnknownEntityException(
                        "Unknown user with email=" + email,
                        "User.unknownEmail"
                )
        );
    }

    /**
     * Возвращает указанную часть всех пользователей.
     * @param pageable параметры пагинации и сортировки
     * @return указанную часть всех пользователей.
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}
