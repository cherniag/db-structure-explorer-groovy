package mobi.nowtechnologies.server.shared.dto.web.payment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;

import org.hibernate.validator.constraints.NotEmpty;

public class CreditCardDto {
	public static final String NAME = "creditCardDto";
	
	public static final Map<String, Object> staticData = new HashMap<String, Object>();
	public static final String[] selectDates = {"01","02","03","04","05","06","07","08","09","10","11","12"};
	
	public static enum Action {EDIT, PREVIEW}
	
	@NotNull
	private CreditCardType cardType;
	@NotEmpty
	@Size(min=13)
	private String cardNumber;
	private String issueNumber;
	private Integer startDateMonth;
	private Integer startDateYear;
	private Integer expireDateMonth;
	private Integer expireDateYear;
	@NotEmpty
	@Size(min=3, max=4)
	private String securityNumber;
	private Title holderTitle;
	@NotEmpty
	private String holderFirstname;
	@NotEmpty
	private String holderLastname;
	@NotEmpty
	private String holderAddress;
	private String holderAddress2;
	@NotEmpty
	private String holderCity;
	@NotEmpty
	private String holderPostcode;
	@NotEmpty
	private String holderCountry;
	
	private Action action;
	
	public CreditCardDto() {
		action=Action.PREVIEW;
		staticData.put("cardTypes",	CreditCardType.values());
		staticData.put("selectDates", selectDates);
		
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);

		String[] selectYears = new String[11];
		String[] selectExpireYears = new String[selectYears.length];
		for (int i = -5, j = 0; j < selectYears.length; i++, j++) {
			selectYears[j] = String.valueOf(currentYear + i);
			selectExpireYears[j] = String.valueOf(currentYear + j);
		}
		
		staticData.put("selectYears", selectYears);
		staticData.put("selectExpireYears", selectExpireYears);
		staticData.put("titles", Title.values());
	}

	public CreditCardType getCardType() {
		return cardType;
	}

	public void setCardType(CreditCardType cardType) {
		this.cardType = cardType;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}

	public Integer getStartDateMonth() {
		return startDateMonth;
	}

	public void setStartDateMonth(Integer startDateMonth) {
		this.startDateMonth = startDateMonth;
	}

	public Integer getStartDateYear() {
		return startDateYear;
	}

	public void setStartDateYear(Integer startDateYear) {
		this.startDateYear = startDateYear;
	}

	public String getSecurityNumber() {
		return securityNumber;
	}

	public void setSecurityNumber(String securityNumber) {
		this.securityNumber = securityNumber;
	}

	public Title getHolderTitle() {
		return holderTitle;
	}

	public void setHolderTitle(Title holderTitle) {
		this.holderTitle = holderTitle;
	}

	public String getHolderFirstname() {
		return holderFirstname;
	}

	public void setHolderFirstname(String holderFirstname) {
		this.holderFirstname = holderFirstname;
	}

	public String getHolderLastname() {
		return holderLastname;
	}

	public void setHolderLastname(String holderLastname) {
		this.holderLastname = holderLastname;
	}

	public String getHolderAddress() {
		return holderAddress;
	}

	public void setHolderAddress(String holderAddress) {
		this.holderAddress = holderAddress;
	}

	public String getHolderAddress2() {
		return holderAddress2;
	}

	public void setHolderAddress2(String holderAddress2) {
		this.holderAddress2 = holderAddress2;
	}

	public String getHolderCity() {
		return holderCity;
	}

	public void setHolderCity(String holderCity) {
		this.holderCity = holderCity;
	}

	public String getHolderPostcode() {
		return holderPostcode;
	}

	public void setHolderPostcode(String holderPostcode) {
		this.holderPostcode = holderPostcode;
	}

	public String getHolderCountry() {
		return holderCountry;
	}

	public void setHolderCountry(String holderCountry) {
		this.holderCountry = holderCountry;
	}

	public Integer getExpireDateMonth() {
		return expireDateMonth;
	}

	public void setExpireDateMonth(Integer expireDateMonth) {
		this.expireDateMonth = expireDateMonth;
	}

	public Integer getExpireDateYear() {
		return expireDateYear;
	}

	public void setExpireDateYear(Integer expireDateYear) {
		this.expireDateYear = expireDateYear;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
	
	public static PaymentDetailsDto toPaymentDetails(CreditCardDto dto) {
		PaymentDetailsDto result = new PaymentDetailsDto();
			result.setBillingAddress(dto.getHolderAddress()+dto.getHolderAddress2());
			result.setBillingCity(dto.getHolderCity());
			result.setBillingCountry(dto.getHolderCountry());
			result.setBillingPostCode(dto.getHolderPostcode());
			result.setCardCv2(dto.getSecurityNumber());
			result.setCardExpirationDate(toMMYY(dto.getExpireDateMonth(), dto.getExpireDateYear()));
			result.setCardHolderFirstName(dto.getHolderFirstname());
			result.setCardHolderLastName(dto.getHolderLastname());
			result.setCardIssueNumber(dto.getIssueNumber());
			result.setCardNumber(dto.getCardNumber());
			result.setCardStartDate(toMMYY(dto.getStartDateMonth(), dto.getStartDateYear()));
			result.setCardType(dto.getCardType().toString());
			result.setDescription("Subcription via Credt Card");
			result.setPaymentType("creditCard");
		return result;
	}
	
	public static String toMMYY(Integer month, Integer year) {
		return (month < 10 ? "0" : "") + month + "" + 
			(year % 100 < 10 ? "0" : "") + year % 100;
	}
}