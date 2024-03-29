package com.bakuard.flashcards.dto.word;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Schema(description = """
        Данные слова для списка повторяемых слов с английского на родной язык пользователя.
        """)
public class WordForRepetitionFromEnglishResponse {

    @Schema(description = "Уникальный идентификатор слова.")
    private UUID wordId;
    @Schema(description = "Уникальный идентификатор пользователя, к словарю которого относится это слово.")
    private UUID userId;
    @Schema(description = "Значение слова.")
    private String value;
    @Schema(description = "Список примеров слова.")
    private List<String> examples;

    public WordForRepetitionFromEnglishResponse() {

    }

    public UUID getWordId() {
        return wordId;
    }

    public WordForRepetitionFromEnglishResponse setWordId(UUID wordId) {
        this.wordId = wordId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public WordForRepetitionFromEnglishResponse setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getValue() {
        return value;
    }

    public WordForRepetitionFromEnglishResponse setValue(String value) {
        this.value = value;
        return this;
    }

    public List<String> getExamples() {
        return examples;
    }

    public WordForRepetitionFromEnglishResponse setExamples(List<String> examples) {
        this.examples = examples;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordForRepetitionFromEnglishResponse that = (WordForRepetitionFromEnglishResponse) o;
        return Objects.equals(wordId, that.wordId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(value, that.value) &&
                Objects.equals(examples, that.examples);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordId, userId, value, examples);
    }

    @Override
    public String toString() {
        return "WordForRepetitionResponse{" +
                "wordId=" + wordId +
                ", userId=" + userId +
                ", value='" + value + '\'' +
                ", examples=" + examples +
                '}';
    }

}
