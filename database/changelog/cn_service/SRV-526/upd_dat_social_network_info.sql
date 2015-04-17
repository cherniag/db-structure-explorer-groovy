-- reusable script. no need transactions

INSERT INTO social_network_info
(id, user_id, social_network_type, social_network_id, email, gender_type, date_of_birth, profile_image_url, last_name, first_name, country, city, user_name, age_range_min, age_range_max,
profile_image_silhouette)
  SELECT fb.id,
    si.user_id,
    'FACEBOOK',
    fb.fb_id,
    fb.email,
    fb.gender,
    CAST(fb.date_of_birth AS DATE),
    fb.profile_image_url,
    fb.surname,
    fb.first_name,
    fb.country,
    fb.city,
    fb.user_name,
    fb.age_range_min,
    fb.age_range_max,
    fb.profile_image_silhouette
  FROM facebook_user_info fb
    INNER JOIN social_info si ON fb.id = si.id
  WHERE fb.id NOT IN(SELECT id FROM social_network_info);

INSERT INTO social_network_info
(id, user_id, social_network_type, social_network_id, email, gender_type, date_of_birth, profile_image_url, last_name, first_name, country, city, user_name, age_range_min, age_range_max, profile_image_silhouette)
  SELECT gp.id,
    si.user_id,
    'GOOGLE',
    gp.gp_id,
    gp.email,
    gp.gender,
    CAST(gp.date_of_birth AS DATE),
    gp.picture_url,
    gp.family_name,
    gp.given_name,
    NULL,
    gp.location,
    gp.display_name,
    NULL,
    NULL,
    NULL
  FROM google_plus_user_info gp
    INNER JOIN social_info si ON gp.id = si.id
  WHERE gp.id NOT IN(SELECT id FROM social_network_info);

