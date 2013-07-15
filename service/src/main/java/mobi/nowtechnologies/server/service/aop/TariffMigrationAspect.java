package mobi.nowtechnologies.server.service.aop;

import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Titov Mykhaylo (titov)
 * 15.07.13 14:37
 */
@Aspect
public class TariffMigrationAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TariffMigrationAspect.class);

    @Around("execution(* mobi.nowtechnologies.server.service.payment.impl.O2PaymentServiceImpl.commitPaymentDetails(..))")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();

        try {
            User user = (User) joinPoint.getArgs()[0];
            PaymentPolicy paymentPolicy = (PaymentPolicy) joinPoint.getArgs()[1];


        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return object;
    }

}
