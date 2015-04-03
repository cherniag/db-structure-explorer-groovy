CREATE TABLE social_network_info (
  id bigint(20) AUTO_INCREMENT PRIMARY KEY,
  user_id int(10) unsigned NOT NULL,
  social_network_type varchar(10) NOT NULL,
  social_network_id varchar(100) NOT NULL,
  email varchar(254) DEFAULT NULL,
  user_name varchar(100) DEFAULT NULL,
  last_name varchar(100) DEFAULT NULL,
  first_name varchar(100) DEFAULT NULL,
  date_of_birth date DEFAULT NULL,
  gender_type varchar(10) DEFAULT NULL,
  age_range_min int(11) DEFAULT NULL,
  age_range_max int(11) DEFAULT NULL,
  profile_image_url varchar(255) DEFAULT NULL,
  profile_image_silhouette bit(1) DEFAULT NULL,
  country varchar(50) DEFAULT NULL,
  city varchar(100) DEFAULT NULL
);

ALTER TABLE social_network_info ADD CONSTRAINT fk_social_network_info_tb_users foreign key (user_id) references tb_users(i);
CREATE INDEX idx_social_network_info_user_id on social_network_info (user_id);
CREATE INDEX idx_social_network_info_social_network_type on social_network_info (social_network_type);
