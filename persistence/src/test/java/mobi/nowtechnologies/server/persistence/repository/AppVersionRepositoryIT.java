/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AppVersion;
import mobi.nowtechnologies.server.persistence.domain.Country;

import javax.annotation.Resource;

import java.util.HashSet;

import com.google.common.collect.Sets;

import org.junit.*;
import static org.junit.Assert.*;


public class AppVersionRepositoryIT extends AbstractRepositoryIT {

    @Resource
    AppVersionRepository appVersionRepository;

    @Resource
    CountryRepository countryRepository;

    @Test
    public void testGetByCommunityName(){
        Country country = new Country("UK", "United Kingdom");
        countryRepository.save(country);

        appVersionRepository.save(getAppVersion("1.1", Sets.newHashSet(country)));

        long versions = appVersionRepository.countAppVersionLinkedWithCountry("1.1", "UK");
        assertEquals(1L, versions);

        appVersionRepository.save(getAppVersion("1.1", Sets.newHashSet(country)));

        versions = appVersionRepository.countAppVersionLinkedWithCountry("1.1", "UK");
        assertEquals(2L, versions);
    }

    private AppVersion getAppVersion(String version, HashSet<Country> countries) {
        AppVersion appVersion = new AppVersion();
        appVersion.setName(version);
        appVersion.setCountries(countries);
        return appVersion;
    }
}

