-- SRV-391
ALTER TABLE facebook_user_info ADD COLUMN profile_image_url CHAR(255) default null;
ALTER TABLE facebook_user_info ADD COLUMN profile_image_silhouette bit(1) DEFAULT 0;
