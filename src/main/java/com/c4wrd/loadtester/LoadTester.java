package com.c4wrd.loadtester;

import com.c4wrd.loadtester.configuration.EndpointConfiguration;
import com.c4wrd.loadtester.configuration.HighLoadConfig;
import com.c4wrd.loadtester.exceptions.HighLoadException;
import com.c4wrd.loadtester.util.LogLevel;
import com.c4wrd.loadtester.util.Output;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class LoadTester {

    private LoadTester(CommandLine line) {
        Config mainConfiguration = this.loadConfig(line.getOptionValue("c"));
        HighLoadConfig loadTestingConfig = null;
        EndpointConfiguration endpointConfiguration = null;

        try {

            if (line.hasOption("v")) {
                Output.setCurrentLogLevel(LogLevel.VERBOSE);
            }

            if (line.hasOption("t")) {
                loadTestingConfig = new HighLoadConfig(mainConfiguration.getConfig(line.getOptionValue("t")));
            } else {
                loadTestingConfig = new HighLoadConfig(mainConfiguration.getConfig("DEFAULT_TEST"));
            }

            endpointConfiguration = new EndpointConfiguration(loadTestingConfig, mainConfiguration.getConfig("endpoints"));
            setOptions(loadTestingConfig, endpointConfiguration, line);

            HighLoad highLoad = new HighLoad(loadTestingConfig, endpointConfiguration);
            highLoad.run();
            highLoad.displayResults();
            highLoad.saveResults(line.hasOption("o") ?
                    line.getOptionValue("o") : String.format("testrun_%d.xlsx", UUID.randomUUID().hashCode()));


        } catch (HighLoadException | IOException | ExecutionException | InterruptedException e) {
            System.err.println("The HighLoad execution service failed to run, please review the following message...");
            System.err.println("-> " + e.getMessage());
            System.exit(-1);
        }

    }

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Output.setOutputStream(System.out);
        try {
            LoadTester tester = new LoadTester(parser.parse(createOptions(), args));
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    private static Options createOptions() {
        Options options = new Options();

        Option testConfig = new Option(
                "c",
                "config",
                true,
                "location of the configuration file to run the test with"
        );
        testConfig.setRequired(true);
        options.addOption(testConfig);

        options.addOption(new Option(
                "t",
                "test",
                true,
                "Run the specified test name in the configuration file," +
                        " overriding the default test"));

        options.addOption(new Option(
                "n",
                "num_threads",
                true,
                "Override the number of threads to use that is specified in the configuration file"
        ));

        options.addOption(new Option(
                "cookie",
                true,
                "cookie to send with request"
        ));

        options.addOption(new Option(
                "r",
                "retrieve_response",
                false,
                "save the response with the request, rather than just connecting to the host"
        ));

        options.addOption(new Option(
                "o",
                "output_file",
                true,
                "save the results to a specified file"
        ));

        options.addOption(new Option(
                "chart",
                false,
                "display the results of this test in a Swing chart"
        ));

        options.addOption(
                "v",
                "verbose",
                false,
                "display verbose results in the output stream");

        return options;
    }

    private void setOptions(HighLoadConfig config, EndpointConfiguration endpointConfiguration, CommandLine line) {

        if (line.hasOption("n")) {
            Output.println(LogLevel.VERBOSE, "Overriding default thread count to %s", line.getOptionValue("n"));
            config.getTestConfig().setNumThreads(Integer.parseInt(line.getOptionValue("n")));
        }

        if (line.hasOption("cookie")) {
            Output.println(LogLevel.VERBOSE, "Overriding default cookie value to %s...", line.getOptionValue("cookie").substring(0, 10));
            config.getRequestOptions().addProperty("Cookie", line.getOptionValue("c"));
        }

        if (line.hasOption("r")) {
            Output.println(LogLevel.VERBOSE, "Overriding default read_response value to %s", line.getOptionValue("r"));
            config.getRequestOptions().setReadInputStream(Boolean.valueOf(line.getOptionValue("r")));
        }

    }

    private Config loadConfig(String location) {
        Config configuration = ConfigFactory.parseFile(new File(location));
        return configuration.resolve();
    }

}
