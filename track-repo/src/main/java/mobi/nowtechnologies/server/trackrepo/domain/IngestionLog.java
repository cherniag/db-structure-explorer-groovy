package mobi.nowtechnologies.server.trackrepo.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class IngestionLog extends AbstractEntity {

	
	@Basic(optional=false)
    @Column(name="Ingestor")
	protected String ingestor;

	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
    @Column(name="IngestionDate")
    protected Date ingestionDate;
	
	@Basic(optional=false)
    @Column(name="Status")
    protected Boolean status;
	
	@Basic(optional=true)
    @Column(name="DropName")
    protected String dropName;

	@Basic(optional=true)
    @Column(name="Message")
    protected String message;

	@OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="DropId") 
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
        return "IngestionLog{" +
                "ingestor='" + ingestor + '\'' +
                ", ingestionDate=" + ingestionDate +
                ", status=" + status +
                ", dropName='" + dropName + '\'' +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}
