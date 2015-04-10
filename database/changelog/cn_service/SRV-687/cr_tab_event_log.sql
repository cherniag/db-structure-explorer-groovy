CREATE TABLE event_log(
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  traceId VARCHAR(60) NOT NULL,
  type VARCHAR(100) NOT NULL,
  data TEXT,
  create_timestamp BIGINT(13) NOT NULL
);
