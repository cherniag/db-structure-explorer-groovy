/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AppVersion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AppVersionRepository extends JpaRepository<AppVersion, Byte> {

    @Query("select count(appVersion) from AppVersion appVersion " +
           "join appVersion.countries country " +
           "where country.name = :countryCode " +
           "and appVersion.name = :appVersion")
    Long countAppVersionLinkedWithCountry(@Param("appVersion") String appVersion, @Param("countryCode") String countryCode);

}
