package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;

public interface ActivationEmailService {
    void save(ActivationEmail activationEmail);

    void sendEmail(String email, String userName, String deviceUID, String community);
}
