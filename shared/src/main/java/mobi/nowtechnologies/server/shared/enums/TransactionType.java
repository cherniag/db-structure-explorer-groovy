/**
 * 
 */
package mobi.nowtechnologies.server.shared.enums;


/**
 * @author Titov Mykhaylo (titov)
 *
 */
public enum TransactionType {
	UNKNOWN((byte)0),
	TRACK_PURCHASE((byte) 1),
	SUBSCRIPTION_CHARGE((byte) 2),
	CARD_TOP_UP((byte) 3),
	REFUND((byte) 4),
	PROMOTION((byte) 5),
	PROMOTION_BY_PROMO_CODE_APPLIED((byte) 6),
	SUPPORT_TOPUP((byte) 7),
	OFFER_PURCHASE((byte) 8),
	TRIAL_TOPUP((byte) 9),
	TRACK_GIFT((byte) 10),
    TRIAL_SKIPPING((byte) 11),
    BOUGHT_PERIOD_SKIPPING((byte) 12),
    ACCOUNT_MERGE((byte) 13);
	
	private byte code;
	
	private TransactionType(byte code) {
		this.code = code;
	}

	public byte getCode() {
		return code;
	}
}