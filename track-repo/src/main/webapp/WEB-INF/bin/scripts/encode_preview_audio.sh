#!/bin/bash

dmg --input-file "$1" \
	--mp4-chunk-span 900 \
	--overwrite \
	--audio-only \
	--repair-all \
	--audio-encoder aac \
	--audio-cbr-rate 96 \
	--aac-mode he-aacv1 \
	--output-file "$2" \
	--input-speech false \
	--clipmode prolimit
