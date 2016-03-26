package com.c4wrd.loadtester.configuration;

public enum TestType {

    CONSTANT_LOAD(0),
    FIXED_LOAD(1),
    BOTTLENECK(2);

    private int value;

    TestType(int i) {
        this.value = i;
    }

    public TestType fromString(String str) {

        switch (str.toLowerCase()) {
            case "constant":
                return CONSTANT_LOAD;
            case "fixed":
                return FIXED_LOAD;
            case "bottleneck":
                return BOTTLENECK;
        }

        return null;
    }
}