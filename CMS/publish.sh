#!/bin/bash

DST_FOLDER=/mnt/diskd/NowTech/store
NERO_DIR=/home/ingest/bin/Nero/linux
#NERO_DIR=~/Desktop/Nero/linux

FFMPEG=/usr/local/bin/ffmpeg
#FFMPEG=/usr/bin/ffmpeg


AUDIO_FILE=$1
PREVIEW_FILE=$2
FULL_AUDIO=$3
IMAGE=$4
META_TITLE=$5
META_AUTHOR=$6
META_ALBUM=$7
META_GENRE=$8
META_TRACK=$9
META_DATE=${10}
META_COPY=${11}
ISRC=${12}
PUBLISH_DIR=${13}
CP=${14}
NERO_DIR=${15}
WORK_DIR=${16}
BIT_RATE=${17}
PREVIEW_ONLY=${18}

echo "********** PARAMS"
echo $AUDIO_FILE
echo $PREVIEW_FILE
echo $FULL_AUDIO
echo $IMAGE
echo $META_TITLE
echo $META_AUTHOR
echo $META_ALBUM
echo $META_GENRE
echo $META_TRACK
echo $META_DATE
echo $META_COPY
echo $ISRC
echo $PUBLISH_DIR
echo $CP
echo $NERO_DIR
echo $WORK_DIR
echo $BIT_RATE
echo $PREVIEW_ONLY

echo "********** PARAMS"

function getEncoding {
TYPE=`${FFMPEG} -i "$1"  2>&1 | grep Stream | sed "s/^.*Audio: \([^,]*\).*$/\1/"`
case $TYPE in 
	"mp3") 

		echo "MP3";;
        "aac") 

		echo "AAC";;
	"pcm_s16le")

		echo "WAV";;
	*)
		echo "UNKNOWN";;
esac
}

mkdir -p ${WORK_DIR}
cd ${WORK_DIR}

mkdir -p files/image
mkdir -p files/header
mkdir -p files/audio
mkdir -p files/purchased
echo ISRC IS ${ISRC}
echo "***** Generating thumbnails *****"
	IMAGE_GENERIC="${ISRC}.jpg"	
	IMAGE_SMALL="${ISRC}S.jpg"
	IMAGE_LARGE="${ISRC}L.jpg"

	convert "${IMAGE}" -resize 70x70 "./files/image/$IMAGE_SMALL"	|| { echo "command failed"; exit 1; } 
	convert "$IMAGE" -resize 200x200 "files/image/$IMAGE_LARGE"|| { echo "command failed"; exit 1; } 
	cp files/image/$IMAGE_LARGE "files/image/${ISRC}_22.jpg"|| { echo "command failed"; exit 1; } 

	convert "$IMAGE" -resize 90x90 "files/image/${ISRC}_21.jpg"|| { echo "command failed"; exit 1; } 
	cp "files/image/${ISRC}_21.jpg" "files/image/${ISRC}_11.jpg"|| { echo "command failed"; exit 1; } 
	convert "$IMAGE" -resize 240x240 "files/image/${ISRC}_3.jpg"|| { echo "command failed"; exit 1; } 
	convert "$IMAGE" -resize 60x60 "files/image/${ISRC}_6.jpg"|| { echo "command failed"; exit 1; } 
	
	if [ "$PREVIEW_ONLY" = "YES" ]; then
		composite -gravity center stamp200.png "files/image/$IMAGE_LARGE"  "files/image/P$IMAGE_LARGE" || { echo "command failed"; exit 1; } 
		mv "files/image/P$IMAGE_LARGE" "files/image/$IMAGE_LARGE" || { echo "command failed"; exit 1; } 
		cp "files/image/$IMAGE_LARGE" "files/image/${ISRC}_22.jpg"|| { echo "command failed"; exit 1; } 
	
		composite -gravity center stamp70.png "files/image/$IMAGE_SMALL"  "files/image/P$IMAGE_SMALL" || { echo "command failed"; exit 1; } 
		mv "files/image/P$IMAGE_SMALL" "files/image/$IMAGE_SMALL" || { echo "command failed"; exit 1; } 
	
		composite -gravity center stamp90.png "files/image/${ISRC}_21.jpg"  "files/image/P${ISRC}_21.jpg" || { echo "command failed"; exit 1; } 
		mv "files/image/P${ISRC}_21.jpg" "files/image/${ISRC}_21.jpg" || { echo "command failed"; exit 1; } 
		cp "files/image/${ISRC}_21.jpg" "files/image/${ISRC}_11.jpg" || { echo "command failed"; exit 1; } 
		
		composite -gravity center stamp240.png "files/image/${ISRC}_3.jpg"  "files/image/P${ISRC}_3.jpg" || { echo "command failed"; exit 1; } 
		mv "files/image/P${ISRC}_3.jpg" "files/image/${ISRC}_3.jpg" || { echo "command failed"; exit 1; } 

		composite -gravity center stamp60.png "files/image/${ISRC}_6.jpg"  "files/image/P${ISRC}_6.jpg" || { echo "command failed"; exit 1; } 
		mv "files/image/P${ISRC}_6.jpg" "files/image/${ISRC}_6.jpg" || { echo "command failed"; exit 1; } 
	fi 
	
	
