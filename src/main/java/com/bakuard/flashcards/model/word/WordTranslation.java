package com.bakuard.flashcards.model.word;

import com.bakuard.flashcards.validation.NotBlankOrNull;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Table("words_translations")
public class WordTranslation {

    @NotBlank(message = "WordTranslation.value.notBlank")
    private String value;

    @NotBlankOrNull(message = "WordTranslation.note.notBlankOrNull")
    private String note;

    public WordTranslation(String value, String note) {
        this.value = value;
        this.note = note;
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
        WordTranslation that = (WordTranslation) o;
        return Objects.equals(value, that.value) && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, note);
    }

    @Override
    public String toString() {
        return "WordTranslation{" +
                "value='" + value + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

}