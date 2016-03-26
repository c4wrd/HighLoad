package com.c4wrd.loadtester.request;

import java.util.HashMap;
import java.util.Map;

public class QueryRow {

    private Map<String, String> valueMap;

    public QueryRow(String[] headers, String[] csvRow) {
        this.valueMap = new HashMap<>();

        int index = 0;
        for ( String val : headers ) {
            valueMap.put(val, csvRow[index]);
            index++;
        }
    }

    public Map<String, String> getValueMap() {
        return this.valueMap;
    }
}
