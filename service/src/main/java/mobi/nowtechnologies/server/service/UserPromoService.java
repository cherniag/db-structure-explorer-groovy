package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;

public interface UserPromoService {

    User applyInitPromoByEmail(User user, Long activationEmailId, String email);
}
