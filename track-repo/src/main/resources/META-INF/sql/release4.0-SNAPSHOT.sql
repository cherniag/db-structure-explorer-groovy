 -- IMP-1781 [Track Repo] Migrate tracks ingestion from CMS to Track Repo
alter table AssetFile add column duration int unsigned;
alter table AssetFile add column external_id varchar(255);

 -- optimize searching tracks
alter table cn_cms.Track add column label varchar(255);
alter table cn_cms.Track add column releaseDate date;
alter table cn_cms.Track add column territoryCodes longtext;
alter table cn_cms.Track add column coverFile bigint;
alter table cn_cms.Track add column mediaFile bigint;

update cn_cms.Track t
  join cn_cms.AssetFile f on t.id = f.TrackId and f.type = 1
set t.mediaFile = f.id;

update cn_cms.Track t
  join cn_cms.AssetFile f on t.id = f.TrackId and f.type = 4
set t.mediaFile = f.id;

update cn_cms.Track t
  join cn_cms.AssetFile f on t.id = f.TrackId and f.type = 2
set t.coverFile = f.id;

update cn_cms.Track t
set
  t.territoryCodes =
  (select group_concat(tr.Code separator ', ')
   from cn_cms.Territory tr where t.id = tr.TrackId and tr.deleted = false group by 'all');

update cn_cms.Track t
join cn_cms.Territory tr on tr.TrackId = t.id
set
  t.label = tr.label,
  t.releaseDate = tr.startdate

alter table cn_cms.Track add column mediaType VARCHAR(255) not null DEFAULT 'DOWNLOAD';

update cn_cms.Track t
  join cn_cms.AssetFile af on t.mediaFile = af.id and af.type = 4
set t.mediaType = 'VIDEO';

-- http://jira.musicqubed.com/browse/IMP-1900
-- EMI migration into WMG
alter table cn_cms.Track add column emi_track_id bigint(20);
alter table cn_cms.Track add index Track_PK_emi_track_id (emi_track_id);

start transaction;

INSERT
    INTO
        cn_cms.Track(
            emi_track_id ,
            Ingestor ,
            ISRC ,
            Title ,
            Artist ,
            ProductId ,
            ProductCode ,
            Genre ,
            Copyright ,
            YEAR ,
            Album ,
            Xml ,
            IngestionDate ,
            IngestionUpdateDate ,
            PublishDate ,
            Info ,
            SubTitle ,
            Licensed ,
            status ,
            resolution ,
            itunesUrl ,
            explicit ,
            label ,
            releaseDate ,
            territoryCodes ,
            coverFile ,
            mediaFile ,
            mediaType
        ) SELECT
            t1.id ,
            'WARNER' ,
            t1.ISRC ,
            t1.Title ,
            t1.Artist ,
            t1.ProductId ,
            t1.ProductCode ,
            t1.Genre ,
            t1.Copyright ,
            t1.YEAR ,
            t1.Album ,
            t1.Xml ,
            t1.IngestionDate ,
            t1.IngestionUpdateDate ,
            t1.PublishDate ,
            t1.Info ,
            t1.SubTitle ,
            t1.Licensed ,
            t1.status ,
            t1.resolution ,
            t1.itunesUrl ,
            t1.explicit ,
            t1.label ,
            t1.releaseDate ,
            t1.territoryCodes ,
            t1.coverFile ,
            t1.mediaFile ,
            t1.mediaType
        FROM
            cn_cms.Track t1 LEFT JOIN cn_cms.Track t2
                ON t2.emi_track_id = t1.id
        WHERE
        	t2.emi_track_id IS NULL
        	AND t1.Ingestor = 'EMI'
;
commit;

start transaction;

UPDATE
    cn_service.tb_media media JOIN cn_cms.Track track
        ON media.trackId = track.emi_track_id
SET
    media.trackId = track.id
;
commit;

start transaction;

update cn_cms.Track track set track.PublishDate='2013-09-01' where track.emi_track_id is not null;

commit;