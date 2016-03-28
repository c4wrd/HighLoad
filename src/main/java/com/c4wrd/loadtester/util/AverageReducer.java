package com.c4wrd.loadtester.util;

import com.c4wrd.loadtester.request.RequestDetail;

import java.util.*;
import java.util.stream.Collectors;

public class AverageReducer {

    private static final String AVERAGE_DISTINCT = "AVG_DISTINCT";
    private static final String TOTAL_AVERAGE = "TOTAL_AVERAGE";
    private static final String LONGEST_TOTAL = "LONGEST_TOTAL";

    /**
     * The requests to work with in the data.
     */
    private List<RequestDetail> requests;

    /**
     * A cache to store our results if used more than once,
     * where results are tagged with a tag that can later be
     * looked up.
     */
    private Map<String, Object> distinctCache;

    /**
     * Stores instances of our reducers in a cache so they can
     * be shared between printers and the data saving
     * class, without needing to recompute any details
     */
    private static Map<List<RequestDetail>, AverageReducer> reducerCache;

    public static AverageReducer Create(List<RequestDetail> requests) {
        if ( reducerCache == null ) {
            reducerCache = new HashMap<>();
        }

        if ( reducerCache.containsKey(requests) ) {
            return reducerCache.get(requests);
        }

        AverageReducer reducer = new AverageReducer(requests);
        reducerCache.put(requests, reducer);

        return reducer;
    }

    private AverageReducer(List<RequestDetail> requests) {
        this.distinctCache = new HashMap<>();
        this.requests = requests;
    }

    /**
     * Calculates the average response time per URL and returns an
     * array of object arrays where the first element is the endpoint
     * and the second element is the average time as a double.
     */
    public List<Object[]> AverageDistinct() {

        if ( this.distinctCache.containsKey(AVERAGE_DISTINCT) ) {
            return (List<Object[]>) this.distinctCache.get("AVG_DISTINCT");
        }

        LinkedList<Object[]> list = requests.stream()
                .collect(Collectors.groupingBy(RequestDetail::getEndpoint))
                .values()
                .stream()
                .map((endpointList) -> {

                    String endpoint = endpointList.get(0).getEndpoint();

                    return new Object[] {
                            endpoint.substring(endpoint.lastIndexOf("/"), endpoint.length()),
                            endpointList.stream()
                                    .mapToInt(RequestDetail::getServerResponseTime)
                                    .average()
                                    .getAsDouble()
                    };
                }).collect(Collectors.toCollection(LinkedList::new));

        this.distinctCache.put(AVERAGE_DISTINCT, list);
        return list;
    }

    /**
     * Returns a double representing the total overall time taken
     * per request in the test.
     */
    public Double TotalAverage() {

        if ( this.distinctCache.containsKey(TOTAL_AVERAGE) ) {
            return (Double)this.distinctCache.get(TOTAL_AVERAGE);
        }

        Double average = requests.parallelStream()
                .mapToInt(RequestDetail::getServerResponseTime)
                .average()
                .getAsDouble();

        this.distinctCache.put(TOTAL_AVERAGE, average);
        return average;

    }

    /**
     * Returns an object array containing the longest URL to query
     * on average, in the format { URL, AVERAGE TIME }
     * @return
     */
    public Object[] LongestOverall() {
        if ( this.distinctCache.containsKey(LONGEST_TOTAL) ) {
            return (Object[]) this.distinctCache.get(LONGEST_TOTAL);
        }

        Object[] result = AverageDistinct()
                .stream()
                .max(Comparator.comparingDouble(object -> (Double)object[1]))
                .get();

        this.distinctCache.put(LONGEST_TOTAL, result);
        return result;
    }

}
