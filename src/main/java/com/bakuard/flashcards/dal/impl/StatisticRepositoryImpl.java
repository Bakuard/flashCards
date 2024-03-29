package com.bakuard.flashcards.dal.impl;

import com.bakuard.flashcards.dal.StatisticRepository;
import com.bakuard.flashcards.model.statistic.*;
import com.bakuard.flashcards.validation.exception.InvalidParameter;
import com.bakuard.flashcards.validation.exception.NotUniqueEntityException;
import com.bakuard.flashcards.validation.exception.UnknownEntityException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class StatisticRepositoryImpl implements StatisticRepository {

    private JdbcTemplate jdbcTemplate;

    public StatisticRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void append(RepeatWordFromEnglishStatistic statistic) {
        Objects.requireNonNull(statistic, "statistic can't be null");

        try {
            jdbcTemplate.update(
                    """
                            insert into repeat_words_from_english_statistic(user_id,
                                                                            word_id,
                                                                            repetition_date,
                                                                            is_remember)
                                values (?,?,?,?);
                            """,
                    ps -> {
                        ps.setObject(1, statistic.userId());
                        ps.setObject(2, statistic.wordId());
                        ps.setDate(3, Date.valueOf(statistic.currentDate()));
                        ps.setBoolean(4, statistic.isRemember());
                    }
            );
        } catch(DuplicateKeyException e) {
            throw new NotUniqueEntityException(
                    "Statistic " + statistic + " already exists",
                    e,
                    "Statistic.unique",
                    true);
        } catch(DataIntegrityViolationException e) {
            throw new UnknownEntityException(
                    "Unknown user with id=" + statistic.userId() + " or word with id=" + statistic.wordId(),
                    e,
                    "Statistic.unknownUserIdAndWordId",
                    true);
        }
    }

    @Override
    public void append(RepeatWordFromNativeStatistic statistic) {
        Objects.requireNonNull(statistic, "statistic can't be null");

        try {
            jdbcTemplate.update(
                    """
                            insert into repeat_words_from_native_statistic(user_id,
                                                                           word_id,
                                                                           repetition_date,
                                                                           is_remember)
                                values (?,?,?,?);
                            """,
                    ps -> {
                        ps.setObject(1, statistic.userId());
                        ps.setObject(2, statistic.wordId());
                        ps.setDate(3, Date.valueOf(statistic.currentDate()));
                        ps.setBoolean(4, statistic.isRemember());
                    }
            );
        } catch(DuplicateKeyException e) {
            throw new NotUniqueEntityException(
                    "Statistic " + statistic + " already exists",
                    e,
                    "Statistic.unique",
                    true);
        } catch(DataIntegrityViolationException e) {
            throw new UnknownEntityException(
                    "Unknown user with id=" + statistic.userId() + " or word with id=" + statistic.wordId(),
                    e,
                    "Statistic.unknownUserIdAndWordId",
                    true);
        }
    }

    @Override
    public void append(RepeatExpressionFromEnglishStatistic statistic) {
        Objects.requireNonNull(statistic, "statistic can't be null");

        try {
            jdbcTemplate.update(
                    """
                            insert into repeat_expressions_from_english_statistic(user_id,
                                                                                  expression_id,
                                                                                  repetition_date,
                                                                                  is_remember)
                                values (?,?,?,?);
                            """,
                    ps -> {
                        ps.setObject(1, statistic.userId());
                        ps.setObject(2, statistic.expressionId());
                        ps.setDate(3, Date.valueOf(statistic.currentDate()));
                        ps.setBoolean(4, statistic.isRemember());
                    }
            );
        } catch(DuplicateKeyException e) {
            throw new NotUniqueEntityException(
                    "Statistic " + statistic + " already exists",
                    e,
                    "Statistic.unique",
                    true);
        } catch(DataIntegrityViolationException e) {
            throw new UnknownEntityException(
                    "Unknown user with id=" + statistic.userId() + " or expression with id=" + statistic.expressionId(),
                    e,
                    "Statistic.unknownUserIdOrExpressionId",
                    true);
        }
    }

    @Override
    public void append(RepeatExpressionFromNativeStatistic statistic) {
        Objects.requireNonNull(statistic, "statistic can't be null");

        try {
            jdbcTemplate.update(
                    """
                            insert into repeat_expressions_from_native_statistic(user_id,
                                                                                 expression_id,
                                                                                 repetition_date,
                                                                                 is_remember)
                                values (?,?,?,?);
                            """,
                    ps -> {
                        ps.setObject(1, statistic.userId());
                        ps.setObject(2, statistic.expressionId());
                        ps.setDate(3, Date.valueOf(statistic.currentDate()));
                        ps.setBoolean(4, statistic.isRemember());
                    }
            );
        } catch(DuplicateKeyException e) {
            throw new NotUniqueEntityException(
                    "Statistic " + statistic + " already exists",
                    e,
                    "Statistic.unique",
                    true);
        } catch(DataIntegrityViolationException e) {
            throw new UnknownEntityException(
                    "Unknown user with id=" + statistic.userId() + " or expression with id=" + statistic.expressionId(),
                    e,
                    "Statistic.unknownUserIdOrExpressionId",
                    true);
        }
    }

    @Override
    public Optional<WordRepetitionByPeriodStatistic> wordRepetitionByPeriod(
            UUID userId, UUID wordId, LocalDate start, LocalDate end) {
        Objects.requireNonNull(userId, "userId can't be null");
        Objects.requireNonNull(wordId, "wordId can't be null");
        Objects.requireNonNull(start, "start can't be null");
        Objects.requireNonNull(end, "end can't be null");
        assertPeriodIsValid(start, end);

        String value = jdbcTemplate.query("select value from words where word_id = ? and user_id = ?;",
                ps -> {
                    ps.setObject(1, wordId);
                    ps.setObject(2, userId);
                },
                rs -> {
                    String result = null;
                    if(rs.next()) result = rs.getString("value");
                    return result;
                });

        WordRepetitionByPeriodStatistic result = null;
        if(value != null) {
            result = jdbcTemplate.query(
                    """
                    select countTrue(repeat_words_statistic.eng_is_remember) as remember_from_english,
                           countFalse(repeat_words_statistic.eng_is_remember) as not_remember_from_english,
                           countTrue(repeat_words_statistic.ntv_is_remember) as remember_from_native,
                           countFalse(repeat_words_statistic.ntv_is_remember) as not_remember_from_native
                     from repeat_words_statistic
                     where user_id = ?
                           and repetition_date >= ?
                           and repetition_date <= ?
                           and word_id = ?;
                    """,
                    ps -> {
                        ps.setObject(1, userId);
                        ps.setObject(2, start);
                        ps.setObject(3, end);
                        ps.setObject(4, wordId);
                    },
                    rs -> {
                        long eng_remember = 0L;
                        long eng_not_remember = 0L;
                        long ntv_remember = 0L;
                        long ntv_not_remember = 0L;

                        if(rs.next()) {
                            eng_remember = rs.getInt("remember_from_english");
                            eng_not_remember = rs.getInt("not_remember_from_english");
                            ntv_remember = rs.getInt("remember_from_native");
                            ntv_not_remember = rs.getInt("not_remember_from_native");
                        }

                        return new WordRepetitionByPeriodStatistic(
                                userId,
                                wordId,
                                value,
                                eng_remember,
                                eng_not_remember,
                                ntv_remember,
                                ntv_not_remember
                        );
                    });
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<ExpressionRepetitionByPeriodStatistic> expressionRepetitionByPeriod(
            UUID userId, UUID expressionId, LocalDate start, LocalDate end) {
        Objects.requireNonNull(userId, "userId can't be null");
        Objects.requireNonNull(expressionId, "expressionId can't be null");
        Objects.requireNonNull(start, "start can't be null");
        Objects.requireNonNull(end, "end can't be null");
        assertPeriodIsValid(start, end);

        String value = jdbcTemplate.query("select value from expressions where expression_id = ? and user_id = ?;",
                ps -> {
                    ps.setObject(1, expressionId);
                    ps.setObject(2, userId);
                },
                rs -> {
                    String result = null;
                    if(rs.next()) result = rs.getString("value");
                    return result;
                });

        ExpressionRepetitionByPeriodStatistic result = null;
        if(value != null) {
            result = jdbcTemplate.query(
                    """
                    select countTrue(repeat_expressions_statistic.eng_is_remember) as remember_from_english,
                           countFalse(repeat_expressions_statistic.eng_is_remember) as not_remember_from_english,
                           countTrue(repeat_expressions_statistic.ntv_is_remember) as remember_from_native,
                           countFalse(repeat_expressions_statistic.ntv_is_remember) as not_remember_from_native
                     from repeat_expressions_statistic
                     where user_id = ?
                           and repetition_date >= ?
                           and repetition_date <= ?
                           and expression_id = ?;
                    """,
                    ps -> {
                        ps.setObject(1, userId);
                        ps.setObject(2, start);
                        ps.setObject(3, end);
                        ps.setObject(4, expressionId);
                    },
                    rs -> {
                        long eng_remember = 0L;
                        long eng_not_remember = 0L;
                        long ntv_remember = 0L;
                        long ntv_not_remember = 0L;

                        if(rs.next()) {
                            eng_remember = rs.getInt("remember_from_english");
                            eng_not_remember = rs.getInt("not_remember_from_english");
                            ntv_remember = rs.getInt("remember_from_native");
                            ntv_not_remember = rs.getInt("not_remember_from_native");
                        }

                        return new ExpressionRepetitionByPeriodStatistic(
                                userId,
                                expressionId,
                                value,
                                eng_remember,
                                eng_not_remember,
                                ntv_remember,
                                ntv_not_remember
                        );
                    });
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Page<WordRepetitionByPeriodStatistic> wordsRepetitionByPeriod(
            UUID userId, LocalDate start, LocalDate end, Pageable pageable) {
        Objects.requireNonNull(userId, "userId can't be null");
        Objects.requireNonNull(start, "start can't be null");
        Objects.requireNonNull(end, "end can't be null");
        Objects.requireNonNull(pageable, "pageable can't be null");
        assertPeriodIsValid(start, end);

        List<WordRepetitionByPeriodStatistic> statistics = jdbcTemplate.query(
                """
                select repeat_words_statistic.value,
                       repeat_words_statistic.word_id,
                       countTrue(repeat_words_statistic.eng_is_remember) as remember_from_english,
                       countFalse(repeat_words_statistic.eng_is_remember) as not_remember_from_english,
                       countTrue(repeat_words_statistic.ntv_is_remember) as remember_from_native,
                       countFalse(repeat_words_statistic.ntv_is_remember) as not_remember_from_native
                 from repeat_words_statistic
                 where user_id = ? and repetition_date >= ? and repetition_date <= ?
                 group by word_id, value
                 order by %s
                 limit ? offset ?;
                """.formatted(toSortString(pageable.getSort())),
                ps -> {
                    ps.setObject(1, userId);
                    ps.setDate(2, Date.valueOf(start));
                    ps.setDate(3, Date.valueOf(end));
                    ps.setInt(4, pageable.getPageSize());
                    ps.setInt(5, (int) pageable.getOffset());
                },
                rs -> {
                    List<WordRepetitionByPeriodStatistic> result = new ArrayList<>();
                    while(rs.next()) {
                        result.add(new WordRepetitionByPeriodStatistic(
                                userId,
                                (UUID) rs.getObject("word_id"),
                                rs.getString("value"),
                                rs.getInt("remember_from_english"),
                                rs.getInt("not_remember_from_english"),
                                rs.getInt("remember_from_native"),
                                rs.getInt("not_remember_from_native")
                        ));
                    }
                    return result;
                });

        return PageableExecutionUtils.getPage(statistics, pageable, statistics::size);
    }

    @Override
    public Page<ExpressionRepetitionByPeriodStatistic> expressionsRepetitionByPeriod(
            UUID userId, LocalDate start, LocalDate end, Pageable pageable) {
        Objects.requireNonNull(userId, "userId can't be null");
        Objects.requireNonNull(start, "start can't be null");
        Objects.requireNonNull(end, "end can't be null");
        Objects.requireNonNull(pageable, "pageable can't be null");
        assertPeriodIsValid(start, end);

        List<ExpressionRepetitionByPeriodStatistic> statistics = jdbcTemplate.query(
                """
                select repeat_expressions_statistic.value,
                       repeat_expressions_statistic.expression_id,
                       countTrue(repeat_expressions_statistic.eng_is_remember) as remember_from_english,
                       countFalse(repeat_expressions_statistic.eng_is_remember) as not_remember_from_english,
                       countTrue(repeat_expressions_statistic.ntv_is_remember) as remember_from_native,
                       countFalse(repeat_expressions_statistic.ntv_is_remember) as not_remember_from_native
                 from repeat_expressions_statistic
                 where user_id = ? and repetition_date >= ? and repetition_date <= ?
                 group by expression_id, value
                 order by %s
                 limit ? offset ?;
                """.formatted(toSortString(pageable.getSort())),
                ps -> {
                    ps.setObject(1, userId);
                    ps.setDate(2, Date.valueOf(start));
                    ps.setDate(3, Date.valueOf(end));
                    ps.setInt(4, pageable.getPageSize());
                    ps.setInt(5, (int) pageable.getOffset());
                },
                rs -> {
                    List<ExpressionRepetitionByPeriodStatistic> result = new ArrayList<>();
                    while(rs.next()) {
                        result.add(new ExpressionRepetitionByPeriodStatistic(
                                userId,
                                (UUID) rs.getObject("expression_id"),
                                rs.getString("value"),
                                rs.getInt("remember_from_english"),
                                rs.getInt("not_remember_from_english"),
                                rs.getInt("remember_from_native"),
                                rs.getInt("not_remember_from_native")
                        ));
                    }
                    return result;
                });

        return PageableExecutionUtils.getPage(statistics, pageable, statistics::size);
    }


    private void assertPeriodIsValid(LocalDate start, LocalDate end) {
        if(start.isAfter(end)) {
            throw new InvalidParameter("Invalid period border: " + start + ", " + end,
                    "StatisticRepository.invalidPeriodBorder");
        }
    }

    private String toSortString(Sort sort) {
        return sort.stream().
                map(order -> order.getProperty() + " " + order.getDirection()).
                reduce((a, b) -> String.join(", ", a, b)).
                orElseThrow();
    }

}
