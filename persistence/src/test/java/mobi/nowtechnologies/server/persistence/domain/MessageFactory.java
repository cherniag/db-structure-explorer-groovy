package mobi.nowtechnologies.server.persistence.domain;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MessageFactory {

	public static Message createMessage(String title) {
		Message message = new Message();
		message.setTitle(title);
		message.setId(new Integer(1));
		message.setActivated(true);
		message.setImageFileName("imageFileName");
		message.setBody("body");

		return message;
	}

	public static Collection<Message> createCollection() {
		int initialCapacity = 6;
		Collection<Message> messages = new ArrayList<Message>(initialCapacity);

		String title;
		for (int i = 0; i < initialCapacity; i++) {
			if (i > 3) {
				title = "https://i.ua";
			} else {
				title = "file://ggg";
			}
			messages.add(createMessage(title + i));
		}

		return messages;
	}
}