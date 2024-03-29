package com.bakuard.flashcards.dto.expression;

import com.bakuard.flashcards.dto.common.ExampleRequest;
import com.bakuard.flashcards.dto.common.InterpretationRequest;
import com.bakuard.flashcards.dto.common.TranslateRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Schema(description = "Данные обновляемого устойчевого выражения.")
public class ExpressionUpdateRequest {

    @Schema(description = """
            Идентификатор пользователя, с которым связано указанное устойчевое выражение. <br/>
            Ограничения: не должно быть null.
            """)
    private UUID userId;
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
            Сам список может принимать значение null либо быть пустым.
            """)
    private List<InterpretationRequest> interpretations;
    @Schema(description = """
            Список переводов устойчевого выражения. <br/>
            Ограничения: <br/>
            1. Не должен содержать null <br/>
            2. Не должен содержать дубликатов <br/>
            Сам список может принимать значение null либо быть пустым.
            """)
    private List<TranslateRequest> translates;
    @Schema(description = """
            Список примеров устойчевого выражения. <br/>
            Ограничения: <br/>
            1. Не должен содержать null <br/>
            2. Не должен содержать дубликатов <br/>
            Сам список может принимать значение null либо быть пустым.
            """)
    private List<ExampleRequest> examples;

    public ExpressionUpdateRequest() {

    }

    public UUID getUserId() {
        return userId;
    }

    public ExpressionUpdateRequest setUserId(UUID userId) {
        this.userId = userId;
        return this;
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

    public List<InterpretationRequest> getInterpretations() {
        return interpretations;
    }

    public ExpressionUpdateRequest setInterpretations(List<InterpretationRequest> interpretations) {
        this.interpretations = interpretations;
        return this;
    }

    public List<TranslateRequest> getTranslates() {
        return translates;
    }

    public ExpressionUpdateRequest setTranslates(List<TranslateRequest> translates) {
        this.translates = translates;
        return this;
    }

    public List<ExampleRequest> getExamples() {
        return examples;
    }

    public ExpressionUpdateRequest setExamples(List<ExampleRequest> examples) {
        this.examples = examples;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionUpdateRequest that = (ExpressionUpdateRequest) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(expressionId, that.expressionId) &&
                Objects.equals(value, that.value) &&
                Objects.equals(note, that.note) &&
                Objects.equals(interpretations, that.interpretations) &&
                Objects.equals(translates, that.translates) &&
                Objects.equals(examples, that.examples);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, expressionId, value, note, interpretations, translates, examples);
    }

    @Override
    public String toString() {
        return "ExpressionUpdateRequest{" +
                "userId=" + userId +
                ", expressionId=" + expressionId +
                ", value='" + value + '\'' +
                ", note='" + note + '\'' +
                ", interpretations=" + interpretations +
                ", translates=" + translates +
                ", examples=" + examples +
                '}';
    }

}
