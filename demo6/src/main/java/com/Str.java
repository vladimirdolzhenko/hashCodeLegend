package com;

import java.util.Objects;

/**
 * @author vladimir.dolzhenko
 * @since 2017-01-19
 */
public final class Str {
    private final String string;
    private final int hashCode;

    public Str(String string) {
        this.string = Objects.requireNonNull(string);
        this.hashCode = string.hashCode();
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Str)) {
            return false;
        }
        Str str = (Str) o;
        return string.equals(str.string);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
