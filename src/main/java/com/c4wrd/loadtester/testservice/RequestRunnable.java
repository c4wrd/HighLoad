package com.c4wrd.loadtester.testservice;

import com.c4wrd.loadtester.request.RequestDetail;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

class RequestRunnable implements Callable<RequestDetail> {

    private static RequestOptions requestOptions = RequestOptions.DEFAULT;
    /**
     * The URL to request
     */
    private String url;
    /**
     * A payload that is saved along with the request detail
     * that an be analyzed when generating a report (for instance
     * the thread ID it was executed on)
     */
    private Object payload;
    /**
     * Whether or not to save the thread ID as the payload of this request
     */
    private boolean saveThreadId = false;

    /**
     * Initialize a request runnable without a payload
     */
    public RequestRunnable(String url) {
        this.url = url;
    }

    /**
     * Initialize a request runnable with a payload
     */
    public RequestRunnable(String url, Object payload) {
        this.url = url;
        this.payload = payload;
    }

    /**
     * Saves the current thread Id as the payload of this request
     *
     * @param url:           The URL being requested
     * @param numberThreads: The number of threads (used to find zero-index based id)
     */
    public RequestRunnable(String url, boolean saveThreadId, int numberThreads) {
        this.url = url;
        this.payload = numberThreads;
        this.saveThreadId = true;
    }

    /**
     * Sets the default request options to the opitons specified by the caller.
     *
     * @param options
     */
    public static void setRequestOptions(@NotNull RequestOptions options) {
        requestOptions = options;
    }

    /**
     * Performs the request, saves the response time, and returns details regarding the
     * request.
     */
    @Override
    public RequestDetail call() throws Exception {
        Long startTime = System.currentTimeMillis();
        URL url = new java.net.URL(this.url);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        this.setupRequestOptions(connection);
        connection.connect();

        int response_time = (int) (System.currentTimeMillis() - startTime);

        RequestDetail detail = new RequestDetail(this.url);

        if (requestOptions.readInputStream) {
            detail.setResponse(this.getResponse(connection));
        }

        if (this.saveThreadId) {
            this.payload = Thread.currentThread().getId() % (int) this.payload;
        }

        detail.setPayload(this.payload);

        detail.setServerResponseTime(response_time);

        return detail;
    }

    private void setupRequestOptions(HttpURLConnection connection) {
        if (requestOptions.readInputStream)
            connection.setDoInput(true);

        for (Map.Entry<String, String> entry : requestOptions.urlProperties.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Helper to get the response from a specified HttpURLConnection
     *
     * @param connection: Connection waiting with an input stream
     */
    private String getResponse(HttpURLConnection connection) throws IOException {
        BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();

        String line;
        while ((line = stream.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }

}