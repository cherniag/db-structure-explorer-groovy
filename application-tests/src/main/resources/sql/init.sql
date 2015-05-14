use cn_service_at;

INSERT INTO tb_userStatus (i, name) VALUES (1, 'EULA');
INSERT INTO tb_userStatus (i, name) VALUES (2, 'SUBSCRIBED');
INSERT INTO tb_userStatus (i, name) VALUES (3, 'LIMITED');

INSERT INTO tb_deviceTypes (i, name) VALUES (1, 'NONE');
INSERT INTO tb_deviceTypes (i, name) VALUES (2, 'ANDROID');
INSERT INTO tb_deviceTypes (i, name) VALUES (3, 'J2ME');
INSERT INTO tb_deviceTypes (i, name) VALUES (4, 'BLACKBERRY');
INSERT INTO tb_deviceTypes (i, name) VALUES (5, 'IOS');
INSERT INTO tb_deviceTypes (i, name) VALUES (6, 'SYMBIAN');
INSERT INTO tb_deviceTypes (i, name) VALUES (7, 'WINDOWS_PHONE');

INSERT INTO tb_paymentStatus (id, name) VALUES (1, 'NULL');
INSERT INTO tb_paymentStatus (id, name) VALUES (2, 'OK');
INSERT INTO tb_paymentStatus (id, name) VALUES (3, 'AWAITING_PSMS');
INSERT INTO tb_paymentStatus (id, name) VALUES (4, 'PSMS_ERROR');
INSERT INTO tb_paymentStatus (id, name) VALUES (5, 'PIN_PENDING');
INSERT INTO tb_paymentStatus (id, name) VALUES (6, 'AWAITING_PAYMENT');
INSERT INTO tb_paymentStatus (id, name) VALUES (7, 'AWAITING_PAY_PAL');
INSERT INTO tb_paymentStatus (id, name) VALUES (8, 'PAY_PAL_ERROR');

INSERT INTO tb_appVersions (i, name, description) VALUES (1, 'CNBETA', 'Commercial Beta');

INSERT INTO tb_drmTypes (i, name) VALUES (1, 'PLAYS');
INSERT INTO tb_drmTypes (i, name) VALUES (2, 'TIME');
INSERT INTO tb_drmTypes (i, name) VALUES (3, 'PURCHASED');

