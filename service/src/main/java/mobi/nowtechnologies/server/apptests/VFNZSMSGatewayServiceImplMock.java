package mobi.nowtechnologies.server.apptests;

import mobi.nowtechnologies.server.persistence.apptests.domain.Email;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class VFNZSMSGatewayServiceImplMock extends VFNZSMSGatewayServiceImpl {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SMSResponse send(String numbers, String message, String title) {
        entityManager.persist(new Email("", new String[]{numbers}, title, message));
        return new SMSResponse() {
            @Override
            public boolean isSuccessful() {
                return true;
            }
        };
    }
}
