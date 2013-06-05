package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.nowtechnologies.server.factory.admin.FilterDtoFactory;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.MessageFactory;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDtoFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, Message.class})
public class MessageServiceTest {

	private MessageService messageServiceSpy;
	private MessageRepository mockMessageRepository;
	private CommunityService mockCommunityService;
	private FilterService mockFilterService;
	private CloudFileService mockCloudFileService;

	@Test
	public void testDelete_Success()
			throws Exception {
		Integer messageId = new Integer(1);

		Message message = MessageFactory.createMessage("https://i.ua");

		Mockito.when(mockMessageRepository.findOne(messageId)).thenReturn(message);

		Mockito.doNothing().when(mockMessageRepository).delete(message);

		messageServiceSpy.delete(messageId);

		assertNotNull(message.getFilterWithCtiteria());
		assertEquals(Collections.<AbstractFilterWithCtiteria> emptySet(), message.getFilterWithCtiteria());

		Mockito.verify(mockMessageRepository, Mockito.times(1)).findOne(messageId);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).delete(message);
	}

	@Test
	public void testGetAds_Success()
			throws Exception {
		String communityURL = "";

		Community community = CommunityFactory.createCommunity();

		Collection<Message> messages = MessageFactory.createCollection();
		List<Message> messageList = new ArrayList<Message>(messages);

		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockMessageRepository.findByCommunityAndMessageTypesAndPublishTimeMillis(Mockito.any(Community.class), Mockito.anyListOf(MessageType.class), Mockito.anyLong())).thenReturn(null);
		Mockito.when(mockMessageRepository.findByCommunityAndMessageTypes(Mockito.any(Community.class), Mockito.anyListOf(MessageType.class))).thenReturn(messageList);

		List<Message> result = messageServiceSpy.getAds(communityURL);

		assertNotNull(result);

		assertEquals(messageList, result);

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockMessageRepository, Mockito.times(0)).findByCommunityAndMessageTypesAndPublishTimeMillis(Mockito.any(Community.class), Mockito.anyListOf(MessageType.class),
				Mockito.anyLong());
		Mockito.verify(mockMessageRepository, Mockito.times(1)).findByCommunityAndMessageTypes(Mockito.any(Community.class), Mockito.anyListOf(MessageType.class));
	}

	@Test
	public void testSaveAd_Success()
			throws Exception {
		String communityURL = "";
		Integer position = null;
		boolean removeImage = false;

		Message message = MessageFactory.createMessage("https://i.ua");
		MultipartFile multipartFile = new MockMultipartFile("test", "1".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();

		Community community = CommunityFactory.createCommunity();
		
		String imageFileName = MessageType.AD + "_" + Utils.getEpochMillis() + "_" + message.getId();

		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.findMaxPosition(community, MessageType.AD, 0L)).thenReturn(position);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, imageFileName)).thenReturn(Boolean.TRUE);

		Message result = messageServiceSpy.saveAd(message, multipartFile, communityURL, filterDtos, removeImage);

		assertNotNull(result);
		assertEquals(message, result);
		assertEquals(1, result.getPosition());

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).findMaxPosition(community, MessageType.AD, 0L);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(1)).uploadFile(multipartFile, imageFileName);
	}
	
	@Test
	public void testSaveAd_RemoveImage_Success()
			throws Exception {
		String communityURL = "";
		Integer position = null;
		boolean removeImage = true;

		Message message = MessageFactory.createMessage("https://i.ua");
		MultipartFile multipartFile = new MockMultipartFile("test", "1".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();

		Community community = CommunityFactory.createCommunity();
		
		String imageFileName = MessageType.AD + "_" + Utils.getEpochMillis() + "_" + message.getId();

		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.findMaxPosition(community, MessageType.AD, 0L)).thenReturn(position);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, imageFileName)).thenReturn(Boolean.TRUE);

		Message result = messageServiceSpy.saveAd(message, multipartFile, communityURL, filterDtos, removeImage);

		assertNotNull(result);
		assertEquals(message, result);
		assertEquals(1, result.getPosition());
		assertEquals(null, result.getImageFileName());

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).findMaxPosition(community, MessageType.AD, 0L);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).save(message);
	}

	@Test
	public void testSaveAd_PositionIsMax_Success()
			throws Exception {
		String communityURL = "";
		Integer position = Integer.MAX_VALUE;
		boolean removeImage = false;

		Message message = MessageFactory.createMessage("https://i.ua");
		MultipartFile multipartFile = new MockMultipartFile("test", "1".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();

		Community community = CommunityFactory.createCommunity();
		
		String imageFileName = MessageType.AD + "_" + Utils.getEpochMillis() + "_" + message.getId();

		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.findMaxPosition(community, MessageType.AD, 0L)).thenReturn(position);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, imageFileName)).thenReturn(Boolean.TRUE);

		Message result = messageServiceSpy.saveAd(message, multipartFile, communityURL, filterDtos, removeImage);

		assertNotNull(result);
		assertEquals(message, result);
		assertEquals(position+1, result.getPosition());

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).findMaxPosition(community, MessageType.AD, 0L);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(1)).uploadFile(multipartFile, imageFileName);
	}
	
	@Test
	public void testUpdateAd_FileIsNotNullAndEmpty_Success()
			throws Exception {
		String communityURL = "";
		Community community = CommunityFactory.createCommunity();
		boolean removeImage = false;
		
		Message message = MessageFactory.createMessage("https://i.ua");
		
		MultipartFile multipartFile = new MockMultipartFile("test", "".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();
		
		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, message.getImageFileName())).thenReturn(Boolean.TRUE);

		Message result = messageServiceSpy.updateAd(message, multipartFile, communityURL, filterDtos, removeImage);
		
		assertNotNull(result);
		assertEquals(message, result);

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(0)).uploadFile(multipartFile, message.getImageFileName());
	}

	@Test
	public void testUpdateAd_FileIsNotNullAndNotEmpty_Success()
			throws Exception {
		boolean removeImage = false;
		String communityURL = "";
		Community community = CommunityFactory.createCommunity();
		
		Message message = MessageFactory.createMessage("https://i.ua");
		
		MultipartFile multipartFile = new MockMultipartFile("test", "1".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();
		
		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, message.getImageFileName())).thenReturn(Boolean.TRUE);

		Message result = messageServiceSpy.updateAd(message, multipartFile, communityURL, filterDtos, removeImage);
		
		assertNotNull(result);
		assertEquals(message, result);

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(1)).uploadFile(multipartFile, message.getImageFileName());
	}

	@Test
	public void testUpdateAd_FileIsNull_Success()
			throws Exception {
		boolean removeImage = false;
		String communityURL = "";
		Community community = CommunityFactory.createCommunity();
		
		Message message = MessageFactory.createMessage("https://i.ua");
		
		MultipartFile multipartFile = new MockMultipartFile("test", "".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();
		
		String imageFileName = MessageType.AD + "_" + System.currentTimeMillis() + "_" + message.getId();
		
		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, message.getImageFileName())).thenReturn(Boolean.TRUE);

		Message result = messageServiceSpy.updateAd(message, multipartFile, communityURL, filterDtos, removeImage);
		
		assertNotNull(result);
		assertEquals(message, result);

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(0)).uploadFile(multipartFile, imageFileName);
	}
	
	@Test(expected=Exception.class)
	public void testUpdateAd_Failure()
			throws Exception {
		boolean removeImage = false;
		String communityURL = "";
		Community community = CommunityFactory.createCommunity();
		
		Message message = MessageFactory.createMessage("https://i.ua");
		
		MultipartFile multipartFile = new MockMultipartFile("test", "".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();
		
		String imageFileName = MessageType.AD + "_" + System.currentTimeMillis() + "_" + message.getId();
		
		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenThrow(new Exception());
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, message.getImageFileName())).thenReturn(Boolean.TRUE);

		Message result = messageServiceSpy.updateAd(message, multipartFile, communityURL, filterDtos, removeImage);
		
		assertNotNull(result);
		assertEquals(message, result);

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(0)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(0)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(0)).uploadFile(multipartFile, imageFileName);
	}
	
	
	@Test
	public void testUpdateAd_FileIsNotNullAndNotEmptyAndRemoveImageIsTrue_Success()
			throws Exception {
		boolean removeImage = true;
		String communityURL = "";
		Community community = CommunityFactory.createCommunity();
		
		Message message = MessageFactory.createMessage("https://i.ua");
		
		MultipartFile multipartFile = new MockMultipartFile("test", "1".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();
		
		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, message.getImageFileName())).thenReturn(Boolean.TRUE);

		Message result = messageServiceSpy.updateAd(message, multipartFile, communityURL, filterDtos, removeImage);
		
		assertNotNull(result);
		assertEquals(message, result);
		assertEquals(null, result.getImageFileName());

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(0)).uploadFile(multipartFile, message.getImageFileName());
	}
	
	@Test
	public void testSave_Success() throws Exception {
		
		MessageDto messageDto = MessageDtoFactory.createMessageDto();
		String communityURL = "o2";
		
		Message message = MessageFactory.createMessage(null);
				
		doReturn(message).when(messageServiceSpy).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
		
		Message actualMessage = messageServiceSpy.save(messageDto, communityURL);
		
		assertNotNull(actualMessage);
		assertEquals(message, actualMessage);
		
		verify(messageServiceSpy, times(1)).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
	}
	
	@Test(expected=Exception.class)
	public void testSave_Failure() throws Exception {
		
		MessageDto messageDto = MessageDtoFactory.createMessageDto();
		String communityURL = "o2";
				
		doThrow(new Exception()).when(messageServiceSpy).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
		
		messageServiceSpy.save(messageDto, communityURL);
	}
	
	@Test
	public void testUpdate_Success() throws Exception {
		
		MessageDto messageDto = MessageDtoFactory.createMessageDto();
		messageDto.setId(Integer.MAX_VALUE);
		
		String communityURL = "o2";
		
		Message message = MessageFactory.createMessage(null);
		
		when(mockMessageRepository.findOne(messageDto.getId())).thenReturn(message);
		
		doReturn(message).when(messageServiceSpy).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
		
		Message actualMessage = messageServiceSpy.update(messageDto, communityURL);
		
		assertNotNull(actualMessage);
		assertEquals(message, actualMessage);
		
		verify(messageServiceSpy, times(1)).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
	}
	
	@Test
	public void testUpdate_NoMessage_Success() throws Exception {
		
		MessageDto messageDto = MessageDtoFactory.createMessageDto();
		messageDto.setId(Integer.MAX_VALUE);
		
		String communityURL = "o2";
		
		Message message = MessageFactory.createMessage(null);
		
		when(mockMessageRepository.findOne(messageDto.getId())).thenReturn(null);
		
		doReturn(message).when(messageServiceSpy).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
		
		Message actualMessage = messageServiceSpy.update(messageDto, communityURL);
		
		assertNull(actualMessage);
		
		verify(messageServiceSpy, times(0)).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
	}
	
	@Test(expected=Exception.class)
	public void testUpdate_saveOrUpdateThrowsException_Failure() throws Exception {
		
		MessageDto messageDto = MessageDtoFactory.createMessageDto();
		messageDto.setId(Integer.MAX_VALUE);
		
		String communityURL = "o2";
		
		Message message = MessageFactory.createMessage(null);
		
		when(mockMessageRepository.findOne(messageDto.getId())).thenReturn(message);
		
		doThrow(new Exception()).when(messageServiceSpy).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
		
		messageServiceSpy.update(messageDto, communityURL);
	}
	