INSERT INTO tb_country (i, name, fullName) VALUES (1, 'GB', 'Great Britain');
INSERT INTO tb_country (i, name, fullName) VALUES (2, 'UA', 'Ukraine');
INSERT INTO tb_country (i, name, fullName) VALUES (3, 'KR', 'South Korea');
INSERT INTO tb_country (i, name, fullName) VALUES (4, 'FR', 'France');
INSERT INTO tb_country (i, name, fullName) VALUES (5, 'US', 'United States');
INSERT INTO tb_country (i, name, fullName) VALUES (6, 'SG', 'Singapore');
INSERT INTO tb_country (i, name, fullName) VALUES (7, 'ES', 'Spain');
INSERT INTO tb_country (i, name, fullName) VALUES (8, 'AR', 'Argentina');
INSERT INTO tb_country (i, name, fullName) VALUES (9, 'BR', 'Brazil');
INSERT INTO tb_country (i, name, fullName) VALUES (10, 'IN', 'India');
INSERT INTO tb_country (i, name, fullName) VALUES (11, 'NO', 'Norway');
INSERT INTO tb_country (i, name, fullName) VALUES (12, 'PK', 'Pakistan');
INSERT INTO tb_country (i, name, fullName) VALUES (13, 'MY', 'Malaysia');
INSERT INTO tb_country (i, name, fullName) VALUES (14, 'PH', 'Philippines');
INSERT INTO tb_country (i, name, fullName) VALUES (15, 'MX', 'Mexico');
INSERT INTO tb_country (i, name, fullName) VALUES (16, 'DE', 'Germany');
INSERT INTO tb_country (i, name, fullName) VALUES (17, 'BO', 'Bolivia');
INSERT INTO tb_country (i, name, fullName) VALUES (18, 'CL', 'Chile');
INSERT INTO tb_country (i, name, fullName) VALUES (19, 'CO', 'Colombia');
INSERT INTO tb_country (i, name, fullName) VALUES (20, 'CR', 'Costa Rica');
INSERT INTO tb_country (i, name, fullName) VALUES (21, 'CU', 'Cuba');
INSERT INTO tb_country (i, name, fullName) VALUES (22, 'DO', 'Dominican Republic');
INSERT INTO tb_country (i, name, fullName) VALUES (23, 'EC', 'Ecuador');
INSERT INTO tb_country (i, name, fullName) VALUES (24, 'SV', 'El Salvador');
INSERT INTO tb_country (i, name, fullName) VALUES (25, 'GT', 'Guatemala');
INSERT INTO tb_country (i, name, fullName) VALUES (26, 'HT', 'Haiti');
INSERT INTO tb_country (i, name, fullName) VALUES (27, 'HN', 'Honduras');
INSERT INTO tb_country (i, name, fullName) VALUES (29, 'NI', 'Nicaragua');
INSERT INTO tb_country (i, name, fullName) VALUES (30, 'PA', 'Panama');
INSERT INTO tb_country (i, name, fullName) VALUES (31, 'PY', 'Paraguay');
INSERT INTO tb_country (i, name, fullName) VALUES (32, 'PE', 'Peru');
INSERT INTO tb_country (i, name, fullName) VALUES (33, 'UY', 'Uruguay');
INSERT INTO tb_country (i, name, fullName) VALUES (34, 'VE', 'Venezuela');
INSERT INTO tb_country (i, name, fullName) VALUES (35, 'AF', 'Afghanistan');
INSERT INTO tb_country (i, name, fullName) VALUES (36, 'AX', 'Aland Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (37, 'AL', 'Albania');
INSERT INTO tb_country (i, name, fullName) VALUES (38, 'DZ', 'Algeria');
INSERT INTO tb_country (i, name, fullName) VALUES (39, 'AS', 'American Samoa');
INSERT INTO tb_country (i, name, fullName) VALUES (40, 'AD', 'Andorra');
INSERT INTO tb_country (i, name, fullName) VALUES (41, 'AO', 'Angola');
INSERT INTO tb_country (i, name, fullName) VALUES (42, 'AI', 'Anguilla');
INSERT INTO tb_country (i, name, fullName) VALUES (43, 'AG', 'Antigua and Barbuda');
INSERT INTO tb_country (i, name, fullName) VALUES (44, 'AM', 'Armenia');
INSERT INTO tb_country (i, name, fullName) VALUES (45, 'AW', 'Aruba');
INSERT INTO tb_country (i, name, fullName) VALUES (46, 'AU', 'Australia');
INSERT INTO tb_country (i, name, fullName) VALUES (47, 'AT', 'Austria');
INSERT INTO tb_country (i, name, fullName) VALUES (48, 'AZ', 'Azerbaijan');
INSERT INTO tb_country (i, name, fullName) VALUES (49, 'BH', 'Bahrain');
INSERT INTO tb_country (i, name, fullName) VALUES (50, 'BD', 'Bangladesh');
INSERT INTO tb_country (i, name, fullName) VALUES (51, 'BB', 'Barbados');
INSERT INTO tb_country (i, name, fullName) VALUES (52, 'BY', 'Belarus');
INSERT INTO tb_country (i, name, fullName) VALUES (53, 'BE', 'Belgium');
INSERT INTO tb_country (i, name, fullName) VALUES (54, 'BZ', 'Belize');
INSERT INTO tb_country (i, name, fullName) VALUES (55, 'BJ', 'Benin');
INSERT INTO tb_country (i, name, fullName) VALUES (56, 'BM', 'Bermuda');
INSERT INTO tb_country (i, name, fullName) VALUES (57, 'BT', 'Bhutan');
INSERT INTO tb_country (i, name, fullName) VALUES (58, 'BA', 'Bosnia and Herzegovina');
INSERT INTO tb_country (i, name, fullName) VALUES (59, 'BW', 'Botswana');
INSERT INTO tb_country (i, name, fullName) VALUES (60, 'IO', 'British Indian Ocean Territory');
INSERT INTO tb_country (i, name, fullName) VALUES (61, 'VG', 'British Virgin Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (62, 'BN', 'Brunei');
INSERT INTO tb_country (i, name, fullName) VALUES (63, 'BG', 'Bulgaria');
INSERT INTO tb_country (i, name, fullName) VALUES (64, 'BF', 'Burkina Faso');
INSERT INTO tb_country (i, name, fullName) VALUES (65, 'MM', 'Burma');
INSERT INTO tb_country (i, name, fullName) VALUES (66, 'BI', 'Burundi');
INSERT INTO tb_country (i, name, fullName) VALUES (67, 'KH', 'Cambodia');
INSERT INTO tb_country (i, name, fullName) VALUES (68, 'CM', 'Cameroon');
INSERT INTO tb_country (i, name, fullName) VALUES (69, 'CA', 'Canada');
INSERT INTO tb_country (i, name, fullName) VALUES (70, 'CV', 'Cape Verde');
INSERT INTO tb_country (i, name, fullName) VALUES (71, 'BQ', 'Caribbean Netherlands');
INSERT INTO tb_country (i, name, fullName) VALUES (72, 'KY', 'Cayman Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (73, 'CF', 'Central African Republic');
INSERT INTO tb_country (i, name, fullName) VALUES (74, 'TD', 'Chad');
INSERT INTO tb_country (i, name, fullName) VALUES (75, 'CN', 'China');
INSERT INTO tb_country (i, name, fullName) VALUES (76, 'CX', 'Christmas Island');
INSERT INTO tb_country (i, name, fullName) VALUES (77, 'KM', 'Comoros');
INSERT INTO tb_country (i, name, fullName) VALUES (78, 'CK', 'Cook Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (79, 'HR', 'Croatia');
INSERT INTO tb_country (i, name, fullName) VALUES (80, 'CW', 'Curacao');
INSERT INTO tb_country (i, name, fullName) VALUES (81, 'CY', 'Cyprus');
INSERT INTO tb_country (i, name, fullName) VALUES (82, 'CZ', 'Czech Republic');
INSERT INTO tb_country (i, name, fullName) VALUES (83, 'CD', 'Democratic Republic of the Congo');
INSERT INTO tb_country (i, name, fullName) VALUES (84, 'DK', 'Denmark');
INSERT INTO tb_country (i, name, fullName) VALUES (85, 'DJ', 'Djibouti');
INSERT INTO tb_country (i, name, fullName) VALUES (86, 'DM', 'Dominica');
INSERT INTO tb_country (i, name, fullName) VALUES (87, 'EG', 'Egypt');
INSERT INTO tb_country (i, name, fullName) VALUES (88, 'GQ', 'Equatorial Guinea');
INSERT INTO tb_country (i, name, fullName) VALUES (89, 'ER', 'Eritrea');
INSERT INTO tb_country (i, name, fullName) VALUES (90, 'EE', 'Estonia');
INSERT INTO tb_country (i, name, fullName) VALUES (91, 'ET', 'Ethiopia');
INSERT INTO tb_country (i, name, fullName) VALUES (92, 'FK', 'Falkland Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (93, 'FO', 'Faroe Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (94, 'FJ', 'Fiji');
INSERT INTO tb_country (i, name, fullName) VALUES (95, 'FI', 'Finland');
INSERT INTO tb_country (i, name, fullName) VALUES (96, 'GF', 'French Guiana');
INSERT INTO tb_country (i, name, fullName) VALUES (97, 'PF', 'French Polynesia');
INSERT INTO tb_country (i, name, fullName) VALUES (98, 'GA', 'Gabon');
INSERT INTO tb_country (i, name, fullName) VALUES (99, 'GE', 'Georgia');
INSERT INTO tb_country (i, name, fullName) VALUES (100, 'GH', 'Ghana');
INSERT INTO tb_country (i, name, fullName) VALUES (101, 'GI', 'Gibraltar');
INSERT INTO tb_country (i, name, fullName) VALUES (102, 'GR', 'Greece');
INSERT INTO tb_country (i, name, fullName) VALUES (103, 'GL', 'Greenland');
INSERT INTO tb_country (i, name, fullName) VALUES (104, 'GD', 'Grenada');
INSERT INTO tb_country (i, name, fullName) VALUES (105, 'GP', 'Guadeloupe');
INSERT INTO tb_country (i, name, fullName) VALUES (106, 'GU', 'Guam');
INSERT INTO tb_country (i, name, fullName) VALUES (107, 'GG', 'Guernsey');
INSERT INTO tb_country (i, name, fullName) VALUES (108, 'GN', 'Guinea');
INSERT INTO tb_country (i, name, fullName) VALUES (109, 'GW', 'Guinea-Bissau');
INSERT INTO tb_country (i, name, fullName) VALUES (110, 'GY', 'Guyana');
INSERT INTO tb_country (i, name, fullName) VALUES (111, 'HK', 'Hong Kong');
INSERT INTO tb_country (i, name, fullName) VALUES (112, 'HU', 'Hungary');
INSERT INTO tb_country (i, name, fullName) VALUES (113, 'IS', 'Iceland');
INSERT INTO tb_country (i, name, fullName) VALUES (114, 'ID', 'Indonesia');
INSERT INTO tb_country (i, name, fullName) VALUES (115, 'IR', 'Iran');
INSERT INTO tb_country (i, name, fullName) VALUES (116, 'IQ', 'Iraq');
INSERT INTO tb_country (i, name, fullName) VALUES (117, 'IE', 'Ireland');
INSERT INTO tb_country (i, name, fullName) VALUES (118, 'IM', 'Isle of Man');
INSERT INTO tb_country (i, name, fullName) VALUES (119, 'IL', 'Israel');
INSERT INTO tb_country (i, name, fullName) VALUES (120, 'IT', 'Italy');
INSERT INTO tb_country (i, name, fullName) VALUES (121, 'CI', 'Ivory Coast');
INSERT INTO tb_country (i, name, fullName) VALUES (122, 'JM', 'Jamaica');
INSERT INTO tb_country (i, name, fullName) VALUES (123, 'JP', 'Japan');
INSERT INTO tb_country (i, name, fullName) VALUES (124, 'JE', 'Jersey');
INSERT INTO tb_country (i, name, fullName) VALUES (125, 'JO', 'Jordan');
INSERT INTO tb_country (i, name, fullName) VALUES (126, 'KZ', 'Kazakhstan');
INSERT INTO tb_country (i, name, fullName) VALUES (127, 'KE', 'Kenya');
INSERT INTO tb_country (i, name, fullName) VALUES (128, 'KI', 'Kiribati');
INSERT INTO tb_country (i, name, fullName) VALUES (129, 'KW', 'Kuwait');
INSERT INTO tb_country (i, name, fullName) VALUES (130, 'KG', 'Kyrgyzstan');
INSERT INTO tb_country (i, name, fullName) VALUES (131, 'LA', 'Laos');
INSERT INTO tb_country (i, name, fullName) VALUES (132, 'LV', 'Latvia');
INSERT INTO tb_country (i, name, fullName) VALUES (133, 'LB', 'Lebanon');
INSERT INTO tb_country (i, name, fullName) VALUES (134, 'LS', 'Lesotho');
INSERT INTO tb_country (i, name, fullName) VALUES (135, 'LR', 'Liberia');
INSERT INTO tb_country (i, name, fullName) VALUES (136, 'LY', 'Libya');
INSERT INTO tb_country (i, name, fullName) VALUES (137, 'LI', 'Liechtenstein');
INSERT INTO tb_country (i, name, fullName) VALUES (138, 'LT', 'Lithuania');
INSERT INTO tb_country (i, name, fullName) VALUES (139, 'LU', 'Luxembourg');
INSERT INTO tb_country (i, name, fullName) VALUES (140, 'MO', 'Macau');
INSERT INTO tb_country (i, name, fullName) VALUES (141, 'MK', 'Macedonia');
INSERT INTO tb_country (i, name, fullName) VALUES (142, 'MG', 'Madagascar');
INSERT INTO tb_country (i, name, fullName) VALUES (143, 'MW', 'Malawi');
INSERT INTO tb_country (i, name, fullName) VALUES (144, 'MV', 'Maldives');
INSERT INTO tb_country (i, name, fullName) VALUES (145, 'ML', 'Mali');
INSERT INTO tb_country (i, name, fullName) VALUES (146, 'MT', 'Malta');
INSERT INTO tb_country (i, name, fullName) VALUES (147, 'MH', 'Marshall Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (148, 'MQ', 'Martinique');
INSERT INTO tb_country (i, name, fullName) VALUES (149, 'MR', 'Mauritania');
INSERT INTO tb_country (i, name, fullName) VALUES (150, 'MU', 'Mauritius');
INSERT INTO tb_country (i, name, fullName) VALUES (151, 'YT', 'Mayotte');
INSERT INTO tb_country (i, name, fullName) VALUES (152, 'FM', 'Micronesia');
INSERT INTO tb_country (i, name, fullName) VALUES (153, 'MD', 'Moldova');
INSERT INTO tb_country (i, name, fullName) VALUES (154, 'MC', 'Monaco');
INSERT INTO tb_country (i, name, fullName) VALUES (155, 'MN', 'Mongolia');
INSERT INTO tb_country (i, name, fullName) VALUES (156, 'ME', 'Montenegro');
INSERT INTO tb_country (i, name, fullName) VALUES (157, 'MS', 'Montserrat');
INSERT INTO tb_country (i, name, fullName) VALUES (158, 'MA', 'Morocco');
INSERT INTO tb_country (i, name, fullName) VALUES (159, 'MZ', 'Mozambique');
INSERT INTO tb_country (i, name, fullName) VALUES (160, 'NA', 'Namibia');
INSERT INTO tb_country (i, name, fullName) VALUES (161, 'NR', 'Nauru');
INSERT INTO tb_country (i, name, fullName) VALUES (162, 'NP', 'Nepal');
INSERT INTO tb_country (i, name, fullName) VALUES (163, 'NL', 'Netherlands');
INSERT INTO tb_country (i, name, fullName) VALUES (164, 'NC', 'New Caledonia');
INSERT INTO tb_country (i, name, fullName) VALUES (165, 'NZ', 'New Zealand');
INSERT INTO tb_country (i, name, fullName) VALUES (166, 'NE', 'Niger');
INSERT INTO tb_country (i, name, fullName) VALUES (167, 'NG', 'Nigeria');
INSERT INTO tb_country (i, name, fullName) VALUES (168, 'NU', 'Niue');
INSERT INTO tb_country (i, name, fullName) VALUES (169, 'NF', 'Norfolk Island');
INSERT INTO tb_country (i, name, fullName) VALUES (170, 'KP', 'North Korea');
INSERT INTO tb_country (i, name, fullName) VALUES (171, 'PW', 'Palau');
INSERT INTO tb_country (i, name, fullName) VALUES (172, 'PS', 'Palestinian Territory');
INSERT INTO tb_country (i, name, fullName) VALUES (173, 'PG', 'Papua New Guinea');
INSERT INTO tb_country (i, name, fullName) VALUES (174, 'PL', 'Poland');
INSERT INTO tb_country (i, name, fullName) VALUES (175, 'PT', 'Portugal');
INSERT INTO tb_country (i, name, fullName) VALUES (176, 'PR', 'Puerto Rico');
INSERT INTO tb_country (i, name, fullName) VALUES (177, 'QA', 'Qatar');
INSERT INTO tb_country (i, name, fullName) VALUES (178, 'CG', 'Republic of the Congo');
INSERT INTO tb_country (i, name, fullName) VALUES (179, 'RE', 'Reunion');
INSERT INTO tb_country (i, name, fullName) VALUES (180, 'RO', 'Romania');
INSERT INTO tb_country (i, name, fullName) VALUES (181, 'RU', 'Russia');
INSERT INTO tb_country (i, name, fullName) VALUES (182, 'RW', 'Rwanda');
INSERT INTO tb_country (i, name, fullName) VALUES (183, 'BL', 'Saint Barthelemy');
INSERT INTO tb_country (i, name, fullName) VALUES (184, 'SH', 'Saint Helena');
INSERT INTO tb_country (i, name, fullName) VALUES (185, 'KN', 'Saint Kitts and Nevis');
INSERT INTO tb_country (i, name, fullName) VALUES (186, 'LC', 'Saint Lucia');
INSERT INTO tb_country (i, name, fullName) VALUES (187, 'MF', 'Saint Martin');
INSERT INTO tb_country (i, name, fullName) VALUES (188, 'PM', 'Saint Pierre and Miquelon');
INSERT INTO tb_country (i, name, fullName) VALUES (189, 'VC', 'Saint Vincent and the Grenadines');
INSERT INTO tb_country (i, name, fullName) VALUES (190, 'WS', 'Samoa');
INSERT INTO tb_country (i, name, fullName) VALUES (191, 'SM', 'San Marino');
INSERT INTO tb_country (i, name, fullName) VALUES (192, 'ST', 'Sao Tome and Principe');
INSERT INTO tb_country (i, name, fullName) VALUES (193, 'SA', 'Saudi Arabia');
INSERT INTO tb_country (i, name, fullName) VALUES (194, 'SN', 'Senegal');
INSERT INTO tb_country (i, name, fullName) VALUES (195, 'RS', 'Serbia');
INSERT INTO tb_country (i, name, fullName) VALUES (196, 'SC', 'Seychelles');
INSERT INTO tb_country (i, name, fullName) VALUES (197, 'SL', 'Sierra Leone');
INSERT INTO tb_country (i, name, fullName) VALUES (198, 'SX', 'Sint Maarten');
INSERT INTO tb_country (i, name, fullName) VALUES (199, 'SK', 'Slovakia');
INSERT INTO tb_country (i, name, fullName) VALUES (200, 'SI', 'Slovenia');
INSERT INTO tb_country (i, name, fullName) VALUES (201, 'SB', 'Solomon Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (202, 'SO', 'Somalia');
INSERT INTO tb_country (i, name, fullName) VALUES (203, 'ZA', 'South Africa');
INSERT INTO tb_country (i, name, fullName) VALUES (204, 'SS', 'South Sudan');
INSERT INTO tb_country (i, name, fullName) VALUES (205, 'LK', 'Sri Lanka');
INSERT INTO tb_country (i, name, fullName) VALUES (206, 'SD', 'Sudan');
INSERT INTO tb_country (i, name, fullName) VALUES (207, 'SR', 'Suriname');
INSERT INTO tb_country (i, name, fullName) VALUES (208, 'SJ', 'Svalbard');
INSERT INTO tb_country (i, name, fullName) VALUES (209, 'SZ', 'Swaziland');
INSERT INTO tb_country (i, name, fullName) VALUES (210, 'SE', 'Sweden');
INSERT INTO tb_country (i, name, fullName) VALUES (211, 'CH', 'Switzerland');
INSERT INTO tb_country (i, name, fullName) VALUES (212, 'SY', 'Syria');
INSERT INTO tb_country (i, name, fullName) VALUES (213, 'TW', 'Taiwan');
INSERT INTO tb_country (i, name, fullName) VALUES (214, 'TJ', 'Tajikistan');
INSERT INTO tb_country (i, name, fullName) VALUES (215, 'TZ', 'Tanzania');
INSERT INTO tb_country (i, name, fullName) VALUES (216, 'TH', 'Thailand');
INSERT INTO tb_country (i, name, fullName) VALUES (217, 'BS', 'The Bahamas');
INSERT INTO tb_country (i, name, fullName) VALUES (218, 'GM', 'The Gambia');
INSERT INTO tb_country (i, name, fullName) VALUES (219, 'TL', 'Timor-Leste');
INSERT INTO tb_country (i, name, fullName) VALUES (220, 'TG', 'Togo');
INSERT INTO tb_country (i, name, fullName) VALUES (221, 'TK', 'Tokelau');
INSERT INTO tb_country (i, name, fullName) VALUES (222, 'TO', 'Tonga');
INSERT INTO tb_country (i, name, fullName) VALUES (223, 'TT', 'Trinidad and Tobago');
INSERT INTO tb_country (i, name, fullName) VALUES (224, 'TN', 'Tunisia');
INSERT INTO tb_country (i, name, fullName) VALUES (225, 'TR', 'Turkey');
INSERT INTO tb_country (i, name, fullName) VALUES (226, 'TM', 'Turkmenistan');
INSERT INTO tb_country (i, name, fullName) VALUES (227, 'TC', 'Turks and Caicos Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (228, 'TV', 'Tuvalu');
INSERT INTO tb_country (i, name, fullName) VALUES (229, 'UG', 'Uganda');
INSERT INTO tb_country (i, name, fullName) VALUES (230, 'AE', 'United Arab Emirates');
INSERT INTO tb_country (i, name, fullName) VALUES (231, 'UZ', 'Uzbekistan');
INSERT INTO tb_country (i, name, fullName) VALUES (232, 'VU', 'Vanuatu');
INSERT INTO tb_country (i, name, fullName) VALUES (233, 'VA', 'Vatican City');
INSERT INTO tb_country (i, name, fullName) VALUES (234, 'VN', 'Vietnam');
INSERT INTO tb_country (i, name, fullName) VALUES (235, 'VI', 'Virgin Islands');
INSERT INTO tb_country (i, name, fullName) VALUES (236, 'WF', 'Wallis and Futuna');
INSERT INTO tb_country (i, name, fullName) VALUES (237, 'EH', 'Western Sahara');
INSERT INTO tb_country (i, name, fullName) VALUES (238, 'YE', 'Yemen');
INSERT INTO tb_country (i, name, fullName) VALUES (239, 'ZM', 'Zambia');
INSERT INTO tb_country (i, name, fullName) VALUES (240, 'ZW', 'Zimbabwe');


INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 1);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 2);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 3);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 5);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 6);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 7);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 8);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 4);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 10);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 11);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 12);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 13);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 14);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 9);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 15);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 16);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 19);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 20);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 21);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 22);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 23);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 24);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 25);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 26);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 27);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 29);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 30);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 31);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 32);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 33);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 34);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 17);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 18);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 35);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 36);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 37);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 38);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 39);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 40);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 41);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 42);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 43);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 44);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 45);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 46);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 47);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 48);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 49);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 50);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 51);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 52);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 53);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 54);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 55);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 56);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 57);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 58);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 59);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 60);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 61);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 62);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 63);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 64);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 65);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 66);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 67);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 68);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 69);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 70);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 71);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 72);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 73);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 74);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 75);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 76);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 77);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 78);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 79);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 80);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 81);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 82);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 83);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 84);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 85);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 86);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 87);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 88);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 89);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 90);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 91);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 92);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 93);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 94);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 95);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 96);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 97);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 98);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 99);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 100);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 101);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 102);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 103);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 104);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 105);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 106);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 107);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 108);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 109);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 110);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 111);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 112);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 113);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 114);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 115);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 116);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 117);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 118);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 119);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 120);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 121);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 122);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 123);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 124);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 125);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 126);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 127);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 128);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 129);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 130);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 131);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 132);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 133);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 134);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 135);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 136);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 137);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 138);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 139);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 140);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 141);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 142);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 143);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 144);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 145);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 146);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 147);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 148);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 149);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 150);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 151);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 152);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 153);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 154);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 155);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 156);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 157);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 158);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 159);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 160);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 161);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 162);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 163);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 164);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 165);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 166);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 167);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 168);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 169);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 170);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 171);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 172);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 173);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 174);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 175);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 176);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 177);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 178);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 179);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 180);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 181);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 182);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 183);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 184);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 185);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 186);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 187);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 188);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 189);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 190);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 191);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 192);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 193);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 194);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 195);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 196);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 197);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 198);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 199);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 200);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 201);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 202);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 203);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 204);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 205);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 206);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 207);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 208);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 209);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 210);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 211);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 212);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 213);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 214);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 215);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 216);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 217);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 218);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 219);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 220);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 221);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 222);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 223);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 224);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 225);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 226);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 227);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 228);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 229);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 230);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 231);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 232);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 233);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 234);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 235);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 236);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 237);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 238);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 239);
INSERT INTO tb_appVersionCountry (appVersion_id, country_id) VALUES (1, 240);

