package com.bakuard.flashcards.dto.expression;

import com.bakuard.flashcards.dto.common.ExampleRequestResponse;
import com.bakuard.flashcards.dto.common.InterpretationRequestResponse;
import com.bakuard.flashcards.dto.common.TranslateRequestResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Schema(description = "Данные обновляемого устойчевого выражения.")
public class ExpressionUpdateRequest {

    @Schema(description = """
            Уникальный идентификатор устойчевого выражения. <br/>
            Ограничения: не должен быть null.
            """)
    private UUID expressionId;
    @Schema(description = """
            Значение устойчевого выражения. <br/>
            Должно представлять собой не пустую строку.
            """)
    private String value;
    @Schema(description = """
            Примечание к устойчевого выражения. <br/>
            Ограничения: не должно быть пустой строкой или должно быть null.
            """)
    private String note;
    @Schema(description = """
            Список интерпретаций устойчевого выражения. <br/>
            Ограничения: <br/>
            1. Не должен содержать null <br/>
            2. Не должен содержать дубликатов <br/>
            Сам список может принимать значение null.
            """)
    private List<InterpretationRequestResponse> interpretations;
    @Schema(description = """
            Список переводов устойчевого выражения. <br/>
            Ограничения: <br/>
            1. Не должен содержать null <br/>
            2. Не должен содержать дубликатов <br/>
            Сам список может принимать значение null.
            """)
    private List<TranslateRequestResponse> translates;
    @Schema(description = """
            Список примеров устойчевого выражения. <br/>
            Ограничения: <br/>
            1. Не должен содержать null <br/>
            2. Не должен содержать дубликатов <br/>
            Сам список может принимать значение null.
            """)
    private List<ExampleRequestResponse> examples;

    public ExpressionUpdateRequest() {

    }

    public UUID getExpressionId() {
        return expressionId;
    }

    public ExpressionUpdateRequest setExpressionId(UUID expressionId) {
        this.expressionId = expressionId;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ExpressionUpdateRequest setValue(String value) {
        this.value = value;
        return this;
    }

    public String getNote() {
        return note;
    }

    public ExpressionUpdateRequest setNote(String note) {
        this.note = note;
        return this;
    }

    public List<InterpretationRequestResponse> getInterpretations() {
        return interpretations;
    }

    public ExpressionUpdateRequest setInterpretations(List<InterpretationRequestResponse> interpretations) {
        this.interpretations = interpretations;
        return this;
    }

    public List<TranslateRequestResponse> getTranslates() {
        return translates;
    }

    public ExpressionUpdateRequest setTranslates(List<TranslateRequestResponse> translates) {
        this.translates = translates;
        return this;
    }

    public List<ExampleRequestResponse> getExamples() {
        return examples;
    }

    public ExpressionUpdateRequest setExamples(List<ExampleRequestResponse> examples) {
        this.examples = examples;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionUpdateRequest that = (ExpressionUpdateRequest) o;
        return Objects.equals(expressionId, that.expressionId) &&
                Objects.equals(value, that.value) &&
                Objects.equals(note, that.note) &&
                Objects.equals(interpretations, that.interpretations) &&
                Objects.equals(translates, that.translates) &&
                Objects.equals(examples, that.examples);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressionId, value, note, interpretations, translates, examples);
    }

    @Override
    public String toString() {
        return "ExpressionUpdateRequest{" +
                "expressionId=" + expressionId +
                ", value='" + value + '\'' +
                ", note='" + note + '\'' +
                ", interpretations=" + interpretations +
                ", translates=" + translates +
                ", examples=" + examples +
                '}';
    }

}