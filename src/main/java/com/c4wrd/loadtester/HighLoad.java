package com.c4wrd.loadtester;

import com.c4wrd.loadtester.configuration.EndpointConfiguration;
import com.c4wrd.loadtester.configuration.HighLoadConfig;
import com.c4wrd.loadtester.configuration.TestConfig;
import com.c4wrd.loadtester.data.DataSaver;
import com.c4wrd.loadtester.exceptions.HighLoadException;
import com.c4wrd.loadtester.request.QueryParameterSupplier;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.testservice.BottleneckRequestTest;
import com.c4wrd.loadtester.testservice.ConstantRequestTest;
import com.c4wrd.loadtester.testservice.FixedRequestTest;
import com.c4wrd.loadtester.testservice.RequestTest;
import com.c4wrd.loadtester.util.LogLevel;
import com.c4wrd.loadtester.util.Output;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HighLoad {

    private HighLoadConfig config;
    private RequestTest executor;
    private EndpointConfiguration endpointConfiguration;
    private List<RequestDetail> results;

    public HighLoad(HighLoadConfig testConfing, EndpointConfiguration endpointConfiguration) throws IOException {
        this.config = testConfing;
        this.endpointConfiguration = endpointConfiguration;
        this.setup();
    }

    public void run() throws InterruptedException, ExecutionException, IOException {
        if (this.executor != null) {
            this.results = this.executor.run();
        }
    }

    private void setup() throws IOException {
        TestConfig testConfig = config.getTestConfig();
        switch (config.getTestType()) {
            case BOTTLENECK:
                this.executor = new BottleneckRequestTest(
                        testConfig.getNumThreads(),
                        testConfig.getInterval());
                break;
            case CONSTANT_LOAD:
                this.executor = new ConstantRequestTest(
                        testConfig.getNumThreads(),
                        testConfig.getInterval());
                break;
            case FIXED_LOAD:
                this.executor = new FixedRequestTest(
                        testConfig.getNumThreads(),
                        testConfig.getInterval());
                break;
        }

        this.executor.setEndpoints(endpointConfiguration.getEndpoints());
        this.executor.setHost(config.getHost());
        QueryParameterSupplier.cacheRows(Paths.get(config.getDataFile()), (int) Math.pow((int) testConfig.getInterval(), 2.0));
    }

    /**
     * Saves the results of this test to a CSV file
     * @param location: The location of which to save the file
     */
    public void saveResults(String location) throws HighLoadException, IOException {
        DataSaver saver = new DataSaver(this.results, this.config);
        saver.save(location);
        Output.println(LogLevel.DEFAULT, "Results saved to %s", location);
    }

    /**
     * Displays the results of the test in a table in the terminal
     */
    public void displayResults(){
        this.executor.printResults(this.results);
    }

}
