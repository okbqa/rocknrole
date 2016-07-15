#!/bin/bash
export MAVEN_OPTS="-Xmx2400M" 
mvn clean compile exec:java 
