/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Titov Mykhaylo (titov)
 */
@Entity
@Table(name = "promoted_devices")
public class PromotedDevice extends DevicePromotion {

}