INSERT INTO tb_communities (id, name, appVersion, communityTypeID, displayName, assetName, rewriteURLParameter) VALUES (10, 'o2', 1, 10, 'O2', 'o2', 'o2');
INSERT INTO tb_communities (id, name, appVersion, communityTypeID, displayName, assetName, rewriteURLParameter) VALUES (11, 'vf_nz', 1, 11, 'Vodafone NZ', 'vf_nz', 'vf_nz');
INSERT INTO tb_communities (id, name, appVersion, communityTypeID, displayName, assetName, rewriteURLParameter) VALUES (12, 'hl_uk', 1, 12, 'HL UK', 'hl_uk', 'hl_uk');
INSERT INTO tb_communities (id, name, appVersion, communityTypeID, displayName, assetName, rewriteURLParameter) VALUES (13, 'demo', 1, 13, 'DEMO', 'demo', 'demo');

INSERT INTO tb_drmPolicy (i, name, drmType, drmValue, community) VALUES (10, 'Default Policy', 1, 100, 10);
INSERT INTO tb_drmPolicy (i, name, drmType, drmValue, community) VALUES (11, 'Default Policy', 1, 100, 11);
INSERT INTO tb_drmPolicy (i, name, drmType, drmValue, community) VALUES (12, 'Default Policy', 1, 100, 12);
INSERT INTO tb_drmPolicy (i, name, drmType, drmValue, community) VALUES (13, 'Default Policy', 1, 100, 13);

