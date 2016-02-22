#!/bin/sh
#
# Timestamp UNIX : unixTimestampOf(now())

DATE=`date +%Y-%m-%d`
NOW=`date +%s`
A_FULL_DAY=`expr 60 \* 60 \* 24`

echo $A_FULL_DAY
echo `expr $TIMESTAMP \/ 7`

# TIMESTAMP e um rand entre corrente e um dia atrás
