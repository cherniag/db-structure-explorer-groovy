package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.support.http.BasicResponse;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigResponse extends PaymentSystemResponse implements SMSResponse {

    public static final String SUCCESSFUL_RESPONSE_START = "000=[GEN] OK ";
    private static final Logger LOGGER = LoggerFactory.getLogger(MigResponse.class);
    MigSuccessfulResponse jsonResonpse;

    public MigResponse(BasicResponse response) {
        super(response, false);
        if (response.getMessage().startsWith(SUCCESSFUL_RESPONSE_START)) {
            isSuccessful = true;
            parseResponse(response.getMessage());
        } else {
            descriptionError = getMessage();
        }
    }

    public static MigResponse successfulMigResponse() {
        return new MigResponse(new BasicResponse() {
            @Override
            public int getStatusCode() {
                return HttpServletResponse.SC_OK;
            }

            @Override
            public String getMessage() {
                return SUCCESSFUL_RESPONSE_START;
            }
        });
    }

    public static MigResponse failMigResponse(final String message) {
        return new MigResponse(new BasicResponse() {
            @Override
            public int getStatusCode() {
                return HttpServletResponse.SC_OK;
            }

            @Override
            public String getMessage() {
                return message;
            }
        });
    }

    private void parseResponse(String message) {
        jsonResonpse = new MigSuccessfulResponse();
        try {
            String[] sections = message.split(" ", 3);
            Gson gson = new Gson();
            jsonResonpse = gson.fromJson(sections[2].replaceAll(" ", ", "), MigSuccessfulResponse.class);
        } catch (JsonSyntaxException ex) {
            LOGGER.warn("Problem while getting external tx id from MIG response {}", message);
        }
    }

    public String getExternalTxId() {
        return jsonResonpse.getI();
    }

    private static class MigSuccessfulResponse {

        private String Q;
        private String B;
        private String I;

        public String getQ() {
            return Q;
        }

        public void setQ(String q) {
            Q = q;
        }

        public String getB() {
            return B;
        }

        public void setB(String b) {
            B = b;
        }

        public String getI() {
            return I;
        }

        public void setI(String i) {
            I = i;
        }
    }
}