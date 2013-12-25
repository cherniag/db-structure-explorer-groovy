package mobi.nowtechnologies.server.persistence.utils;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Alexsandr_Kolpakov
 * Date: 12/20/13
 * Time: 3:36 PM
 */
public class SQLTestInitializer {
    public SQLUtils sqlUtils;
    protected UserRepository userRepository;
    protected DrmRepository drmRepository;
    protected PaymentDetailsRepository paymentDetailsRepository;
    protected ChartRepository chartRepository;
    protected ChartDetailRepository chartDetailRepository;

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
    public void prepareDynamicTestData() throws Exception{
        if(userRepository.count() != 0){
            cleanDynamicTestData();
        }

        sqlUtils.importScript("classpath:META-INF/dynamic-test-data.sql");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void cleanDynamicTestData(){
        List<PaymentDetails> paymentDetails = paymentDetailsRepository.findAll();
        for(PaymentDetails paymentDetail : paymentDetails){
            if(paymentDetail.getOwner() != null){
                paymentDetail.getOwner().setCurrentPaymentDetails(null);
                paymentDetailsRepository.delete(paymentDetail);
            }
        }

        List<Chart> charts = new ArrayList<Chart>();
        List<ChartDetail> chartDetails = chartDetailRepository.findAll();
        for (ChartDetail chartDetail : chartDetails) {
            if(chartDetail.getChart().getI() > 10){
                chartDetailRepository.delete(chartDetail);
                charts.add(chartDetail.getChart());
            }
        }
        chartRepository.delete(charts);

        drmRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.flush();
    }
}
