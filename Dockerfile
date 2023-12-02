# Build client
FROM node:12-alpine as build-client
WORKDIR /home/node
COPY ./erin-bpms/erin-bpms-web-client .

RUN npm install

#FROM gradle:5.2.1-jdk11 as build-server
#
#COPY . .
#
## Build server
#RUN gradle clean build --refresh-dependencies
