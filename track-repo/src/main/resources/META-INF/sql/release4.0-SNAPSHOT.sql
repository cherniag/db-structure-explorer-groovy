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

create index index_productCode on cn_cms.Track(productcode);
create index index_uniqueId on cn_cms.Track(productcode,title,artist);
create index index_Artist on cn_cms.Track(artist);
create index index_Title on cn_cms.Track(title);