echo "***** Generating Download Audio *****"
    echo getting encodding for "${FULL_AUDIO}"
	INPUT_ENCODING=`getEncoding "${FULL_AUDIO}"`
	echo $INPUT_ENCODING $FULL_AUDIO
	if [ "${INPUT_ENCODING}" = "UNKNOWN" ]; then
		echo "Trying to fix ${FULL_AUDIO}"
		lame --mp3input -q 0 -b 320 "${FULL_AUDIO}" ${ISRC}_fix.mp3
		FULL_AUDIO=${ISRC}_fix.mp3
		INPUT_ENCODING=`getEncoding "${FULL_AUDIO}"`
	fi
	echo $INPUT_ENCODING $FULL_AUDIO
	case $INPUT_ENCODING in
		"MP3") 
#	cp "${FULL_AUDIO}" "./files/purchased/${ISRC}.mp3" || { echo "command failed"; exit 1; } ;;
# Make sure we have an ID3 tag in the track (Warner tracks does not have it)
			${FFMPEG} -i "${FULL_AUDIO}" -y -acodec copy "./files/purchased/${ISRC}.mp3"|| { echo "command failed"; exit 1; } ;;
		"AAC") 
			faad -o - "${FULL_AUDIO}" | lame - -b 256 "./files/purchased/${ISRC}.mp3" || { echo "command failed"; exit 1; } ;;
		"WAV") 
			lame -b 256 "${FULL_AUDIO}" "./files/purchased/${ISRC}.mp3" || { echo "command failed"; exit 1; } ;;
	esac
# Add temporary UITS header to the MP3
	java -cp ${CP}/UITS.jar:${CP}/bcprov-jdk16-146.jar  mobi.nowtechnologies.UITS "./files/purchased/${ISRC}.mp3" "./${ISRC}.mp3" ${CP}/UITS/key.der  ${CP}/UITS/privateRSA2048.pem || { echo "command failed"; exit 1; } 
	cp "./${ISRC}.mp3" "./files/purchased/${ISRC}.mp3" || { echo "CP command failed"; exit 1; }
# Keep the final MP3 in work dir -> used to get the media hash
#	cp "./files/purchased/${ISRC}.mp3" . || { echo "command failed"; exit 1; }

