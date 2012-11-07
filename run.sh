#!/bin/bash

java -cp ./*:./lib/* queue.ProducerMain localhost 10
sleep 5
java -cp ./*:./lib/* queue.ConsumerMain localhost
