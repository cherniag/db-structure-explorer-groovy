-- IMP-2388 - Some of the drops fail to ingest
-- http://jira.musicqubed.com/browse/IMP-2388

CREATE
TABLE
  duplicated_track LIKE cn_cms.track
;

INSERT
INTO
  cn_cms.duplicated_track SELECT
                            t1.*
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

delete from cn_cms.assetfile where assetfile.type=3 and assetfile.id in (select duplicated_track.coverFile from cn_cms.duplicated_track);
delete from cn_cms.assetfile where assetfile.id in (select duplicated_track.mediaFile from cn_cms.duplicated_track);

delete from cn_cms.track where track.id in (select id from cn_cms.duplicated_track);