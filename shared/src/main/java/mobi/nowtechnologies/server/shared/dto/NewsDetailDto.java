package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@XmlRootElement(name="item")
public class NewsDetailDto {
	
	private int id;
	
	private int i;
	
	private String detail;

	private int position;
	
	private String body;
	
	private String imageFileName;
	
	private long timestampMilis;

	private MessageType messageType;
	
	private MessageFrequence messageFrequence;

	public enum MessageType {
		NEWS, POPUP, NOTIFICATION, AD
	}
	
	public enum MessageFrequence {
		ONCE, DAILY, WEEKLY
	}
	
	public enum UserHandset {
		IOS, ANDROID, BLACKBERRY, J2ME, NONE
	}
	
	public enum UserState{
		NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS, LIMITED, FREE_TRIAL, LAST_TRIAL_DAY, PAYMENT_ERROR, LIMITED_AFTER_TRIAL, ONE_MONTH_PROMO
	}
	
	public NewsDetailDto() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public MessageType getMessageType() {
		return messageType;
	}


	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}


	public MessageFrequence getMessageFrequence() {
		return messageFrequence;
	}


	public void setMessageFrequence(MessageFrequence messageFrequence) {
		this.messageFrequence = messageFrequence;
	}
	
	public long getTimestampMilis() {
		return timestampMilis;
	}

	public void setTimestampMilis(long timestampMilis) {
		this.timestampMilis = timestampMilis;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	@Override
	public String toString() {
		return "NewsDetailDto [body=" + body + ", detail=" + detail + ", i=" + i + ", id=" + id + ", imageFileName=" + imageFileName + ", messageFrequence="
				+ messageFrequence + ", messageType=" + messageType + ", position=" + position + ", timestampMilis=" + timestampMilis + "]";
	}

}
