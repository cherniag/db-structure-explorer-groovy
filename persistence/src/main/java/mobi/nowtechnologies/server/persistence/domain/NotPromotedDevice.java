package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "not_promoted_devices")
public class NotPromotedDevice extends Device {}