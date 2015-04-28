/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Country;

import javax.annotation.Resource;

import org.junit.*;
import static org.junit.Assert.assertNotNull;

public class CountryRepositoryTest extends AbstractRepositoryIT {
    @Resource
    CountryRepository countryRepository;

    @Test
    public void testFindByName() throws Exception {
        String name = "GB";

        Country c = countryRepository.findByName(name);
        assertNotNull(c);
    }
}