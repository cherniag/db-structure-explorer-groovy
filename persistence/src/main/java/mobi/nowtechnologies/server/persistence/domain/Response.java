package mobi.nowtechnologies.server.persistence.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Response {
	private Object [] object;
	
	public Response() {}
	
	public Response(Object [] object) {
		this.object = object;
	}

	@XmlAnyElement
	public Object [] getObject() {
		return object;
	}

	public void setObject(Object [] object) {
		this.object = object;
	}

	
}
