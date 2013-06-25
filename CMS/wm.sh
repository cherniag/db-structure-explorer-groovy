 convert -size 200x200 xc:grey50  -font Arial -pointsize 45 -gravity center -draw "fill grey80  rotate -45 text 0,0  'PREVIEW'"   stamp_fgnd.png
 convert -size 200x200 xc:black  -font Arial -pointsize 45 -gravity center -draw "fill white rotate -45 text  1,1  'PREVIEW' text  0,0  'PREVIEW'  fill black  text -1,-1  'PREVIEW'" +matte stamp_mask.png
 composite -compose CopyOpacity  stamp_mask.png  stamp_fgnd.png  stamp200.png
mogrify -trim +repage stamp200.png
 composite -gravity center stamp200.png $1  P$1
