package mobi.nowtechnologies.server.service.itunes;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Author: Gennadii Cherniaiev Date: 1/6/2015
 */
public class ITunesResult {

    private int result;
    private String productId;
    private String originalTransactionId;
    private Long expireTime;
    private Long purchaseTime;

    public ITunesResult(int result) {
        this(result, null, null, null, null);
    }

    public ITunesResult(int result, String productId, String originalTransactionId, Long expireTime, Long purchaseTime) {
        this.result = result;
        this.productId = productId;
        this.originalTransactionId = originalTransactionId;
        this.expireTime = expireTime;
        this.purchaseTime = purchaseTime;
    }

    public boolean isSuccessful() {
        return result == 0;
    }

    public int getResult() {
        return result;
    }

    public String getProductId() {
        return productId;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public Long getPurchaseTime() {
        return purchaseTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append("result", result)
                                                            .append("productId", productId)
                                                            .append("originalTransactionId", originalTransactionId)
                                                            .append("expireTime", expireTime)
                                                            .append("purchaseTime", purchaseTime)
                                                            .toString();
    }
}
