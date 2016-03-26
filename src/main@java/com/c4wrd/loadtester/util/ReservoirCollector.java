package com.c4wrd.loadtester.util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * A reservoir sample is an algorithm that takes a list N of n elements
 * and selects k random elements from the list into list K, where each element in
 * the list N has a 1/n chance of being in K.
 *
 * This implementation allows the random choosing of elements from an indefinite
 * stream of elements, forming an accurate (at the cost of Random not necessarily
 * producing random integers).
 * @param <T>
 */
public class ReservoirCollector<T> implements Collector<T, List<T>, List<T>> {

    /**
     * The desired number of elements to acquire randomly from the stream
     */
    private final int k;

    /**
     * Represents the current index in the stream, used for generating our seed.
     */
    private int sIndex;

    /**
     * Random instance used to calculate our seed.
     */
    private final Random rand;

    /**
     * Initializes a Reservoir sampler
     * @param numDesiredItems
     */
    public ReservoirCollector(int numDesiredItems) {
        this.k = numDesiredItems;
        this.rand = new Random();
    }

    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    /**
     * Accumulates a list of random elements from a stream.
     * We first add the first k elements, then proceeding
     * we will create a seed based off of a random number
     * % read items, which will in the end result in every
     * element having a 1/n chance of being selected.
     */
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return (final List<T> selected, T element) -> {
            sIndex++;    // increment our sIndex in the stream

            if (selected.size() < k)
            {
                // we want to add the first K elements to our array
                selected.add(element);
            }
            else
            {
                // note, the sIndex will be off by 1 as
                // we are using a zero-sIndex base
                int seed = Math.abs(rand.nextInt() % (this.sIndex + 1));

                if (seed < k) {   // we have an item we can add
                    selected.set(seed, element);
                }

            }
        };
    }

    /**
     * @return Returns a function to merge two sets of lists
     */
    @Override
    public BinaryOperator<List<T>> combiner() {
        return (left, right) -> {
            return left.addAll(right) ? left : left;
        };
    }

    /**
     * The resulting stream will be unordered, so let's ensure that Java
     * knows this...
     */
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH);
    }

    /**
     * No other operations need to be performed on the stream,
     * we can just return the stream.
     */
    @Override
    public Function<List<T>, List<T>> finisher() {
        return (i) -> i;
    }

}