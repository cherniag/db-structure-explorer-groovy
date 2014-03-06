package mobi.nowtechnologies.server.shared.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ITunesInAppSubscriptionResponseDto {

	private String status;

	private Receipt receipt;
	
	@Expose
	@SerializedName("latest_receipt")
	private String latestReceipt;
	
	@Expose
	@SerializedName("latest_receipt_info")
	private Receipt latestReceiptInfo;
	
	public static class Receipt{
		
		private String quantity;
		
		@Expose
		@SerializedName("product_id")
		private String productId;
		
		@Expose
		@SerializedName("transaction_id")
		private String transactionId;
		
		@Expose
		@SerializedName("purchaseDate")
		private String purchase_date;
		
		@Expose
		@SerializedName("original_transaction_id")
		private String originalTransactionId;
		
		@Expose
		@SerializedName("original_purchase_date")
		private String originalPurchaseDate;
		
		@Expose
		@SerializedName("app_item_id")
		private String appItemId;
		
		@Expose
		@SerializedName("version_external_identifier")
		private String versionExternalIdentifier;
		
		private String bid;
		private String bvrs;
		
		@Expose
		@SerializedName("expires_date")
		private long expiresDate;

		public String getQuantity() {
			return quantity;
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

		public String getProductId() {
			return productId;
		}

		public void setProductId(String productId) {
			this.productId = productId;
		}

		public String getTransactionId() {
			return transactionId;
		}

		public void setTransactionId(String transactionId) {
			this.transactionId = transactionId;
		}

		public String getPurchase_date() {
			return purchase_date;
		}

		public void setPurchase_date(String purchaseDate) {
			purchase_date = purchaseDate;
		}

		public String getOriginalTransactionId() {
			return originalTransactionId;
		}

		public void setOriginalTransactionId(String originalTransactionId) {
			this.originalTransactionId = originalTransactionId;
		}

		public String getOriginalPurchaseDate() {
			return originalPurchaseDate;
		}

		public void setOriginalPurchaseDate(String originalPurchaseDate) {
			this.originalPurchaseDate = originalPurchaseDate;
		}

		public String getAppItemId() {
			return appItemId;
		}

		public void setAppItemId(String appItemId) {
			this.appItemId = appItemId;
		}

		public String getVersionExternalIdentifier() {
			return versionExternalIdentifier;
		}

		public void setVersionExternalIdentifier(String versionExternalIdentifier) {
			this.versionExternalIdentifier = versionExternalIdentifier;
		}

		public String getBid() {
			return bid;
		}

		public void setBid(String bid) {
			this.bid = bid;
		}

		public String getBvrs() {
			return bvrs;
		}

		public void setBvrs(String bvrs) {
			this.bvrs = bvrs;
		}

		public long getExpiresDate() {
			return expiresDate;
		}
		
		public int getExpiresDateSeconds(){
			return (int)(expiresDate/1000);
		}

		public void setExpiresDate(long expiresDate) {
			this.expiresDate = expiresDate;
		}

		@Override
		public String toString() {
			return "Receipt [transactionId=" + transactionId + ", originalTransactionId=" + originalTransactionId + ", productId=" + productId + ", appItemId=" + appItemId + ", purchase_date="
					+ purchase_date + ", expiresDate=" + expiresDate + ", quantity=" + quantity + ", bid=" + bid + ", bvrs=" + bvrs + ", originalPurchaseDate=" + originalPurchaseDate
					+ ", versionExternalIdentifier=" + versionExternalIdentifier + "]";
		}
		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public String getLatestReceipt() {
		return latestReceipt;
	}

	public void setLatestReceipt(String latestReceipt) {
		this.latestReceipt = latestReceipt;
	}

	public Receipt getLatestReceiptInfo() {
		return latestReceiptInfo;
	}

	public void setLatestReceiptInfo(Receipt latestReceiptInfo) {
		this.latestReceiptInfo = latestReceiptInfo;
	}

	public boolean isSuccess() {
		return "0".equals(status);
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("receipt", receipt)
                .append("latestReceipt", latestReceipt)
                .append("latestReceiptInfo", latestReceiptInfo)
                .toString();
    }
}