INSERT INTO tb_news (i, name, numEntries, community, timestamp) VALUES (10, 'O2 Chart', 10, 10, 1357126045);
INSERT INTO tb_news (i, name, numEntries, community, timestamp) VALUES (11, 'Vodafone NZ', 10, 11, 1383791899);
INSERT INTO tb_news (i, name, numEntries, community, timestamp) VALUES (12, 'HL UK', 10, 12, 1396864659);
INSERT INTO tb_news (i, name, numEntries, community, timestamp) VALUES (13, 'DEMO', 10, 13, 1400059607);

INSERT INTO tb_genres (i, name) VALUES (1, 'Default');
INSERT INTO tb_genres (i, name) VALUES (2, 'Metal');
INSERT INTO tb_genres (i, name) VALUES (3, 'Pop');
INSERT INTO tb_genres (i, name) VALUES (4, 'Jazz');
INSERT INTO tb_genres (i, name) VALUES (5, 'Rock');
INSERT INTO tb_genres (i, name) VALUES (6, 'Metal/Hard Rock');
INSERT INTO tb_genres (i, name) VALUES (7, 'Alternative');
INSERT INTO tb_genres (i, name) VALUES (8, 'Dance');
INSERT INTO tb_genres (i, name) VALUES (9, 'Hip Hop');
INSERT INTO tb_genres (i, name) VALUES (10, 'Hip Hop/Rap');
INSERT INTO tb_genres (i, name) VALUES (11, 'Soundtrack');
INSERT INTO tb_genres (i, name) VALUES (12, 'Alternative Rock');
INSERT INTO tb_genres (i, name) VALUES (13, 'Singer/Songwriter');
INSERT INTO tb_genres (i, name) VALUES (14, 'Rap');
INSERT INTO tb_genres (i, name) VALUES (15, 'RnB');
INSERT INTO tb_genres (i, name) VALUES (16, 'R n B');
INSERT INTO tb_genres (i, name) VALUES (17, 'Holiday');
INSERT INTO tb_genres (i, name) VALUES (18, 'R & B');
INSERT INTO tb_genres (i, name) VALUES (19, 'R  B');
INSERT INTO tb_genres (i, name) VALUES (20, 'RB');
INSERT INTO tb_genres (i, name) VALUES (21, 'Classical');
INSERT INTO tb_genres (i, name) VALUES (22, 'Soul');
INSERT INTO tb_genres (i, name) VALUES (23, 'Rap/Hip Hop');
INSERT INTO tb_genres (i, name) VALUES (24, 'Electronic');
INSERT INTO tb_genres (i, name) VALUES (25, 'Easy Listening');
INSERT INTO tb_genres (i, name) VALUES (26, 'Hard Rock');
INSERT INTO tb_genres (i, name) VALUES (27, 'Christian');
INSERT INTO tb_genres (i, name) VALUES (28, 'Alternative/Indie');
INSERT INTO tb_genres (i, name) VALUES (29, 'Folk');
INSERT INTO tb_genres (i, name) VALUES (30, 'RB/Soul');
INSERT INTO tb_genres (i, name) VALUES (31, 'Thrash Metal');
INSERT INTO tb_genres (i, name) VALUES (32, 'Urban');
INSERT INTO tb_genres (i, name) VALUES (33, 'Gospel');
INSERT INTO tb_genres (i, name) VALUES (34, 'Country');
INSERT INTO tb_genres (i, name) VALUES (35, 'Rap/Hip-Hop');
INSERT INTO tb_genres (i, name) VALUES (36, 'Reggae');
INSERT INTO tb_genres (i, name) VALUES (37, 'Classic Rock');
INSERT INTO tb_genres (i, name) VALUES (38, 'World');
INSERT INTO tb_genres (i, name) VALUES (39, 'Electronica');
INSERT INTO tb_genres (i, name) VALUES (40, 'Singer-Songwriter');
INSERT INTO tb_genres (i, name) VALUES (41, 'Miscellaneous');
INSERT INTO tb_genres (i, name) VALUES (42, 'Latin');
INSERT INTO tb_genres (i, name) VALUES (43, 'Soundtracks');
INSERT INTO tb_genres (i, name) VALUES (44, 'Fitness  Workout');
INSERT INTO tb_genres (i, name) VALUES (45, 'Punk');
INSERT INTO tb_genres (i, name) VALUES (46, 'Indie Rock');
INSERT INTO tb_genres (i, name) VALUES (47, 'Vocal');
INSERT INTO tb_genres (i, name) VALUES (48, 'Dance/House');
INSERT INTO tb_genres (i, name) VALUES (49, 'Children''s Music');
INSERT INTO tb_genres (i, name) VALUES (50, 'Adult Contemporary');
INSERT INTO tb_genres (i, name) VALUES (51, 'Variété française');
INSERT INTO tb_genres (i, name) VALUES (52, 'French Pop');
INSERT INTO tb_genres (i, name) VALUES (53, 'Broadway');
INSERT INTO tb_genres (i, name) VALUES (54, 'Techno/Electronica');
INSERT INTO tb_genres (i, name) VALUES (55, 'Latin / Pop');
INSERT INTO tb_genres (i, name) VALUES (56, 'Soccer');
INSERT INTO tb_genres (i, name) VALUES (57, 'Latin / Urban');
INSERT INTO tb_genres (i, name) VALUES (58, 'Singer Songwriter');

INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (10, 'Basic Chart', 40, 1, 1357126036, 10, 'BASIC_CHART');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (37, 'Basic Chart', 40, 1, 1383791841, 0, 'BASIC_CHART');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (41, 'FOURTH_CHART FOR HL UK', 20, 1, 1396864667, 0, 'FOURTH_CHART');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (42, 'HOT_TRACKS FOR HL UK', 10, 1, 1396864667, 0, 'HOT_TRACKS');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (43, 'FIFTH_CHART FOR HL UK', 10, 1, 1396864667, 0, 'FIFTH_CHART');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (44, 'HL_UK_PLAYLIST_1 FOR HL U', 10, 1, 1396864667, 0, 'HL_UK_PLAYLIST_1');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (45, 'HL_UK_PLAYLIST_2 FOR HL U', 10, 1, 1396864667, 0, 'HL_UK_PLAYLIST_2');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (46, 'OTHER_CHART FOR HL UK', 40, 1, 1396864667, 0, 'OTHER_CHART');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (47, 'BASIC_CHART FOR DEMO', 10, 1, 1400059618, 0, 'HOT_TRACKS');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (48, 'HOT_TRACKS FOR DEMO', 40, 1, 1400059618, 0, 'BASIC_CHART');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (49, 'FIFTH_CHART FOR DEMO', 20, 1, 1400059618, 0, 'FIFTH_CHART');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (50, 'HL_UK_PLAYLIST_1 FOR DEMO', 18, 1, 1400059618, 0, 'HL_UK_PLAYLIST_1');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (51, 'HL_UK_PLAYLIST_2 FOR DEMO', 10, 1, 1400059618, 0, 'HL_UK_PLAYLIST_2');
INSERT INTO tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) VALUES (52, 'OTHER_CHART FOR DEMO', 10, 1, 1400059618, 0, 'OTHER_CHART');

INSERT INTO tb_userGroups (id, name, community, chart, news, drmPolicy) VALUES (10, 'O2 Chart', 10, 10,    10, 10);
INSERT INTO tb_userGroups (id, name, community, chart, news, drmPolicy) VALUES (11, 'Vodafone NZ', 11, 37, 11, 11);
INSERT INTO tb_userGroups (id, name, community, chart, news, drmPolicy) VALUES (12, 'HL UK', 12, 42,       12, 12);
INSERT INTO tb_userGroups (id, name, community, chart, news, drmPolicy) VALUES (13, 'DEMO', 13, 48,        13, 13);


INSERT INTO tb_operators (i, name, migName) VALUES (1, 'Orange UK', 'MIG01OU');
INSERT INTO tb_operators (i, name, migName) VALUES (2, 'Vodafone UK', 'MIG00VU');
INSERT INTO tb_operators (i, name, migName) VALUES (3, 'O2 UK', 'MIG01XU');
INSERT INTO tb_operators (i, name, migName) VALUES (4, 'T-Mobile UK', 'MIG01TU');
INSERT INTO tb_operators (i, name, migName) VALUES (5, 'Three UK', 'MIG01HU');
INSERT INTO tb_operators (i, name, migName) VALUES (6, 'ASDA Mobile', 'MIG00VU');
INSERT INTO tb_operators (i, name, migName) VALUES (7, 'BT', 'MIG00VU');
INSERT INTO tb_operators (i, name, migName) VALUES (8, 'Giffgaff', 'MIG01XU');
INSERT INTO tb_operators (i, name, migName) VALUES (9, 'IDT Mobile', 'MIG01OU');
INSERT INTO tb_operators (i, name, migName) VALUES (11, 'TalkTalk', 'MIG00VU');
INSERT INTO tb_operators (i, name, migName) VALUES (12, 'Tesco Mobile', 'MIG01XU');
INSERT INTO tb_operators (i, name, migName) VALUES (13, 'Talk Mobile', 'MIG00VU');
INSERT INTO tb_operators (i, name, migName) VALUES (14, 'Truphone', 'MIG00VU');

INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (22, 'o2 2 Free Weeks', 334193, 0, 1357126067, 1606788000, 1, 2, 0, 10, 'PromoCode', 0, null, false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (23, 'o2 8 Free Weeks', 656321, 0, 1356342064, 1606780800, 0, 8, 0, 10, 'PromoCode', 0, null, false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (24, 'Affectd16may2weeks', 0, 0, 1368730050, 1388534400, 1, 2, 0, 10, 'PromoCode', 0, 'Affectd16may2weeks', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (25, 'staff Promotion', 0, 0, 1356342067, 1388534400, 1, 0, 0, 10, 'PromoCode', 0, 'staff', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (26, 'store Promotion', 0, 0, 1356342067, 1606780800, 1, 52, 0, 10, 'PromoCode', 0, 'store', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (27, 'CRMBH001', 0, 0, 1369324718, 1369954800, 1, 1, 0, 10, 'PromoCode', 0, 'CRMBH001', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (28, 'PROMO_INFRA_562', 0, 0, 1370942512, 1386288000, 1, 26, 0, 10, 'PromoCode', 0, 'PROMO_INFRA_562', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (29, 'PROMO_INFRA_573', 0, 0, 1371039839, 1606780800, 1, 8, 0, 10, 'PromoCode', 0, 'PROMO_INFRA_573', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (30, 'PROMO_INFRA_572', 0, 0, 1371131143, 1606780800, 1, 2, 0, 10, 'PromoCode', 0, 'PROMO_INFRA_572', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (31, 'O2 Tracks VIP FF', 0, 0, 1371462191, 1606780800, 1, 67, 0, 10, 'PromoCode', 0, 'O2 Tracks VIP FF', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (32, 'O2 TRACKS VIP SC', 0, 0, 1371462741, 1606780800, 1, 67, 0, 10, 'PromoCode', 0, 'O2 TRACKS VIP SC', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (33, 'CRM2A_1WK', 0, 0, 1377185951, 1409353200, 1, 1, 0, 10, 'PromoCode', 0, 'CRM2A_1WK', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (34, 'CRM2A_8WK', 0, 0, 1377185968, 1382024368, 1, 8, 0, 10, 'PromoCode', 0, 'CRM2A_8WK', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (35, 'o2 Video Audio Free Trial for 4G PAYM direct consumers before 2014', 11952, 0, 1377220884, 1388527200, 1, 52, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.paym.direct.till.end.of.2013', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (36, 'o2 Video Audio Free Trial for 4G PAYM direct consumers after 2013', 6318, 0, 1388527200, 2147483647, 0, 8, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.paym.direct.after.end.of.2013', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (37, 'o2 Video Audio Free Trial for 4G PAYM indirect consumers', 13177, 0, 1377220905, 2147483647, 0, 8, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.paym.indirect', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (38, 'o2 Video Audio Free Trial for 4G PAYG consumers', 222, 0, 1377220915, 2147483647, 0, 8, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.payg', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (39, 'O2 VIPs Video Free Trial', 0, 0, 1379981012, 1606788000, 1, 52, 0, 10, 'PromoCode', 0, 'O2_VIPs_VFT', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (40, 'O2 13 Weeks Video Free Trial', 0, 0, 1379981023, 1606788000, 1, 13, 0, 10, 'PromoCode', 0, 'O2_13_Weeks_VFT', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (41, 'Project Mario Non-O2 iOS lapsed user 2 wk free trial', 0, 0, 1382819972, 1384041600, 1, 2, 0, 10, 'PromoCode', 0, 'Mario_iOS_oot', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (42, 'TwoWeeksOnSubscription', 1453, 0, 1382137200, 1384128000, 1, 2, 0, 10, 'PromoCode', 0, 'TwoWeeksOnSubscription', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (43, 'Vodafone NZ 2 weeks', 3516, 0, 1383792104, 1606788000, 1, 2, 0, 11, 'PromoCode', 0, 'vf_nz_promo2', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (44, 'Vodafone NZ 4 weeks', 18502, 0, 1383792116, 1606788000, 1, 4, 0, 11, 'PromoCode', 0, 'vf_nz_promo4', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (45, 'Vodafone NZ Android 1 week', 0, 0, 1385477081, 1386028800, 1, 1, 0, 11, 'PromoCode', 0, 'VFNZAndroid1Week', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (46, 'Vodafone NZ Android Ext 1 week', 0, 0, 1385560096, 1386633600, 1, 1, 0, 11, 'PromoCode', 0, 'VFNZAndroidExt1Week', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (47, 'O2 Christmas Promotion', 0, 0, 1387375262, 1388102400, 1, 1, 0, 10, 'PromoCode', 0, 'o2_xmas_promo', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (48, 'VFNZ12MonthVIP', 0, 0, 1390396407, 1421020800, 1, 52, 0, 11, 'PromoCode', 0, 'VFNZ12MonthVIP', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (53, 'O2promo4weeksAudio', 32766, 0, 1356342064, 1606780800, 1, 4, 0, 10, 'PromoCode', 0, 'o2.promo.4weeks.audio', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (54, 'O2promo4weeksVideo PAYM direct', 11552, 0, 1388527200, 2147483647, 1, 4, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.paym.direct.4weeks.video', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (55, 'O2promo4weeksVideo PAYM indirect', 6006, 0, 1377220905, 2147483647, 1, 4, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.paym.indirect.4weeks.video', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (56, 'O2promo4weeksVideo PAYG', 141, 0, 1377220915, 2147483647, 1, 4, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.payg.4weeks.video', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (57, 'O2 Kylie', 0, 0, 1394722433, 1395619200, 1, 1, 0, 10, 'PromoCode', 0, 'o2.crm.audio.kylie', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (58, 'HL_UKPromo2weeksAudio', 106, 0, 1396864691, 1606788000, 1, 4, 0, 12, 'PromoCode', 0, 'hl_uk.promo.2weeks.audio', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (59, 'O2CommComp3months', 0, 0, 1397516400, 1418601600, 0, 13, 0, 10, 'PromoCode', 0, 'o2.comp3months', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (60, 'o2Campaign3G', 28, 0, 1356342064, 1606780800, 1, 1, 0, 10, 'PromoCode', 0, 'o2Campaign3G', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (61, 'o2Campaign4G', 4, 0, 1356342064, 1606780800, 1, 1, 0, 10, 'PromoCode', 0, 'o2Campaign4G', false);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (62, 'GO1121 52-week video subscription to additional users', 0, 0, 1399548721, 1420070400, 1, 52, 0, 10, 'PromoCode', 0, 'GO1121', true);
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES (63, 'DEMOPromo26weeksAudio', 13, 0, 1400059643, 1606788000, 1, 26, 0, 13, 'PromoCode', 0, 'demo.promo.26weeks.audio', false);


INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (20, 'promo2', 22, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (21, 'promo8', 23, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (22, 'Affectd16may2weeks', 24, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (23, 'staff', 25, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (24, 'store', 26, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (25, 'CRMBH001', 27, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (26, 'PROMO_INFRA_562', 28, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (27, 'PROMO_INFRA_573', 29, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (28, 'PROMO_INFRA_572', 30, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (29, 'O2 Tracks VIP FF', 31, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (30, 'O2 TRACKS VIP SC', 32, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (31, 'CRM2A_1WK', 33, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (32, 'CRM2A_8WK', 34, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (33, 'o2.consumer.4g.paym.direct', 35, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (34, 'o2.consumer.4g.paym.direct', 36, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (35, 'o2.consumer.4g.paym.indirect', 37, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (36, 'o2.consumer.4g.payg', 38, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (37, 'O2_VIPs_VFT', 39, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (38, 'O2_13_Weeks_VFT', 40, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (39, 'Mario_iOS_oot', 41, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (41, 'TwoWeeksOnSubscription', 42, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (42, 'vf_nz_promo2', 43, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (43, 'vf_nz_promo4', 44, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (44, 'VFNZAndroid1Week', 45, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (45, 'VFNZAndroidExt1Week', 46, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (46, 'o2_xmas_promo', 47, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (47, 'VFNZ12MonthVIP', 48, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (48, 'o2.promo.4weeks.audio', 53, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (49, 'o2.consumer.4g.paym.direct.4weeks.video', 54, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (50, 'o2.consumer.4g.paym.indirect.4weeks.video', 55, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (51, 'o2.consumer.4g.payg.4weeks.video', 56, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (52, 'o2.crm.audio.kylie', 57, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (53, 'hl_uk.promo.2weeks.audio', 58, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (54, 'o2.comp3months', 59, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (55, 'o2Campaign3G', 60, 'AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (56, 'o2Campaign4G', 61, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (57, 'GO1121', 62, 'VIDEO_AND_AUDIO');
INSERT INTO tb_promoCode (id, code, promotionId, media_type) VALUES (58, 'demo.promo.26weeks.audio', 63, 'AUDIO');


INSERT INTO qrtz_locks (LOCK_NAME) VALUES ('CALENDAR_ACCESS');
INSERT INTO qrtz_locks (LOCK_NAME) VALUES ('JOB_ACCESS');
INSERT INTO qrtz_locks (LOCK_NAME) VALUES ('MISFIRE_ACCESS');
INSERT INTO qrtz_locks (LOCK_NAME) VALUES ('STATE_ACCESS');
INSERT INTO qrtz_locks (LOCK_NAME) VALUES ('TRIGGER_ACCESS');

INSERT INTO qrtz_job_details (JOB_NAME, JOB_GROUP, DESCRIPTION, JOB_CLASS_NAME, IS_DURABLE, IS_VOLATILE, IS_STATEFUL, REQUESTS_RECOVERY, JOB_DATA) VALUES ('job.ForkO2UsersForUpdateJob', 'DEFAULT', null, 'mobi.nowtechnologies.server.job.ForkO2UsersForUpdateJob', '0', '0', '1', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787000737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C770800000010000000007800);
INSERT INTO qrtz_job_details (JOB_NAME, JOB_GROUP, DESCRIPTION, JOB_CLASS_NAME, IS_DURABLE, IS_VOLATILE, IS_STATEFUL, REQUESTS_RECOVERY, JOB_DATA) VALUES ('job.updateO2User', 'DEFAULT', null, 'mobi.nowtechnologies.server.job.UpdateO2UserJob', '0', '0', '1', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787000737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C770800000010000000007800);

INSERT INTO qrtz_triggers (TRIGGER_NAME, TRIGGER_GROUP, JOB_NAME, JOB_GROUP, IS_VOLATILE, DESCRIPTION, NEXT_FIRE_TIME, PREV_FIRE_TIME, PRIORITY, TRIGGER_STATE, TRIGGER_TYPE, START_TIME, END_TIME, CALENDAR_NAME, MISFIRE_INSTR, JOB_DATA) VALUES ('jobTrigger.ForkO2UsersForUpdateJob', 'DEFAULT', 'job.ForkO2UsersForUpdateJob', 'DEFAULT', '0', null, 1402843148000, -1, 5, 'WAITING', 'CRON', 1402843148000, 0, null, 0, null);
INSERT INTO qrtz_triggers (TRIGGER_NAME, TRIGGER_GROUP, JOB_NAME, JOB_GROUP, IS_VOLATILE, DESCRIPTION, NEXT_FIRE_TIME, PREV_FIRE_TIME, PRIORITY, TRIGGER_STATE, TRIGGER_TYPE, START_TIME, END_TIME, CALENDAR_NAME, MISFIRE_INSTR, JOB_DATA) VALUES ('jobTrigger.updateO2User', 'DEFAULT', 'job.updateO2User', 'DEFAULT', '0', null, 1402843208041, -1, 5, 'WAITING', 'SIMPLE', 1402843208041, 0, null, 0, null);

INSERT INTO qrtz_simple_triggers (TRIGGER_NAME, TRIGGER_GROUP, REPEAT_COUNT, REPEAT_INTERVAL, TIMES_TRIGGERED) VALUES ('jobTrigger.updateO2User', 'DEFAULT', -1, 60000, 0);

INSERT INTO qrtz_cron_triggers (TRIGGER_NAME, TRIGGER_GROUP, CRON_EXPRESSION, TIME_ZONE_ID) VALUES ('jobTrigger.ForkO2UsersForUpdateJob', 'DEFAULT', '0/1 * * * * ?', 'Europe/Helsinki');

INSERT INTO tb_paymentpolicy
(communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyISO,availableInStore,app_store_product_id,contract,segment   ,content_category,content_type          ,content_description     ,sub_merchant_id,provider,tariff,media_type       ,is_default,advanced_payment_seconds,after_next_sub_payment_seconds,online, start_date_time     , end_date_time        , duration, duration_unit) values
(10         ,2       ,'2'    ,'o2Psms'   ,NULL    ,'3107055','GBP'      ,1               ,NULL                ,'PAYG'  ,'CONSUMER','other'         ,'mqbed_tracks_3107055','Description of content','O2 Tracks'    ,'O2'    ,'_3G' ,'AUDIO'          ,1         ,86400                   ,172800                        ,1     ,'1970-01-01 00:00:01', '9999-12-31 23:59:59',2        ,'WEEKS'),
(10         ,2       ,'2'    ,'o2Psms'   ,NULL    ,'3107055','GBP'      ,1               ,NULL                ,'PAYM'  ,'CONSUMER','other'         ,'mqbed_tracks_3107055','Description of content','O2 Tracks'    ,'O2'    ,'_3G' ,'AUDIO'          ,1         ,86400                   ,0                             ,1     ,'1970-01-01 00:00:01', '9999-12-31 23:59:59',2        ,'WEEKS'),
(10         ,1       ,'1.5'  ,'o2Psms'   ,NULL    ,'3107057','GBP'      ,1               ,NULL                ,'PAYG'  ,'CONSUMER','other'         ,'mqbed_tracks_3107057','Description of content','O2 Tracks'    ,'O2'    ,'_4G' ,'VIDEO_AND_AUDIO',1         ,86400                   ,172800                        ,1     ,'1970-01-01 00:00:01', '9999-12-31 23:59:59',1        ,'WEEKS'),
(10         ,1       ,'1.5'  ,'o2Psms'   ,NULL    ,'3107057','GBP'      ,1               ,NULL                ,'PAYM'  ,'CONSUMER','other'         ,'mqbed_tracks_3107057','Description of content','O2 Tracks'    ,'O2'    ,'_4G' ,'VIDEO_AND_AUDIO',1         ,86400                   ,0                             ,1     ,'1970-01-01 00:00:01', '9999-12-31 23:59:59',1        ,'WEEKS');

--
-- Medias and Charts
--
insert into tb_charts (i, name, numTracks, genre, timestamp, numBonusTracks, type) values (1,'Default Chart',20,1,1307035342,0, 'BASIC_CHART');

-- Tracks:
INSERT INTO `cn_cms`.`Track`
(`id`,`Ingestor`,`ISRC`,`Title`,`Artist`,`ProductId`,`ProductCode`,`Genre`,`Copyright`,`Year`,`Album`,`IngestionDate`,`PublishDate`,`Licensed`,`status`,`resolution`,`itunesUrl`,`explicit`,`label`,`releaseDate`,`territoryCodes`,`mediaType`,`amazonUrl`)
VALUES
(0,'UNIVERSAL','GB0000000000','Title 0','Artist 0','ID0000000','PC0000000','Pop','(C) 2014 Sony','2014','Album 0','2014-11-00','2014-11-10',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000000?i=0000000000&uo=4',0,'Columbia','2014-10-20','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000000#tGB0000000000?partner=3734'),
(1,'UNIVERSAL','GB0000000001','Title 1','Artist 1','ID0000001','PC0000001','Pop','(C) 2014 Sony','2014','Album 1','2014-11-01','2014-11-11',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000001?i=0000000001&uo=4',0,'Columbia','2014-10-21','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000001#tGB0000000001?partner=3734'),
(2,'UNIVERSAL','GB0000000002','Title 2','Artist 2','ID0000002','PC0000002','Pop','(C) 2014 Sony','2014','Album 2','2014-11-02','2014-11-12',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000002?i=0000000002&uo=4',0,'Columbia','2014-10-22','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000002#tGB0000000002?partner=3734'),
(3,'UNIVERSAL','GB0000000003','Title 3','Artist 3','ID0000003','PC0000003','Pop','(C) 2014 Sony','2014','Album 3','2014-11-03','2014-11-13',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000003?i=0000000003&uo=4',0,'Columbia','2014-10-23','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000003#tGB0000000003?partner=3734'),
(4,'UNIVERSAL','GB0000000004','Title 4','Artist 4','ID0000004','PC0000004','Pop','(C) 2014 Sony','2014','Album 4','2014-11-04','2014-11-14',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000004?i=0000000004&uo=4',0,'Columbia','2014-10-24','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000004#tGB0000000004?partner=3734'),
(5,'UNIVERSAL','GB0000000005','Title 5','Artist 5','ID0000005','PC0000005','Pop','(C) 2014 Sony','2014','Album 5','2014-11-05','2014-11-15',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000005?i=0000000005&uo=4',0,'Columbia','2014-10-25','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000005#tGB0000000005?partner=3734'),
(6,'UNIVERSAL','GB0000000006','Title 6','Artist 6','ID0000006','PC0000006','Pop','(C) 2014 Sony','2014','Album 6','2014-11-06','2014-11-16',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000006?i=0000000006&uo=4',0,'Columbia','2014-10-26','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000006#tGB0000000006?partner=3734'),
(7,'UNIVERSAL','GB0000000007','Title 7','Artist 7','ID0000007','PC0000007','Pop','(C) 2014 Sony','2014','Album 7','2014-11-07','2014-11-17',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000007?i=0000000007&uo=4',0,'Columbia','2014-10-27','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000007#tGB0000000007?partner=3734'),
(8,'UNIVERSAL','GB0000000008','Title 8','Artist 8','ID0000008','PC0000008','Pop','(C) 2014 Sony','2014','Album 8','2014-11-08','2014-11-18',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000008?i=0000000008&uo=4',0,'Columbia','2014-10-28','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000008#tGB0000000008?partner=3734'),
(9,'UNIVERSAL','GB0000000009','Title 9','Artist 9','ID0000009','PC0000009','Pop','(C) 2014 Sony','2014','Album 9','2014-11-09','2014-11-19',1,'PUBLISHED','RATE_48','https://itunes.apple.com/gb/album/album0/GB0000000009?i=0000000009&uo=4',0,'Columbia','2014-10-29','Worldwide','DOWNLOAD','https://m.7digital.com/GB/releases/GB0000000009#tGB0000000009?partner=3734');

use `cn_service_at`;

-- fileTypes
INSERT INTO `tb_fileTypes`
(`i`,`name`)
VALUES
(1,'Header'),
(2,'Audio'),
(3,'Image'),
(4,'VIDEO');

/* FILES */
-- audioFiles
INSERT INTO `tb_files`
(`i`,`filename`,`size`,`fileType`,`version`,`duration`)
VALUES
(0,'GB0000000000_48.aud','2000000',2,0,0),
(1,'GB0000000001_48.aud','2000001',2,0,0),
(2,'GB0000000002_48.aud','2000002',2,0,0),
(3,'GB0000000003_48.aud','2000003',2,0,0),
(4,'GB0000000004_48.aud','2000004',2,0,0),
(5,'GB0000000005_48.aud','2000005',2,0,0),
(6,'GB0000000006_48.aud','2000006',2,0,0),
(7,'GB0000000007_48.aud','2000007',2,0,0),
(8,'GB0000000008_48.aud','2000008',2,0,0),
(9,'GB0000000009_48.aud','2000009',2,0,0);
-- headerFiles
INSERT INTO `tb_files`
(`i`,`filename`,`size`,`fileType`,`version`,`duration`)
VALUES
(10,'GB0000000000_48.hdr','1000000',1,0,0),
(11,'GB0000000001_48.hdr','1000001',1,0,0),
(12,'GB0000000002_48.hdr','1000002',1,0,0),
(13,'GB0000000003_48.hdr','1000003',1,0,0),
(14,'GB0000000004_48.hdr','1000004',1,0,0),
(15,'GB0000000005_48.hdr','1000005',1,0,0),
(16,'GB0000000006_48.hdr','1000006',1,0,0),
(17,'GB0000000007_48.hdr','1000007',1,0,0),
(18,'GB0000000008_48.hdr','1000008',1,0,0),
(19,'GB0000000009_48.hdr','1000009',1,0,0);
-- imageFileSmall
INSERT INTO `tb_files`
(`i`,`filename`,`size`,`fileType`,`version`,`duration`)
VALUES
(20,'GB0000000000S.jpg','3000000',3,0,0),
(21,'GB0000000001S.jpg','3000001',3,0,0),
(22,'GB0000000002S.jpg','3000002',3,0,0),
(23,'GB0000000003S.jpg','3000003',3,0,0),
(24,'GB0000000004S.jpg','3000004',3,0,0),
(25,'GB0000000005S.jpg','3000005',3,0,0),
(26,'GB0000000006S.jpg','3000006',3,0,0),
(27,'GB0000000007S.jpg','3000007',3,0,0),
(28,'GB0000000008S.jpg','3000008',3,0,0),
(29,'GB0000000009S.jpg','3000009',3,0,0);
-- imageFileLarge
INSERT INTO `tb_files`
(`i`,`filename`,`size`,`fileType`,`version`,`duration`)
VALUES
(30,'GB0000000000L.jpg','3000000',3,0,0),
(31,'GB0000000001L.jpg','3000001',3,0,0),
(32,'GB0000000002L.jpg','3000002',3,0,0),
(33,'GB0000000003L.jpg','3000003',3,0,0),
(34,'GB0000000004L.jpg','3000004',3,0,0),
(35,'GB0000000005L.jpg','3000005',3,0,0),
(36,'GB0000000006L.jpg','3000006',3,0,0),
(37,'GB0000000007L.jpg','3000007',3,0,0),
(38,'GB0000000008L.jpg','3000008',3,0,0),
(39,'GB0000000009L.jpg','3000009',3,0,0);
-- imgFileResolution
INSERT INTO `tb_files`
(`i`,`filename`,`size`,`fileType`,`version`,`duration`)
VALUES
(40,'GB0000000000.jpg','0',3,0,0),
(41,'GB0000000001.jpg','0',3,0,0),
(42,'GB0000000002.jpg','0',3,0,0),
(43,'GB0000000003.jpg','0',3,0,0),
(44,'GB0000000004.jpg','0',3,0,0),
(45,'GB0000000005.jpg','0',3,0,0),
(46,'GB0000000006.jpg','0',3,0,0),
(47,'GB0000000007.jpg','0',3,0,0),
(48,'GB0000000008.jpg','0',3,0,0),
(49,'GB0000000009.jpg','0',3,0,0);
-- purchasedFile
INSERT INTO `tb_files`
(`i`,`filename`,`size`,`fileType`,`version`,`duration`)
VALUES
(50,'GB0000000000.mp3','1000000',1,0,0),
(51,'GB0000000001.mp3','1000001',1,0,0),
(52,'GB0000000002.mp3','1000002',1,0,0),
(53,'GB0000000003.mp3','1000003',1,0,0),
(54,'GB0000000004.mp3','1000004',1,0,0),
(55,'GB0000000005.mp3','1000005',1,0,0),
(56,'GB0000000006.mp3','1000006',1,0,0),
(57,'GB0000000007.mp3','1000007',1,0,0),
(58,'GB0000000008.mp3','1000008',1,0,0),
(59,'GB0000000009.mp3','1000009',1,0,0);
-- audioPreviewFile
INSERT INTO `tb_files`
(`i`,`filename`,`size`,`fileType`,`version`,`duration`)
VALUES
(60,'GB0000000000P.aud','2000000',2,0,0),
(61,'GB0000000001P.aud','2000001',2,0,0),
(62,'GB0000000002P.aud','2000002',2,0,0),
(63,'GB0000000003P.aud','2000003',2,0,0),
(64,'GB0000000004P.aud','2000004',2,0,0),
(65,'GB0000000005P.aud','2000005',2,0,0),
(66,'GB0000000006P.aud','2000006',2,0,0),
(67,'GB0000000007P.aud','2000007',2,0,0),
(68,'GB0000000008P.aud','2000008',2,0,0),
(69,'GB0000000009P.aud','2000009',2,0,0);
-- headerPreviewFile
INSERT INTO `tb_files`
(`i`,`filename`,`size`,`fileType`,`version`,`duration`)
VALUES
(70,'GB0000000000P.hdr','1000000',1,0,0),
(71,'GB0000000001P.hdr','1000001',1,0,0),
(72,'GB0000000002P.hdr','1000002',1,0,0),
(73,'GB0000000003P.hdr','1000003',1,0,0),
(74,'GB0000000004P.hdr','1000004',1,0,0),
(75,'GB0000000005P.hdr','1000005',1,0,0),
(76,'GB0000000006P.hdr','1000006',1,0,0),
(77,'GB0000000007P.hdr','1000007',1,0,0),
(78,'GB0000000008P.hdr','1000008',1,0,0),
(79,'GB0000000009P.hdr','1000009',1,0,0);

-- Artist
INSERT INTO `tb_artist`
(`i`,`name`,`info`,`realName`)
VALUES
(0,'Artist 0','Info about artist 0','Artist 0'),
(1,'Artist 1','Info about artist 1','Artist 1'),
(2,'Artist 2','Info about artist 2','Artist 2'),
(3,'Artist 3','Info about artist 3','Artist 3'),
(4,'Artist 4','Info about artist 4','Artist 4'),
(5,'Artist 5','Info about artist 5','Artist 5'),
(6,'Artist 6','Info about artist 6','Artist 6'),
(7,'Artist 7','Info about artist 7','Artist 7'),
(8,'Artist 8','Info about artist 8','Artist 8'),
(9,'Artist 9','Info about artist 9','Artist 9');

-- Labels
INSERT INTO `tb_labels`
(`i`,`name`)
VALUES
(1001,'Label 1001');

-- Media
INSERT INTO `tb_media`
(`i`,`isrc`,`title`,`artist`,`audioFile`,`headerFile`,`imageFileSmall`,`imageFIleLarge`,`info`,`genre`,`imgFileResolution`,`purchasedFile`,`audioPreviewFile`,`headerPreviewFile`,`iTunesUrl`,`publishDate`,`trackId`,`amazonUrl`,`areArtistUrls`,`label`)
VALUES
(0,'GB0000000000','Title 0',0,0,10,20,30,'Info 0',3,40,50,60,70,'https://itunes.apple.com/gb/album/album0/GB0000000000?i=0000000000&uo=4',1416528000,0,'https://m.7digital.com/GB/releases/GB0000000000#tGB0000000000?partner=3734',0,1001),
(1,'GB0000000001','Title 1',1,1,11,21,31,'Info 1',3,41,51,61,71,'https://itunes.apple.com/gb/album/album1/GB0000000001?i=0000000001&uo=4',1416528000,1,'https://m.7digital.com/GB/releases/GB0000000001#tGB0000000001?partner=3734',0,1001),
(2,'GB0000000002','Title 2',2,2,12,22,32,'Info 2',3,42,52,62,72,'https://itunes.apple.com/gb/album/album2/GB0000000002?i=0000000002&uo=4',1416528000,2,'https://m.7digital.com/GB/releases/GB0000000002#tGB0000000002?partner=3734',0,1001),
(3,'GB0000000003','Title 3',3,3,13,23,33,'Info 3',3,43,53,63,73,'https://itunes.apple.com/gb/album/album3/GB0000000003?i=0000000003&uo=4',1416528000,3,'https://m.7digital.com/GB/releases/GB0000000003#tGB0000000003?partner=3734',0,1001),
(4,'GB0000000004','Title 4',4,4,14,24,34,'Info 4',3,44,54,64,74,'https://itunes.apple.com/gb/album/album4/GB0000000004?i=0000000004&uo=4',1416528000,4,'https://m.7digital.com/GB/releases/GB0000000004#tGB0000000004?partner=3734',0,1001),
(5,'GB0000000005','Title 5',5,5,15,25,35,'Info 5',3,45,55,65,75,'https://itunes.apple.com/gb/album/album5/GB0000000005?i=0000000005&uo=4',1416528000,5,'https://m.7digital.com/GB/releases/GB0000000005#tGB0000000005?partner=3734',0,1001),
(6,'GB0000000006','Title 6',6,6,16,26,36,'Info 6',3,46,56,66,76,'https://itunes.apple.com/gb/album/album6/GB0000000006?i=0000000006&uo=4',1416528000,6,'https://m.7digital.com/GB/releases/GB0000000006#tGB0000000006?partner=3734',0,1001),
(7,'GB0000000007','Title 7',7,7,17,27,37,'Info 7',3,47,57,67,77,'https://itunes.apple.com/gb/album/album7/GB0000000007?i=0000000007&uo=4',1416528000,7,'https://m.7digital.com/GB/releases/GB0000000007#tGB0000000007?partner=3734',0,1001),
(8,'GB0000000008','Title 8',8,8,18,28,38,'Info 8',3,48,58,68,78,'https://itunes.apple.com/gb/album/album8/GB0000000008?i=0000000008&uo=4',1416528000,8,'https://m.7digital.com/GB/releases/GB0000000008#tGB0000000008?partner=3734',0,1001),
(9,'GB0000000009','Title 9',9,9,19,29,39,'Info 9',3,49,59,69,79,'https://itunes.apple.com/gb/album/album9/GB0000000009?i=0000000009&uo=4',1416528000,9,'https://m.7digital.com/GB/releases/GB0000000009#tGB0000000009?partner=3734',0,1001);
