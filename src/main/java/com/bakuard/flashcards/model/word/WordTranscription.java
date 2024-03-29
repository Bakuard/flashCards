package com.bakuard.flashcards.model.word;

import com.bakuard.flashcards.validation.annotation.NotBlankOrNull;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Table("words_transcriptions")
public class WordTranscription {

    @Column("value")
    @NotBlank(message = "WordTranscription.value.notBlank")
    private String value;
    @Column("note")
    @NotBlankOrNull(message = "WordTranscription.note.notBlankOrNull")
    private String note;

    @PersistenceCreator
    public WordTranscription(String value, String note) {
        this.value = value;
        this.note = note;
    }

    public WordTranscription(WordTranscription other) {
        this.value = other.value;
        this.note = other.note;
    }

    public String getValue() {
        return value;
    }

    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordTranscription that = (WordTranscription) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, note);
    }

    @Override
    public String toString() {
        return "WordTranscription{" +
                "value='" + value + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

}
