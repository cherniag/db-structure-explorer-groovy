/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import javax.annotation.Resource;

import org.junit.*;
import static org.junit.Assert.*;


public class AppVersionRepositoryIT extends AbstractRepositoryIT {

    @Resource
    AppVersionRepository appVersionRepository;

    @Test
    public void testGetByCommunityName(){
        long versions = appVersionRepository.countAppVersionLinkedWithCountry("CBEMA", "UA");
        assertEquals(1L, versions);
    }

}

