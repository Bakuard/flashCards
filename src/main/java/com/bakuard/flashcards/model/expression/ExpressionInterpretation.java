package com.bakuard.flashcards.model.expression;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Table("expressions_interpretations")
public class ExpressionInterpretation {

    @NotBlank(message = "ExpressionInterpretation.value.notBlank")
    private String value;

    @PersistenceCreator
    public ExpressionInterpretation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionInterpretation that = (ExpressionInterpretation) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ExpressionInterpretation{" +
                "value='" + value + '\'' +
                '}';
    }

}
