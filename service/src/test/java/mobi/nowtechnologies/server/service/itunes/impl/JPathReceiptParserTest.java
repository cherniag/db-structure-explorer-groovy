package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.service.itunes.ITunesResponseFormatException;

import java.io.InputStream;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.fileupload.util.Streams;

import org.junit.*;

public class JPathReceiptParserTest {

    @Test
    public void parseValidAutoRenewableReceipt() throws Exception {
        JPathReceiptParser jPathReceiptParser =
            create(JsonPath.compile("$.status"), JsonPath.compile("$.latest_receipt_info.product_id"), JsonPath.compile("$.latest_receipt_info.original_transaction_id"),
                   JsonPath.compile("$.latest_receipt_info.expires_date"), JsonPath.compile("$.latest_receipt_info.purchase_date_ms"));
        InputStream inputStream = this.getClass().getResourceAsStream("/itunes/renewable.json");
        String json = Streams.asString(inputStream);

        ITunesResult parseResult = jPathReceiptParser.parseVerifyReceipt(json);

        Assert.assertTrue(parseResult.isSuccessful());
        Assert.assertEquals("com.musicqubed.o2.autorenew.test", parseResult.getProductId());
        Assert.assertEquals("1000000064861007", parseResult.getOriginalTransactionId());
        Assert.assertEquals(1360756242000L, parseResult.getExpireTime().longValue());
        Assert.assertEquals(1360756062000L, parseResult.getPurchaseTime().longValue());
    }

    @Test
    public void parseValidOneTimeReceipt() throws Exception {
        JPathReceiptParser jPathReceiptParser =
            create(JsonPath.compile("$.status"), JsonPath.compile("$.receipt.product_id"), JsonPath.compile("$.receipt.original_transaction_id"), null, JsonPath.compile("$.receipt.purchase_date_ms"));
        InputStream inputStream = this.getClass().getResourceAsStream("/itunes/onetime.json");
        String json = Streams.asString(inputStream);

        ITunesResult parseResult = jPathReceiptParser.parseVerifyReceipt(json);

        Assert.assertTrue(parseResult.isSuccessful());
        Assert.assertEquals("com.musicqubed.ios.mtv_nz.onetime.0", parseResult.getProductId());
        Assert.assertEquals("1000000137405768", parseResult.getOriginalTransactionId());
        Assert.assertNull(parseResult.getExpireTime());
        Assert.assertEquals(1420206332704L, parseResult.getPurchaseTime().longValue());
    }

    @Test(expected = ITunesResponseFormatException.class)
    public void parseOnetimeAsAutoRenewableReceipt() throws Exception {
        JPathReceiptParser jPathReceiptParser =
            create(JsonPath.compile("$.status"), JsonPath.compile("$.latest_receipt_info.product_id"), JsonPath.compile("$.latest_receipt_info.original_transaction_id"),
                   JsonPath.compile("$.latest_receipt_info.expires_date"), null);
        InputStream inputStream = this.getClass().getResourceAsStream("/itunes/onetime.json");
        String json = Streams.asString(inputStream);

        jPathReceiptParser.parseVerifyReceipt(json);
    }

    @Test
    public void parseExpiredReceipt() throws Exception {
        JPathReceiptParser jPathReceiptParser =
            create(JsonPath.compile("$.status"), JsonPath.compile("$.latest_receipt_info.product_id"), JsonPath.compile("$.latest_receipt_info.original_transaction_id"),
                   JsonPath.compile("$.latest_receipt_info.expires_date"), null);
        InputStream inputStream = this.getClass().getResourceAsStream("/itunes/expired.json");
        String json = Streams.asString(inputStream);

        ITunesResult parseResult = jPathReceiptParser.parseVerifyReceipt(json);

        Assert.assertFalse(parseResult.isSuccessful());
    }


    private JPathReceiptParser create(JsonPath statusPath, JsonPath productIdPath, JsonPath originalTransactionIdPath, JsonPath expireTimestampPath, JsonPath purchaseTimestampPath) {
        JPathReceiptParser jPathReceiptParser = new JPathReceiptParser();
        jPathReceiptParser.setStatusPath(statusPath);
        jPathReceiptParser.setProductIdPath(productIdPath);
        jPathReceiptParser.setOriginalTransactionIdPath(originalTransactionIdPath);
        jPathReceiptParser.setExpireTimestampPath(expireTimestampPath);
        jPathReceiptParser.setPurchaseTimestampPath(purchaseTimestampPath);
        return jPathReceiptParser;
    }
}