# HighLoad

## About

HighLoad is a fast and strong load testing tool built for TopCoder.
It requires JDK8. Please read the following to understand more of how
it works and what it can do for your service.

## Types of Tests

### Constant Load:
      Run as many requests as we can in a specified time interval
      with the number of threads we have supplied.
      (E.g. Run a specified number of threads that will continuously
      connect to specified endpoints of our host using randomized
       or fixed parameter values)

 - Options
    - threads
        - Number of threads to run in total
    - interval (in seconds)
        - The specified number of seconds to run the test for

### Fixed Load:
      Run a fixed number of connections at a specified interval
      a certain amount of times. (E.g., 1000 connections per 5 seconds
      for 5 runs, totalling 5000 connections in 25 seconds)

 - Options
    - interval
        -  The number of times this test will run
    - threads
        - The number of threads (connections) to run per interval

### Bottleneck:
      Start with one thread/connection and actively increase this over a specified interval
      until we reach our threads specified

 - Options
    - threads
        - The maximum number of connections we will reach on the last interval

    - interval
        - The number of runs we will continue to add connections until
        we reach the max connections. Number of connections per run = (run) * (max_connectins/number of runs)


By default only small statistics are displayed when running the tests, including:
  - Each url and respective average response time
  - Cumulative response time
  - Average response time over time

## Configuration

HighLoad runs off of a HOCON based configuration. The application will search for a variable "DEFAULT_TEST", which will be the configuration of the test in HOCON format.

### Test Configuration

The following keys are read from the test configuration, and must be present:

 - host
      - The host to be testing on (e.g. "https://google.com")
 - request_config
      - HOCON based config for the HttpURLConneciton setup (see request config below)
 - test_type
      - String representing current test type, must be: constant, bottleneck, or fixed
 - test_config
      - The thread and interval configuration for the test (see test setup configuration)
 - test_endpoints
      - An array of endpoints that must be tested (see endpoint setup below)
      
### Endpoint configuration

Highload looks for a key called "endpoints" that contains information regarding the specifics of an
endpoint that will be tested. Any endpoints in your test configuration must be present.

Basic format:
```
{
   endpoint: [ <- List of possible query parameter combinations
      { query_parameter = acceptable query parameter values,... } <- represents one possible combination of query values
   ]
}
```

### Test Setup Configuration

Within your test configuration, you must provide a test setup configuration. This must contain two keys:

- interval
      - The number of times to run the test
      
- threads
      - the number of threads to supply the test. This is test independent. For a fixed test, this is the number of threads to run per interval. For a bottleneck test, this is the targetted thread count for the last interval. For a constant test, this is the number of worker threads performing requests.
      

### Example configuration

This is a sample configuration supplied for the TopCoder challenge for which this was performed.

```
# list of all endpoints that may be used in the tests
endpoints = {
  graph: [
    { c = round_overview
      rd.key = round_id
      pm.key = problem_id }
  ],
  stat: [
      { c = round_overview
        rd.key = round_id     },

      { c = round_stats
       rd.key = round_id      },

      { c = problem_statement
        pm.key = problem_id
        rd.key = round_id     },

      { c = problem_solution
        cr.key = coder_id
        rd.key = round_id
        pm.key = problem_id   }
  ]
}

# config for fixed tests
fixed_config = {
  interval = 10      # default is seconds
  threads = 100 # simulate 100 connections per interval
}

# config for constant tests
constant_config = {
  interval = 10     # run this test for 60 seconds
  threads = 100     # have 100 worker threads run as many requests possible
}

# config for bottleneck tests
bottleneck_config = {
  threads = 500 # we want to reach 1500 connections
  interval = 10         # increase number of connections over 100 seocnds
}

# config for setting properties on our HttpURLConnection
request_config = {
  return_response = false
  properties = {
    "Cookie": "specify topcoder cookie here for authorization"
  }
}

# configuration for testing the dev environment
TEST_DEV = {
  host = "https://community.topcoder-dev.com"
  data_file = "data.csv"
  request_config = ${request_config}
  test_type = fixed
  test_config = ${fixed_config}
  test_endpoints = [graph, stat]
}

# configuration for testing the prod environment
TEST_PROD = {
  host = "https://community.topcoder.com"
  data_file = "data.csv"
  request_config = ${request_config}
  test_type = constant
  test_config = ${fixed_config}
  test_endpoints = [graph, stat]
}

# TEST is required and denotes which test we will run
DEFAULT_TEST = ${TEST_DEV}
```


### Running
To run this tool, you must first package it with maven. By default, all dependencies are placed in the jar file as well.

Following, you can run the tool with the following command

```
java -jar loadtester-1.0.jar -c configuration.conf [options]
```

Here is a list of possible commands:

```
 -c,--config <arg>        location of the configuration file to run the
                          test with
 -cookie <arg>            cookie to send with request
 -n,--num_threads <arg>   Override the number of threads to use that is
                          specified in the configuration file
 -o,--output_file <arg>   save the results to a specified file
 -r,--retrieve_response   save the response with the request, rather than
                          just connecting to the host
 -t,--test <arg>          Run the specified test name in the configuration
                          file, overriding the default test
 -v,--verbose             display verbose results in the output stream
```
