package com;

import java.util.Objects;

/**
 * @author vladimir.dolzhenko
 * @since 2017-01-19
 */
public final class StrCmp implements Comparable<StrCmp> {
    private final String string;
    private final int hashCode;

    public StrCmp(String string) {
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
        if (!(o instanceof StrCmp)) {
            return false;
        }
        StrCmp str = (StrCmp) o;
        return string.equals(str.string);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public int compareTo(StrCmp o) {
        return this.string.compareTo(o.string);
    }
}
