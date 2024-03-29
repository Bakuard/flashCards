package com.bakuard.flashcards.service;

import com.bakuard.flashcards.dal.StatisticRepository;
import com.bakuard.flashcards.model.statistic.*;
import com.bakuard.flashcards.validation.exception.UnknownEntityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сервис по сбору и предоставлению статистики связанной с повторением слов и устойчивых выражений.
 * Каждый метод этого класса выполняется в отдельной транзакции.
 */
@Transactional
public class StatisticService {

    private StatisticRepository statisticRepository;
    private Clock clock;

    /**
     * Создает новый сервис статистики.
     * @param statisticRepository репозиторий статистики
     * @param clock часы используемые для получения текущей даты (параметр добавлен для удобства тестирования)
     */
    public StatisticService(StatisticRepository statisticRepository, Clock clock) {
        this.statisticRepository = statisticRepository;
        this.clock = clock;
    }

    /**
     * Делегирует вызов методу {@link StatisticRepository#append(RepeatWordFromEnglishStatistic)}.
     * @param userId идентификатор пользователя, к словарю которого относится слово
     * @param wordId идентификатор слова
     * @param isRemember true - если пользователь успешно повторил слово, иначе - false.
     * @see StatisticRepository
     * @see RepeatWordFromEnglishStatistic
     */
    public void appendWordFromEnglish(UUID userId, UUID wordId, boolean isRemember) {
        statisticRepository.append(
                new RepeatWordFromEnglishStatistic(
                        userId,
                        wordId,
                        LocalDate.now(clock),
                        isRemember)
        );
    }

    /**
     * Делегирует вызов методу {@link StatisticRepository#append(RepeatWordFromNativeStatistic)}.
     * userId. Данные относятся к повторению слова с родного на английский язык.
     * @param userId идентификатор пользователя, к словарю которого относится слово
     * @param wordId идентификатор слова
     * @param isRemember true - если пользователь успешно повторил слово, иначе - false.
     * @see StatisticRepository
     * @see RepeatWordFromNativeStatistic
     */
    public void appendWordFromNative(UUID userId, UUID wordId, boolean isRemember) {
        statisticRepository.append(
                new RepeatWordFromNativeStatistic(
                        userId,
                        wordId,
                        LocalDate.now(clock),
                        isRemember)
        );
    }

    /**
     * Делегирует вызов методу {@link StatisticRepository#append(RepeatExpressionFromEnglishStatistic)}.
     * @param userId идентификатор пользователя, к словарю которого относится слово
     * @param expressionId идентификатор устойчивого выражения
     * @param isRemember true - если пользователь успешно повторил устойчивое выражение, иначе - false.
     * @see StatisticRepository
     * @see RepeatExpressionFromEnglishStatistic
     */
    public void appendExpressionFromEnglish(UUID userId, UUID expressionId, boolean isRemember) {
        statisticRepository.append(
                new RepeatExpressionFromEnglishStatistic(
                        userId,
                        expressionId,
                        LocalDate.now(clock),
                        isRemember)
        );
    }

    /**
     * Делегирует вызов методу {@link StatisticRepository#append(RepeatExpressionFromNativeStatistic)}.
     * @param userId идентификатор пользователя, к словарю которого относится слово
     * @param expressionId идентификатор устойчивого выражения
     * @param isRemember true - если пользователь успешно повторил устойчивое выражение, иначе - false.
     * @see StatisticRepository
     * @see RepeatExpressionFromNativeStatistic
     */
    public void appendExpressionFromNative(UUID userId, UUID expressionId, boolean isRemember) {
        statisticRepository.append(
                new RepeatExpressionFromNativeStatistic(
                        userId,
                        expressionId,
                        LocalDate.now(clock),
                        isRemember)
        );
    }

    /**
     * Делегирует вызов методу {@link StatisticRepository#wordRepetitionByPeriod(UUID, UUID, LocalDate, LocalDate)}.
     * Если указанный метод возвращает пустой Optional - выбрасывает исключение.
     * @throws UnknownEntityException если метод, которому делегируется вызов, возвращает пустой Optional.
     *                                {@link UnknownEntityException#getMessageKey()} вернет Statistic.unknownUserIdOrWordId
     * @see WordRepetitionByPeriodStatistic
     */
    public WordRepetitionByPeriodStatistic tryGetWordRepetitionByPeriod(
            UUID userId, UUID wordId, String start, String end) {
        return statisticRepository.wordRepetitionByPeriod(
                userId, wordId, LocalDate.parse(start), LocalDate.parse(end)
        ).orElseThrow(() -> new UnknownEntityException(
                "Unknown statistic for word with id=" + wordId + " and user with id=" + userId,
                "Statistic.unknownUserIdOrWordId"));
    }

    /**
     * Делегирует вызов методу {@link StatisticRepository#wordRepetitionByPeriod(UUID, UUID, LocalDate, LocalDate)}.
     * Если указанный метод возвращает пустой Optional - выбрасывает исключение.
     * @throws UnknownEntityException если метод, которому делегируется вызов, возвращает пустой Optional.
     *                                {@link UnknownEntityException#getMessageKey()} вернет Statistic.unknownUserIdOrExpressionId
     * @see ExpressionRepetitionByPeriodStatistic
     */
    public ExpressionRepetitionByPeriodStatistic tryGetExpressionRepetitionByPeriod(
            UUID userId, UUID expressionId, String start, String end) {
        return statisticRepository.expressionRepetitionByPeriod(
                userId, expressionId, LocalDate.parse(start), LocalDate.parse(end)
        ).orElseThrow(() -> new UnknownEntityException(
                "Unknown statistic for expression with id=" + expressionId + " and user with id=" + userId,
                "Statistic.unknownUserIdOrExpressionId"));
    }

    /**
     * Делегирует вызов методу {@link StatisticRepository#wordsRepetitionByPeriod(UUID, LocalDate, LocalDate, Pageable)}.
     * @see WordRepetitionByPeriodStatistic
     */
    public Page<WordRepetitionByPeriodStatistic> getWordsRepetitionByPeriod(
            UUID userId, String start, String end, Pageable pageable) {
        return statisticRepository.wordsRepetitionByPeriod(
                userId, LocalDate.parse(start), LocalDate.parse(end), pageable
        );
    }

    /**
     * Делегирует вызов методу {@link StatisticRepository#expressionsRepetitionByPeriod(UUID, LocalDate, LocalDate, Pageable)}.
     * @see ExpressionRepetitionByPeriodStatistic
     */
    public Page<ExpressionRepetitionByPeriodStatistic> getExpressionsRepetitionByPeriod(
            UUID userId, String start, String end, Pageable pageable) {
        return statisticRepository.expressionsRepetitionByPeriod(
                userId, LocalDate.parse(start), LocalDate.parse(end), pageable
        );
    }

}
