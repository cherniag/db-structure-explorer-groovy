package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Maksym Chernolevskyi (maksym)
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
public class Response {
    @JsonProperty(value = "data")
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
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
