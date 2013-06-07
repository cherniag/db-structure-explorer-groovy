package mobi.nowtechnologies.server.assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.MessageFactory;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;

import org.junit.Test;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class NewsAsmTest {

	@Test
	public void tesToNewsDetailDto_Success() {
		Message message = MessageFactory.createMessage("title");

		message.setId(Integer.MAX_VALUE);
		message.setPosition(Integer.MAX_VALUE);
		message.setMessageType(MessageType.RICH_POPUP);
		message.setAction("action");
		message.setActionButtonText("actionButtonText");
		message.setBody("body");
		message.setId(Integer.MIN_VALUE);
		message.setFrequence(MessageFrequence.ONCE_AFTER_1ST_TRACK_DOWNLOAD);
		message.setPublishTimeMillis(Long.MAX_VALUE);
		message.setImageFileName("imageFileName");
		message.setAction("action");
		message.setActionType(MessageActionType.A_SPECIFIC_NEWS_STORY);
		message.setActionButtonText("actionButtonText");

		NewsDetailDto newsDetailDto = NewsAsm.toNewsDetailDto(message);

		assertNotNull(newsDetailDto);
		assertEquals(message.getPosition(), newsDetailDto.getPosition());
		assertEquals(message.getTitle(), newsDetailDto.getDetail());
		assertEquals(message.getBody(), newsDetailDto.getBody());
		assertEquals(message.getMessageType(), newsDetailDto.getMessageType());
		assertEquals(message.getFrequence(), newsDetailDto.getMessageFrequence());
		assertEquals(message.getMessageType(), newsDetailDto.getMessageType());
		assertEquals(message.getPublishTimeMillis(), newsDetailDto.getTimestampMilis());
		assertEquals(message.getImageFileName(), newsDetailDto.getImageFileName());
		assertEquals(message.getId(), (Integer) newsDetailDto.getI());

		assertEquals(message.getAction(), newsDetailDto.getAction());
		assertEquals(message.getActionType(), newsDetailDto.getActionType());
		assertEquals(message.getActionButtonText(), newsDetailDto.getActionButtonText());

	}

	@Test
	public void tesToNewsDetailDto_NEWS_Success() {
		Message message = MessageFactory.createMessage("title");

		message.setId(Integer.MAX_VALUE);
		message.setPosition(Integer.MAX_VALUE);
		message.setMessageType(MessageType.NEWS);
		message.setAction("action");
		message.setActionButtonText("actionButtonText");
		message.setBody("body");
		message.setId(Integer.MIN_VALUE);
		message.setFrequence(MessageFrequence.ONCE_AFTER_1ST_TRACK_DOWNLOAD);
		message.setPublishTimeMillis(Long.MAX_VALUE);
		message.setImageFileName("imageFileName");
		message.setAction("action");
		message.setActionType(MessageActionType.A_SPECIFIC_NEWS_STORY);
		message.setActionButtonText("actionButtonText");

		NewsDetailDto newsDetailDto = NewsAsm.toNewsDetailDto(message);

		assertNotNull(newsDetailDto);
		assertEquals(message.getPosition(), newsDetailDto.getPosition());
		assertEquals(message.getTitle(), newsDetailDto.getDetail());
		assertEquals(message.getBody(), newsDetailDto.getBody());
		assertEquals(message.getMessageType(), newsDetailDto.getMessageType());
		assertEquals(message.getFrequence(), newsDetailDto.getMessageFrequence());
		assertEquals(message.getMessageType(), newsDetailDto.getMessageType());
		assertEquals(message.getPublishTimeMillis(), newsDetailDto.getTimestampMilis());
		assertEquals(message.getImageFileName(), newsDetailDto.getImageFileName());
		assertEquals(message.getPosition(), newsDetailDto.getI());

		assertEquals(null, newsDetailDto.getAction());
		assertEquals(null, newsDetailDto.getActionType());
		assertEquals(null, newsDetailDto.getActionButtonText());

	}

	@Test(expected = NullPointerException.class)
	public void tesToNewsDetailDto_Fail() {
		Message message = null;

		NewsAsm.toNewsDetailDto(message);
	}

}
