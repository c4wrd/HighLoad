package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.request.RequestDetail;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public abstract class RequestExecutorBase implements Callable<List<RequestDetail>> {

    protected int threadCount, interval;

    RequestExecutorBase() {}

    RequestExecutorBase(int interval) {
        this.interval = interval;
    }

    RequestExecutorBase(int threadCount, int interval) {
        this.threadCount = threadCount;
        this.interval = interval;
    }

    List<RequestDetail> getResults(Queue<Future<RequestDetail>> requestQueue) throws IOException {
        return requestQueue.parallelStream()
                .map(
                        (item) -> {
                            try {
                                return item.isDone() ? item.get() : null;
                            } catch (InterruptedException | ExecutionException e) {
                                return null;
                            }
                        })
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

}
