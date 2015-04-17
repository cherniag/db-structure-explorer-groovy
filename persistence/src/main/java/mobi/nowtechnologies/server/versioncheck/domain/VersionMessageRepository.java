/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.versioncheck.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionMessageRepository extends JpaRepository<VersionMessage, Long> {

}
