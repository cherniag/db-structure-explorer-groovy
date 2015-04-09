package mobi.nowtechnologies.server.apptests.sms;

import mobi.nowtechnologies.server.apptests.email.DbMailService;
import mobi.nowtechnologies.server.service.payment.request.MigRequest;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

public class MigPostService extends PostService {

    public static final String MIG = "MIG";

    @Resource
    private DbMailService dbMailService;

    @Override
    public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
        Map<String, String> model = new HashMap<String, String>();
        model.put(MigRequest.MigRequestParam.OADCTYPE.toString(), extractField(nameValuePairs, MigRequest.MigRequestParam.OADCTYPE));
        model.put(MigRequest.MigRequestParam.MESSAGEID.toString(), extractField(nameValuePairs, MigRequest.MigRequestParam.MESSAGEID));

        dbMailService.sendMessage(MIG,
                                  extractField(nameValuePairs, MigRequest.MigRequestParam.NUMBERS),
                                  extractField(nameValuePairs, MigRequest.MigRequestParam.OADC),
                                  extractField(nameValuePairs, MigRequest.MigRequestParam.BODY),
                                  model);

        return new BasicResponse() {
            @Override
            public int getStatusCode() {
                return HttpServletResponse.SC_OK;
            }

            @Override
            public String getMessage() {
                return MigResponse.SUCCESSFUL_RESPONSE_START;
            }
        };
    }

    private String extractField(List<NameValuePair> nameValuePairs, MigRequest.MigRequestParam paramType) {
        for (NameValuePair nameValuePair : nameValuePairs) {
            if (paramType.toString().equals(nameValuePair.getName())) {
                return nameValuePair.getValue();
            }
        }
        throw new IllegalArgumentException("Not found " + paramType + " in " + nameValuePairs);
    }
}
