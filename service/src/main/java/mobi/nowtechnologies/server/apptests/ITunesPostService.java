package mobi.nowtechnologies.server.apptests;

import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Author: Gennadii Cherniaiev
 * Date: 8/15/2014
 */
public class ITunesPostService extends PostService {
    @Override
    public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setStatusCode(200);
        final String productId = "com.musicqubed.ios.mp.subscription.weekly.1";

        basicResponse.setMessage("{ \"receipt\" : " +
                "{ \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", " +
                    "\"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", " +
                    "\"original_transaction_id\" : \"123456789\", " +
                    "\"expires_date\" : \"1423820502000\", " +
                    "\"transaction_id\" : \"987654321\", " +
                    "\"quantity\" : \"1\", " +
                    "\"product_id\" : \"" + productId + "\", " +
                    "\"original_purchase_date_ms\" : \"1360755703334\", " +
                    "\"bid\" : \"com.musicqubed.o2\", " +
                    "\"web_order_line_item_id\" : \"1000000026638439\", " +
                    "\"bvrs\" : \"1.0\", " +
                    "\"expires_date_formatted\" : \"2015-02-13 11:44:42 Etc/GMT\", " +
                    "\"purchase_date\" : \"2013-02-13 11:41:42 Etc/GMT\", " +
                    "\"purchase_date_ms\" : \"1360755702795\"," +
                    "\"expires_date_formatted_pst\" : \"2015-02-13 03:44:42 America/Los_Angeles\", " +
                    "\"purchase_date_pst\" : \"2013-02-13 03:41:42 America/Los_Angeles\", " +
                    "\"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", " +
                    "\"item_id\" : \"602725828\" }, " +
                "\"latest_receipt_info\" : { " +
                    "\"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", " +
                    "\"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", " +
                    "\"original_transaction_id\" : \"123456789\", " +
                    "\"expires_date\" : \"1423820502000\", " +
                    "\"transaction_id\" : \"987654321\", " +
                    "\"quantity\" : \"1\", " +
                    "\"product_id\" : \"" + productId + "\", " +
                    "\"original_purchase_date_ms\" : \"1360755703000\", " +
                    "\"bid\" : \"com.musicqubed.o2\", " +
                    "\"web_order_line_item_id\" : \"1000000026638446\", " +
                    "\"bvrs\" : \"1.0\", " +
                    "\"expires_date_formatted\" : \"2015-02-13 11:50:42 Etc/GMT\", " +
                    "\"purchase_date\" : \"2013-02-13 11:47:42 Etc/GMT\", " +
                    "\"purchase_date_ms\" : \"1360756062000\", " +
                    "\"expires_date_formatted_pst\" : \"2015-02-13 03:50:42 America/Los_Angeles\", " +
                    "\"purchase_date_pst\" : \"2013-02-13 03:47:42 America/Los_Angeles\", " +
                    "\"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", " +
                    "\"item_id\" : \"602725828\" }, " +
                "\"status\" : 0, " +
                "\"latest_receipt\" : \"NEW_TRANS_RECEIPT\" " +
                "}");

        return basicResponse;
    }
}
