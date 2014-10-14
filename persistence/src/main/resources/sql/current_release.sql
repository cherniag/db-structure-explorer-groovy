-- begin SRV-89

alter table cn_service.tb_labels modify name VARCHAR(255);

insert into cn_service.tb_labels (name)
  select distinct(label) from cn_cms.Track tr
    LEFT JOIN cn_service.tb_labels tl on (tr.label = tl.name)
  where tl.name is null  and tr.label is not null;


update  cn_service.tb_media tb
  LEFT JOIN cn_cms.Track tr on (tb.trackId = tr.id)
  LEFT JOIN cn_service.tb_labels tl on (tr.label = tl.name)
set tb.label = tl.i
where tb.trackId is not null;

alter table cn_service.tb_media add CONSTRAINT `media_label_fk` FOREIGN KEY (`label`) REFERENCES cn_service.tb_labels (i);
-- end SRV-89