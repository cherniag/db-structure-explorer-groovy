package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.merge.OperationResult;

/**
 * Created by Oleg Artomov on 7/8/2014.
 */
public class OperationResultFactory {

    public static OperationResult getOperationResult(boolean mergeIsDone, User operationResultUser) {
        return new OperationResult(mergeIsDone, operationResultUser);
    }
}
