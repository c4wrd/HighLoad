package com.c4wrd.loadtester.request;

import com.c4wrd.loadtester.util.ReservoirCollector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Provides us with a list of random elements from our dataset to work with.
 */
public class QueryParameterSupplier {

    private static Random random = new Random();
    private static List<QueryDataRow> cachedData;

    /**
     * Reservoir selects desiredNumResults (or less) random rows of data from our csv file.
     * In this instance, we will only read the number of threads^2.
     * Note: Less than the desired number of results can be selected.
     */
    public static List<QueryDataRow> cacheRows(Path file, int rowsToQuery) throws IOException {

        String[] header = StreamSupplier(file).findFirst().get().split(",");
        Stream<String> rowStream = StreamSupplier(file).skip(1);

        List<String> randomRows = rowStream.skip(1).collect(new ReservoirCollector<String>(rowsToQuery));
        List<QueryDataRow> rows = new ArrayList<>(rowsToQuery);

        randomRows.forEach(item -> {
            rows.add(new QueryDataRow(header, item.split(",")));
        });

        cachedData = rows;
        return rows;
    }

    private static Stream<String> StreamSupplier(Path file) throws IOException {
        return Files.lines(file);
    }

    public static QueryDataRow getRandomRow() {
        return cachedData.get(Math.abs(random.nextInt() % cachedData.size()));
    }

}