//	@Test
//	public void testSaveOrUpdate() {
//		final Set<FilterDto> filterDtos = FilterDtoFactory.createSet(2);
//		
//		MessageDto messageDto = MessageDtoFactory.createMessageDto();
//		
//		messageDto.setPublishTime(new Date(0));
//		messageDto.setMessageType(MessageType.RICH_POPUP);
//		messageDto.setFilterDtos(filterDtos);
//		
//		String communityURL ="o2";
//		Message message = MessageFactory.createMessage(null);
//		message.setId(Integer.MAX_VALUE);
//		message.setPosition(Integer.MAX_VALUE);
//
//		Community community = CommunityFactory.createCommunity(); 
//		
//		when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
//		when(mockMessageRepository.findMaxPosition(community, messageDto.getMessageType(), messageDto.getPublishTime().getTime())).thenReturn(Integer.MIN_VALUE);
//		when(mockFilterService.find(filterDtos));
//
//		final Set<AbstractFilterWithCtiteria> filterWithCtiteria;
//		if (filterDtos != null)
//			filterWithCtiteria = filterService.find(filterDtos);
//		else
//			filterWithCtiteria = Collections.EMPTY_SET;
//
//		message.setTitle(messageDto.getHeadline());
//		message.setActivated(messageDto.isActivated());
//		message.setBody(messageDto.getBody());
//		message.setFrequence(messageDto.getFrequence());
//		message.setMessageType(messageDto.getMessageType());
//		message.setPublishTimeMillis(publishTimeMillis);
//		message.setFilterWithCtiteria(filterWithCtiteria);
//		message.setPosition(position);
//		message.setCommunity(community);
//		message.setAction(messageDto.getAction());
//		message.setActionType(messageDto.getActionType());
//		message.setActionButtonText(messageDto.getActionButtonText());
//
//		message = messageRepository.save(message);
//
//		
//	}
	
	@Test(expected=Exception.class)
	public void testUpdate_findOneThrowsException_Failure() throws Exception {
		
		MessageDto messageDto = MessageDtoFactory.createMessageDto();
		messageDto.setId(Integer.MAX_VALUE);
		
		String communityURL = "o2";
		
		Message message = MessageFactory.createMessage(null);
		
		when(mockMessageRepository.findOne(messageDto.getId())).thenThrow(new Exception());
		
		doReturn(message).when(messageServiceSpy).saveOrUpdate(eq(messageDto), eq(communityURL), any(Message.class));
		
		messageServiceSpy.update(messageDto, communityURL);
	}
	
	@Before
	public void setUp()
			throws Exception {

		mockMessageRepository = Mockito.mock(MessageRepository.class);
		mockCommunityService = Mockito.mock(CommunityService.class);
		mockFilterService = Mockito.mock(FilterService.class);
		mockCloudFileService = Mockito.mock(CloudFileService.class);

		messageServiceSpy = spy(new MessageService());
		messageServiceSpy.setCloudFileService(mockCloudFileService);
		messageServiceSpy.setMessageRepository(mockMessageRepository);
		messageServiceSpy.setFilterService(mockFilterService);
		messageServiceSpy.setCommunityService(mockCommunityService);
		messageServiceSpy.setUserService(new UserService());
		messageServiceSpy.dateFormat = new SimpleDateFormat();
		
		PowerMockito.mockStatic(Utils.class);
		
		long maxValue = Long.MAX_VALUE;
		PowerMockito.when(Utils.getEpochMillis()).thenReturn(maxValue);
	}
}