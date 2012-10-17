--Hot fix for Payment Jobs
alter table tb_pendingPayments add column externalTxId varchar(255) NOT NULL;
alter table tb_submittedPayments add column externalTxId varchar(255) NOT NULL;

-- IMP-4 What's Hot: day 1
-- http://jira.dev.now-technologies.mobi:8181/browse/IMP-4
alter table tb_charts add column numBonusTracks TINYINT not null;

alter table tb_chartDetail modify column chgPosition int UNSIGNED default '3' not null;

alter table tb_newsDetail add column body text not null;

create table tb_filter (id tinyint unsigned not null auto_increment, filterType char(31) not null, primary key (id)) engine=INNODB;
 
alter table tb_newsDetail add column online BIT not null default true;
alter table tb_newsDetail add column userHandset char(15);
alter table tb_newsDetail add column userState char(50);
alter table tb_newsDetail add column messageFrequence char(30);
alter table tb_newsDetail add column messageType char(15) not null;
alter table tb_newsDetail add column timestampMilis bigint not null;

update tb_newsDetail set messageType='NEWS', timestampMilis=UNIX_TIMESTAMP(NOW())*1000;

create table tb_filter_tb_newsDetail (filters_id tinyint unsigned not null, newDetails_i integer not null) engine=INNODB;

insert into tb_filter (id, filterType) values (1, 'UserStateFilter'), (2, 'UserHandsetFilter');
