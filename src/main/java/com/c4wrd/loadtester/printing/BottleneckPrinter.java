package com.c4wrd.loadtester.printing;

import com.c4wrd.loadtester.configuration.TestType;
import com.c4wrd.loadtester.request.RequestDetail;
import dnl.utils.text.table.TextTable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BottleneckPrinter extends BasicPrinter {

    public BottleneckPrinter(List<RequestDetail> requestDetailList, int interval, int threads) {
        super(requestDetailList, interval, threads, TestType.BOTTLENECK);
    }

    @Override
    public void print() {

        ConcurrentHashMap<Integer, LinkedList<RequestDetail>> detailMap = new ConcurrentHashMap<>();

        requests.stream()
                .forEach((item) -> {
                    detailMap.putIfAbsent((int) item.getPayload(), new LinkedList<>());
                    detailMap.get((int)item.getPayload()).add(item);
                });

        String[] columns = { "Interval", "Connection count", "Avg. Response Time" };

        Object[][] data = new Object[interval][3];

        for ( Map.Entry<Integer, LinkedList<RequestDetail>> value : detailMap.entrySet() ) {
            Double averageTime = value.getValue()
                    .parallelStream()
                    .mapToInt(RequestDetail::getServerResponseTime)
                    .average().getAsDouble();

            data[(int)value.getKey()-1] = new Object[]{
                    value.getKey(),
                    (threads/interval) * (int)(value.getKey()), // number of connections at this interval
                    averageTime,
            };
        }

        TextTable table = new TextTable(columns, data);
        table.printTable();

        super.printUrlSummary();
        super.printTestSummary();

    }
}
