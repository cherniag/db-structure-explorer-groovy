package mobi.nowtechnologies.server.service.aop;

import mobi.nowtechnologies.server.service.payment.request.MigRequest.MigRequestParam;
import mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam;
import mobi.nowtechnologies.server.service.payment.request.SagePayRequest.SageRequestParam;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.util.MultiValueMap;

/**
 * @author Titov Mykhaylo (titov)
 */
@Aspect
public class ProfileLoggingAspect {

    public static final Logger THIRD_PARTY_REQUESTS_PROFILE_LOGGER = LoggerFactory.getLogger("THIRD_PARTY_REQUESTS_PROFILE_LOGGER");
    private static final String[] logNameArrayForNameValuePair = new String[] {SageRequestParam.TxType.name(),
                                                                               SageRequestParam.VendorTxCode.name(),
                                                                               SageRequestParam.Amount.name(),
                                                                               SageRequestParam.Currency.name(),
                                                                               SageRequestParam.Description.name(),
                                                                               SageRequestParam.Vendor.name(),
                                                                               SageRequestParam.VPSProtocol.name(),
                                                                               SageRequestParam.VendorTxCode.name(),
                                                                               SageRequestParam.VPSTxId.name(),
                                                                               SageRequestParam.TxAuthNo.name(),
                                                                               SageRequestParam.ReleaseAmount.name(),
                                                                               SageRequestParam.RelatedVPSTxId.name(),
                                                                               SageRequestParam.RelatedVendorTxCode.name(),
                                                                               SageRequestParam.RelatedTxAuthNo.name(),
                                                                               PayPalRequestParam.L_BILLINGAGREEMENTDESCRIPTION0.name(),
                                                                               PayPalRequestParam.L_BILLINGTYPE0.name(),
                                                                               PayPalRequestParam.PAYMENTACTION.name(),
                                                                               PayPalRequestParam.CURRENCYCODE.name(),
                                                                               PayPalRequestParam.RETURNURL.name(),
                                                                               PayPalRequestParam.CANCELURL.name(),
                                                                               PayPalRequestParam.TOKEN.name(),
                                                                               PayPalRequestParam.METHOD.name(),
                                                                               PayPalRequestParam.VERSION.name(),
                                                                               PayPalRequestParam.REFERENCEID.name(),
                                                                               PayPalRequestParam.AMT.name(),
                                                                               MigRequestParam.OADC.name(),
                                                                               MigRequestParam.OADCTYPE.name(),
                                                                               MigRequestParam.MESSAGEID.name(),
                                                                               MigRequestParam.BODY.name(),
                                                                               MigRequestParam.TIMETOLIVE.name()};
    private static final Pattern RECEIPT_DATA_PATTERN = Pattern.compile("\"receipt-data\"\\s*:\\s*\"(.*?)\"");
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileLoggingAspect.class);

    private List<String> logNameListForNameValuePair = Arrays.asList(logNameArrayForNameValuePair);

    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    /** logs third party request to CSV file */
    public static void logThirdPartyRequest(long beforeExecutionTimeNano, Throwable throwable, String body, Object responseMessage, String url) {
        try {
            if (THIRD_PARTY_REQUESTS_PROFILE_LOGGER.isDebugEnabled()) {
                String errorMessage = null;
                String result = "success";
                if (throwable != null) {
                    errorMessage = throwable.getMessage();
                    result = "fail";
                }
                long executionDurationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beforeExecutionTimeNano);
                Object nameValuePairs = null;
                LogUtils.set3rdParyRequestProfileMDC(executionDurationMillis, errorMessage, result, url, nameValuePairs, body, responseMessage);
                THIRD_PARTY_REQUESTS_PROFILE_LOGGER.debug("THIRD_PARTY_REQUESTS_PROFILE_LOGGER values in the MDC");
            }
        } catch (Exception ex) {
            LOGGER.error("Can't log request ", ex);
        }
    }

    public void setCommunityResourceBundleMessageSource(CommunityResourceBundleMessageSource communityResourceBundleMessageSource) {
        this.communityResourceBundleMessageSource = communityResourceBundleMessageSource;
    }

    @Around("execution(* mobi.nowtechnologies.server.support.http.PostService.sendHttpPost(..))")
    public Object aroundPostService_sendHttpPost(ProceedingJoinPoint joinPoint) throws Throwable {
        Throwable throwable = null;
        Object[] args = null;
        Long beforeExecutionTimeNano = null;
        Object postServiceResponseObject = null;

        try {
            beforeExecutionTimeNano = System.nanoTime();
            args = joinPoint.getArgs();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            postServiceResponseObject = joinPoint.proceed();

            return postServiceResponseObject;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            profilePostService(args, beforeExecutionTimeNano, postServiceResponseObject, throwable);
        }
    }

    @Around("execution(* mobi.nowtechnologies.server.service.o2.impl.WebServiceGateway.sendAndReceive(..))")
    public Object aroundWebServiceGateway_sendAndReceiveMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Throwable throwable = null;
        Object[] args = null;
        Long beforeExecutionTimeNano = null;
        Object responseObject = null;

        try {
            beforeExecutionTimeNano = System.nanoTime();
            args = joinPoint.getArgs();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            responseObject = joinPoint.proceed();

            return responseObject;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            profileWebServiceGateway(args, beforeExecutionTimeNano, responseObject, throwable);
        }
    }

    @Around("execution(* org.springframework.web.client.RestTemplate.postForObject(..))")
    public Object aroundRestTemplate_postForObject(ProceedingJoinPoint joinPoint) throws Throwable {
        Throwable throwable = null;
        Object[] args = null;
        Long beforeExecutionTimeNano = null;
        Object responseObject = null;

        try {
            beforeExecutionTimeNano = System.nanoTime();
            args = joinPoint.getArgs();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            responseObject = joinPoint.proceed();

            return responseObject;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            profileRestTemplate(args, beforeExecutionTimeNano, responseObject, throwable);
        }
    }

    @SuppressWarnings("unchecked")
    private void profileRestTemplate(Object[] args, Long beforeExecutionTimeNano, Object responseObject, Throwable throwable) {
        try {
            if (THIRD_PARTY_REQUESTS_PROFILE_LOGGER.isDebugEnabled()) {
                String url = (String) args[0];
                String body = null;
                MultiValueMap<String, Object> multiValueMap = null;
                if (args[1] != null && args[1] instanceof MultiValueMap) {
                    multiValueMap = (MultiValueMap<String, Object>) args[1];
                    body = multiValueMap.toString();
                }

                if (responseObject instanceof DOMSource) {
                    StringWriter stringWriter = new StringWriter();
                    Result result = new StreamResult(stringWriter);
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer transformer = factory.newTransformer();
                    transformer.transform((DOMSource) responseObject, result);
                    responseObject = stringWriter.getBuffer().toString();
                }

                commonProfileLogic(beforeExecutionTimeNano, responseObject, throwable, url, null, body);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    private void profileWebServiceGateway(Object[] args, long beforeExecutionTimeNano, Object responseObject, Throwable throwable) {
        try {
            if (THIRD_PARTY_REQUESTS_PROFILE_LOGGER.isDebugEnabled()) {
                String url = (String) args[0];
                String body = null;
                if (args[1] != null) {
                    body = args[1].toString();
                }

                commonProfileLogic(beforeExecutionTimeNano, responseObject, throwable, url, null, body);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void profilePostService(Object[] args, long beforeExecutionTimeNano, Object responseObject, Throwable throwable) {
        try {
            if (THIRD_PARTY_REQUESTS_PROFILE_LOGGER.isDebugEnabled()) {
                String url = (String) args[0];
                List<NameValuePair> nameValuePairs = null;
                String body;
                if (args.length == 2) {
                    body = (String) args[1];
                } else {
                    nameValuePairs = (List<NameValuePair>) args[1];
                    body = (String) args[2];
                }

                if (body != null) {
                    Matcher matcher = RECEIPT_DATA_PATTERN.matcher(body);
                    if (matcher.find()) {
                        body = matcher.group(0);
                    } else {
                        body = "seccurity params";
                    }
                }

                if (nameValuePairs != null) {
                    List<String> actualLogNameListForNameValuePair = logNameListForNameValuePair;
                    try {
                        String logNamesStringForNameValuePairFromConfig =
                            communityResourceBundleMessageSource.getMessage(MDC.get(LogUtils.LOG_COMMUNITY), "profileLoggingAspect.logNamesForNameValuePair", null, null);
                        if (!StringUtils.isBlank(logNamesStringForNameValuePairFromConfig)) {
                            actualLogNameListForNameValuePair = Arrays.asList(logNamesStringForNameValuePairFromConfig.split(","));
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    for (Iterator<NameValuePair> iterator = nameValuePairs.iterator(); iterator.hasNext(); ) {
                        NameValuePair nameValuePair = iterator.next();
                        final String name = nameValuePair.getName();
                        if (!actualLogNameListForNameValuePair.contains(name)) {
                            iterator.remove();
                        }

                    }
                }

                commonProfileLogic(beforeExecutionTimeNano, responseObject, throwable, url, nameValuePairs, body);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void commonProfileLogic(long beforeExecutionTimeNano, Object responseObject, Throwable throwable, String url, List<NameValuePair> nameValuePairs, String body) {
        try {
            long afterExecutionTimeNano = System.nanoTime();
            long executionDurationMillis = TimeUnit.NANOSECONDS.toMillis(afterExecutionTimeNano - beforeExecutionTimeNano);

            String result = "success";
            String errorMessage = null;
            if (throwable != null) {
                result = "fail";
                errorMessage = throwable.getMessage();
            }

            LogUtils.set3rdParyRequestProfileMDC(executionDurationMillis, errorMessage, result, url, nameValuePairs, body, responseObject);

            THIRD_PARTY_REQUESTS_PROFILE_LOGGER.debug("THIRD_PARTY_REQUESTS_PROFILE_LOGGER values in the MDC");
        } finally {
            LogUtils.remove3rdParyRequestProfileMDCWithoutSpecific();
        }

    }
}
