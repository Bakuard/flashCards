package com.bakuard.flashcards.model.credential;

import com.bakuard.flashcards.validation.Password;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class RawPassword {

    @Password(message = "Password.format")
    private String password;

    public RawPassword(String password) {
        this.password = password;
    }

    public String asString() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawPassword that = (RawPassword) o;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }

    @Override
    public String toString() {
        return "RawPassword{" + password + '}';
    }

}