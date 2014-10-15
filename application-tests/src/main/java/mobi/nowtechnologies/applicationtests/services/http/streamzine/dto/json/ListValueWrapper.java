package mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

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
