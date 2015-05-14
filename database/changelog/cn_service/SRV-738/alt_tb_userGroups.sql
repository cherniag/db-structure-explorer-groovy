ALTER TABLE `tb_userGroups` DROP FOREIGN KEY `FK908D03F0F8D07E3F`;
ALTER TABLE `tb_userGroups` MODIFY `drmPolicy` tinyint(3) unsigned default 1;
