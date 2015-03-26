package mobi.nowtechnologies.server.persistence.utils;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.persistence.repository.AppsFlyerDataRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.DrmRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.ReactivationUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UrbanAirshipTokenRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserTransactionRepository;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Alexsandr_Kolpakov Date: 12/20/13 Time: 3:36 PM
 */
//@TODO Remove it ASAP
public class SQLTestInitializer {

    public SQLUtils sqlUtils;
    protected UserRepository userRepository;
    protected DrmRepository drmRepository;
    protected PaymentDetailsRepository paymentDetailsRepository;
    protected ChartRepository chartRepository;
    protected ChartDetailRepository chartDetailRepository;

    @Resource
    private ActivationEmailRepository activationEmailRepository;

    @Resource
    private SubmittedPaymentRepository submittedPaymentRepository;

    @Resource
    private ReactivationUserInfoRepository reactivationUserInfoRepository;

    @Resource
    private StreamzineUpdateRepository streamzineUpdateRepository;

    @Resource
    private AccountLogRepository accountLogRepository;

    @Resource
    private AppsFlyerDataRepository appsFlyerDataRepository;

    @Resource
    private UrbanAirshipTokenRepository urbanAirshipTokenRepository;

    @Resource
    private UserTransactionRepository userTransactionRepository;

    public void setChartDetailRepository(ChartDetailRepository chartDetailRepository) {
        this.chartDetailRepository = chartDetailRepository;
    }

    public void setChartRepository(ChartRepository chartRepository) {

        this.chartRepository = chartRepository;
    }

    public void setDrmRepository(DrmRepository drmRepository) {
        this.drmRepository = drmRepository;
    }

    public void setPaymentDetailsRepository(PaymentDetailsRepository paymentDetailsRepository) {
        this.paymentDetailsRepository = paymentDetailsRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setSqlUtils(SQLUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void prepareDynamicTestData(String... scripts) throws Exception {
        if (userRepository.count() != 0) {
            cleanDynamicTestData();
        }

        sqlUtils.importScript(scripts);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void cleanDynamicTestData() {
        List<PaymentDetails> paymentDetails = paymentDetailsRepository.findAll();
        for (PaymentDetails paymentDetail : paymentDetails) {
            if (paymentDetail.getOwner() != null) {
                paymentDetail.getOwner().setCurrentPaymentDetails(null);
                paymentDetailsRepository.delete(paymentDetail);
            }
        }

        Set<Chart> charts = new HashSet<Chart>();
        List<ChartDetail> chartDetails = chartDetailRepository.findAll();
        for (ChartDetail chartDetail : chartDetails) {
            Chart chart = chartDetail.getChart();
            Integer chartId = chart.getI();
            if (chartId > 10) {
                chartDetailRepository.delete(chartDetail);
                if (chartId != 16) {
                    charts.add(chart);
                }
            }
        }
        chartRepository.delete(charts);

        userTransactionRepository.deleteAll();
        urbanAirshipTokenRepository.deleteAll();
        appsFlyerDataRepository.deleteAll();
        drmRepository.deleteAll();
        accountLogRepository.deleteAll();
        reactivationUserInfoRepository.deleteAll();
        streamzineUpdateRepository.deleteAll();
        userRepository.deleteAll();
        activationEmailRepository.deleteAll();
        submittedPaymentRepository.deleteAll();
        userRepository.flush();
    }
}
