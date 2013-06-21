#! /bin/bash

function generateWM() {
PT=`expr  45 \* $1  / 200`
 convert -size ${1}x${1} xc:grey50  -font Arial -pointsize $PT -gravity center -draw "fill grey80  rotate -45 text 0,0  'PREVIEW'"   stamp_fgnd.png
 convert -size ${1}x${1} xc:black  -font Arial -pointsize $PT -gravity center -draw "fill white rotate -45 text  1,1  'PREVIEW' text  0,0  'PREVIEW'  fill black  text -1,-1  'PREVIEW'" +matte stamp_mask.png
 composite -compose CopyOpacity  stamp_mask.png  stamp_fgnd.png  stamp${1}.png
mogrify -trim +repage stamp${1}.png
rm stamp_mask.png  stamp_fgnd.png
}

generateWM 200
generateWM 240
generateWM 90
generateWM 60
generateWM 70
