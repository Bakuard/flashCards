package com.bakuard.flashcards.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Транскрипция слова.")
public class TranscriptionRequest {

    @Schema(description = """
            Значение транскрипции слова. <br/>
            Должна представлять собой не пустую строку.
            """)
    private String value;
    @Schema(description = """
            Примечание к транскрипции. <br/>
            Должно представлять собой не пустую строку или иметь значение null.
            """)
    private String note;

    public TranscriptionRequest() {
    }

    public String getValue() {
        return value;
    }

    public TranscriptionRequest setValue(String value) {
        this.value = value;
        return this;
    }

    public String getNote() {
        return note;
    }

    public TranscriptionRequest setNote(String note) {
        this.note = note;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscriptionRequest that = (TranscriptionRequest) o;
        return Objects.equals(value, that.value) && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, note);
    }

    @Override
    public String toString() {
        return "TranscriptionRequestResponse{" +
                "value='" + value + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

}
