-- IMP-2388 - Some of the drops fail to ingest
-- http://jira.musicqubed.com/browse/IMP-2388

delete
FROM cn_cms.Track t1,
    (
        SELECT
            t2.ISRC ,
            t2.ProductCode ,
            t2.Ingestor,
            max(t2.id) maxId
        FROM
            cn_cms.Track t2
        GROUP BY
            t2.ISRC ,
            t2.ProductCode ,
            t2.Ingestor
        HAVING
            COUNT(*) > 1
    ) a
    where a.ISRC = t1.ISRC
    AND a.ProductCode = t1.ProductCode
    AND a.Ingestor = t1.Ingestor
    and a.maxId<>t1.id;