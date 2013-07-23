 -- IMP-1781 [Track Repo] Migrate tracks ingestion from CMS to Track Repo
alter table AssertFile add column duration int unsigned;
alter table AssertFile add column external_id varchar(255);