echo "***** Generating Mobile Audio *****"
	case $INPUT_ENCODING in
		"MP3") 
			mpg123 -w "${ISRC}.wav" "$FULL_AUDIO"|| { echo "command failed"; exit 1; } 
			INPUT="${ISRC}.wav";;
		"AAC") 			
			INPUT="${FULL_AUDIO}";;
		"WAV") 
			INPUT="${FULL_AUDIO}";;
	esac

	dmg --input-file "${INPUT}" --overwrite --audio-only --repair-all --audio-encoder aac --audio-cbr-rate 48 --aac-mode he-aacv1 --output-file "${ISRC}_48.m4a" --input-speech false --clipmode prolimit|| { echo "command failed"; exit 1; } 
		
	java -cp ${CP}/UITS.jar:${CP}/bcprov-jdk16-146.jar  mobi.nowtechnologies.UITS ${ISRC}_48.m4a ${ISRC}_48.m4a.u ${CP}/UITS/key.der  ${CP}/UITS/privateRSA2048.pem || { echo "command failed"; exit 1; } 
	mv ${ISRC}_48.m4a.u ${ISRC}_48.m4a  || { echo "command failed"; exit 1; } 
	
	dmg --input-file "${INPUT}" --overwrite --audio-only --repair-all --audio-encoder aac --audio-cbr-rate 96 --aac-mode he-aacv1 --output-file "${ISRC}_96.m4a" --input-speech false --clipmode prolimit|| { echo "command failed"; exit 1; } 

	java -cp ${CP}/UITS.jar:${CP}/bcprov-jdk16-146.jar  mobi.nowtechnologies.UITS ${ISRC}_96.m4a ${ISRC}_96.m4a.u ${CP}/UITS/key.der  ${CP}/UITS/privateRSA2048.pem || { echo "command failed"; exit 1; } 
	mv ${ISRC}_96.m4a.u ${ISRC}_96.m4a  || { echo "command failed"; exit 1; } 

	${NERO_DIR}/neroAacTag "${ISRC}_48.m4a" "-meta:title=${META_TITLE}" "-meta:artist=${META_AUTHOR}" "-meta:album=${META_ALBUM}" "-meta:genre=${META_GENRE}" "-meta:year=${META_DATE}" "-meta:track=${META_TRACK}" "-meta:copyright=${META_COPY}" "-meta:isrc=${ISRC}" "-add-cover:front:${IMAGE}"|| { echo "command failed"; exit 1; } 
	${NERO_DIR}/neroAacTag "${ISRC}_96.m4a" "-meta:title=${META_TITLE}" "-meta:artist=${META_AUTHOR}" "-meta:album=${META_ALBUM}" "-meta:genre=${META_GENRE}" "-meta:year=${META_DATE}" "-meta:track=${META_TRACK}" "-meta:copyright=${META_COPY}" "-meta:isrc=${ISRC}" "-add-cover:front:${IMAGE}"|| { echo "command failed"; exit 1; } 

	java -cp ${CP}  Split "${ISRC}_48"||. { echo "command failed"; exit 1; } 
	java -cp ${CP}  Split "${ISRC}_96"|| { echo "command failed"; exit 1; } 
	mv "${ISRC}_48.hdr" files/header|| { echo "command failed"; exit 1; } 
	mv "${ISRC}_48.aud" files/audio|| { echo "command failed"; exit 1; } 
	mv "${ISRC}_96.hdr" files/header|| { echo "command failed"; exit 1; } 
	mv "${ISRC}_96.aud" files/audio|| { echo "command failed"; exit 1; } 

echo "***** Generating Mobile Preview Audio *****"
	if [ "$PREVIEW_FILE" = "" ]; then
		${FFMPEG} -i "${ISRC}_96.m4a" -y -t 30 -acodec copy "${ISRC}P.m4a"|| { echo "command failed"; exit 1; } 
	else
		PREVIEW_TMP=`basename ${PREVIEW_FILE}`
		cp "${PREVIEW_FILE}" "${ISRC}P.m4a"|| { echo "command failed"; exit 1; } 
	fi
	${NERO_DIR}/neroAacTag "${ISRC}P.m4a" "-meta:title=${META_TITLE}" "-meta:artist=${META_AUTHOR}" "-meta:album=${META_ALBUM}" "-meta:genre=${META_GENRE}" "-meta:year=${META_DATE}" "-meta:track=${META_TRACK}" "-meta:copyright=${META_COPY}" -meta:isrc=${ISRC} -add-cover:front:"${IMAGE}"|| { echo "command failed"; exit 1; } 
	java -cp ${CP}  Split "${ISRC}P"|| { echo "command failed"; exit 1; } 
	mv "${ISRC}P.hdr" files/header/|| { echo "command failed"; exit 1; } 
	mv "${ISRC}P.aud" files/audio/|| { echo "command failed"; exit 1; } 

# Cleaning
	#rm -f ${ISRC}P.m4a
	#rm -f ${ISRC}_48.m4a
	#rm -f ${ISRC}_96.m4a
	#rm -f ${ISRC}.mp3
	rm -f ${ISRC}.wav
	
# Publish
	curl -D - -H "X-Auth-Key: 2db8095b7bb8e8e17432d5286ff7e7e0" -H "X-Auth-User: chartsnowcloud" https://lon.auth.api.rackspacecloud.com/v1.0 > auth
	URL=`grep "X-Storage-Url:" auth | cut -f2- -d':' | sed "s/\r//g"`
	TOKEN=`grep "X-Storage-Token:" auth | cut -f2- -d':' | sed "s/\r//g"`

	for i in `ls files/audio`
	do
		curl -X PUT -T $i  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 25000" ${URL}/data/`basename $i`
	done
	mv files/image/* ${PUBLISH_DIR}/image|| { echo "command failed"; exit 1; } 
	mv files/header/* ${PUBLISH_DIR}/header|| { echo "command failed"; exit 1; } 
	mv files/audio/* ${PUBLISH_DIR}/audio|| { echo "command failed"; exit 1; } 
	mv files/purchased/* ${PUBLISH_DIR}/purchased|| { echo "command failed"; exit 1; } 
	
	

