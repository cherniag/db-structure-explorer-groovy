 -- IMP-1781 [Track Repo] Migrate tracks ingestion from CMS to Track Repo
alter table AssetFile add column duration int unsigned;
alter table AssetFile add column external_id varchar(255);