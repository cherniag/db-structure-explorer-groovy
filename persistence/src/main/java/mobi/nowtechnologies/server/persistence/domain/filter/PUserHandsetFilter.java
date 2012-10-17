package mobi.nowtechnologies.server.persistence.domain.filter;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilter;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("PromotionUserHandsetFilter")
public class PUserHandsetFilter extends AbstractFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PUserHandsetFilter.class);
	
	@ElementCollection(targetClass=UserHandset.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="tb_filter_params")
	private List<UserHandset> userHandset;
	
	@Override
	public boolean doFilter(User user, Object param) {
		
		final DeviceType deviceType = user.getDeviceType();
		return userHandset.contains(UserHandset.valueOf(deviceType.getName()));
	}

	public List<UserHandset> getUserHandset() {
		return userHandset;
	}

	public void setUserHandset(List<UserHandset> userHandset) {
		this.userHandset = userHandset;
	}
}