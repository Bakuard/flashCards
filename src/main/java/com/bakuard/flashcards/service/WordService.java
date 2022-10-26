package com.bakuard.flashcards.service;

import com.bakuard.flashcards.config.ConfigData;
import com.bakuard.flashcards.dal.IntervalRepository;
import com.bakuard.flashcards.dal.WordRepository;
import com.bakuard.flashcards.model.word.Word;
import com.bakuard.flashcards.model.RepetitionResult;
import com.bakuard.flashcards.validation.UnknownEntityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public class WordService {

    private WordRepository wordRepository;
    private IntervalRepository intervalRepository;
    private Clock clock;
    private ConfigData configData;

    public WordService(WordRepository wordRepository,
                       IntervalRepository intervalRepository,
                       Clock clock,
                       ConfigData configData) {
        this.wordRepository = wordRepository;
        this.intervalRepository = intervalRepository;
        this.clock = clock;
        this.configData = configData;
    }

    public int getLowestRepeatInterval(UUID userId) {
        return intervalRepository.findAll(userId).get(0);
    }

    public Word save(Word word) {
        return wordRepository.save(word);
    }

    public void tryDeleteById(UUID userId, UUID wordId) {
        if(!existsById(userId, wordId)) {
            throw new UnknownEntityException(
                    "Unknown word with id=" + wordId + " userId=" + userId,
                    "Word.unknownId");
        }
        wordRepository.deleteById(userId, wordId);
    }

    public boolean existsById(UUID userId, UUID wordId) {
        return wordRepository.existsById(userId, wordId);
    }

    public Optional<Word> findById(UUID userId, UUID wordId) {
        return wordRepository.findById(userId, wordId);
    }

    public Page<Word> findByValue(UUID userId, String value, int maxDistance, Pageable pageable) {
        maxDistance = Math.max(maxDistance, 1);
        maxDistance = Math.min(configData.levenshteinMaxDistance(), maxDistance);
        final int distance = maxDistance;

        return PageableExecutionUtils.getPage(
                wordRepository.findByValue(userId, value, maxDistance, pageable.getPageSize(), pageable.getPageNumber()),
                pageable,
                () -> wordRepository.countForValue(userId, value, distance)
        );
    }

    public Word tryFindById(UUID userId, UUID wordId) {
        return findById(userId, wordId).
                orElseThrow(
                        () -> new UnknownEntityException(
                                "Unknown word with id=" + wordId + " userId=" + userId,
                                "Word.unknownId"
                        )
                );
    }

    public long count(UUID userId) {
        return wordRepository.count(userId);
    }

    public long countForRepeat(UUID userId) {
        return wordRepository.countForRepeatFromEnglish(userId, LocalDate.now(clock));
    }

    public Page<Word> findByUserId(UUID userId, Pageable pageable) {
        return wordRepository.findByUserId(userId, pageable);
    }

    public Page<Word> findAllForRepeatFromEnglish(UUID userId, Pageable pageable) {
        LocalDate date = LocalDate.now(clock);

        return PageableExecutionUtils.getPage(
                wordRepository.findAllForRepeatFromEnglish(userId, date, pageable.getPageSize(), pageable.getPageNumber()),
                pageable,
                () -> wordRepository.countForRepeatFromEnglish(userId, date)
        );
    }

    public Page<Word> findAllForRepeatFromNative(UUID userId, Pageable pageable) {
        LocalDate date = LocalDate.now(clock);

        return PageableExecutionUtils.getPage(
                wordRepository.findAllForRepeatFromNative(userId, date, pageable.getPageSize(), pageable.getPageNumber()),
                pageable,
                () -> wordRepository.countForRepeatFromNative(userId, date)
        );
    }

    public Word repeatFromEnglish(UUID userId, UUID wordId, boolean isRemember) {
        Word word = tryFindById(userId, wordId);
        word.repeatFromEnglish(isRemember, LocalDate.now(clock), intervalRepository.findAll(userId));
        save(word);
        return word;
    }

    public RepetitionResult<Word> repeatFromNative(UUID userId, UUID wordId, String inputWordValue) {
        Word word = tryFindById(userId, wordId);
        boolean isRemember = word.repeatFromNative(inputWordValue, LocalDate.now(clock), intervalRepository.findAll(userId));
        save(word);
        return new RepetitionResult<>(word, isRemember);
    }

    public Word markForRepetitionFromEnglish(UUID userId, UUID wordId) {
        Word word = tryFindById(userId, wordId);
        word.markForRepetitionFromEnglish(LocalDate.now(clock), intervalRepository.findAll(userId).get(0));
        save(word);
        return word;
    }

    public Word markForRepetitionFromNative(UUID userId, UUID wordId) {
        Word word = tryFindById(userId, wordId);
        word.markForRepetitionFromNative(LocalDate.now(clock), intervalRepository.findAll(userId).get(0));
        save(word);
        return word;
    }

    public boolean isHotRepeatFromEnglish(Word word) {
        List<Integer> intervals = intervalRepository.findAll(word.getUserId());
        return word.isHotRepeatFromEnglish(intervals.get(0));
    }

    public boolean isHotRepeatFromNative(Word word) {
        List<Integer> intervals = intervalRepository.findAll(word.getUserId());
        return word.isHotRepeatFromNative(intervals.get(0));
    }


}
