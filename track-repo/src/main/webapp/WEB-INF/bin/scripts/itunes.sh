#! /bin/bash

cd /tmp/
DATA=`echo $1 $2 | sed "s/ /+/g"`
wget -O itunes.data "http://ax.phobos.apple.com.edgesuite.net/WebObjects/MZStoreServices.woa/wa/wsSearch?term=${DATA}&country=gb"
URL=`cat itunes.data | grep "\"track\"" | grep "\"song\"" | grep trackViewUrl | sed "s/^.*trackViewUrl\":\"\([^\"]*\)\".*$/\1/" | head -1`
if [ "${URL}" != "" ]
then
	echo "$URL"
	exit 0
fi
exit 1
