package com.bakuard.flashcards.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Толкование слова или устойчивого выражения")
public class InterpretationResponse {

    @Schema(description = "Подробное описание значения и употребления слова или устойчивого выражения.")
    private String value;

    public InterpretationResponse() {

    }

    public String getValue() {
        return value;
    }

    public InterpretationResponse setValue(String value) {
        this.value = value;
        return this;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterpretationResponse that = (InterpretationResponse) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "InterpretationResponse{" +
                "value='" + value + '\'' +
                '}';
    }

}
