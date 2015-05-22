
/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.MediaLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaLogRepository extends JpaRepository<MediaLog, Integer> {
}
