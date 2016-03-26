package com.c4wrd.topcoder;

import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.QueryCombination;
import com.c4wrd.loadtester.request.QueryParam;
import com.c4wrd.loadtester.testservice.ConstantRequestTest;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.testservice.FixedRequestTest;
import com.c4wrd.loadtester.testservice.RequestTest;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoadTester {

    /**
     * Sample implementation of the load tester for TopCoder.
     *
     * Configuration files are stored in resources
     *  'application.conf' contains the thread count, host url, and url:query map
     *  'query.conf' contains the list of acceptable values for a specified query value
     */
    /*public static void main(String[] args) throws Exception {


        Terminal term = new UnixTerminal(System.in, System.out, Charset.defaultCharset());
        Screen screen = TerminalFacade.createScreen(term);
        screen.startScreen();

        screenWriter writer = new ScreenWriter(screen);
        writer.setForegroundColor(Terminal.Color.BLUE);
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.drawString(10, 1, "Hello World");
        writer.drawString(0, 1, "X", ScreenCharacterStyle.Blinking);
        screen.refresh();

        Thread.sleep(2500);

        screen.stopScreen();

        System.exit(0);

        Endpoint endp = new Endpoint("endpoint");
        QueryParam param = new QueryParam("param1");
        param.addValue("1");
        param.addValue("2");
        param.addValue("3");

        QueryParam param2 = new QueryParam("param2");
        param2.addValue("4");
        param2.addValue("5");
        param2.addValue("6");

        QueryCombination combo = new QueryCombination();
        combo.addQueryParameter(param);
        combo.addQueryParameter(param2);

        endp.addQueryCombination(combo);

        FixedRequestTest requestExecutor = new FixedRequestTest(100, 10);
        requestExecutor.setEndpoints(Arrays.asList(endp));
        requestExecutor.setUrl("c4wd.com");
        List<RequestDetail> responses = requestExecutor.run();
        responses.stream()
                .forEach(request -> System.out.println(
                        String.format("%d: %d", request.getRequestTime(), (int)request.getPayload())));

        requestExecutor.shutdown();


        XYSeriesCollection dataset = new XYSeriesCollection();


        final double[] curVal = {0.0};
        XYSeries series = new XYSeries("Requests over time");

        responses.stream()
                .forEach(item -> {
                    curVal[0] = curVal[0] + 1.0;
                    series.add(curVal[0], item.getServerResponseTime());
                });

        dataset.addSeries(series);

        String chartTitle = "Request/Response Chart";
        String xAxisLabel = "Time";
        String yAxisLabel = "Response Time";

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        ChartPanel cp = new ChartPanel(chart);
        cp.getChart().getXYPlot().getRangeAxis().setUpperBound(600);

        JFrame mainFrame = new JFrame("Graph");
        mainFrame.setSize(500, 400);
        mainFrame.add(new ChartPanel(chart));
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }*/

    public static void main(String[] args) {
        Config cfg = ConfigFactory.load("application.conf");
        cfg.getString("");
    }

}
