package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mayboroda Dmytro
 * @author Titov Mykhaylo (titov)
 * 
 */
@Entity
@Table(name = "messages", uniqueConstraints = @UniqueConstraint(columnNames = { "position", "community_id", "messageType", "publishTimeMillis" }))
public class Message {

	private static final Logger LOGGER = LoggerFactory.getLogger(Message.class);

	private Integer id;

	private Community community;

	private byte communityId;

	private String title;

	private String body;

	private boolean activated;

	private MessageFrequence frequence;

	private MessageType messageType;

	private long publishTimeMillis;

	private int position;

	private String imageFileName;

	private Set<AbstractFilterWithCtiteria> filterWithCtiteria = new HashSet<AbstractFilterWithCtiteria>();
	
	private MessageActionType actionType;
	
	private String action;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@JoinColumn(name = "community_id")
	@ManyToOne(optional = false)
	public Community getCommunity() {
		return this.community;
	}

	public void setCommunity(Community community) {
		this.community = community;
		if (community != null)
			communityId = community.getId();
	}

	protected void setCommunityId(byte communityId) {
		this.communityId = communityId;
	}

	@Column(name = "community_id", insertable = false, updatable = false)
	public byte getCommunityId() {
		return communityId;
	}

	@Column(nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Lob
	@Column(nullable = false)
	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isActivated() {
		return this.activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Enumerated(EnumType.STRING)
	public MessageFrequence getFrequence() {
		return this.frequence;
	}

	public void setFrequence(MessageFrequence frequence) {
		this.frequence = frequence;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


	public Message() {
	}

	@Enumerated(EnumType.STRING)
	public MessageType getMessageType() {
		return this.messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
	public java.util.Set<AbstractFilterWithCtiteria> getFilterWithCtiteria() {
		return this.filterWithCtiteria;
	}

	public void setFilterWithCtiteria(java.util.Set<AbstractFilterWithCtiteria> filterWithCtiteria) {
		this.filterWithCtiteria = filterWithCtiteria;
	}

	public void addFilterWithCtiteria(AbstractFilterWithCtiteria filterWithCtiteria) {
		getFilterWithCtiteria().add(filterWithCtiteria);
	}

	public void removeFilterWithCtiteria(AbstractFilterWithCtiteria filterWithCtiteria) {
		getFilterWithCtiteria().remove(filterWithCtiteria);
	}

	public long getPublishTimeMillis() {
		return publishTimeMillis;
	}

	public void setPublishTimeMillis(long publishTimeMillis) {
		this.publishTimeMillis = publishTimeMillis;
	}

	@Enumerated(EnumType.STRING)
	public MessageActionType getActionType() {
		return actionType;
	}

	public void setActionType(MessageActionType actionType) {
		this.actionType = actionType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public static Message newInstance(Message message) {
		LOGGER.debug("input parameters message: [{}], [{}]", message);

		Set<AbstractFilterWithCtiteria> filterWithCtiterias = new HashSet<AbstractFilterWithCtiteria>(message.getFilterWithCtiteria());

		Message clonedMessage = new Message();
		clonedMessage.setActivated(message.isActivated());
		clonedMessage.setBody(message.getBody());
		clonedMessage.setCommunity(message.getCommunity());
		clonedMessage.setFilterWithCtiteria(filterWithCtiterias);
		clonedMessage.setFrequence(message.getFrequence());
		clonedMessage.setImageFileName(message.getImageFileName());
		clonedMessage.setMessageType(message.getMessageType());
		clonedMessage.setPosition(message.getPosition());
		clonedMessage.setPublishTimeMillis(message.getPublishTimeMillis());
		clonedMessage.setTitle(message.getTitle());

		LOGGER.debug("Output parameter clonedMessage=[{}]", clonedMessage);
		return clonedMessage;
	}

	@Override
	public String toString() {
		return "Message [activated=" + activated + ", actionType=" + actionType + ", action=" + action + ", body=" + body + ", communityId=" + communityId + ", filterWithCtiteria="
				+ filterWithCtiteria
				+ ", frequence=" + frequence + ", id=" + id + ", messageType=" + messageType + ", position=" + position + ", publishTimeMillis="
				+ publishTimeMillis + ", title=" + title + ", imageFileName=" + imageFileName + "]";
	}
}