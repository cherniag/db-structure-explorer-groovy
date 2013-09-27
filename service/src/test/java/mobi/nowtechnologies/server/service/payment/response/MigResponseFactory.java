package mobi.nowtechnologies.server.service.payment.response;


import mobi.nowtechnologies.server.shared.service.BasicResponse;

/**
 * The class <code>MigResponseFactory</code> implements static methods that return instances of the class <code>{@link MigResponse}</code>.
 *
 * @generatedBy CodePro at 29.08.12 12:06
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class MigResponseFactory
 {
	/**
	 * Prevent creation of instances of this class.
	 *
	 * @generatedBy CodePro at 29.08.12 12:06
	 */
	private MigResponseFactory() {
	}


	/**
	 * Create an instance of the class <code>{@link MigResponse}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 12:06
	 */
	public static MigResponse createFailMigResponse() {
		return MigResponse.failMigResponse("");
	}


	/**
	 * Create an instance of the class <code>{@link MigResponse}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 12:06
	 */
	public static MigResponse createSuccessfulMigResponse() {
		return MigResponse.successfulMigResponse();
	}


	/**
	 * Create an instance of the class <code>{@link MigResponse}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 12:06
	 */
	public static MigResponse createMigResponse2() {
		return new MigResponse(new BasicResponse());
	}
}