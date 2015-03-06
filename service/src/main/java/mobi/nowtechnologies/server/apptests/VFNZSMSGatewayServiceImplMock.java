package mobi.nowtechnologies.server.apptests;

import mobi.nowtechnologies.server.persistence.apptests.domain.Email;
import mobi.nowtechnologies.server.service.sms.SMPPMessage;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class VFNZSMSGatewayServiceImplMock extends VFNZSMSGatewayServiceImpl {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public SMSResponse send(String numbers, String message, String title) {
        entityManager.persist(new Email("", new String[]{numbers}, title, message));
        return new SMSResponse() {
            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public String getDescriptionError() {
                return null;
            }
        };
    }

    public SMSResponse send(String numbers, String message, String originator, SMSCDeliveryReceipt smscDeliveryReceipt, long expireTimeMillis) {
        return new SMSResponse() {
            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public String getDescriptionError() {
                return null;
            }
        };
    }
}
