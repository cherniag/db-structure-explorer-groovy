-- SRV-290 MTV1 playlist order fix
UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 0
WHERE
  com.name = 'mtv1'
  AND cd.media IS NULL
  AND c.name = 'HOT_TRACKS - MTV1'
;