-- IMP-2388 - Some of the drops fail to ingest
-- http://jira.musicqubed.com/browse/IMP-2388

CREATE
TABLE
  duplicated_track LIKE cn_cms.Track
;

alter
TABLE
duplicated_track add column last_id bigint(20) NOT NULL;

INSERT
INTO
  cn_cms.duplicated_track SELECT
                             t1.*, a.maxId
                          FROM
                              cn_cms.Track t1 ,
                              (
                                SELECT
                                  t2.ISRC ,
                                  t2.ProductCode ,
                                  t2.Ingestor ,
                                  MAX( t2.id ) maxId
                                FROM
                                  cn_cms.Track t2
                                GROUP BY
                                  t2.ISRC ,
                                  t2.ProductCode ,
                                  t2.Ingestor
                                HAVING
                                  COUNT(*) > 1
                              ) a
                          WHERE
                            a.ISRC = t1.ISRC
                            AND a.ProductCode = t1.ProductCode
                            AND a.Ingestor = t1.Ingestor
                            AND a.maxId <> t1.id
;

delete from cn_cms.AssetFile where AssetFile.trackId in (select duplicated_track.id from cn_cms.duplicated_track);
delete from cn_cms.Territory where Territory.trackId in (select duplicated_track.id from cn_cms.duplicated_track);
delete from cn_cms.ResourceFile where ResourceFile.trackId in (select duplicated_track.id from cn_cms.duplicated_track);

delete from cn_cms.Track where Track.id in (select id from cn_cms.duplicated_track);

ALTER TABLE cn_cms.Track ADD CONSTRAINT track_U_isrc_productCode_ingestor UNIQUE KEY (ISRC,ProductCode,Ingestor);

-- SQLs for correct removing
SELECT
  *
FROM cn_cms.Track t1
  right join
  duplicated_track d
    on d.last_id = t1.id
where t1.id is null;