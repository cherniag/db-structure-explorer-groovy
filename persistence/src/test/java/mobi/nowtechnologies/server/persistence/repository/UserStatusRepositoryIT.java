/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;

import javax.annotation.Resource;

import org.junit.*;
import static org.junit.Assert.*;


public class UserStatusRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private UserStatusRepository userStatusRepository;

    @Test
    public void testFindByName() {
        UserStatus userStatus = userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name());

        assertNotNull(userStatus);
        assertEquals(UserStatusType.SUBSCRIBED.name(), userStatus.getName());
    }

}