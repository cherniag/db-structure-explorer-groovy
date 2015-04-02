package mobi.nowtechnologies.server.service.impl;


import mobi.nowtechnologies.server.service.MessageNotificationService;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

//
// replace with
// mobi.nowtechnologies.server.web.model.CommunityServiceFactory
//
public class SmsServiceFacade implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private CommunityResourceBundleMessageSource messageSource;

    public SMSGatewayService getSMSProvider(String communityUrl) {
        String smsProviderBeanName = messageSource.getMessage(communityUrl, "service.bean.smsProvider", null, null);
        return applicationContext.getBean(smsProviderBeanName, SMSGatewayService.class);
    }

    public MessageNotificationService getMessageNotificationService(String communityUrl) {
        String messageNotificationServiceBeanName = messageSource.getMessage(communityUrl, "service.bean.messageNotificationService", null, null);
        return applicationContext.getBean(messageNotificationServiceBeanName, MessageNotificationService.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
