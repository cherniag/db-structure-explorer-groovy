package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Contract;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
//@Entity
//@Table(name = "grace_period", uniqueConstraints = @UniqueConstraint(columnNames = { "segment", "contract", "community_id", "provider" }))
//@Table(name = "grace_period", uniqueConstraints = @UniqueConstraint(columnNames = { "segment", "contract", "user_group_id", "provider" }))
public class GracePeriod implements Serializable{
	
	private static final long serialVersionUID = 3156970365842675613L;

	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	//@Enumerated(EnumType.STRING)
	//@Column(columnDefinition = "char(255)")
	private SegmentType segment;

	//@Enumerated(EnumType.STRING)
	//@Column(columnDefinition = "char(255)")
	private Contract contract;

	//@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "community_id", nullable = false)
	//private Community community;

	//@Column(name = "community_id", insertable = false, updatable = false)
	//private byte communityId;
	
	//@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "user_group_id", nullable = false)
	private UserGroup userGroup;
	
	//@Column(name = "user_group_id", insertable = false, updatable = false)
	private byte userGroupId;

	private String provider;

	//@Column(name = "duration_millis", nullable = false)
	private long durationMillis;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SegmentType getSegment() {
		return segment;
	}

	public void setSegment(SegmentType segment) {
		this.segment = segment;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public long getDurationMillis() {
		return durationMillis;
	}

	public void setDurationMillis(long durationMillis) {
		this.durationMillis = durationMillis;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
		if(userGroup!=null){
			userGroupId =userGroup.getI();
		}
	}
	
/*	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
		if (community != null) {
			communityId = community.getId();
		}
	}
*/
	@Override
	public String toString() {
		return "GracePeriod [id=" + id + ", userGroupId=" + userGroupId + ", provider=" + provider + ", segment=" + segment + ", contract=" + contract
				+ ", durationMillis=" + durationMillis + "]";
	}

//	@Override
//	public String toString() {
//		return "GracePeriod [id=" + id + ", communityId=" + communityId + ", contract=" + contract + ", provider=" + provider + ", segment=" + segment
//				+ ", durationMillis=" + durationMillis + "]";
//	}

}
