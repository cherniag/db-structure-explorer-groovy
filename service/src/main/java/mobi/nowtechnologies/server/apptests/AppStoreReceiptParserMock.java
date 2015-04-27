package mobi.nowtechnologies.server.apptests;

import mobi.nowtechnologies.server.service.itunes.AppStoreReceiptParser;
/**
 * Author: Gennadii Cherniaiev Date: 4/24/2015
 */
public class AppStoreReceiptParserMock extends AppStoreReceiptParser {

    @Override
    public String getProductId(String appStoreReceipt) {
        logger.info("Receipt : [{}]", appStoreReceipt);
        return appStoreReceipt.split(":")[3];
    }
}
