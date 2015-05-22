package mobi.nowtechnologies.server.apptests;

import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionRequestDto;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.http.NameValuePair;

/**
 * Author: Gennadii Cherniaiev Date: 8/15/2014
 */
public class ITunesPostService extends PostService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String oneTimeResponse = "{\"receipt\":{\"bid\":\"com.musicqubed.ios.mtv-nz\",\"bvrs\":\"1.0\",\"item_id\":\"955132922\",\"original_purchase_date\":\"2015-01-02 13:45:32Etc/GMT\"," +
                                     "\"original_purchase_date_ms\":\"1420206332704\",\"original_purchase_date_pst\":\"2015-01-02 05:45:32 America/Los_Angeles\"," +
                                     "\"original_transaction_id\":\"${transactionId}\"," +
                                     "\"product_id\":\"${productId}\",\"purchase_date\":\"2015-01-0213:45:32Etc/GMT\",\"purchase_date_ms\":\"${timestamp}\",\"purchase_date_pst\":\"2015-01-02 " +
                                     "05:45:32 America/Los_Angeles\"," +
                                     "\"quantity\":1,\"transaction_id\":\"1000000137405769\",\"unique_identifier\":\"ee04c152d11f0f6b47ac7f77ec7340e423731efb\"," +
                                     "\"unique_vendor_identifier\":\"75F921D3-8FB4-40D5-9D98-DCF195DC723A\"},\"status\":${status}}";
    private String renewableResponse = "{\"receipt\":{\"expires_date_formatted\":\"2015-01-08 15:05:33 Etc/GMT\",\"original_purchase_date_pst\":\"2013-11-11 13:00:11 America/Los_Angeles\"," +
                                       "\"unique_identifier\":\"81d48333aabc9f137fd3f12b59e5a301bff410ee\",\"original_transaction_id\":\"${transactionId}\",\"expires_date\":\"1420729533000\"," +
                                       "\"app_item_id\":\"595423926\"," +
                                       "\"transaction_id\":\"140000117466697\",\"quantity\":\"1\",\"expires_date_formatted_pst\":\"2015-01-08 07:05:33 America/Los_Angeles\"," +
                                       "\"product_id\":\"${productId}\",\"bvrs\":\"3.2.6\"," +
                                       "\"unique_vendor_identifier\":\"637A05DB-5EEF-46C9-8B11-91538CC54509\",\"web_order_line_item_id\":\"140000008465808\"," +
                                       "\"original_purchase_date_ms\":\"1384203611000\"," +
                                       "\"version_external_identifier\":\"810777743\",\"bid\":\"com.musicqubed.o2\",\"purchase_date_ms\":\"1418051133000\",\"purchase_date\":\"2014-12-08 15:05:33 " +
                                       "Etc/GMT\"," +
                                       "\"purchase_date_pst\":\"2014-12-08 07:05:33 America/Los_Angeles\",\"original_purchase_date\":\"2013-11-11 21:00:11 Etc/GMT\",\"item_id\":\"609085677\"}," +
                                       "\"latest_receipt_info\":{\"original_purchase_date_pst\":\"2013-11-11 13:00:11 America/Los_Angeles\"," +
                                       "\"unique_identifier\":\"81d48333aabc9f137fd3f12b59e5a301bff410ee\"," +
                                       "\"original_transaction_id\":\"${transactionId}\",\"expires_date\":\"${timestamp}\",\"app_item_id\":\"595423926\",\"transaction_id\":\"140000122115878\"," +
                                       "\"quantity\":\"1\"," +
                                       "\"product_id\":\"${productId}\",\"bvrs\":\"3.0.1\",\"bid\":\"com.musicqubed.o2\",\"unique_vendor_identifier\":\"61CC393A-5496-4C52-9C9D-CBC40D92526E\"," +
                                       "\"web_order_line_item_id\":\"140000009450140\",\"original_purchase_date_ms\":\"1384203611000\",\"expires_date_formatted\":\"2015-02-08 23:08:56Etc/GMT\"," +
                                       "\"purchase_date\":\"2015-01-08 " +
                                       "23:08:56 Etc/GMT\",\"purchase_date_ms\":\"1420758536000\",\"expires_date_formatted_pst\":\"2015-02-0815:08:56 America/Los_Angeles\"," +
                                       "\"purchase_date_pst\":\"2015-01-08 15:08:56 " +
                                       "America/Los_Angeles\",\"original_purchase_date\":\"2013-11-11 21:00:11Etc/GMT\",\"item_id\":\"609085677\"},\"status\":${status}," +
                                       "\"latest_receipt\":\"SOMELATESTRECEIPT==\"}";

    @Override
    public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
        ITunesInAppSubscriptionRequestDto requestDto;
        try {
            requestDto = objectMapper.readValue(body, ITunesInAppSubscriptionRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String token = requestDto.getReceiptData();

        // token in format  [type:statusCode:receiptStatus:productId:transactionId:timestamp]
        //    0.   type = onetime|renewable
        //    1.   statusCode = 200 (OK)
        //    2.   receiptStatus = 0 (OK)
        //    3.   productId
        //    4.   transactionId
        //    5.   timestamp = purchaseTimestamp|expiresTimestamp for onetime|renewable correspondingly

        String[] parts = token.split(":");
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setStatusCode(Integer.parseInt(parts[1]));

        String type = parts[0];
        if ("onetime".equalsIgnoreCase(type)) {
            basicResponse.setMessage(getStrSubstitutor(parts).replace(oneTimeResponse));
        } else if ("renewable".equalsIgnoreCase(type)) {
            basicResponse.setMessage(getStrSubstitutor(parts).replace(renewableResponse));
        } else {
            throw new RuntimeException("unknown type " + type + " for receipt : " + Arrays.toString(parts));
        }

        return basicResponse;
    }

    private StrSubstitutor getStrSubstitutor(String[] parts) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("status", parts[2]);
        values.put("productId", parts[3]);
        values.put("transactionId", parts[4]);
        values.put("timestamp", parts[5]);
        return new StrSubstitutor(values, "${", "}");
    }
}
