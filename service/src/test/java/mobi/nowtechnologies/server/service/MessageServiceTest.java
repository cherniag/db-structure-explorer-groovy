package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.MessageFactory;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class MessageServiceTest {

	private MessageService fixture;
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

		fixture.delete(messageId);

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

		List<Message> result = fixture.getAds(communityURL);

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

		Message message = MessageFactory.createMessage("https://i.ua");
		MultipartFile multipartFile = new MockMultipartFile("test", "".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();

		Community community = CommunityFactory.createCommunity();
		
		String imageFileName = MessageType.AD + "_" + Utils.getEpochMillis() + "_" + message.getId();

		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.findMaxPosition(community, MessageType.AD, 0L)).thenReturn(position);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, imageFileName)).thenReturn(Boolean.TRUE);

		Message result = fixture.saveAd(message, multipartFile, communityURL, filterDtos);

		assertNotNull(result);
		assertEquals(message, result);
		assertEquals(1, result.getPosition());

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).findMaxPosition(community, MessageType.AD, 0L);
		Mockito.verify(mockMessageRepository, Mockito.times(2)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(1)).uploadFile(multipartFile, imageFileName);
	}

	@Test
	public void testSaveAd_PositionIsMax_Success()
			throws Exception {
		String communityURL = "";
		Integer position = Integer.MAX_VALUE;

		Message message = MessageFactory.createMessage("https://i.ua");
		MultipartFile multipartFile = new MockMultipartFile("test", "".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();

		Community community = CommunityFactory.createCommunity();
		
		String imageFileName = MessageType.AD + "_" + Utils.getEpochMillis() + "_" + message.getId();

		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.findMaxPosition(community, MessageType.AD, 0L)).thenReturn(position);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, imageFileName)).thenReturn(Boolean.TRUE);

		Message result = fixture.saveAd(message, multipartFile, communityURL, filterDtos);

		assertNotNull(result);
		assertEquals(message, result);
		assertEquals(position+1, result.getPosition());

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(1)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(1)).findMaxPosition(community, MessageType.AD, 0L);
		Mockito.verify(mockMessageRepository, Mockito.times(2)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(1)).uploadFile(multipartFile, imageFileName);
	}
	
	@Test
	public void testUpdateAd_FileIsNotNullAndEmpty_Success()
			throws Exception {
		String communityURL = "";
		Community community = CommunityFactory.createCommunity();
		
		Message message = MessageFactory.createMessage("https://i.ua");
		
		MultipartFile multipartFile = new MockMultipartFile("test", "".getBytes());
		Set<FilterDto> filterDtos = Collections.<FilterDto> emptySet();
		Set<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = Collections.<AbstractFilterWithCtiteria>emptySet();
		
		Mockito.when(mockCommunityService.getCommunityByUrl(communityURL)).thenReturn(community);
		Mockito.when(mockFilterService.find(filterDtos)).thenReturn(abstractFilterWithCtiterias);
		Mockito.when(mockMessageRepository.save(message)).thenReturn(message);
		Mockito.when(mockCloudFileService.uploadFile(multipartFile, message.getImageFileName())).thenReturn(Boolean.TRUE);

		Message result = fixture.updateAd(message, multipartFile, communityURL, filterDtos);
		
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

		Message result = fixture.updateAd(message, multipartFile, communityURL, filterDtos);
		
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

		Message result = fixture.updateAd(message, multipartFile, communityURL, filterDtos);
		
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

		Message result = fixture.updateAd(message, multipartFile, communityURL, filterDtos);
		
		assertNotNull(result);
		assertEquals(message, result);

		Mockito.verify(mockCommunityService, Mockito.times(1)).getCommunityByUrl(communityURL);
		Mockito.verify(mockFilterService, Mockito.times(0)).find(filterDtos);
		Mockito.verify(mockMessageRepository, Mockito.times(0)).save(message);
		Mockito.verify(mockCloudFileService, Mockito.times(0)).uploadFile(multipartFile, imageFileName);
	}
	
	@Before
	public void setUp()
			throws Exception {

		mockMessageRepository = Mockito.mock(MessageRepository.class);
		mockCommunityService = Mockito.mock(CommunityService.class);
		mockFilterService = Mockito.mock(FilterService.class);
		mockCloudFileService = Mockito.mock(CloudFileService.class);

		fixture = new MessageService();
		fixture.setCloudFileService(mockCloudFileService);
		fixture.setMessageRepository(mockMessageRepository);
		fixture.setFilterService(mockFilterService);
		fixture.setCommunityService(mockCommunityService);
		fixture.setUserService(new UserService());
		fixture.dateFormat = new SimpleDateFormat();
		
		PowerMockito.mockStatic(Utils.class);
		
		long maxValue = Long.MAX_VALUE;
		PowerMockito.when(Utils.getEpochMillis()).thenReturn(maxValue);
	}
}