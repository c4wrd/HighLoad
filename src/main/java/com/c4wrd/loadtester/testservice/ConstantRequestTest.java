package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.util.RandomSelector;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ConstantRequestTest extends RequestTest<ExecutorService> {

    public ConstantRequestTest(int connectionCount, int interval) throws Exception {
        super(connectionCount, interval);
        this.intervalService = Executors.newFixedThreadPool(1);
        this.requestExecutorService = Executors.newFixedThreadPool(this.connectionCount);
    }

    public List<RequestDetail> run() throws ExecutionException, InterruptedException {

        List<RequestDetail> requestDetailList = new LinkedList<>();

        Future<List<RequestDetail>> details =
                this.intervalService.submit(new RequestExecutorRunnable(connectionCount, interval));

        for ( int i = 0 ; i < interval; i++ ) {
            //this.report("")
            Thread.sleep(1000);
        }

        this.intervalService.shutdownNow(); // notify our worker to stop working

        return details.get();

    }

    private class RequestExecutorRunnable implements Callable<List<RequestDetail>> {

        private int connectionCount, interval;

        RequestExecutorRunnable(int connectionCount, int interval) {
            this.connectionCount = connectionCount;
            this.interval = interval;
        }

        List<RequestDetail> getResults(Queue<Future<RequestDetail>> requestQueue) {
            return requestQueue.parallelStream()
                    .map(item -> {
                        try {
                            return item.isDone() ? item.get() : null;
                        } catch (InterruptedException | ExecutionException e) {
                            return null;
                        }
                    })
                    .filter(item -> item != null)
                    .collect(Collectors.toList());
        }

        @Override
        public List<RequestDetail> call() throws Exception {
            Queue<Future<RequestDetail>> requestQueue = new LinkedBlockingDeque<>();

            while (!Thread.interrupted()) {
                int selectedIndex = RandomSelector.nextInt(endpoints.size());
                Endpoint selectedEndpoint = endpoints.get(selectedIndex);
                String url = String.format("http://%s/%s?%s", host, selectedEndpoint.getEndpoint(),
                        selectedEndpoint.buildRandomData());
                requestQueue.add(requestExecutorService.submit((new RequestRunnable(url, true, connectionCount))));
            }

            List<RequestDetail> results = getResults(requestQueue);

            return results;
        }
    }
}
