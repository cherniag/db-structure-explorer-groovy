package mobi.nowtechnologies.server.transport.service;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import junit.framework.Assert;
import mobi.nowtechnologies.server.job.CleanExpirePendingPaymentsJob;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by oar on 12/20/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:transport-servlet-test.xml",
        "classpath:META-INF/service-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/dao-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/shared.xml",
        "classpath:post-service-test.xml"}, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.AccCheckController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class FailedSmsAfterFailedPaymentForO2IT {

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
        List<PendingPayment> createPendingPayments = pendingPaymentService.createPendingPayments();
        User currentUser = userRepository.findOne(1);
        currentUser.getUserGroup().setCommunity(communityRepository.findByRewriteUrlParameter("o2"));
        PaymentDetails paymentDetails = paymentDetailsRepository.findOne(4L);
        paymentDetails.setOwner(currentUser);
        paymentDetails.setMadeRetries(paymentDetails.getRetriesOnError());
        currentUser.setCurrentPaymentDetails(paymentDetails);
        assertEquals(createPendingPayments.size(), 1);
        PendingPayment pendingPayment = Iterables.getFirst(createPendingPayments, null);
        pendingPayment.setPaymentDetails(paymentDetails);
        pendingPayment.setExpireTimeMillis(0);
        pendingPayment.setPaymentSystem("o2Psms");
        expirePendingPaymentsJob.execute();

        monitor.waitToComplete(5000);

        File smsFile = getLastSmsFile(time);
        List<String> smsText = Files.readLines(smsFile, Charsets.UTF_8);
        assertTrue(smsText.contains("URL: " + smsUrl));

   }

    private File getLastSmsFile(long time) {
        String[] list = smsTemporaryFolder.list(new AgeFileFilter(time, false));

        Assert.assertEquals(1, list.length);

        return new File(smsTemporaryFolder, list[0]);
    }


}
