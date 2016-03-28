package com.c4wrd.loadtester.printing;

import com.c4wrd.loadtester.configuration.TestType;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.util.AverageReducer;
import com.c4wrd.loadtester.util.LogLevel;
import com.c4wrd.loadtester.util.Output;
import dnl.utils.text.table.TextTable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BasicPrinter {

    protected int threads;
    protected int interval;
    List<RequestDetail> requests;
    private TestType type;
    private AverageReducer reducer;

    public BasicPrinter(List<RequestDetail> requestDetailList, int interval, int threads, TestType testType) {
        this.requests = requestDetailList;
        this.threads = threads;
        this.interval = interval;
        this.reducer = AverageReducer.Create(requests);
        this.type = testType;
    }

    public void print() {
        if ( this.type != TestType.CONSTANT_LOAD )
            this.printAverageOverTime();
        this.printUrlSummary();
        this.printTestSummary();
    }

    void printTestSummary() {

        List<Object[]> averagePerUrl = reducer.AverageDistinct();
        String averageOverall = String.format("%.3f", reducer.TotalAverage());
        Object[] longestOverall = reducer.LongestOverall();

        Output.println(
                LogLevel.DEFAULT,
                String.format(
                "Summary\n" +
                "-------\n" +
                "Average overall: %25s ms\n" +
                "Longest on avg:  %25s ms",
                        averageOverall,
                        String.format("%s: %.3f", longestOverall[0], (Double)longestOverall[1])
        ));

    }

    void printUrlSummary() {
        String[] cols = new String[] { "Endpoint", "Avg. Response Time" };
        List<Object[]> averagePerUrl = reducer.AverageDistinct();

        Object[][] tableItems = new Object[averagePerUrl.size()][2];

        final int[] index = {0};
        averagePerUrl.forEach(item -> {
            tableItems[index[0]++] = item;
        });

        TextTable table = new TextTable(cols, tableItems);
        table.printTable();

        Output.println();
    }

    private void printAverageOverTime() {
        ConcurrentHashMap<Integer, LinkedList<RequestDetail>> detailMap = new ConcurrentHashMap<>();

        requests.parallelStream()
                .forEach((item) -> {
                    detailMap.putIfAbsent((int) item.getPayload(), new LinkedList<>());
                    detailMap.get((int)item.getPayload()).add(item);
                });

        String[] columns = { "Interval", "Avg. Response Time" };

        Object[][] data = new Object[detailMap.entrySet().size()][2];

        for ( Map.Entry<Integer, LinkedList<RequestDetail>> value : detailMap.entrySet() ) {
            Double averageTime = value.getValue()
                    .parallelStream()
                    .mapToInt(RequestDetail::getServerResponseTime)
                    .average().getAsDouble();

            data[(int)value.getKey()-1] = new Object[]{value.getKey(), averageTime};
        }

        TextTable table = new TextTable(columns, data);

        table.printTable();
        Output.println();
    }

}
