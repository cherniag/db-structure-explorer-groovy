package mobi.nowtechnologies.server.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.MessageFactory;
import mobi.nowtechnologies.server.persistence.domain.filter.AndroidFilter;
import mobi.nowtechnologies.server.persistence.domain.filter.LimitedFilter;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.enums.AdActionType;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class AdItemDtoTest {
	@Test
	public void testAdItemDto_Success()
		throws Exception {
		AdItemDto result = new AdItemDto();
		assertNotNull(result);
	}

	@Test
	public void testFromDto_Success()
		throws Exception {
		AdItemDto adItemDto = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);

		Message result = AdItemDto.fromDto(adItemDto);

		assertNotNull(result);
		assertEquals("Message [activated=true, actionType=null, action=null, actionButtonText=null, body=message, communityId=0, filterWithCtiteria=[], frequence=null, id=1, messageType=AD, position=0, publishTimeMillis=0, title=https://i.ua, imageFileName=imageFileName]", result.toString());
		assertEquals(adItemDto.getId(), result.getId());
		assertEquals(0, result.getPosition());
		assertEquals(adItemDto.isActivated(), result.isActivated());
		assertEquals(adItemDto.getMessage(), result.getBody());
		assertEquals(adItemDto.getAction(), result.getTitle());
		assertEquals(adItemDto.getImageFileName(), result.getImageFileName());
		assertEquals(null, result.getCommunity());
		assertEquals((byte) 0, result.getCommunityId());
		assertEquals(null, result.getFrequence());
		assertEquals(0L, result.getPublishTimeMillis());
	}

	@Test
	public void testGetAction_Success()
		throws Exception {
		String action = "https://i.ua";
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto(action, AdActionType.URL);

		String result = fixture.getAction();

		assertNotNull(result);
		assertEquals(action, result);
	}

	@Test
	public void testGetActionType_Success()
		throws Exception {
		AdActionType actionType = AdActionType.URL;
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", actionType);
	
		AdActionType result = fixture.getActionType();

		assertNotNull(result);
		assertEquals(actionType, result);
	}

	@Test
	public void testGetFile_Success()
		throws Exception {
		AdActionType actionType = AdActionType.URL;
		MultipartFile file= new MockMultipartFile("test", "".getBytes());
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", actionType);
		
		fixture.setFile(file);

		MultipartFile result = fixture.getFile();

		assertNotNull(result);
		assertEquals(file, result);
	}

	@Test
	public void testGetFilterDtos_Success()
		throws Exception {
		Set<FilterDto> filterDtos = Collections.<FilterDto>emptySet();
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		fixture.setFilterDtos(filterDtos);

		Set<FilterDto> result = fixture.getFilterDtos();

		assertNotNull(result);
		assertEquals(filterDtos, result);
	}

	@Test
	public void testGetId_Success()
		throws Exception {
		Integer id = Integer.MIN_VALUE;
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		fixture.setId(id);

		Integer result = fixture.getId();

		assertNotNull(result);
		assertEquals(id, fixture.getId());
	}

	@Test
	public void testGetImageFileName_Success()
		throws Exception {
		String imageFileName="imageFileName";
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		fixture.setImageFileName(imageFileName);

		String result = fixture.getImageFileName();

		assertNotNull(result);
		assertEquals(imageFileName, fixture.getImageFileName());
	}

	@Test
	public void testGetMessage_Success()
		throws Exception {
		String message = "message";
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		fixture.setMessage(message);
		
		String result = fixture.getMessage();

		assertNotNull(result);
		assertEquals(message, fixture.getMessage());
	}

	@Test
	public void testIsActivated_True_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		fixture.setActivated(true);

		boolean result = fixture.isActivated();

		assertTrue(result);
	}

	@Test
	public void testIsActivated_False_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		fixture.setActivated(false);

		boolean result = fixture.isActivated();

		assertFalse(result);
	}

	@Test
	public void testSetAction_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		String action = "";

		fixture.setAction(action);
		
		assertEquals(action, fixture.getAction());
	}

	@Test
	public void testSetActionType_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		AdActionType actionType = AdActionType.ISRC;

		fixture.setActionType(actionType);
		
		assertEquals(actionType, fixture.getActionType());
	}

	@Test
	public void testSetActivated_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		boolean activated = true;

		fixture.setActivated(activated);
		
		assertTrue(fixture.isActivated());
	}

	@Test
	public void testSetFile_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		
		MultipartFile file = new MockMultipartFile("test", "".getBytes());

		fixture.setFile(file);
		
		assertEquals(file, fixture.getFile());
	}

	@Test
	public void testSetFilterDtos_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		
		Set<FilterDto> filterDtos = Collections.<FilterDto>emptySet();

		fixture.setFilterDtos(filterDtos);

		assertEquals(filterDtos, fixture.getFilterDtos());
	}

	@Test
	public void testSetId_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		Integer id = Integer.MAX_VALUE;

		fixture.setId(id);
		
		assertEquals(id, fixture.getId());
	}

	@Test
	public void testSetImageFileName_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		String imageFileName = "newImageFileName";

		fixture.setImageFileName(imageFileName);
		
		assertEquals(imageFileName, fixture.getImageFileName());
	}

	@Test
	public void testSetMessage_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);
		String message = "newMessage";

		fixture.setMessage(message);
		
		assertEquals(message, fixture.getMessage());
	}

	@Test
	public void testToDtoItem_URL_Success()
		throws Exception {
		String title = "https://i.ua";
		Message message = MessageFactory.createMessage(title);

		Set<AbstractFilterWithCtiteria> filterWithCtiterias = new HashSet<AbstractFilterWithCtiteria>(); 
		
		filterWithCtiterias.add(new AndroidFilter());
		filterWithCtiterias.add(new LimitedFilter());
		
		message.setFilterWithCtiteria(filterWithCtiterias);

		AdItemDto result = AdItemDto.toDtoItem(message);

		assertNotNull(result);
		assertEquals("AdItemDto [id=1, action=https://i.ua, message=body, activated=true, imageFileName=imageFileName, actionType=URL, filterDtos=[FilterDto [name=null]], position=0, removeImage=false]", result.toString());
		assertEquals("body", result.getMessage());
		assertEquals(new Integer(1), result.getId());
		assertEquals(null, result.getFile());
		assertEquals(true, result.isActivated());
		assertEquals(title, result.getAction());
		assertEquals(AdActionType.URL, result.getActionType());
		assertEquals("imageFileName", result.getImageFileName());
		assertEquals(false, result.isRemoveImage());
	}

	@Test
	public void testToDtoItem_ISRC_Sucess()
		throws Exception {
		String title = "file://ggg";
		Message message = MessageFactory.createMessage(title);
		
		message.setTitle(title);

		Set<AbstractFilterWithCtiteria> filterWithCtiterias = new HashSet<AbstractFilterWithCtiteria>(); 
		
		filterWithCtiterias.add(new AndroidFilter());
		filterWithCtiterias.add(new LimitedFilter());
		
		message.setFilterWithCtiteria(filterWithCtiterias);

		AdItemDto result = AdItemDto.toDtoItem(message);

		assertNotNull(result);
		assertEquals("AdItemDto [id=1, action=file://ggg, message=body, activated=true, imageFileName=imageFileName, actionType=ISRC, filterDtos=[FilterDto [name=null]], position=0, removeImage=false]", result.toString());
		assertEquals("body", result.getMessage());
		assertEquals(new Integer(1), result.getId());
		assertEquals(null, result.getFile());
		assertEquals(true, result.isActivated());
		assertEquals(title, result.getAction());
		assertEquals(AdActionType.ISRC, result.getActionType());
		assertEquals("imageFileName", result.getImageFileName());
		assertEquals(false, result.isRemoveImage());
	}
	
	@Test
	public void testToDtoItem_NotHasImage_Success()
		throws Exception {
		String title = "https://i.ua";
		Message message = MessageFactory.createMessage(title);
		message.setImageFileName(null);

		Set<AbstractFilterWithCtiteria> filterWithCtiterias = new HashSet<AbstractFilterWithCtiteria>(); 
		
		filterWithCtiterias.add(new AndroidFilter());
		filterWithCtiterias.add(new LimitedFilter());
		
		message.setFilterWithCtiteria(filterWithCtiterias);

		AdItemDto result = AdItemDto.toDtoItem(message);

		assertNotNull(result);
		assertEquals(null, result.getImageFileName());
		assertEquals(true, result.isRemoveImage());
	}

	@Test
	public void testToDtoItem_MessageIsNull_Success()
		throws Exception {
		Message message = null;

		AdItemDto result = AdItemDto.toDtoItem(message);

		assertEquals(null, result);
	}

	@Test
	public void testToDtoList_Success()
		throws Exception {
		Collection<Message> messages = MessageFactory.createCollection();

		List<AdItemDto> result = AdItemDto.toDtoList(messages);

		assertNotNull(result);
		assertEquals(messages.size(), result.size());
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void testToDtoList_MessagesIsNull_Failure()
		throws Exception {
		List<Message> messages = null;

		List<AdItemDto> result = AdItemDto.toDtoList(messages);

		assertNotNull(result);
	}

	@Test
	public void testToString_Success()
		throws Exception {
		AdItemDto fixture = AdItemDtoFactory.createAdItemDto("https://i.ua", AdActionType.URL);

		String result = fixture.toString();
		
		assertNotNull(result);
		
		assertEquals("AdItemDto [id=1, action=https://i.ua, message=message, activated=true, imageFileName=imageFileName, actionType=URL, filterDtos=[], position=null, removeImage=false]", result);
	}
}