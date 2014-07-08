package mobi.nowtechnologies.server.service.merge;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * Created by Oleg Artomov on 7/8/2014.
 */
public class OperationResult {

    private boolean mergeDone;

    private User resultOfOperation;


    public boolean isMergeDone() {
        return mergeDone;
    }

    public User getResultOfOperation() {
        return resultOfOperation;
    }

    public OperationResult(boolean mergeDone, User resultOfMerge) {
        this.mergeDone = mergeDone;
        this.resultOfOperation = resultOfMerge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationResult that = (OperationResult) o;

        if (mergeDone != that.mergeDone) return false;
        if (resultOfOperation != null ? !resultOfOperation.equals(that.resultOfOperation) : that.resultOfOperation != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (mergeDone ? 1 : 0);
        result = 31 * result + (resultOfOperation != null ? resultOfOperation.hashCode() : 0);
        return result;
    }

}
