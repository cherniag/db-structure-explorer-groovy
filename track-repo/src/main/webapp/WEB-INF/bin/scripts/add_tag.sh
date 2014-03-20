#!/bin/bash

${1}/neroAacTag "$2" \
	"-meta:title=$3" \
	"-meta:artist=$4" \
	"-meta:album=$5" \
	"-meta:genre=$6" \
	"-meta:year=$7" \
	"-meta:track=$8" \
	"-meta:copyright=$9" \
	"-meta:isrc=${10}" \
	"-add-cover:front:${11}"
