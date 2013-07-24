ALTER TABLE tb_paymentPolicy ADD COLUMN mediatype VARCHAR(25) NULL DEFAULT 'AUDIO';

-- insert 3 new audio payment policies for o2 consumer group

INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,mediatype) VALUES
(10,5,5,'o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107056','Description of content','O2 Tracks','o2','_4G','AUDIO');


INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,mediatype) VALUES
(10,2,2,'o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107055','Description of content','O2 Tracks','o2','_4G','AUDIO');


INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,mediatype) VALUES
(10,1,1,'o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107054','Description of content','O2 Tracks','o2','_4G','AUDIO');



-- insert 3 new audio+video payment policies for o2 consumer group

INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,mediatype) VALUES
(10,5,'7.5','o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107056','Description of content','O2 Tracks','o2','_4G','AUDIOPLUSVIDEO');


INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,mediatype) VALUES
(10,2,'3','o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107055','Description of content','O2 Tracks','o2','_4G','AUDIOPLUSVIDEO');


INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,mediatype) VALUES
(10,1,'1.5','o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107054','Description of content','O2 Tracks','o2','_4G','AUDIOPLUSVIDEO');




ALTER TABLE tb_users ADD COLUMN optedInForVideo TINYINT(1) NULL DEFAULT NULL;