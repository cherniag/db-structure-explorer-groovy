package mobi.nowtechnologies.server.service.itunes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Author: Gennadii Cherniaiev Date: 4/24/2015
 */
public class AppStoreReceiptParser {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private static final Pattern purchaseInfoPattern = Pattern.compile("\"purchase-info\" = \"(.*)\";");
    private static final Pattern productIdPattern = Pattern.compile("\"product-id\" = \"(.*)\";");

    public String getProductId(String appStoreReceipt) {
        logger.debug("Try to decode and parse receipt [{}]", appStoreReceipt);

        String decodedReceipt = new String(Base64.decodeBase64(appStoreReceipt));
        Matcher purchaseInfoMatcher = purchaseInfoPattern.matcher(decodedReceipt);
        if(!purchaseInfoMatcher.find()) {
            throw new RuntimeException("Not found purchase info in receipt [" + decodedReceipt + "], encoded [" + appStoreReceipt + "]");
        }
        String purchaseInfoEncoded = purchaseInfoMatcher.group(1);

        logger.debug("Try to decode and parse purchase info [{}]", purchaseInfoEncoded);
        String decodedPuchaseInfo = new String(Base64.decodeBase64(purchaseInfoEncoded));
        Matcher productIdMatcher = productIdPattern.matcher(decodedPuchaseInfo);
        if(!productIdMatcher.find()) {
            throw new RuntimeException("Not found product id in purchase info [" + decodedPuchaseInfo + "], receipt [" + decodedReceipt + "], encoded [" + appStoreReceipt + "]");
        }

        String productId = productIdMatcher.group(1);
        logger.debug("Result product id [{}]", productId);

        return productId;
    }

}
