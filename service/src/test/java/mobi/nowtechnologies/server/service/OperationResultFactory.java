package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * Created by Oleg Artomov on 7/8/2014.
 */
public class OperationResultFactory {

    public static OperationResult getOperationResult(boolean mergeIsDone, User operationResultUser) {
        return new OperationResult(mergeIsDone, operationResultUser);
    }
}
