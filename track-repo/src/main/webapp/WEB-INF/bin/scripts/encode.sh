#!/bin/bash



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
TRACK_ID=${17}
PREVIEW_ONLY=${18}
PRIVATE_KEY=${19}
BIT_RATE=${20}

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
echo $TRACK_ID
echo $PREVIEW_ONLY
echo $PRIVATE_KEY
echo $BIT_RATE

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
mkdir -p files/encoded
mkdir -p files/purchased
mkdir -p files/preview
echo Processing ISRC ${ISRC}
echo "***** Generating thumbnails *****"
	IMAGE_GENERIC="${ISRC}.jpg"	
	IMAGE_SMALL="${ISRC}S.jpg"
	IMAGE_LARGE="${ISRC}L.jpg"
	IMAGE_COVER="files/image/${ISRC}_cover.png"

	convert "${IMAGE}" -resize 70x70 "./files/image/$IMAGE_SMALL"	|| { echo "command failed"; exit 1; } 
	convert "$IMAGE" -resize 200x200 "files/image/$IMAGE_LARGE"|| { echo "command failed"; exit 1; } 
	cp files/image/$IMAGE_LARGE "files/image/${ISRC}_22.jpg"|| { echo "command failed"; exit 1; } 

	convert "$IMAGE" -resize 90x90 "files/image/${ISRC}_21.jpg"|| { echo "command failed"; exit 1; } 
	cp "files/image/${ISRC}_21.jpg" "files/image/${ISRC}_11.jpg"|| { echo "command failed"; exit 1; } 
	convert "$IMAGE" -resize 240x240 "files/image/${ISRC}_3.jpg"|| { echo "command failed"; exit 1; } 
	convert "$IMAGE" -resize 60x60 "files/image/${ISRC}_6.jpg"|| { echo "command failed"; exit 1; } 
	
	convert "$IMAGE"  -resize 150x150 -depth 8 -quality 00 "${IMAGE_COVER}"|| { echo "command failed"; exit 1; } 
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
	java -jar ${CP}/uits-3.7-SNAPSHOT.jar ${PRIVATE_KEY} "./files/purchased/${ISRC}.mp3" "./${ISRC}.mp3"    || { echo "command failed"; exit 1; }
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
	
	echo "Encoding " ${INPUT} "with dmg"

	dmg --input-file "${INPUT}" --mp4-chunk-span 900 --overwrite --audio-only --repair-all --audio-encoder aac --audio-cbr-rate 48 --aac-mode he-aacv1 --output-file "${ISRC}_48.m4a" --input-speech false --clipmode prolimit|| { echo "command failed"; exit 1; } 
	${NERO_DIR}/neroAacTag "${ISRC}_48.m4a" "-meta:title=${META_TITLE}" "-meta:artist=${META_AUTHOR}" "-meta:album=${META_ALBUM}" "-meta:genre=${META_GENRE}" "-meta:year=${META_DATE}" "-meta:track=${META_TRACK}" "-meta:copyright=${META_COPY}" "-meta:isrc=${ISRC}" "-add-cover:front:${IMAGE_COVER}"|| { echo "command failed"; exit 1; } 		
	java -jar ${CP}/uits-3.7-SNAPSHOT.jar ${PRIVATE_KEY} ${ISRC}_48.m4a ${ISRC}_48.aud ${ISRC}_48.hdr ${ISRC}_48.enc || { echo "command failed"; exit 1; }

	
	dmg --input-file "${INPUT}" --mp4-chunk-span 900 --overwrite --audio-only --repair-all --audio-encoder aac --audio-cbr-rate 96 --aac-mode he-aacv1 --output-file "${ISRC}_96.m4a" --input-speech false --clipmode prolimit|| { echo "command failed"; exit 1; } 
	${NERO_DIR}/neroAacTag "${ISRC}_96.m4a" "-meta:title=${META_TITLE}" "-meta:artist=${META_AUTHOR}" "-meta:album=${META_ALBUM}" "-meta:genre=${META_GENRE}" "-meta:year=${META_DATE}" "-meta:track=${META_TRACK}" "-meta:copyright=${META_COPY}" "-meta:isrc=${ISRC}" "-add-cover:front:${IMAGE_COVER}"|| { echo "command failed"; exit 1; } 

	java -jar ${CP}/uits-3.7-SNAPSHOT.jar ${PRIVATE_KEY} ${ISRC}_96.m4a ${ISRC}_96.aud ${ISRC}_96.hdr ${ISRC}_96.enc || { echo "command failed"; exit 1; }
