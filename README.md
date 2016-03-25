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
    - connections
         - Number of connections to simulate per interval
    - interval (in seconds)
        -  The number of seconds that each run will last
    - runs
        - The number of times to run the test

### Bottleneck:
      Start with one thread/connection and actively increase this over a specified interval
      until we reach our threads specified

 - Options
    - max_connections
        - The maximum number of connections to reach in our specified interval

    - runs
        - The number of times we will continue to add connections until
        we reach the max connections. Number of connections per run = (run) * (max_connectins/number of runs)


By default only small statistics are displayed when running the tests, including:
  - Each url and respective average response time
  - Cumulative response time
  - Average response time over time