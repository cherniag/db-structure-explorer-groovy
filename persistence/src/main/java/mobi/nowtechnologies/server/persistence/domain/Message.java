package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;
import mobi.nowtechnologies.server.shared.enums.MessageType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mayboroda Dmytro
 * @author Titov Mykhaylo (titov)
 */
@Entity
@Table(name = "messages", uniqueConstraints = @UniqueConstraint(columnNames = {"position", "community_id", "messageType", "publishTimeMillis"}))
public class Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(Message.class);

    private Integer id;

    private Community community;

    private Integer communityId;

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

    private String actionButtonText;

    public Message() {
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
        if (community != null) {
            communityId = community.getId();
        }
    }

    @Column(name = "community_id", insertable = false, updatable = false)
    public Integer getCommunityId() {
        return communityId;
    }

    protected void setCommunityId(Integer communityId) {
        this.communityId = communityId;
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

    @Enumerated(EnumType.STRING)
    public MessageType getMessageType() {
        return this.messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
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

    public String getActionButtonText() {
        return actionButtonText;
    }

    public void setActionButtonText(String actionButtonText) {
        this.actionButtonText = actionButtonText;
    }

    public Message withActivated(boolean activated) {
        this.activated = activated;
        return this;
    }

    public Message withCommunity(Community community) {
        this.community = community;
        return this;
    }

    public Message withBody(String body) {
        this.body = body;
        return this;
    }

    public Message withPosition(int position) {
        this.position = position;
        return this;
    }

    public Message withPublishTimeMillis(long publishTimeMillis) {
        this.publishTimeMillis = publishTimeMillis;
        return this;
    }

    public Message withTitle(String title) {
        this.title = title;
        return this;
    }

    public Message withMessageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public Message withActionType(MessageActionType actionType) {
        this.actionType = actionType;
        return this;
    }

    public Message withAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("communityId", communityId).append("title", title).append("body", body).append("activated", activated).append("frequence", frequence)
                                        .append("messageType", messageType).append("publishTimeMillis", publishTimeMillis).append("position", position).append("imageFileName", imageFileName)
                                        .append("actionType", actionType).append("action", action).append("actionButtonText", actionButtonText).toString();
    }
}