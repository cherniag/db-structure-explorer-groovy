package mobi.nowtechnologies.server.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ITunesInAppSubscriptionRequestDto {
	
	@Expose
	@SerializedName("receipt-data")
    @JsonProperty("receipt-data")
	private String receiptData;
	
	private String password;

    public ITunesInAppSubscriptionRequestDto() {
    }

    public ITunesInAppSubscriptionRequestDto(String receiptData, String password) {
        this.receiptData = receiptData;
        this.password = password;
    }

    public String getReceiptData() {
		return receiptData;
	}

	public void setReceiptData(String receiptData) {
		this.receiptData = receiptData;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "ITunesInAppSubscriptionRequestDto [receiptData=" + receiptData + "]";
	}

}
