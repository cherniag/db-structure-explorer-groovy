package mobi.nowtechnologies.server.apptests.email;

import java.util.Map;

public class MailModelSerializer {
    public String serialize(Map<String, String> model) {
        StringBuilder serialized = new StringBuilder();
        for (Map.Entry<String, String> entry : model.entrySet()) {
            serialized.append(entry.getKey()).append('=').append(entry.getValue()).append(';');
        }
        return serialized.toString();
    }
}
