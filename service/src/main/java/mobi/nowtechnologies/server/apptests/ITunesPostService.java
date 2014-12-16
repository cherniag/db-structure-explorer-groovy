package mobi.nowtechnologies.server.apptests;

import com.fasterxml.jackson.databind.ObjectMapper;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionRequestDto;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.List;

/**
 * Author: Gennadii Cherniaiev
 * Date: 8/15/2014
 */
public class ITunesPostService extends PostService {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
        ITunesInAppSubscriptionRequestDto requestDto;
        try {
            requestDto = objectMapper.readValue(body, ITunesInAppSubscriptionRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // receipt-data in format  statusCode:productId:expiresDate
        String token = requestDto.getReceiptData();
        String[] parts = token.split(":");
        String statusCode = parts[0];
        String productId = parts[1];
        String expiresDate = parts[2];

        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setStatusCode(Integer.parseInt(statusCode));

        basicResponse.setMessage("{ \"receipt\" : " +
                "{ \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", " +
                    "\"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", " +
                    "\"original_transaction_id\" : \"123456789\", " +
                    "\"expires_date\" : \""+ expiresDate+"\", " +
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
                    "\"expires_date\" : \"" + expiresDate + "\", " +
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
