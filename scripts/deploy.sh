#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/code/Backend
cd $REPOSITORY

APP_NAME=moa
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME
LOG_PATH=$REPOSITORY/src/main/resources/logback-spring.xml

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할 애플리케이션이 없습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> Deploy - $JAR_PATH "
sudo nohup java -jar $JAR_PATH --spring.profiles.active=prod > /dev/null 2> /dev/null < /dev/null &