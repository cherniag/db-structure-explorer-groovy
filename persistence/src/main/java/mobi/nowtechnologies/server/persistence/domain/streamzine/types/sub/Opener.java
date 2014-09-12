package mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub;

/**
 * Created by Oleg Artomov on 8/15/2014.
 */
public enum Opener {
    BROWSER("externally"), IN_APP("internally");

    private String queryParamValue;

    public String getQueryParamValue() {
        return queryParamValue;
    }

    Opener(String queryParamValue) {
        this.queryParamValue = queryParamValue;
    }

}
