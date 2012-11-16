package mobi.nowtechnologies.server.persistence.domain.filter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilter;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.NewsDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset;

@Entity
@DiscriminatorValue("UserHandsetFilter")
public class UserHandsetFilter extends AbstractFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserHandsetFilter.class);

	@Override
	public boolean doFilter(User user, Object param) {
		LOGGER.debug("input parameters user, param: [{}], [{}]", new Object[]{user, param});
		
		NewsDetail newsDetail = (NewsDetail) param;

		UserHandset userHandset = newsDetail.getUserHandset();

		final DeviceType deviceType = user.getDeviceType();

		boolean filtrate = false;

		switch (userHandset) {
		case ANDROID:
			if (deviceType.equals(DeviceTypeDao.getAndroidDeviceType()))
				filtrate = true;
			break;
		case BLACKBERRY:
			if (deviceType.equals(DeviceTypeDao.getBlackberryDeviceType()))
				filtrate = true;
			break;
		case IOS:
			if (deviceType.equals(DeviceTypeDao.getIOSDeviceType()))
				filtrate = true;
			break;
		case J2ME:
			if (deviceType.equals(DeviceTypeDao.getJ2meDeviceType()))
				filtrate = true;
			break;
		default:
			throw new PersistenceException("Unknown user handset ["+userHandset+"]");
		}

		LOGGER.debug("Output parameter filtrate=[{}]", filtrate);
		return filtrate;
	}
}
