package mobi.nowtechnologies.server.service.payment.http;

import mobi.nowtechnologies.server.shared.service.PostService;

public abstract class PaymentHttpService {
	
	protected PostService httpService;
	
	public void setPostService(PostService httpService) {
		this.httpService = httpService;
	}
	
	public PostService getPostService() {
		return httpService;
	}
}