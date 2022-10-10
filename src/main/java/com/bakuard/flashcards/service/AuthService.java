package com.bakuard.flashcards.service;

import com.bakuard.flashcards.config.ConfigData;
import com.bakuard.flashcards.dal.UserRepository;
import com.bakuard.flashcards.model.auth.credential.User;
import com.bakuard.flashcards.validation.UnknownEntityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
public class AuthService {

    private UserRepository userRepository;
    private JwsService jwsService;
    private ConfigData configData;

    public AuthService(UserRepository userRepository, JwsService jwsService, ConfigData configData) {
        this.userRepository = userRepository;
        this.jwsService = jwsService;
        this.configData = configData;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void tryDeleteById(UUID userId) {
        if(!existsById(userId)) {
            throw new UnknownEntityException(
                    "Unknown user with id=" + userId,
                    "User.unknown");
        }
        userRepository.deleteById(userId);
    }

    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    public void assertExists(UUID userId) {
        if(!existsById(userId)) {
            throw new UnknownEntityException(
                    "Unknown user with id=" + userId,
                    "User.unknownId"
            );
        }
    }

    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User tryFindById(UUID userId) {
        return findById(userId).orElseThrow(
                () -> new UnknownEntityException(
                        "Unknown user with id=" + userId,
                        "User.unknownId"
                )
        );
    }

    public User tryFindByEmail(String email) {
        return findByEmail(email).orElseThrow(
                () -> new UnknownEntityException(
                        "Unknown user with email=" + email,
                        "User.unknownEmail"
                )
        );
    }

    public long count() {
        return userRepository.count();
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}
