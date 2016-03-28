package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.configuration.TestType;
import com.c4wrd.loadtester.printing.BottleneckPrinter;
import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.util.LogLevel;
import com.c4wrd.loadtester.util.Output;
import com.c4wrd.loadtester.util.RandomSelector;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BottleneckRequestTest extends RequestTest<ExecutorService> {

    public BottleneckRequestTest(int connectionCount, int interval) throws IOException {
        super(connectionCount, interval);
        this.intervalService = Executors.newFixedThreadPool(1);
        this.requestExecutorService = Executors.newFixedThreadPool(this.connectionCount);
    }

    public List<RequestDetail> run() throws ExecutionException, InterruptedException {

        Output.println(LogLevel.VERBOSE, "Starting a Bottleneck Test");

        Future<List<RequestDetail>> results = this.intervalService.submit(
                new RequestExecutorRunnable((int) (connectionCount / interval), interval));

        List<RequestDetail> result = results.get();
        this.intervalService.shutdownNow();
        this.requestExecutorService.shutdownNow();
        return result;
    }

    private class RequestExecutorRunnable extends RequestExecutorBase {

        private int currentConnectionCount, step, interval;

        public RequestExecutorRunnable(int step, int interval) {
            this.step = step;
            this.interval = interval;
        }

        @Override
        public List<RequestDetail> call() throws Exception {
            Queue<Future<RequestDetail>> requestQueue = new LinkedBlockingDeque<>();

            for (int i = 1; i <= interval; i++) {

                Queue<RequestRunnable> workQueue = new LinkedBlockingDeque<>();

                currentConnectionCount += step;

                if ( Output.getCurrentLogLevel() == LogLevel.VERBOSE ) {
                    Output.println(LogLevel.VERBOSE, "Creating interval %d of %d with %d connections", i, interval, currentConnectionCount);
                } else {
                    Output.print(LogLevel.DEFAULT, ".");
                }

                for (int j = 0; j < currentConnectionCount; j++) {

                    int selectedIndex = RandomSelector.nextInt(endpoints.size());
                    Endpoint selectedEndpoint = endpoints.get(selectedIndex);
                    String url = String.format("%s/%s?%s", host, selectedEndpoint.getEndpoint(),
                            selectedEndpoint.buildRandomData());

                    workQueue.add(new RequestRunnable(url, i));

                }

                requestQueue.addAll(requestExecutorService.invokeAll(workQueue));
            }

            List<RequestDetail> results = this.getResults(requestQueue);

            Output.println(LogLevel.VERBOSE, "Bottleneck request test is complete");

            return results;
        }
    }

    @Override
    public void printResults(List<RequestDetail> results) {
        if ( results.size() == 0 ) {
            Output.println(LogLevel.DEFAULT, "No results have been saved to report!");
        } else {
            new BottleneckPrinter(results, interval, connectionCount).print();
        }
    }
}
