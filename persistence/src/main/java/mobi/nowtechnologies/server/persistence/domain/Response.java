package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Maksym Chernolevskyi (maksym)
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("object", object)
                .toString();
    }
}
