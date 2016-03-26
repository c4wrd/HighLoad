package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.util.RandomSelector;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BottleneckRequestTest extends RequestTest<ExecutorService> {

    public BottleneckRequestTest(int connectionCount, int interval) throws Exception {
        super(connectionCount, interval);
        this.intervalService = Executors.newFixedThreadPool(1);
        this.requestExecutorService = Executors.newFixedThreadPool(this.connectionCount);

    }

    public List<RequestDetail> run() throws ExecutionException, InterruptedException {

        Future<List<RequestDetail>> results = this.intervalService.submit(
                new RequestExecutorRunnable((int) (connectionCount / interval), interval));

        return results.get();

    }

    private class RequestExecutorRunnable implements Callable<List<RequestDetail>> {

        private int currentConnectionCount, step, interval;

        public RequestExecutorRunnable(int step, int interval) {
            this.step = step;
            this.interval = interval;
        }

        public List<RequestDetail> getResults(Queue<Future<RequestDetail>> requestQueue) {
            return requestQueue.parallelStream()
                    .map(item -> {
                        try {
                            return item.get();
                        } catch (InterruptedException | ExecutionException e) { // the request failed, ignore
                            return null;
                        }
                    })
                    .filter(item -> item != null)
                    .collect(Collectors.toList());
        }

        @Override
        public List<RequestDetail> call() throws Exception {
            Queue<Future<RequestDetail>> requestQueue = new LinkedBlockingDeque<>();

            for (int i = 0; i < interval; i++) {
                currentConnectionCount += step;

                for (int j = 0; j < currentConnectionCount; j++) {

                    int selectedIndex = RandomSelector.nextInt(endpoints.size());
                    Endpoint selectedEndpoint = endpoints.get(selectedIndex);
                    String url = String.format("http://%s/%s?%s", host, selectedEndpoint.getEndpoint(),
                            selectedEndpoint.buildRandomData());

                    requestQueue.add(requestExecutorService.submit(new RequestRunnable(url, i)));

                }

                Thread.sleep(1000);
            }

            List<RequestDetail> results = getResults(requestQueue);

            return results;
        }
    }
}
