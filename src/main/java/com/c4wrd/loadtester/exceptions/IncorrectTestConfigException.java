package com.c4wrd.loadtester.exceptions;

public class IncorrectTestConfigException extends HighLoadException {

    public IncorrectTestConfigException(String option) {
        super(String.format("The option '%s' has an incorrect value in your configuration", option));
    }

}
