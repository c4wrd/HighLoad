package com.c4wrd.loadtester.util;

public enum  LogLevel {
    RESULTS(0), DEFAULT(1), VERBOSE(2);

    private int value;

    LogLevel(int value) {
        this.value = value;
    }

    public LogLevel fromInt(int value) {
        switch ( value ) {
            case 0:
                return RESULTS;
            case 1:
                return DEFAULT;
            case 2:
                return VERBOSE;
        }

        return null;
    }

    public int toInteger() {
        return this.value;
    }
}
