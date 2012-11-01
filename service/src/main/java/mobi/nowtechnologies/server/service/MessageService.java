package mobi.nowtechnologies.server.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobi.nowtechnologies.server.assembler.MessageAsm;
import mobi.nowtechnologies.server.assembler.NewsAsm;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.NewsDto;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.dto.admin.NewsItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.NewsPositionsDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public class MessageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private MessageRepository messageRepository;
	private UserService userService;
	private FilterService filterService;
	private CommunityService communityService;
	private CloudFileService cloudFileService;

	public void setCloudFileService(CloudFileService cloudFileService) {
		this.cloudFileService = cloudFileService;
	}

	@Transactional(readOnly = true)
	public Object[] processGetNewsCommand(User user, String communityName, Long lastUpdateNewsTimeMillis) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");

		LOGGER.debug("input parameters user, communityName, lastUpdateNewsTimeMillis: [{}], [{}], [{}]", new Object[] { user, communityName, lastUpdateNewsTimeMillis });

		int userId = user.getId();
		user = userService.findUserTree(userId);

		AccountCheckDTO accountCheck = user.toAccountCheckDTO(null);
		Community community = user.getUserGroup().getCommunity();

		NewsDto newsDto = getNews(user, community, lastUpdateNewsTimeMillis);
		Object[] objects = new Object[] { accountCheck, newsDto };
		LOGGER.debug("Output parameter objects=[{}], [{}]", objects);
		return objects;
	}

	@Transactional(readOnly = true)
	public NewsDto getNews(User user, Community community, Long lastUpdateNewsTimeMillis) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		LOGGER.debug("input parameters user, community, lastUpdateNewsTimeMillis: [{}], [{}], [{}]", new Object[] { user, community, lastUpdateNewsTimeMillis });

		long lastClientUpdateNewsTimeMillis = 0L;
		if (lastUpdateNewsTimeMillis != null)
			lastClientUpdateNewsTimeMillis = lastUpdateNewsTimeMillis;

		final long currentTimeMillis = Utils.getEpochSeconds() * 1000L;

		Long nextNewsPublishTimeMillis = messageRepository.findNextNewsPublishDate(lastClientUpdateNewsTimeMillis, community, currentTimeMillis);
		if (nextNewsPublishTimeMillis == null)
			nextNewsPublishTimeMillis = -1L;

		List<Message> messages = messageRepository.findByCommunityAndPublishTimeMillisAfterOrderByPositionAsc(community, nextNewsPublishTimeMillis, lastClientUpdateNewsTimeMillis,
				currentTimeMillis);

		List<NewsDetailDto> newsDetailDtos = NewsAsm.toNewsDetailDtos(user, messages);

		NewsDto newsDto = new NewsDto();
		newsDto.setNewsDetailDtos(newsDetailDtos.toArray(new NewsDetailDto[0]));
		LOGGER.debug("Output parameter newsDto=[{}]", newsDto);
		return newsDto;
	}

	@Transactional(readOnly = true)
	public List<MessageDto> getMessageDtos(String communityURL) {
		LOGGER.debug("input parameters communityURL: [{}]", communityURL);

		List<Message> messages = getMessages(communityURL, Arrays.asList(MessageType.NOTIFICATION, MessageType.POPUP), null);
		List<MessageDto> messageDtos = MessageAsm.toDtos(messages);
		LOGGER.debug("Output parameter [{}]", messageDtos);
		return messageDtos;
	}

	@Transactional(readOnly = true)
	public List<NewsItemDto> getNewsByDate(String communityURL, Date choosedPublishDate) {
		LOGGER.info("Selecting news for {} date", choosedPublishDate);
		List<Message> messages = getMessages(communityURL, Arrays.asList(MessageType.NEWS), choosedPublishDate);
		List<NewsItemDto> newsItemDtos = NewsAsm.toDtos(messages);
		LOGGER.info("Done selecting news for {} date", choosedPublishDate);
		return newsItemDtos;
	}

	private List<Message> getMessages(String communityURL, List<MessageType> messageTypes, Date choosedPublishDate) {
		LOGGER.debug(
				"input parameters communityURL, messageTypes: [{}], [{}], [{}]",
				new Object[] { communityURL, messageTypes, choosedPublishDate });

		Community community = CommunityDao.getMapAsUrls().get(communityURL.toUpperCase());

		final List<Message> messages;
		if (choosedPublishDate != null)
			messages = messageRepository.findByCommunityAndMessageTypesAndPublishTimeMillis(community, messageTypes, choosedPublishDate.getTime());
		else
			messages = messageRepository.findByCommunityAndMessageTypes(community, messageTypes);

		LOGGER.debug("Output parameter [{}]", messages);
		return messages;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Message update(MessageDto messageDto, String communityURL) {
		LOGGER.debug("input parameters messageDto, communityURL: [{}], [{}]", messageDto, communityURL);

		Message message = null;

		final Integer id = messageDto.getId();
		if (id != null)
			message = messageRepository.findOne(id);

		if (message != null)
			message = saveOrUpdate(messageDto, communityURL, message);

		LOGGER.debug("Output parameter message=[{}]", message);
		return message;
	}

	@SuppressWarnings("unchecked")
	private Message saveOrUpdate(MessageDto messageDto, String communityURL, Message message) {
		LOGGER.debug("input parameters messageDto, communityURL, message: [{}], [{}]", new Object[] { messageDto, communityURL, message });

		Community community = CommunityDao.getMapAsUrls().get(communityURL.toUpperCase());

		final long publishTimeMillis = messageDto.getPublishTime().getTime();
		Integer position;
		if (message.getId() != null)
			position = messageDto.getPosition();
		else {
			position = messageRepository.findMaxPosition(community,	messageDto.getMessageType(), publishTimeMillis);
			if (position != null) {
				position++;
			} else
				position = 1;
		}

		final Set<FilterDto> filterDtos = messageDto.getFilterDtos();
		final Set<AbstractFilterWithCtiteria> filterWithCtiteria;
		if (filterDtos != null)
			filterWithCtiteria = filterService.find(filterDtos);
		else
			filterWithCtiteria = Collections.EMPTY_SET;

		message.setTitle(messageDto.getHeadline());
		message.setActivated(messageDto.isActivated());
		message.setBody(messageDto.getBody());
		message.setFrequence(messageDto.getFrequence());
		message.setMessageType(messageDto.getMessageType());
		message.setPublishTimeMillis(publishTimeMillis);
		message.setFilterWithCtiteria(filterWithCtiteria);
		message.setPosition(position);
		message.setCommunity(community);

		message = messageRepository.save(message);

		LOGGER.debug("Output parameter message=[{}]", message);
		return message;
	}

	@Transactional(readOnly = true)
	public MessageDto getMessageDto(Integer messageId) {
		LOGGER.debug("input parameters messageId: [{}]", messageId);

		Message message = messageRepository.findOneWithFilters(messageId);

		MessageDto messageDto = null;
		if (message != null)
			messageDto = MessageAsm.toDto(message);
		LOGGER.debug("Output parameter messageDto=[{}]", messageDto);
		return messageDto;
	}

	@Transactional(readOnly = true)
	public NewsItemDto getNewsById(Integer messageId) {
		LOGGER.info("Selecting news by id {}", messageId);

		Message message = messageRepository.findOneWithFilters(messageId);

		NewsItemDto newsItemDto = null;
		if (message != null)
			newsItemDto = NewsAsm.toDto(message);
		LOGGER.debug("Done selection of the news :{}", newsItemDto);
		return newsItemDto;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer messageId) {
		LOGGER.info("Deleting message with id: {}", messageId);
		Message message = messageRepository.findOne(messageId);
		message.setFilterWithCtiteria(new HashSet<AbstractFilterWithCtiteria>());
		messageRepository.delete(message);
		LOGGER.debug("Done deleting message with id {}", messageId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Message save(MessageDto messageDto, String communityURL) {
		LOGGER.debug("input parameters messageDto, communityURL: [{}], [{}]", messageDto, communityURL);

		Message message = saveOrUpdate(messageDto, communityURL, new Message());

		LOGGER.debug("Output parameter message=[{}]", message);
		return message;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private Message save(NewsItemDto newsItemDto, String communityURL) {
		LOGGER.debug("input parameters newsItemDto, communityURL: [{}], [{}]", newsItemDto, communityURL);

		Message message = saveOrUpdate(newsItemDto, communityURL, new Message());

		LOGGER.debug("Output parameter message=[{}]", message);
		return message;
	}

	@SuppressWarnings("unchecked")
	private Message saveOrUpdate(NewsItemDto newsItemDto, String communityURL, Message message) {
		LOGGER.debug("input parameters newsItemDto, communityURL, message: [{}], [{}]", new Object[] { newsItemDto, communityURL, message });

		Community community = CommunityDao.getMapAsUrls().get(communityURL.toUpperCase());

		final Date publishTime = newsItemDto.getPublishTime();
		final long publishTimeMillis = publishTime.getTime();
		Integer position;
		if (message.getId() != null)
			position = newsItemDto.getPosition();
		else {
			position = messageRepository.findMaxPosition(community, MessageType.NEWS, publishTimeMillis);
			if (position != null) {
				position++;
			} else
				position = 1;
		}

		final Set<FilterDto> filterDtos = newsItemDto.getFilterDtos();
		final Set<AbstractFilterWithCtiteria> filterWithCtiteria;
		if (filterDtos != null)
			filterWithCtiteria = filterService.find(filterDtos);
		else
			filterWithCtiteria = Collections.EMPTY_SET;


		message.setTitle(newsItemDto.getHeadline());
		message.setActivated(newsItemDto.isActivated());
		message.setBody(newsItemDto.getBody());
		message.setFrequence(newsItemDto.getFrequence());
		message.setMessageType(MessageType.NEWS);
		message.setPublishTimeMillis(publishTimeMillis);
		message.setFilterWithCtiteria(filterWithCtiteria);
		message.setPosition(position);
		message.setCommunity(community);

		message = messageRepository.save(message);

		// in order not to remove the image name if there was no file selected for update
		if (null != newsItemDto.getFile() && !newsItemDto.getFile().isEmpty()) {
			String imageFileName = MessageType.NEWS + "_" + System.currentTimeMillis() + "_" + message.getId();
			message.setImageFileName(imageFileName);
			message = messageRepository.save(message);
		}

		LOGGER.debug("Output parameter message=[{}]", message);
		return message;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private Message update(NewsItemDto newsItemDto, String communityURL) {
		LOGGER.debug("input parameters newsItemDto, communityURL: [{}], [{}]", newsItemDto, communityURL);

		Message message = null;

		final Integer id = newsItemDto.getId();
		if (id != null)
			message = messageRepository.findOne(id);

		if (message != null)
			message = saveOrUpdate(newsItemDto, communityURL, message);

		LOGGER.debug("Output parameter message=[{}]", message);
		return message;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Message> cloneNewsForSelectedPublishDateIfOnesDoesNotExsistForSelectedPublishDate(Date choosedPublishDate, String communityURL) {
		LOGGER.debug("input parameters choosedPublishDate, communityURL: [{}], [{}]", choosedPublishDate, communityURL);
		List<Message> clonedMessages = new LinkedList<Message>();

		Community community = CommunityDao.getMapAsUrls().get(communityURL.toUpperCase());

		final long choosedPublishTimeMillis = choosedPublishDate.getTime();
		final long count = messageRepository.getCount(community, choosedPublishTimeMillis, MessageType.NEWS);
		boolean isNewsForChoosedPublishDateAlreadyExist = (count > 0);
		if (!isNewsForChoosedPublishDateAlreadyExist) {

			Long nearestLatestPublishTimeMillis = messageRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, community, MessageType.NEWS);

			if (nearestLatestPublishTimeMillis != null) {
				List<Message> messages = messageRepository.findByCommunityAndMessageTypesAndPublishTimeMillis(community, Arrays.asList(MessageType.NEWS), nearestLatestPublishTimeMillis);
				for (Message message : messages) {
					Message clonedMessage = Message.newInstance(message);
					clonedMessage.setPublishTimeMillis(choosedPublishTimeMillis);
					clonedMessage = messageRepository.save(clonedMessage);
					clonedMessages.add(clonedMessage);
				}
			}
		}

		LOGGER.debug("Output parameter clonedMessages=[{}]", clonedMessages);
		return clonedMessages;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Message> getActualNews(String communityUrl, Date selectedDate) {
		LOGGER.debug("input parameters communityUrl, selectedDate: [{}], [{}]", communityUrl, selectedDate);

		Community community = communityService.getCommunityByUrl(communityUrl);
		long currentTimeMillis = selectedDate.getTime();

		Long nearestLatestPublishTimeMillis = messageRepository.findNearestLatestPublishDate(currentTimeMillis, community, MessageType.NEWS);

		final List<Message> messages;
		if (nearestLatestPublishTimeMillis != null)
			messages = messageRepository.getActualNews(community, nearestLatestPublishTimeMillis);
		else
			messages = Collections.EMPTY_LIST;
		LOGGER.debug("Done selecting actual news with messages=[{}]", messages);
		return messages;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateNewsPositions(NewsPositionsDto newsPositionsDto) {
		LOGGER.info("Starting updating news positions: {}", newsPositionsDto);

		Map<Integer, Integer> positionMap = newsPositionsDto.getPositionMap();
		if (positionMap.isEmpty()) {
			LOGGER.debug("Nothing to update. No positions were changed");
			return;
		}

		List<Message> newsList = messageRepository.findAll(positionMap.keySet());
		if(!newsList.isEmpty()){
			long publishTimeMillis = newsList.get(0).getPublishTimeMillis();
			Integer max_position = messageRepository.findMaxPosition(newsList.get(0).getCommunity(), MessageType.NEWS, publishTimeMillis);
			max_position++;

			for (Message news : newsList) {
				Integer position = positionMap.get(news.getId());
				news.setPosition(max_position + position);
			}
			messageRepository.save(newsList);
			messageRepository.flush();

			for (Message news : newsList) {
				Integer position = positionMap.get(news.getId());
				news.setPosition(position);
			}
			messageRepository.save(newsList);
		}
		LOGGER.info("Done updating news positions");
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Message saveNews(NewsItemDto newsItemDto, String communityURL) {

		Message message = save(newsItemDto, communityURL);
		if (null != newsItemDto.getFile() && !newsItemDto.getFile().isEmpty()) {
			cloudFileService.uploadFile(newsItemDto.getFile(), message.getImageFileName());
		}

		return message;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Message updateNews(NewsItemDto newsItemDto, String communityURL) {
		LOGGER.info("Updating news with id: {}", newsItemDto.getId());

		Message message = update(newsItemDto, communityURL);
		final MultipartFile file = newsItemDto.getFile();
		if (null != newsItemDto.getFile() && !newsItemDto.getFile().isEmpty())
			cloudFileService.uploadFile(file, message.getImageFileName());

		LOGGER.debug("Done updating news: {}", message);
		return message;
	}

	@Transactional(readOnly = true)
	public List<Long> getAllPublishTimeMillis(String communityUrl) {
		LOGGER.debug("input parameters communityUrl: [{}]", communityUrl);

		Community community = communityService.getCommunityByUrl(communityUrl);

		List<Long> allPublishTimeMillis = messageRepository.getAllPublishTimeMillis(community);
		LOGGER.info("Output parameter [{}]", allPublishTimeMillis);
		return allPublishTimeMillis;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setMessageRepository(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	public void setFilterService(FilterService filterService) {
		this.filterService = filterService;
	}

	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}
}