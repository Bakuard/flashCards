package com.bakuard.flashcards.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("conf")
public record ConfigData(int maxPageSize,
                         int defaultPageSize,
                         int minPageSize,
                         long jwsLifeTimeInDays,
                         String jdbcUrl,
                         String gmailService,
                         String gmailPassword,
                         String pathToGmailLetterForRegistration,
                         String pathToGmailLetterForRestorePass,
                         String pathToGmailLetterForDeletion,
                         String gmailLetterReturnAddress,
                         int levenshteinMaxDistance) {}
