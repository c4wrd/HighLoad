package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.util.RandomSelector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class FixedRequestTest extends RequestTest<ScheduledExecutorService> {

    public FixedRequestTest(int connectionCount, int interval) throws Exception {
        super(connectionCount, interval);
        this.intervalService = Executors.newScheduledThreadPool(1);
        this.requestExecutorService = Executors.newFixedThreadPool(this.connectionCount);
        this.report("Creating FixedRequestTest");
    }

    public List<RequestDetail> run() throws ExecutionException, InterruptedException, IOException {

        List<RequestDetail> requestDetailList = new LinkedList<>();

        for (int i = 0; i < interval; i++) {
            this.report("Creating interval %d with %d connections", i, connectionCount);
            Future<List<RequestDetail>> details =
                    this.intervalService.submit(new RequestExecutorRunnable(connectionCount, i + 1));
            requestDetailList.addAll(details.get());
            this.report("Interval %d is complete", i + 1);
        }

        return requestDetailList;

    }

    private class RequestExecutorRunnable implements Callable<List<RequestDetail>> {

        private int connectionCount, interval;

        RequestExecutorRunnable(int connectionCount, int interval) {
            this.connectionCount = connectionCount;
            this.interval = interval;
        }

        List<RequestDetail> getResults(Queue<Future<RequestDetail>> requestQueue) throws IOException {
            return requestQueue.parallelStream()
                    .map(item -> {
                        try {
                            return item.get();
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

            for (int i = 0; i < this.connectionCount; i++) {

                int selectedIndex = RandomSelector.nextInt(endpoints.size());
                Endpoint selectedEndpoint = endpoints.get(selectedIndex);
                String url = String.format("http://%s/%s?%s", host, selectedEndpoint.getEndpoint(),
                        selectedEndpoint.buildRandomData());
                requestQueue.add(requestExecutorService.submit(new RequestRunnable(url, interval)));

            }

            List<RequestDetail> results = getResults(requestQueue);

            return results;
        }
    }
}
