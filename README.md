# TourGuide

TourGuide is a mobile and PC application that will change the way you travel!

## Documentation

TODO

## Getting started

TODO

## Deployment

TODO

## Enhanced performance (730x faster!)

Tracking performance was improved by parallelizing tasks and replicating
overloaded services (to remove the bottlenecks).

Here is a benchmark for 100 000 users (AMD Ryzen 9 3900XT - 24 x 4.0GHz • 64GB
RAM):

- Before **(2 hours, 5 minutes and 48 seconds)**:
  ```
  Begin Tracker. Tracking 100000 users.
  [...]
  Tracker Time Elapsed: 7548.387s.
  ```

- After **(10 seconds)**:
  ```
  Begin Tracker. Tracking 100000 users.
  Tracking progression:   7984/100000 Δ  7984 | 1.000s
  Tracking progression:  17514/100000 Δ  9530 | 2.000s
  Tracking progression:  27282/100000 Δ  9768 | 3.000s
  Tracking progression:  37610/100000 Δ 10328 | 4.000s
  Tracking progression:  47746/100000 Δ 10136 | 5.000s
  Tracking progression:  57736/100000 Δ  9990 | 6.000s
  Tracking progression:  67714/100000 Δ  9978 | 7.000s
  Tracking progression:  77620/100000 Δ  9906 | 8.000s
  Tracking progression:  87556/100000 Δ  9936 | 9.000s
  Tracking progression:  97174/100000 Δ  9618 | 10.000s
  Tracking progression: 100000/100000 Δ  2826 | 10.327s
  Tracker Time Elapsed: 10.327s.
  ```

## Notes

This is a school project (for OpenClassrooms).

The goal is to resolve performance issues, split a monolithic application into
multiple services and deploy them with Docker.
