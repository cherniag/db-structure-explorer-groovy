package mobi.nowtechnologies.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class IngestionLog extends AbstractEntity {

	
	@Basic(optional=false)
	protected String Ingestor;

	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	protected Date IngestionDate;	
	
	@Basic(optional=false)
	protected Boolean Status;	
	
	@Basic(optional=true)
	protected String DropName;	


	@Basic(optional=true)
	protected String Message;	


	@OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="DropId") 
	protected Set<DropContent> Content;


	public String getIngestor() {
		return Ingestor;
	}


	public void setIngestor(String ingestor) {
		Ingestor = ingestor;
	}


	public Date getIngestionDate() {
		return IngestionDate;
	}


	public void setIngestionDate(Date ingestionDate) {
		IngestionDate = ingestionDate;
	}


	public Boolean getStatus() {
		return Status;
	}


	public void setStatus(Boolean status) {
		Status = status;
	}


	public Set<DropContent> getContent() {
		return Content;
	}


	public void setContent(Set<DropContent> content) {
		Content = content;
	}


	public String getMessage() {
		return Message;
	}


	public void setMessage(String message) {
		Message = message;
	}


	public String getDropName() {
		return DropName;
	}


	public void setDropName(String drop) {
		DropName = drop;
	} 


	


}
