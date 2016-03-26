package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.RequestDetail;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public abstract class RequestTest<ExecutorType extends ExecutorService> {

    protected int interval;
    protected int connectionCount;
    protected String host;

    protected ExecutorType intervalService;
    protected ExecutorService requestExecutorService;
    protected List<Endpoint> endpoints;

    /**
     * Creates a RequestTest with a specified connectionCount and interval,
     * which are used differently dependent upon the test type.
     */
    public RequestTest(int connectionCount, int interval) throws IOException {
        if (connectionCount < 0 || interval < 1) {
            throw new IllegalArgumentException("Invalid executor parameters!");
        }

        this.connectionCount = connectionCount;
        this.interval = interval;
    }

    /**
     * Sets the endpoints used within the test.
     */
    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    /**
     * Set's the url used within the test.
     */
    public void setUrl(String host) {
        this.host = host;
    }

    /**
     * Shuts down the test after it has been ran. If the test is still running, this will
     * cause the test to not save results.
     */
    public void shutdown() {
        this.requestExecutorService.shutdownNow();
        this.intervalService.shutdownNow();
    }

    /**
     * Implemented based on the test type, this runes the test and returns the request details received
     * after execution.
     */
    public abstract List<RequestDetail> run() throws ExecutionException, InterruptedException, IOException;

    /**
     * Reports the progress.
     */
    protected void report(String formattedString, Object... args) throws IOException {
        System.out.println(String.format(formattedString, args));
    }

}
