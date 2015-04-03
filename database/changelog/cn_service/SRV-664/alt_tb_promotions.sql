alter table tb_promotions
  add column duration int not null default 0 after maxUsers,
  add column duration_unit VARCHAR(255) not null default 'WEEKS' after duration;