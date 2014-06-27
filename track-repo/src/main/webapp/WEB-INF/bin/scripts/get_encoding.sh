#!/bin/bash

TYPE=`ffmpeg -i "$1"  2>&1 | grep Stream | sed "s/^.*Audio: \([^,]*\).*$/\1/"`
echo $TYPE
