package com.c4wrd.loadtester.configuration;

import com.c4wrd.loadtester.request.QueryRow;
import com.c4wrd.loadtester.util.ReservoirCollector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Provides us with a list of random elements from our dataset to work with.
 */
public class QueryParameterSupplier {

    /**
     * Reservoir selects desiredNumResults (or less) random rows of data from our csv file.
     * Note: Less than the desired number of results can be selected.
     */
    public static List<QueryRow> get(Path file, int desiredNumResults) throws IOException, URISyntaxException {

        String[] header = StreamSupplier(file).findFirst().get().split(",");
        Stream<String> rowStream = StreamSupplier(file).skip(1);

        List<String> randomRows = rowStream.skip(1).collect(new ReservoirCollector<String>(desiredNumResults));
        List<QueryRow> rows = new ArrayList<>(desiredNumResults);

        randomRows.forEach(item -> {
           rows.add(new QueryRow(header, item.split(",")));
        });

        return rows;

    }

    private static Stream<String> StreamSupplier(Path file) throws IOException {
        return Files.lines(file);
    }

}
