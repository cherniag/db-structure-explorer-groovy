package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.util.Assert;

/**
 * Created by Oleg Artomov on 7/8/2014.
 */
public class MergeResult {
    private boolean mergeDone;
    private User resultOfOperation;

    public boolean isMergeDone() {
        return mergeDone;
    }

    public User getResultOfOperation() {
        return resultOfOperation;
    }

    public MergeResult(boolean mergeDone, User resultOfMerge) {
        Assert.notNull(resultOfMerge);

        this.mergeDone = mergeDone;
        this.resultOfOperation = resultOfMerge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MergeResult result = (MergeResult) o;

        if (mergeDone != result.mergeDone) return false;
        if (!resultOfOperation.equals(result.resultOfOperation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (mergeDone ? 1 : 0);
        result = 31 * result + resultOfOperation.hashCode();
        return result;
    }
}
