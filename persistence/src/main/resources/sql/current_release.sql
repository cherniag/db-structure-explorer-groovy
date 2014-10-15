-- SRV-296

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
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'BASIC_CHART FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 2
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'HOT_TRACKS FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 3
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'FIFTH_CHART FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 4
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_1 FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 5
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_2 FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 6
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'OTHER_CHART FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 1
WHERE
  com.name = 'hl_uk'
  AND cd.media IS NULL
  AND c.name = 'HOT_TRACKS FOR HL UK'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 2
WHERE
  com.name = 'hl_uk'
  AND cd.media IS NULL
  AND c.name = 'FIFTH_CHART FOR HL UK'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 3
WHERE
  com.name = 'hl_uk'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_1 FOR HL U'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 4
WHERE
  com.name = 'hl_uk'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_2 FOR HL U'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 5
WHERE
  com.name = 'hl_uk'
  AND cd.media IS NULL
  AND c.name = 'OTHER_CHART FOR HL UK'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 6
WHERE
  com.name = 'hl_uk'
  AND cd.media IS NULL
  AND c.name = 'FOURTH_CHART FOR HL UK'
;


commit;

-- SRV-296