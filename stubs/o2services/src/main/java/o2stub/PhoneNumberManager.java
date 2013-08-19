package o2stub;

import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class PhoneNumberManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberManager.class);

	private static final PhoneNumberManager INSTANCE = new PhoneNumberManager();

	private static final Map<String, SubsData> phonesMap = new HashMap<String, SubsData>();

	public static PhoneNumberManager getInstance() {
		return INSTANCE;
	}

	public synchronized SubsData getData(String phone) {
		String numberWithCode = getNumberWithCode(phone);

		SubsData d = phonesMap.get(numberWithCode);
		LOGGER.info("getData: " + numberWithCode + " d=" + d);
		if (d == null) {
			
			d = new SubsData();
			d.setPhoneNumberWithCode(numberWithCode);
			calculateFromPhoneNumber(phone, d);
		}
		return d;
	}

	public List<SubsData> list(){
		List<SubsData> res=Lists.newArrayList();
		for(String key: phonesMap.keySet()){
			
			SubsData d=phonesMap.get(key);
			d.setPhoneNumberWithCode(key);
			res.add(d);
		}
		return res;
	}
	
	public synchronized void setPhoneDetails(String phone, SubsData subsData) {
	
		String numberWithCode = getNumberWithCode(phone);

		LOGGER.info("setting phone details " + numberWithCode + " " + subsData);

		subsData.setPhoneNumberWithCode(numberWithCode);
		phonesMap.put(numberWithCode, subsData);
	}

	private void calculateFromPhoneNumber(String phone, SubsData d) {
		d.setO2(true);
		if (phone.endsWith("0")) {
			d.setPayAsYouGo(false);
			d.setBusiness(false);
			d.setTariff4G(false);
		} else if (phone.endsWith("1")) {
			d.setPayAsYouGo(true);
			d.setBusiness(false);
			d.setTariff4G(false);
		} else if (phone.endsWith("2")) {
			d.setPayAsYouGo(false);
			d.setBusiness(true);
			d.setTariff4G(false);
		} else if (phone.endsWith("3")) {
			d.setPayAsYouGo(false);
			d.setBusiness(false);
			d.setTariff4G(true);
		} else {
			d.setO2(false);
		}

		d.setDirectChannel4G(true);
	}

	public static String getNumberWithCode(String phone) {
		if (phone == null) {
			return "";
		}

		if (phone.startsWith("0")) {
			phone = "+44" + phone.substring(1);
		}
		return phone;
	}

}
