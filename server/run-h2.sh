#!/usr/bin/env bash
./mvnw exec:java -D"exec.mainClass"="org.h2.tools.Server" -D"exec.args"="-web -browser"
