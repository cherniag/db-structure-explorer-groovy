package mobi.nowtechnologies.server.service.aop;

import java.util.List;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.service.PostService.Response;

import org.apache.http.NameValuePair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Aspect
public class ProfileLoggingAspect {

	private static final Logger THIRD_PARTY_REQUESTS_PROFILE_LOGGER = LoggerFactory.getLogger("THIRD_PARTY_REQUESTS_PROFILE_LOGGER");
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileLoggingAspect.class);

	@Around("execution(* mobi.nowtechnologies.server.shared.service.PostService.sendHttpPost(..))")
	public Object aroundPostService_sendHttpPost(ProceedingJoinPoint joinPoint) throws Throwable {
		Throwable throwable = null;
		Object[] args = null;
		Long beforeExecutionTimeMillis = null;
		Object postServiceResponseObject = null;

		try {
			beforeExecutionTimeMillis = Utils.getEpochMillis();
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
			profilePostService(args, beforeExecutionTimeMillis, postServiceResponseObject, throwable);
		}
	}
	
	@Around("execution(* mobi.nowtechnologies.server.service.o2.impl.WebServiceGateway.sendAndReceive(..))")
	public Object aroundWebServiceGateway_sendAndReceiveMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		Throwable throwable = null;
		Object[] args = null;
		Long beforeExecutionTimeMillis = null;
		Object responseObject = null;

		try {
			beforeExecutionTimeMillis = Utils.getEpochMillis();
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
			profileWebServiceGateway(args, beforeExecutionTimeMillis, responseObject, throwable);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void profileWebServiceGateway(Object[] args, long beforeExecutionTimeMillis, Object responseObject, Throwable throwable) {
		try {
			if (THIRD_PARTY_REQUESTS_PROFILE_LOGGER.isDebugEnabled()) {
				String url = (String) args[0];
				String body =(String) args[1];

				long afterExecutionTimeMillis = Utils.getEpochMillis();
				long executionDurationMillis = afterExecutionTimeMillis - beforeExecutionTimeMillis;

				boolean result = true;
				String errorMessage = null;
				if (throwable != null) {
					result = false;
					errorMessage = throwable.getMessage();
				}

				LogUtils.set3rdParyRequestProfileMDC(executionDurationMillis, errorMessage, result, url, null, body, responseObject);

				THIRD_PARTY_REQUESTS_PROFILE_LOGGER.debug("");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			LogUtils.remove3rdParyRequestProfileMDC();
		}
	}

	@SuppressWarnings("unchecked")
	private void profilePostService(Object[] args, long beforeExecutionTimeMillis, Object postServiceResponseObject, Throwable throwable) {
		try {
			if (THIRD_PARTY_REQUESTS_PROFILE_LOGGER.isDebugEnabled()) {
				String url = (String) args[0];
				List<NameValuePair> nameValuePairs = (List<NameValuePair>) args[1];		
				String body = (String) args[2];
				
				//nameValuePairs.remove(o);

				long afterExecutionTimeMillis = Utils.getEpochMillis();
				long executionDurationMillis = afterExecutionTimeMillis - beforeExecutionTimeMillis;

				boolean result = true;
				String errorMessage = null;
				if (throwable != null) {
					result = false;
					errorMessage = throwable.getMessage();
				}

				Response response = (Response) postServiceResponseObject;

				String responseMessage = null;
				if (response != null) {
					responseMessage = response.toString();
				}

				LogUtils.set3rdParyRequestProfileMDC(executionDurationMillis, errorMessage, result, url, nameValuePairs, body, responseMessage);

				THIRD_PARTY_REQUESTS_PROFILE_LOGGER.debug("");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			LogUtils.remove3rdParyRequestProfileMDC();
		}
	}

}
