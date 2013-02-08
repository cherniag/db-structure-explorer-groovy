package mobi.nowtechnologies.server.service;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.error.ThrottlingException;
import net.spy.memcached.CASMutation;
import net.spy.memcached.CASMutator;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.IntegerTranscoder;
import net.spy.memcached.transcoders.LongTranscoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThrottlingServiceImpl implements ThrottlingService {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public final static String THROTTLING_HEADER = "X-Throttling-3g";
	public final static String MAX_AMOUNT_OF_REQUESTS = "max_requests_amount";
	public final static String CACHE_EXPIRATION_TIME = "cache-expiration-time";
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
						if (!shouldReject(i)) {
							reject = false;
							break;
						}
						reject = true;
						i++;
					} while (i < maxRequests);
					
					if (reject)
						throw new ThrottlingException(username, communityUrl);
				} catch (Exception e) {
					LOGGER.error("Error while making throtlling", e);
				}
			return true;
		}
		return false;
	}
		
	protected boolean shouldReject(final int i) throws Exception {
		final Long initial = new Long(i);
		CASMutator<Long> mutator = new CASMutator<Long>(memcachedClient, new LongTranscoder());
		CASMutation<Long> m = new CASMutation<Long>() {
			public Long getNewValue(Long current) {
				return initial.equals(current)?Long.MAX_VALUE:initial;
			}
		};
		if(mutator.cas("thread-"+i, initial, getCacheExpirationTime(), m) != Long.MAX_VALUE)
			return false;
		return true;
	}

	protected int getMaxAmountOfRequests() throws Exception {
		CASMutator<Integer> mutator = new CASMutator<Integer>(memcachedClient, new IntegerTranscoder());
		CASMutation<Integer> m = new CASMutation<Integer>() {
			public Integer getNewValue(Integer current) {
				return current;
			}
		};
		return mutator.cas(MAX_AMOUNT_OF_REQUESTS, MAX_AMOUNT_OF_REQUESTS_DEFAULT_VALUE, 0, m);
	}
	
	protected int getCacheExpirationTime () throws Exception {
		CASMutator<Integer> mutator = new CASMutator<Integer>(memcachedClient, new IntegerTranscoder());
		CASMutation<Integer> m = new CASMutation<Integer>() {
			public Integer getNewValue(Integer current) {
				return current;
			}
		};
		return mutator.cas(CACHE_EXPIRATION_TIME, CACHE_EXPIRE_SEC, 0, m);
	}
	
	public boolean isActive() {
		Object result = memcachedClient.get(THROTTLING_IS_ON);
		if (null == result)
			return false;
		return (Boolean) result;
	}
	
	
	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
}