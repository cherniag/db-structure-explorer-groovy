-- SRV-290 MTV1 playlist order fix

SET autocommit = 0;
START TRANSACTION;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 1
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'HOT_TRACKS - MTV1'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 2
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'FIFTH_CHART - MTV1'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 3
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_1 - MTV1'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 4
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_2 - MTV1'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 5
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_3 - MTV1'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 6
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'OTHER_CHART - MTV1'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 7
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_4 - MTV1'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 8
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_5 - MTV1'
;

commit;