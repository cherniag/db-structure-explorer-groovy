package mobi.nowtechnologies.server.service.impl;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.error.ThrottlingException;
import mobi.nowtechnologies.server.service.ThrottlingService;
import net.spy.memcached.CASMutation;
import net.spy.memcached.CASMutator;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.IntegerTranscoder;
import net.spy.memcached.transcoders.SerializingTranscoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThrottlingServiceImpl implements ThrottlingService {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public final static String THROTTLING_HEADER = "X-Throttling-3g";
	public final static String MAX_AMOUNT_OF_REQUESTS = "max_requests_amount";
	public final static String CACHE_EXPIRATION_TIME = "cache_expiration_time";
	public final static String THROTTLING_IS_ON = "throttling";
	public final static Integer MAX_AMOUNT_OF_REQUESTS_DEFAULT_VALUE = 27;
	public static final int CACHE_EXPIRE_SEC = 60;
	
	private MemcachedClient memcachedClient;
	
	@Override
	public boolean handle(HttpServletRequest request, String username, String communityUrl) throws ThrottlingException {
		if (isActive() && request.getHeader(THROTTLING_HEADER) != null && request.getHeader(THROTTLING_HEADER).equalsIgnoreCase("true")) {
			try {
				int maxRequests = getMaxAmountOfRequests();
				int i=0;
				boolean reject = false;
				do {
					if (mayProceed(i)) {
						reject = false;
						break;
					}
					reject = true;
					i++;
				} while (i < maxRequests);
				if (reject)
					return false;
			} finally {
				
			}
		}
		return true;
	}
		
	protected boolean mayProceed(final int i) {
		CASValue<Object> casValue = memcachedClient.gets("thread"+i, new SerializingTranscoder());
		if (null == casValue) {
			CASMutator<Object> mutator = new CASMutator<Object>(memcachedClient, new SerializingTranscoder());
			CASMutation<Object> m = new CASMutation<Object>() {
				public Object getNewValue(Object current) {
					return current;
				}
			};
			try {
				mutator.cas("thread"+i, "INCOME", getCacheExpirationTime(), m);
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
			return true;
		}
		return false;
	}

	protected int getMaxAmountOfRequests() {
		CASValue<Integer> casValue = memcachedClient.gets(MAX_AMOUNT_OF_REQUESTS, new IntegerTranscoder());
		return (null!=casValue)?casValue.getValue():MAX_AMOUNT_OF_REQUESTS_DEFAULT_VALUE;
	}
	
	protected int getCacheExpirationTime () throws Exception {
		CASValue<Integer> casValue = memcachedClient.gets(CACHE_EXPIRATION_TIME, new IntegerTranscoder());
		return (null!=casValue)?casValue.getValue():CACHE_EXPIRE_SEC;
	}
	
	public boolean isActive() {
		Object result = memcachedClient.get(THROTTLING_IS_ON);
		return (null != result && "1".equals(result)) ? true : false;
	}
	
	
	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
}