package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.configuration.TestType;
import com.c4wrd.loadtester.printing.BasicPrinter;
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

public class ConstantRequestTest extends RequestTest<ExecutorService> {

    public ConstantRequestTest(int connectionCount, int interval) throws IOException {
        super(connectionCount, interval);
        this.intervalService = Executors.newFixedThreadPool(1);
        this.requestExecutorService = new ThreadPoolExecutor(connectionCount,
                connectionCount,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(connectionCount),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public List<RequestDetail> run() throws ExecutionException, InterruptedException {

        Output.println(LogLevel.VERBOSE, "Creating a Constant Request Test");

        Future<List<RequestDetail>> details =
                this.intervalService.submit(new ConstantRequestExecutor());

        Long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(interval);

        while (System.currentTimeMillis() < endTime) {
            Output.print(LogLevel.DEFAULT, ".");
            Thread.sleep(1000);
        }

        Output.println(LogLevel.DEFAULT, "Gathering results...");

        this.intervalService.shutdownNow();
        this.requestExecutorService.shutdownNow();

        return details.get();

    }

    private class ConstantRequestExecutor extends RequestExecutorBase {

        @Override
        public List<RequestDetail> call() throws Exception {

            Queue<Future<RequestDetail>> requestQueue = new LinkedBlockingDeque<>();

            while (!Thread.interrupted()) {

                int selectedIndex = RandomSelector.nextInt(endpoints.size());
                Endpoint selectedEndpoint = endpoints.get(selectedIndex);

                String url = String.format("%s/%s?%s",
                        host,
                        selectedEndpoint.getEndpoint(),
                        selectedEndpoint.buildRandomData()
                );

                try {
                    requestQueue.add(requestExecutorService.submit(
                            (new RequestRunnable(url))));
                } catch (RejectedExecutionException ex) {
                    // do nothing
                };

            }

            // cancel queued requests
            requestExecutorService.shutdownNow();

            List<RequestDetail> results = getResults(requestQueue);
            return results;
        }
    }

    @Override
    public void printResults(List<RequestDetail> results) {
        if ( results.size() == 0 ) {
            Output.println(LogLevel.DEFAULT, "No results have been saved to report!");
        } else {
            new BasicPrinter(results, interval, this.connectionCount, TestType.CONSTANT_LOAD).print();
        }
    }
}
