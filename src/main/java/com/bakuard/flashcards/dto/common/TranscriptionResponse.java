package com.bakuard.flashcards.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

@Schema(description = "Транскрипция слова.")
public class TranscriptionResponse {

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
    @Schema(description = "Данные всех внешних источников из которых получена данная транскрипция.")
    private List<OuterSourceResponse> sourceInfo;

    public TranscriptionResponse() {

    }

    public String getValue() {
        return value;
    }

    public TranscriptionResponse setValue(String value) {
        this.value = value;
        return this;
    }

    public String getNote() {
        return note;
    }

    public TranscriptionResponse setNote(String note) {
        this.note = note;
        return this;
    }

    public List<OuterSourceResponse> getSourceInfo() {
        return sourceInfo;
    }

    public TranscriptionResponse setSourceInfo(List<OuterSourceResponse> sourceInfo) {
        this.sourceInfo = sourceInfo;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscriptionResponse that = (TranscriptionResponse) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(note, that.note) &&
                Objects.equals(sourceInfo, that.sourceInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, note, sourceInfo);
    }

    @Override
    public String toString() {
        return "TranscriptionResponse{" +
                "value='" + value + '\'' +
                ", note='" + note + '\'' +
                ", sourceInfo=" + sourceInfo +
                '}';
    }

}