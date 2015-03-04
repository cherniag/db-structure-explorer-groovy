package mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ListValueWrapperDeserializer.class)
public class ListValueWrapper {

    String value;
    List<Integer> values;

    public String getValue() {
        return value;
    }

    public List<Integer> getValues() {
        return values;
    }
}
