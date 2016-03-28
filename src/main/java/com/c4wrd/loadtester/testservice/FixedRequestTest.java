package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.configuration.TestType;
import com.c4wrd.loadtester.printing.BasicPrinter;
import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.util.LogLevel;
import com.c4wrd.loadtester.util.Output;
import com.c4wrd.loadtester.util.RandomSelector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class FixedRequestTest extends RequestTest<ScheduledExecutorService> {

    public FixedRequestTest(int connectionCount, int interval) throws IOException {
        super(connectionCount, interval);
        this.intervalService = Executors.newScheduledThreadPool(1);
        this.requestExecutorService = Executors.newFixedThreadPool(this.connectionCount);
        Output.println(LogLevel.VERBOSE, "Creating a Fixed Request Test");
    }

    public List<RequestDetail> run() throws ExecutionException, InterruptedException, IOException {

        List<RequestDetail> requestDetailList = new LinkedList<>();

        for (int i = 0; i < interval; i++) {

            if (Output.getCurrentLogLevel() == LogLevel.VERBOSE ) {
                Output.println(LogLevel.VERBOSE, "Creating interval %d with %d connections", i, connectionCount);
            } else {
                Output.print(LogLevel.DEFAULT, ".");
            }

            Future<List<RequestDetail>> details =
                    this.intervalService.submit(new RequestExecutorRunnable(connectionCount, i + 1));

            requestDetailList.addAll(details.get());

            Output.println(LogLevel.VERBOSE, "Interval %d is complete", i + 1);

        }

        this.requestExecutorService.shutdown();
        this.intervalService.shutdown();

        return requestDetailList;

    }

    private class RequestExecutorRunnable extends RequestExecutorBase {

        RequestExecutorRunnable(int threadCount, int interval) {
            super(threadCount, interval);
        }

        @Override
        public List<RequestDetail> call() throws Exception {
            Queue<Future<RequestDetail>> requestQueue = new LinkedBlockingDeque<>();

            Queue<RequestRunnable> workQueue = new LinkedBlockingDeque<>();

            for (int i = 0; i < this.threadCount; i++) {

                int selectedIndex = RandomSelector.nextInt(endpoints.size());
                Endpoint selectedEndpoint = endpoints.get(selectedIndex);

                String url = String.format("%s/%s?%s", host, selectedEndpoint.getEndpoint(),
                        selectedEndpoint.buildRandomData());

                workQueue.add(new RequestRunnable(url, this.interval));
            }

            requestQueue.addAll(requestExecutorService.invokeAll(workQueue));

            List<RequestDetail> results = this.getResults(requestQueue);

            return results;
        }
    }

    @Override
    public void printResults(List<RequestDetail> results) {
        if ( results.size() == 0 ) {
            Output.println(LogLevel.DEFAULT, "No results have been saved to report!");
        } else {
            new BasicPrinter(results, interval, connectionCount, TestType.FIXED_LOAD).print();
        }
    }

}
