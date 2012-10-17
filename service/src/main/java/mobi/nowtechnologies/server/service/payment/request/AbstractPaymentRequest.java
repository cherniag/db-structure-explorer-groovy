package mobi.nowtechnologies.server.service.payment.request;

import java.util.ArrayList;
import java.util.List;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public abstract class AbstractPaymentRequest<T extends AbstractPaymentRequest<?>> {
	
	private final List<NameValuePair> nameValuePairs;
	
	public AbstractPaymentRequest() {
		nameValuePairs = new ArrayList<NameValuePair>();
	}
	
	@SuppressWarnings("unchecked")
	public T addParam(PaymentRequestParam name, String value) {
		nameValuePairs.add(new BasicNameValuePair(name.toString(), value));
		return (T) this;
	}
	
	public List<NameValuePair> build() {
		return nameValuePairs;
	}
}