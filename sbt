#!/bin/bash

NAME=`basename $PWD`
echo -n -e "\033]0;${NAME}\007"

java $SBT_OPTS -Dfile.encoding=UTF-8 -Xss16M -Xmx1024M -XX:MaxPermSize=128M -XX:NewSize=128M -XX:NewRatio=3 -jar `dirname $0`/etc/sbt-launch-0.7.2-SNAPSHOT.jar "$@"

echo -n -e "\033]0;\007"
