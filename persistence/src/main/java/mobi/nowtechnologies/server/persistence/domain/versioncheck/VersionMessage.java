package mobi.nowtechnologies.server.persistence.domain.versioncheck;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

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

    @Column(name="message_key",columnDefinition="char(100)", nullable = false)
    private String messageKey;

    @Column(name="url",columnDefinition="char(2000)")
    private String url;

    protected VersionMessage() {
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
