docker build -t openjdk-8-111:MemEat -f DOCKERFILE_openjdk-8u111 .
docker run -m 100m openjdk-8-111:MemEat
