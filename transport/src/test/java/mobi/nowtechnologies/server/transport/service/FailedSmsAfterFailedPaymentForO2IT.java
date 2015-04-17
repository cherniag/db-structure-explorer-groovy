package mobi.nowtechnologies.server.transport.service;

import mobi.nowtechnologies.server.job.CleanExpirePendingPaymentsJob;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.support.http.PostsSaverPostService;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.RETRY;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;

import javax.annotation.Resource;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.base.Charsets.UTF_8;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy(
    {@ContextConfiguration(locations = {"classpath:transport-root-test.xml", "classpath:post-service-test.xml"}), @ContextConfiguration(locations = {"classpath:transport-servlet-test.xml"})})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class FailedSmsAfterFailedPaymentForO2IT {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CleanExpirePendingPaymentsJob expirePendingPaymentsJob;

    @Resource
    private PendingPaymentService pendingPaymentService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private PaymentDetailsRepository paymentDetailsRepository;

    @Resource
    private CommunityRepository communityRepository;

    @Value("${sms.temporaryFolder}")
    private File smsTemporaryFolder;

    @Value("${mig.freeSMSURL}")
    private String smsUrl;

    @Resource
    private PostsSaverPostService postsSaverPostService;

    @Test
    public void test() throws Exception {
        PostsSaverPostService.Monitor monitor = postsSaverPostService.getMonitor();
        final long time = new Date().getTime();
        logger.info("Start time {}", time);

        List<PendingPayment> pendingPayments = pendingPaymentService.createPendingPayments();
        User currentUser = userRepository.findOne(101);
        currentUser.getUserGroup().setCommunity(communityRepository.findByRewriteUrlParameter("o2"));

        PaymentDetails paymentDetails = paymentDetailsRepository.findOne(4L);
        paymentDetails.setOwner(currentUser);
        paymentDetails.withLastPaymentStatus(ERROR);
        paymentDetails.withMadeRetries(paymentDetails.getRetriesOnError() - 1);
        currentUser.setCurrentPaymentDetails(paymentDetails);

        assertEquals(pendingPayments.size(), 1);

        PendingPayment pendingPayment = Iterables.getFirst(pendingPayments, null);
        pendingPayment.setPaymentDetails(paymentDetails);
        pendingPayment.setExpireTimeMillis(0);
        pendingPayment.setPaymentSystem("o2Psms");
        pendingPayment.setType(RETRY);

        expirePendingPaymentsJob.execute();

        monitor.waitToComplete(5000);

        File smsFile = getLastSmsFile(time);
        List<String> smsText = Files.readLines(smsFile, UTF_8);
        assertTrue(smsText.contains("URL: " + smsUrl));

    }

    private File getLastSmsFile(long time) {
        File[] list = smsTemporaryFolder.listFiles(new TimestampExtFileNameFilter(time));

        Assert.assertEquals(1, list.length);

        return list[0];
    }


}
