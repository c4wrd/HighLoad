package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.util.LogLevel;
import com.c4wrd.loadtester.util.Output;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

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
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Implemented based on the test type, this runes the test and returns the request details received
     * after execution.
     */
    public abstract List<RequestDetail> run() throws ExecutionException, InterruptedException, IOException;


    public abstract void printResults(List<RequestDetail> results);

}
