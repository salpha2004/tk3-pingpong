#!/bin/bash
javac -cp ./dyn4j-3.2.1.jar: ./mine/Pong.java ./mine/Graphics2DRenderer.java
java  -cp .:./dyn4j-3.2.1.jar mine.Pong
