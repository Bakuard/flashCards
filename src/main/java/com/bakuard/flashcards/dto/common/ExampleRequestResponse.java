package com.bakuard.flashcards.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Пример использования слова или устойчевого выражения.")
public class ExampleRequestResponse {

    @Schema(description = """
            Пример на оригинальном языке. <br/>
            Должен представлять собой не пустую строку.
            """)
    private String origin;
    @Schema(description = """
            Перевод примера. <br/>
            Должен представлять собой не пустую строку.
            """)
    private String translate;
    @Schema(description = """
            Примечание к примеру. <br/>
            Должно представлять собой не пустую строку или иметь значение null.
            """)
    private String note;

    public ExampleRequestResponse() {
    }

    public String getOrigin() {
        return origin;
    }

    public ExampleRequestResponse setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getTranslate() {
        return translate;
    }

    public ExampleRequestResponse setTranslate(String translate) {
        this.translate = translate;
        return this;
    }

    public String getNote() {
        return note;
    }

    public ExampleRequestResponse setNote(String note) {
        this.note = note;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExampleRequestResponse that = (ExampleRequestResponse) o;
        return Objects.equals(origin, that.origin) &&
                Objects.equals(translate, that.translate) &&
                Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, translate, note);
    }

    @Override
    public String toString() {
        return "ExampleRequestResponse{" +
                "origin='" + origin + '\'' +
                ", translate='" + translate + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

}