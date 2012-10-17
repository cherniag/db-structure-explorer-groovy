package mobi.nowtechnologies.server.persistence.domain;

/**
 * SupportMessage
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class SupportMessage {
	private String displayName;
	private String userName;
	private String captcha;
	private String subject;
	private String message;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}