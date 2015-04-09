CREATE TABLE event_log(
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  event_log_type VARCHAR(100) NOT NULL,
  data TEXT,
  create_timestamp BIGINT(13) NOT NULL
);
