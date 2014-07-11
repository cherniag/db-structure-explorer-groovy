package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Oleg Artomov on 7/8/2014.
 */
public class OperationResultFactory {

    public static MergeResult createOperationResult(boolean mergeIsDone, User user) {
        MergeResult result = mock(MergeResult.class);
        when(result.isMergeDone()).thenReturn(mergeIsDone);
        when(result.getResultOfOperation()).thenReturn(user);
        return result;
    }
}
