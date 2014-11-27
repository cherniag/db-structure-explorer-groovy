-- 5.12.0:
-- http://jira.musicqubed.com/browse/SRV-130
-- SRV-130 [SERVER] Replace tinyint type in tb_charts.i by int
ALTER TABLE community_charts DROP FOREIGN KEY FK3410E96B4E1D2677;
ALTER TABLE community_charts DROP INDEX FK3410E96B4E1D2677;
ALTER TABLE tb_chartDetail DROP FOREIGN KEY tb_chartdetail_U_chart;
ALTER TABLE tb_chartDetail DROP INDEX tb_chartdetail_PK_chart;
ALTER TABLE user_charts DROP FOREIGN KEY FK_chart_id;
ALTER TABLE user_charts DROP INDEX FK_chart_id;
ALTER TABLE sz_deeplink_music_list DROP FOREIGN KEY sz_deeplink_music_list_U_chart_id;
ALTER TABLE sz_deeplink_music_list DROP INDEX sz_deeplink_music_list_PK_chart_id;

ALTER TABLE community_charts MODIFY chart_id INT NOT NULL;
ALTER TABLE tb_chartDetail MODIFY chart INT NOT NULL;
ALTER TABLE user_charts MODIFY chart_id INT NOT NULL;
ALTER TABLE sz_deeplink_music_list MODIFY chart_id INT;
ALTER TABLE tb_charts MODIFY i INT NOT NULL AUTO_INCREMENT;

ALTER TABLE community_charts ADD INDEX community_charts_PK_chart_id (chart_id), ADD CONSTRAINT community_charts_U_chart_id FOREIGN KEY (chart_id) REFERENCES tb_charts (i);
ALTER TABLE tb_chartDetail ADD INDEX tb_chartDetail_PK_chart (chart), ADD CONSTRAINT tb_chartDetail_U_chart FOREIGN KEY (chart) REFERENCES tb_charts (i);
ALTER TABLE user_charts ADD INDEX user_charts_PK_chart_id (chart_id), ADD CONSTRAINT user_charts_U_chart_id FOREIGN KEY (chart_id) REFERENCES tb_charts (i);
ALTER TABLE sz_deeplink_music_list ADD INDEX sz_deeplink_music_list_PK_chart_id (chart_id), ADD CONSTRAINT sz_deeplink_music_list_PK_chart_id FOREIGN KEY (chart_id) REFERENCES tb_charts (i);
