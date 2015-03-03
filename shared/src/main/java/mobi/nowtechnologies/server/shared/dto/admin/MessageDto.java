package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;
import mobi.nowtechnologies.server.shared.enums.MessageType;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 */
public class MessageDto {

    public static final String MESSAGE_DTO = "MESSAGE_DTO";

    public static final String MESSAGE_DTO_LIST = "MESSAGE_DTO_LIST";

    private Integer position;

    private Integer id;

    private String headline = "";

    private String body;

    private boolean activated;

    private MessageFrequence frequence;

    private MessageType messageType;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date publishTime;

    private Set<FilterDto> filterDtos;

    private MultipartFile file;

    private MessageActionType actionType;

    private String action;

    private String actionButtonText;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public MessageFrequence getFrequence() {
        return frequence;
    }

    public void setFrequence(MessageFrequence frequence) {
        this.frequence = frequence;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Set<FilterDto> getFilterDtos() {
        return filterDtos;
    }

    public void setFilterDtos(Set<FilterDto> filterDtos) {
        this.filterDtos = filterDtos;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public MessageDto withPosition(Integer position) {
        setPosition(position);
        return this;
    }

    public MessageDto withId(Integer id) {
        setId(id);
        return this;
    }

    public MessageDto withHeadline(String headline) {
        setHeadline(headline);
        return this;
    }

    public MessageDto withBody(String body) {
        setBody(body);
        return this;
    }

    public MessageDto withActivated(boolean activated) {
        setActivated(activated);
        return this;
    }

    public MessageDto withFrequence(MessageFrequence frequence) {
        setFrequence(frequence);
        return this;
    }

    public MessageDto withMessageType(MessageType messageType) {
        setMessageType(messageType);
        return this;
    }

    public MessageDto withPublishTime(Date publishTime) {
        setPublishTime(publishTime);
        return this;
    }

    public MessageDto withFilterDtos(Set<FilterDto> filterDtos) {
        setFilterDtos(filterDtos);
        return this;
    }

    public MessageDto withFile(MultipartFile file) {
        setFile(file);
        return this;
    }

    public MessageDto withActionType(MessageActionType actionType) {
        setActionType(actionType);
        return this;
    }

    public MessageDto withAction(String action) {
        setAction(action);
        return this;
    }

    public MessageDto withActionButtonText(String actionButtonText) {
        setActionButtonText(actionButtonText);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("position", position).append("id", id).append("headline", headline).append("body", body).append("activated", activated).append("frequence", frequence)
                                        .append("messageType", messageType).append("publishTime", publishTime).append("filterDtos", filterDtos).append("file", file).append("actionType", actionType)
                                        .append("action", action).append("actionButtonText", actionButtonText).toString();
    }


}
