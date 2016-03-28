package com.c4wrd.loadtester.exceptions;

public class MalformedEndpointConfigException extends HighLoadException {

    public MalformedEndpointConfigException() {
        super("The endpoint configuration specified was not correct");
    }

}
