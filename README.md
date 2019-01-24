# Java and Docker memory limit experiments

## Playing with Docker memory limits, Java versions and JVM flags

Inspired by [Java and Docker, the limitations](https://royvanrijn.com/blog/2018/05/java-and-docker-memory-limits/)

The memory eating test app is run with 100m memory assigned to a docker container. We want to see the application run until either the application is killed by the docker service (bad) or until we see OutOfMemory exceptions (good). Seeing exceptions means that the application is aware of the memory limit we are imposing on it. If the app is getting OOMkilled by Docker the app is not aware and will consume memory until Docker terminates it.

Steps are the same for all experiments:
1. compile the Java test app:
```
javac MemEat.java
```
2. build an image from the dockerfile for the experiment:
```
docker build -t <tag> -f <Dockerfile> .
```
3. run the container
```
docker run -m 100m <tag>
```

## Experiment 1 - Run the app in Java 8u111 with no JVM flag
Running the app we can see two things:
- Java thinks it still has a lot of memory left
- Docker is killing the process because it is consuming all available resources
This is bad. The JVM is not aware of what resources are available to it.

## Experiment 2 - Running the app in Java 8u111 with -Xmx100m flag
The difference here is that we are telling the JVM how much memory is available.
Java knows how much memory it is allowed to consume and will throw OutOfMemory exceptions accordingly.
This is much better, but raises another problem. We need to maintain the memory limits for the container and the JVM separately. Furthermore, the memory available to the container cannot be used to 100% by the JVM as there are other processes using memory as well.

## Experiment 3 - Running the app in Java 8u144
Some new flags were introduced to address this issue. Specifically, to make the JVM use the CGroup memory limit of the underlying linux OS, which in turn is set by Docker. So, we have to specify the memory limit only once.

Without these flags Docker will kill the app for us.

Update: -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap are now enabled by default for this base image (upgraded to 8u172) and so this now passes the test as well

## Experiment 4 -  Running the app in Java 8u144 with UseCGroupMemoryLimitForHeap flag
To use the new flag we also need to enable experimental options. Adding the following flags to the JVM makes it use the CGroup memory limit:
  -XX:+UnlockExperimentalVMOptions
  -XX:+UseCGroupMemoryLimitForHeap
Running the app with these two flags will cause it to throw OutOfMemory exceptions.
