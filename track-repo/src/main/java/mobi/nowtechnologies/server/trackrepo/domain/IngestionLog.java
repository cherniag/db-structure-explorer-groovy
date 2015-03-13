package mobi.nowtechnologies.server.trackrepo.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Entity
@Inheritance(strategy = JOINED)
public class IngestionLog extends AbstractEntity {

    @Column(name = "Ingestor", nullable = false)
    protected String ingestor;

    @Temporal(TIMESTAMP)
    @Column(name = "IngestionDate", nullable = false)
    protected Date ingestionDate;

    @Column(name = "Status", nullable = false)
    protected Boolean status;

    @Basic(optional = true)
    @Column(name = "DropName")
    protected String dropName;

    @Column(name = "Message")
    protected String message;

    @OneToMany(cascade = ALL)
    @JoinColumn(name = "DropId")
    protected Set<DropContent> content;

    public String getIngestor() {
        return ingestor;
    }

    public void setIngestor(String ingestor) {
        this.ingestor = ingestor;
    }

    public Date getIngestionDate() {
        return ingestionDate;
    }

    public void setIngestionDate(Date ingestionDate) {
        this.ingestionDate = ingestionDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDropName() {
        return dropName;
    }

    public void setDropName(String dropName) {
        this.dropName = dropName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<DropContent> getContent() {
        return content;
    }

    public void setContent(Set<DropContent> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("ingestor", ingestor).append("ingestionDate", ingestionDate).append("status", status)
                                                            .append("dropName", dropName).append("message", message).toString();
    }
}
