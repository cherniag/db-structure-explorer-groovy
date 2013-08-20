package mobi.nowtechnologies.o2;

import java.net.URL;
import java.util.List;

import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Lists;

public class UpdatePhoneClient {
	private static final String serverUrl = "http://localhost:8998/updatePhone";

	public String updatePhone(String phone, O2SubscriberData data) {
		try {
			List<NameValuePair> req = Lists.newArrayList();
			addParam(req, "phone_number", phone);
			addParam(req, "o2", "" + data.isProviderO2());
			addParam(req, "business", "" + data.isBusinessOrConsumerSegment());
			addParam(req, "payAsYouGo", "" + data.isContractPrePay());
			addParam(req, "tariff4G", "" + data.isTariff4G());
			addParam(req, "directChannel4G", "" + data.isDirectOrIndirect4GChannel());

			WebRequest request = new WebRequest(new URL(serverUrl), HttpMethod.POST);
			request.setRequestParameters(req);

			WebClient webClient = new WebClient();

			Page page = webClient.getPage(request);
			return page.getWebResponse().getContentAsString();

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	private void addParam(List<NameValuePair> req, String name, String value) {
		req.add(new NameValuePair(name, value));
	}
}
