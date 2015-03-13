package mobi.nowtechnologies.server.service.payment.request;

import mobi.nowtechnologies.server.shared.Utils;


public class MigRequest extends AbstractPaymentRequest<MigRequest> {

    public static final String OADC_FREE = "MusicQubed";

    private static final String OADCTYPE_PREMIUM = "0";
    private static final String OADCTYPE_FREE = "2";

    public MigRequest createFreeSMSRequest(String numbers, String message) {
        return new MigRequest().addParam(MigRequestParam.OADC, OADC_FREE).addParam(MigRequestParam.OADCTYPE, OADCTYPE_FREE).addParam(MigRequestParam.MESSAGEID, Utils.getBigRandomInt().toString())
                               .addParam(MigRequestParam.NUMBERS, numbers).addParam(MigRequestParam.BODY, message);
    }

    public MigRequest createFreeSMSRequest(String numbers, String message, String title) {
        return new MigRequest().addParam(MigRequestParam.OADC, title).addParam(MigRequestParam.OADCTYPE, OADCTYPE_FREE).addParam(MigRequestParam.MESSAGEID, Utils.getBigRandomInt().toString())
                               .addParam(MigRequestParam.NUMBERS, numbers).addParam(MigRequestParam.BODY, message);
    }

    public MigRequest createPremiumSMSRequest(String messageId, String oadc, String numbers, String message, String timeToLive) {
        return new MigRequest().addParam(MigRequestParam.OADC, oadc).addParam(MigRequestParam.OADCTYPE, OADCTYPE_PREMIUM).addParam(MigRequestParam.MESSAGEID, messageId)
                               .addParam(MigRequestParam.NUMBERS, numbers).addParam(MigRequestParam.BODY, message).addParam(MigRequestParam.TIMETOLIVE, timeToLive);
    }

    public static enum MigRequestParam implements PaymentRequestParam {
        OADC,
        OADCTYPE,
        MESSAGEID,
        NUMBERS,
        BODY,
        TIMETOLIVE
    }
}