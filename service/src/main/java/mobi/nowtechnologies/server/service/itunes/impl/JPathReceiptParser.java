package mobi.nowtechnologies.server.service.itunes.impl;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import mobi.nowtechnologies.server.service.itunes.ITunesParseResult;
import mobi.nowtechnologies.server.service.itunes.ITunesReceiptParseException;
import mobi.nowtechnologies.server.service.itunes.ITunesReceiptParser;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * Author: Gennadii Cherniaiev
 * Date: 1/6/2015
 */
public class JPathReceiptParser implements ITunesReceiptParser {
    private JsonPath statusPath;
    private JsonPath productIdPath;
    private JsonPath originalTransactionIdPath;
    private JsonPath expireTimestampPath;
    private JsonPath purchaseTimestampPath;

    @Override
    public ITunesParseResult parse(String response) throws ITunesReceiptParseException {
        try {
            int result = statusPath.<Integer>read(response);

            if (result != 0) {
                return new ITunesParseResult(result);
            }

            String productId = productIdPath.read(response);
            String originalTransactionId = originalTransactionIdPath.read(response);
            Long expireTime = safeReadTime(expireTimestampPath, response);
            Long purchaseTime = safeReadTime(purchaseTimestampPath, response);

            return new ITunesParseResult(result, productId, originalTransactionId, expireTime, purchaseTime);
        } catch (InvalidPathException e) {
            throw new ITunesReceiptParseException(e);
        }
    }

    private Long safeReadTime(JsonPath compiledJsonPath, String response) {
        if (compiledJsonPath != null) {
            return Long.parseLong(compiledJsonPath.<String>read(response));
        }
        return null;
    }

    public void setStatusPath(JsonPath statusPath) {
        this.statusPath = statusPath;
    }

    public void setProductIdPath(JsonPath productIdPath) {
        this.productIdPath = productIdPath;
    }

    public void setOriginalTransactionIdPath(JsonPath originalTransactionIdPath) {
        this.originalTransactionIdPath = originalTransactionIdPath;
    }

    public void setExpireTimestampPath(JsonPath expireTimestampPath) {
        this.expireTimestampPath = expireTimestampPath;
    }

    public void setPurchaseTimestampPath(JsonPath purchaseTimestampPath) {
        this.purchaseTimestampPath = purchaseTimestampPath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("statusPath", toString(statusPath))
                .append("productIdPath", toString(productIdPath))
                .append("originalTransactionIdPath", toString(originalTransactionIdPath))
                .append("expireTimestampPath", toString(expireTimestampPath))
                .append("purchaseTimestampPath", toString(purchaseTimestampPath))
                .toString();
    }

    @PostConstruct
    void init() {
        Assert.notNull(statusPath);
        Assert.notNull(productIdPath);
        Assert.notNull(originalTransactionIdPath);

        if (expireTimestampPath == null && purchaseTimestampPath == null) {
            throw new IllegalArgumentException("expireTimestampPath or purchaseTimestampPath should be defined");
        }
    }

    private String toString(JsonPath path) {
        return (path == null) ? "null" : path.getPath();
    }
}
