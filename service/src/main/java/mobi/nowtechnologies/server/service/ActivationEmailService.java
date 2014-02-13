package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;

public interface ActivationEmailService {

    void activate(Long id, String email, String token);

    ActivationEmail sendEmail(String email, String userName, String deviceUID, String community);
}
