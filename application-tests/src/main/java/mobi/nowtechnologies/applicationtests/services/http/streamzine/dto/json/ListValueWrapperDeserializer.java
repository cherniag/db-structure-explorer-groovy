package mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ListValueWrapperDeserializer extends JsonDeserializer<ListValueWrapper> {

    @Override
    public ListValueWrapper deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        ListValueWrapper wrapper = new ListValueWrapper();

        if (node instanceof ArrayNode) {
            ArrayNode n = (ArrayNode) node;
            List<JsonNode> elements = Lists.newArrayList(n.elements());

            wrapper.values = Lists.transform(elements, new Function<JsonNode, Integer>() {
                @Override
                public Integer apply(JsonNode input) {
                    return Integer.parseInt(input.asText());
                }
            });
        }
        else {
            wrapper.value = node.asText();
        }
        return wrapper;
    }
}
