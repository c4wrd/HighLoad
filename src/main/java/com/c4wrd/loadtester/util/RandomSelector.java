package com.c4wrd.loadtester.util;

import java.util.Random;

public class RandomSelector {

    private static Random randomizer = new Random();

    public static int nextInt(int upper) {
        return Math.abs(randomizer.nextInt() % upper);
    }

    public static int nextInt(int lower, int upper) {
        return Math.abs(randomizer.nextInt() % upper + lower);
    }

}
