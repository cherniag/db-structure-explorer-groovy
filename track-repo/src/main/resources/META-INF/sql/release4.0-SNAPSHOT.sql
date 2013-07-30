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


