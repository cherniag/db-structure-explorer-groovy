package mobi.nowtechnologies.common.dto;

import javax.validation.constraints.Pattern;

import java.util.Hashtable;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserRegInfo {

    private String title;

    private String firstName;
    private String lastName;

    @Email
    @NotBlank
    private String email;
    private String confEmail;

    @NotBlank
    private String displayName;
    @NotBlank
    private String storedToken;

    private String deviceType;
    private String deviceString;
    private String appVersion;
    private String communityName;

    private String paymentType;
    private String promotionCode;
    private Boolean newsByEmail;

    private String address;
    private String city;
    private String postCode;
    private String countryFullName;

    @NotBlank
    @Pattern(regexp = "^(07(\\d ?){9})$")
    private String phoneNumber;
    private int operator;
    private String ipAddress;

    private String cardType;
    private String cardNumber;
    private String cardHolderFirstName;
    private String cardHolderLastName;
    private Integer cardExpirationMonth;
    private Integer cardExpirationYear;
    private String cardCv2;
    private Integer cardStartMonth;
    private Integer cardStartYear;
    private String cardIssueNumber;
    private String cardBillingAddress;
    private String cardBillingAddress2;
    private String cardBillingCity;
    private String cardBillingPostCode;
    private String cardBillingCountry;
    private String cardBillingCountyOrState;

    private String countryCodeByIpAddress;

    // PayPal options
    private String billingAgreementDescription;

    @NotBlank
    private String confirmStoredToken;
    private boolean isEula;

    public static PaymentDetailsDto getPaymentDetailsDto(UserRegInfo regInfo) {
        PaymentDetailsDto dto = new PaymentDetailsDto();
        dto.setBillingAddress(regInfo.getCardBillingAddress() + " " + regInfo.getCardBillingAddress2());
        dto.setBillingCity(regInfo.getCardBillingCity());
        dto.setBillingCountry(regInfo.getCardBillingCountry());
        dto.setBillingPostCode(regInfo.getCardBillingPostCode());
        dto.setCardCv2(regInfo.getCardCv2());
        dto.setCardExpirationDate(toMMYY(regInfo.getCardExpirationMonth(), regInfo.getCardExpirationYear()));
        dto.setCardHolderFirstName(regInfo.getCardHolderFirstName());
        dto.setCardHolderLastName(regInfo.getCardHolderLastName());
        dto.setCardIssueNumber(regInfo.getCardIssueNumber());
        dto.setCardNumber(regInfo.getCardNumber());
        dto.setCardStartDate(toMMYY(regInfo.getCardStartMonth(), regInfo.getCardStartYear()));
        dto.setCardType(regInfo.getCardType());
        dto.setPaymentType(regInfo.getPaymentType());
        dto.setPhoneNumber(regInfo.getPhoneNumber());
        dto.setOperator(regInfo.getOperator());
        dto.setBillingAgreementDescription(regInfo.getBillingAgreementDescription());
        return dto;
    }

    public static String toMMYY(Integer month, Integer year) {
        if (month == null || year == null) {
            return null;
        }
        return (month < 10 ?
                "0" :
                "") + month + "" +
               (year % 100 < 10 ?
                "0" :
                "") + year % 100;
    }

    @Override
    public String toString() {
        return "UserRegInfo [email=" + email + ", deviceType=" + deviceType + ", deviceString=" + deviceString + ", appVersion=" + appVersion + ", communityName=" + communityName + ", operator=" +
               operator + ", ipAddress=" + ipAddress + "]";
    }

    public boolean getEula() {
        return isEula;
    }

    public void setEula(boolean isEula) {
        this.isEula = isEula;
    }

    public String getEmail() {
        return email != null ?
               email.trim() :
               null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStoredToken() {
        return storedToken;
    }

    public void setStoredToken(String storedToken) {
        this.storedToken = storedToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountryFullName() {
        return countryFullName;
    }

    public void setCountryFullName(String aCountryFullName) {
        this.countryFullName = aCountryFullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderFirstName() {
        return cardHolderFirstName;
    }

    public void setCardHolderFirstName(String aCardHolderFirstName) {
        this.cardHolderFirstName = aCardHolderFirstName;
    }

    public String getCardHolderLastName() {
        return cardHolderLastName;
    }

    public void setCardHolderLastName(String aCardHolderLastName) {
        this.cardHolderLastName = aCardHolderLastName;
    }

    public Integer getCardExpirationMonth() {
        return cardExpirationMonth;
    }

    public void setCardExpirationMonth(Integer aCardExpirationMonth) {
        this.cardExpirationMonth = aCardExpirationMonth;
    }

    public Integer getCardExpirationYear() {
        return cardExpirationYear;
    }

    public void setCardExpirationYear(Integer cardExpirationYear) {
        this.cardExpirationYear = cardExpirationYear;
    }

    public String getCardCv2() {
        return cardCv2;
    }

    public void setCardCv2(String cardCv2) {
        this.cardCv2 = cardCv2;
    }

    public Integer getCardStartMonth() {
        return cardStartMonth;
    }

    public void setCardStartMonth(Integer cardStartMonth) {
        this.cardStartMonth = cardStartMonth;
    }

    public Integer getCardStartYear() {
        return cardStartYear;
    }

    public void setCardStartYear(Integer cardStartYear) {
        this.cardStartYear = cardStartYear;
    }

    public String getCardIssueNumber() {
        return cardIssueNumber;
    }

    public void setCardIssueNumber(String cardIssueNumber) {
        this.cardIssueNumber = cardIssueNumber;
    }

    public String getCardBillingAddress() {
        return cardBillingAddress;
    }

    public void setCardBillingAddress(String cardBillingAddress) {
        this.cardBillingAddress = cardBillingAddress;
    }

    public String getCardBillingCity() {
        return cardBillingCity;
    }

    public void setCardBillingCity(String cardBillingCity) {
        this.cardBillingCity = cardBillingCity;
    }

    public String getCardBillingPostCode() {
        return cardBillingPostCode;
    }

    public void setCardBillingPostCode(String cardBillingPostCode) {
        this.cardBillingPostCode = cardBillingPostCode;
    }

    public String getCardBillingCountry() {
        return cardBillingCountry;
    }

    public void setCardBillingCountry(String cardBillingCountry) {
        this.cardBillingCountry = cardBillingCountry;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountryCodeByIpAddress() {
        return countryCodeByIpAddress;
    }

    public void setCountryCodeByIpAddress(String countryCodeByIpAddress) {
        this.countryCodeByIpAddress = countryCodeByIpAddress;
    }

    public String getConfirmStoredToken() {
        return confirmStoredToken;
    }

    public void setConfirmStoredToken(String confirmStoredToken) {
        this.confirmStoredToken = confirmStoredToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCardBillingAddress2() {
        return cardBillingAddress2;
    }

    public void setCardBillingAddress2(String cardBillingAddress2) {
        this.cardBillingAddress2 = cardBillingAddress2;
    }

    public String getCardBillingCountyOrState() {
        return cardBillingCountyOrState;
    }

    public void setCardBillingCountyOrState(String cardBillingCountyOrState) {
        this.cardBillingCountyOrState = cardBillingCountyOrState;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceString() {
        return deviceString;
    }

    public void setDeviceString(String deviceString) {
        this.deviceString = deviceString;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Boolean getNewsByEmail() {
        return newsByEmail;
    }

    public void setNewsByEmail(Boolean newsByEmail) {
        this.newsByEmail = newsByEmail;
    }

    public String getConfEmail() {
        return confEmail != null ?
               confEmail.trim() :
               null;
    }

    public void setConfEmail(String confEmail) {
        this.confEmail = confEmail;
    }

    public String getBillingAgreementDescription() {
        return billingAgreementDescription;
    }

    public void setBillingAgreementDescription(String billingAgreementDescription) {
        this.billingAgreementDescription = billingAgreementDescription;
    }

    public static final class DeviceType {

        public static final String ANDROID = "ANDROID";
        public static final String IOS = "IOS";
        public static final String J2ME = "J2ME";
        public static final String BLACKBERRY = "BLACKBERRY";

        public static String[] getValues() {
            return new String[] {ANDROID, IOS, J2ME, BLACKBERRY};
        }
    }

    public static final class PaymentType {

        public static final String CREDIT_CARD = "creditCard";
        public static final String PREMIUM_USER = "PSMS";
        public static final String FREEMIUM = "FREEMIUM";
        public static final String PAY_PAL = "PAY_PAL";
        public static final String UNKNOWN = "UNKNOWN";
        public static final String O2_PSMS = "o2Psms";
        public static final String ITUNES_SUBSCRIPTION = "ITUNES_SUBSCRIPTION";

        public static String[] getPaymentTypes() {
            return new String[] {CREDIT_CARD, PREMIUM_USER, FREEMIUM, PAY_PAL, ITUNES_SUBSCRIPTION, O2_PSMS, UNKNOWN};
        }
    }

    public static final class CardType implements Comparable {

        public static final String VISA = "VISA";
        public static final String MC = "MC";
        public static final String DELTA = "DELTA";
        public static final String MAESTRO = "MAESTRO";
        public static final String UKE = "UKE";
        public static final String JCB = "JCB";
        private static final Hashtable SORTED_KEY_DISPLAY_VALUES = new Hashtable();

        static {
            SORTED_KEY_DISPLAY_VALUES.put(new CardType(MC, 0), "MasterCard");
            SORTED_KEY_DISPLAY_VALUES.put(new CardType(DELTA, 1), "Visa Debit");
            SORTED_KEY_DISPLAY_VALUES.put(new CardType(VISA, 2), "Visa Credit");
            SORTED_KEY_DISPLAY_VALUES.put(new CardType(UKE, 3), "Visa Electron");
            SORTED_KEY_DISPLAY_VALUES.put(new CardType(MAESTRO, 4), "Maestro");
            SORTED_KEY_DISPLAY_VALUES.put(new CardType(JCB, 5), "JCB");
        }

        private String constantName;
        private int order;

        private CardType(String constantName, int order) {
            if (order < 0) {
                throw new IllegalArgumentException("The parameter order is less than zero");
            }
            if (constantName == null) {
                throw new NullPointerException("The parameter constantName is null");
            }
            this.constantName = constantName;
            this.order = order;
        }

        public static Hashtable getSortedKeyDisplayValues() {
            return SORTED_KEY_DISPLAY_VALUES;
        }

        public String getConstantName() {
            return constantName;
        }

        @Override
        public String toString() {
            return getConstantName();
        }

        @Override
        public int compareTo(Object o) {
            CardType cardType = (CardType) o;
            if (this.order < cardType.order) {
                return -1;
            }
            else if (this.order > cardType.order) {
                return 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CardType)) {
                return false;
            }
            CardType cardType = (CardType) obj;
            return cardType.order == this.order;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 37 * result + order;
            return result;
        }
    }

    public static final class Title {

        public static final String MR = "Mr";
        public static final String MS = "Ms";
        public static final String MRS = "Mrs";
        public static final String MISS = "Miss";

        public static String[] TITLES = new String[] {MR, MS, MRS, MISS};
    }
}