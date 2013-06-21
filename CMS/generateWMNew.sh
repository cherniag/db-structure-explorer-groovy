#! /bin/bash

function generateWM() {
PT=`expr  20 \* $1  / 200`
LINE1=`expr $PT + 3`
LINE2=`expr $LINE1 + $PT + 3`
convert -size ${1}x${1} xc:grey70  -font Arial -pointsize $PT -gravity center -draw "fill red  text 0,0  'OOPS THIS IS NOT' text 0,$LINE1 'LANA' text 0,$LINE2 'CLICK INFO'"   stamp_fgnd.png
#convert stamp_fgnd.png -transparent white stamp${1}.png
#mogrify -trim +repage stamp${1}.png
convert -size ${1}x${1} xc:black  -font Arial -pointsize $PT -gravity center -draw "fill RosyBrown1  text 2,2  'OOPS THIS IS NOT' text 2,$LINE1 'LANA' text 2,$LINE2 'CLICK INFO' fill white text 0,0  'OOPS THIS IS NOT' text 0,$LINE1 'LANA' text 0,$LINE2 'CLICK INFO' fill red  text -1,-1  'OOPS THIS IS NOT' text -1,$LINE1 'LANA' text -1,$LINE2 'CLICK INFO'" +matte stamp_mask.png
#convert stamp_mask.png -transparent white stamp${1}.png
#mogrify -trim +repage stamp${1}.png
composite -compose CopyOpacity  stamp_mask.png  stamp_fgnd.png  stamp${1}.png
#cp stamp_fgnd.png stamp${1}.png
mogrify -trim +repage stamp${1}.png
#rm stamp_mask.png  stamp_fgnd.png
}

generateWM 200
generateWM 90
generateWM 60
generateWM 70
generateWM 240
