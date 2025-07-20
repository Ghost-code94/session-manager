#!/bin/sh
# start Redis in the background
redis-server --save "" --appendonly no &
# now launch your JVM app
exec java -jar /app/app.jar
