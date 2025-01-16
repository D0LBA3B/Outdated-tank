#!/bin/bash

Xvfb :99 -screen 0 1024x768x24 &
export DISPLAY=:99

java -jar /app/outdatedtank.jar
