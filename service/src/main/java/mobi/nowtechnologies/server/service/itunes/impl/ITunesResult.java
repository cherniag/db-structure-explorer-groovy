package mobi.nowtechnologies.server.service.itunes.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Author: Gennadii Cherniaiev Date: 1/6/2015
 */
public class ITunesResult {
    private static final int OK = 0;

    int result = OK;
    String productId;
    String originalTransactionId;
    Long expireTime;
    Long purchaseTime;
    String response;

    ITunesResult(String response) {
        this.response = response;
    }

    public boolean isSuccessful() {
        return result == OK;
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

    public String getResponse() {
        return response;
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
