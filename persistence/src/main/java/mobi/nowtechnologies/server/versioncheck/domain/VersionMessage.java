/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.versioncheck.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
@Entity
@Table(name = "client_version_messages")
public class VersionMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "message_key", columnDefinition = "char(100)", nullable = false)
    private String messageKey;

    @Column(name = "url", columnDefinition = "char(2000)")
    private String url;

    protected VersionMessage() {
    }

    public VersionMessage(String messageKey, String url) {
        this(messageKey);
        this.url = url;
    }

    public VersionMessage(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getUrl() {
        return url;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
