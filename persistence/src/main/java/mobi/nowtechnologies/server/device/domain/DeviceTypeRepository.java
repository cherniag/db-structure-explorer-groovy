/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.device.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @deprecated todo: remove this along with {@link DeviceTypeDao} when possible.
 */
@Deprecated
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Byte> {

}

