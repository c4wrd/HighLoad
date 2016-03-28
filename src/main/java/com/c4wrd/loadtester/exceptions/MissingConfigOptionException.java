package com.c4wrd.loadtester.exceptions;

public class MissingConfigOptionException extends HighLoadException {

    public MissingConfigOptionException(String optionName) {
        super(String.format("You are missing the required config option '%s' in your test configuration", optionName));
    }

}
