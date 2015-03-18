CREATE TABLE social_network_info (
  id bigint(20) AUTO_INCREMENT PRIMARY KEY,
  user_id int(10) unsigned NOT NULL,
  social_network varchar(45) NOT NULL,
  social_network_id varchar(100) NOT NULL,
  email varchar(100) DEFAULT NULL,
  gender varchar(10) DEFAULT NULL,
  date_of_birth date DEFAULT NULL,
  profile_image_url varchar(255) DEFAULT NULL,
  last_name varchar(100) DEFAULT NULL,
  first_name varchar(100) DEFAULT NULL,
  profile_url varchar(255) DEFAULT NULL,
  location varchar(100) DEFAULT NULL,
  user_name varchar(100) DEFAULT NULL,
  age_range_min int(11) DEFAULT NULL,
  age_range_max int(11) DEFAULT NULL,
  profile_image_silhouette bit(1) DEFAULT NULL
);

ALTER TABLE social_network_info ADD CONSTRAINT fk_tb_users_i foreign key (user_id) references tb_users(i);
