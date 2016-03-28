package com.c4wrd.loadtester.exceptions;

public class DataFileNotFoundException extends HighLoadException {

    public DataFileNotFoundException(String fileName) {
        super(String.format("The specified file %s does not exist", fileName));
    }

}
