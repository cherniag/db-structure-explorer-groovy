package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.NewsDetailDao;
import mobi.nowtechnologies.server.persistence.domain.NewsDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.dto.NewsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Deprecated
public class NewsDetailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsDetailService.class);

	private NewsDetailDao newsDetailDao;
	private EntityService entityService;
	private UserService userService;

	public void setNewsDetailDao(NewsDetailDao newsDetailDao) {
		this.newsDetailDao = newsDetailDao;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Object[] processGetNewsCommand(User user, String communityName) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");
		
		LOGGER.debug("input parameters user, communityName: [{}], [{}]", user, communityName);
		
		int userId = user.getId();
		user = userService.findUserTree(userId);
		
		AccountCheckDTO accountCheck = user.toAccountCheckDTO(null, null, userService.getGraceDurationSeconds(user)); 
		byte newsId = user.getUserGroup().getNewsId();

		NewsDto newsDto = getNews(user, newsId);
		Object[] objects = new Object[] { accountCheck, newsDto };
		LOGGER.debug("Output parameter objects=[{}], [{}]", objects);
		return objects;
	}

	public NewsDto getNews(User user, byte newsId) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		LOGGER.debug("input parameters user, newsId: [{}], [{}]", user, newsId);

		List<NewsDetail> newsDetails = newsDetailDao.getNewsDetails(newsId);

		List<NewsDetailDto> newsDetailDtos = NewsDetail.toNewsDetailDtos(user, newsDetails);
		
		NewsDto newsDto = new NewsDto();
		newsDto.setNewsDetailDtos(newsDetailDtos.toArray(new NewsDetailDto[0]));
		LOGGER.debug("Output parameter newsDto=[{}]", newsDto);
		return newsDto;
	}

}
