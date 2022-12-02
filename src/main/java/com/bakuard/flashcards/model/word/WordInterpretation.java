package com.bakuard.flashcards.model.word;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Table("words_interpretations")
public class WordInterpretation {

    @Column("value")
    @NotBlank(message = "WordInterpretation.value.notBlank")
    private String value;
    @Transient
    private final List<OuterSource> outerSource;

    @PersistenceCreator
    public WordInterpretation(String value) {
        this.value = value;
        this.outerSource = new ArrayList<>();
    }

    public WordInterpretation(WordInterpretation other) {
        this.value = other.value;
        this.outerSource = new ArrayList<>(other.outerSource);
    }

    public String getValue() {
        return value;
    }

    public List<OuterSource> getSourceInfo() {
        return outerSource;
    }

    public Optional<LocalDate> getRecentUpdateDate(String outerSourceName) {
        return outerSource.stream().
                filter(outerSource -> outerSource.sourceName().equals(outerSourceName)).
                findFirst().
                map(OuterSource::recentUpdateDate);
    }

    public boolean hasOuterSource(String outerSourceName) {
        return outerSource.stream().
                anyMatch(outerSource -> outerSource.sourceName().equals(outerSourceName));
    }

    public boolean merge(WordInterpretation other) {
        boolean isMerged = value.equals(other.value);
        if(isMerged) {
            for(int i = 0; i < other.outerSource.size(); i++) {
                OuterSource otherInfo = other.outerSource.get(i);
                boolean isFind = false;
                int index = 0;
                for(int j = 0; j < outerSource.size() && !isFind; j++) {
                    isFind = outerSource.get(j).sourceName().equals(otherInfo.sourceName());
                    index = j;
                }
                if(isFind) outerSource.set(index, otherInfo);
                else outerSource.add(otherInfo);
            }
        }
        return isMerged;
    }

    public WordInterpretation addSourceInfo(OuterSource info) {
        outerSource.add(info);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordInterpretation that = (WordInterpretation) o;
        return Objects.equals(value, that.value) && Objects.equals(outerSource, that.outerSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, outerSource);
    }

    @Override
    public String toString() {
        return "WordInterpretation{" +
                "value='" + value + '\'' +
                ", sourceInfo=" + outerSource +
                '}';
    }

}
