package mobi.nowtechnologies.server.web.model;

import mobi.nowtechnologies.server.persistence.domain.User;

import java.util.Map;

public interface PinModelService {
    Map<String, Object> getModel(User user, String phone);
}
