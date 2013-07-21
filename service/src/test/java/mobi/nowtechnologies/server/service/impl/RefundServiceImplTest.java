package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.RefundRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * User: Titov Mykhaylo (titov)
 * 16.07.13 9:02
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = Utils.class)
public class RefundServiceImplTest {

    User user;
    RefundServiceImpl dataToDoRefundService;
    RefundRepository refundRepositoryMock;
    long expectedLogTimeMillis;
    Refund resultRefund;
    int nextSubPayment;
    Tariff userTariff;
    Tariff newUserTariff;

    @Before
    public void setUp(){
        refundRepositoryMock = mock(RefundRepository.class);
        dataToDoRefundService = new RefundServiceImpl();
        dataToDoRefundService.setRefundRepository(refundRepositoryMock);
    }
    
    

}