#	mv ${ISRC}_96.m4a.u ${ISRC}_96.m4a  || { echo "command failed"; exit 1; } 

	mv "${ISRC}_48.hdr" files/header|| { echo "command failed"; exit 1; } 
	mv "${ISRC}_48.aud" files/audio|| { echo "command failed"; exit 1; } 
	mv "${ISRC}_48.enc" files/encoded|| { echo "command failed"; exit 1; }
	mv "${ISRC}_96.hdr" files/header|| { echo "command failed"; exit 1; } 
	mv "${ISRC}_96.aud" files/audio|| { echo "command failed"; exit 1; } 
	mv "${ISRC}_96.enc" files/encoded|| { echo "command failed"; exit 1; } 

echo "***** Generating Mobile Preview Audio *****"
	if [ "$PREVIEW_FILE" = "" ]; then
	dmg --input-file "${INPUT}" --mp4-chunk-span 900 --overwrite --audio-only --repair-all --audio-encoder aac --audio-cbr-rate 96 --aac-mode he-aacv1 --output-file "${ISRC}P.m4a" --input-speech false --clipmode prolimit --trim-end 30000 --audio-fade-interval 1000 || { echo "command failed"; exit 1; }
	
#		${FFMPEG} -i "${ISRC}_96.m4a" -y -t 30 -acodec copy "${ISRC}P.m4a"|| { echo "command failed"; exit 1; } 
	else
		PREVIEW_TMP=`basename ${PREVIEW_FILE}`
		cp "${PREVIEW_FILE}" "${ISRC}P.m4a"|| { echo "command failed"; exit 1; } 
	fi
	${NERO_DIR}/neroAacTag "${ISRC}P.m4a" "-meta:title=${META_TITLE}" "-meta:artist=${META_AUTHOR}" "-meta:album=${META_ALBUM}" "-meta:genre=${META_GENRE}" "-meta:year=${META_DATE}" "-meta:track=${META_TRACK}" "-meta:copyright=${META_COPY}" -meta:isrc=${ISRC} "-add-cover:front:${IMAGE_COVER}"|| { echo "command failed"; exit 1; } 

	mv "${ISRC}P.m4a" files/preview/|| { echo "command failed"; exit 1; } 

# Cleaning: don't clean ! Files are reused to compute the MD5 hash
	#rm -f ${ISRC}P.m4a
	#rm -f ${ISRC}_48.m4a
	#rm -f ${ISRC}_96.m4a
	#rm -f ${ISRC}.mp3
	rm -f ${ISRC}.wav
	
# Publish
	curl -D - -H "X-Auth-Key: b283a8dec498dee9e6a11f459bdcb194" -H "X-Auth-User: chartsnow" https://lon.auth.api.rackspacecloud.com/v1.0 > auth
	URL=`grep "X-Storage-Url:" auth | cut -f2- -d':' | sed "s/\r//g"`
	TOKEN=`grep "X-Storage-Token:" auth | cut -f2- -d':' | sed "s/\r//g"`

	curl -X PUT -T files/audio/${ISRC}${BIT_RATE}.aud  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/private/${TRACK_ID}_${ISRC}.aud
	curl -X PUT -T files/encoded/${ISRC}${BIT_RATE}.enc  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/private/${TRACK_ID}_${ISRC}.enc

	for i in files/audio/${ISRC}*
	do
		curl -X PUT -T ${i}  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/private/${TRACK_ID}_`basename $i`
	done

	for i in files/image/${ISRC}*
	do
		curl -X PUT -T ${i}  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/private/${TRACK_ID}_`basename $i`
	done
	
	for i in files/encoded/${ISRC}*
	do
		curl -X PUT -T ${i}  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/private/${TRACK_ID}_`basename $i`
	done
	
	for i in files/header/${ISRC}*
	do
		curl -X PUT -T ${i}  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/private/${TRACK_ID}_`basename $i`
	done
	
	for i in files/purchased/${ISRC}*
	do
		curl -X PUT -T ${i}  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/private/${TRACK_ID}_`basename $i`
	done
	
	for i in files/preview/${ISRC}*
	do
		curl -X PUT -T ${i}  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/data/`basename $i`
	done
	
	for i in ${ISRC}*.m4a; 
    do
        curl -X PUT -T ${i}  -H "X-Auth-Token: ${TOKEN}" -H "X-CDN-Enabled: True" -H "X-TTL: 900" ${URL}/private/${TRACK_ID}_`basename $i`
    done
	
	# Don't push to public directory
	#mv files/image/* ${PUBLISH_DIR}/image|| { echo "command failed"; exit 1; } 
	#mv files/header/* ${PUBLISH_DIR}/header|| { echo "command failed"; exit 1; } 
	#mv files/audio/* ${PUBLISH_DIR}/audio|| { echo "command failed"; exit 1; } 
	#mv files/encoded/* ${PUBLISH_DIR}/encoded|| { echo "command failed"; exit 1; } 
	#mv files/purchased/* ${PUBLISH_DIR}/purchased|| { echo "command failed"; exit 1; } 
	

