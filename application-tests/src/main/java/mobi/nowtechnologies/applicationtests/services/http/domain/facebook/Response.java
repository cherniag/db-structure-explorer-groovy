package mobi.nowtechnologies.applicationtests.services.http.domain.facebook;

import java.util.List;

/**
 * @author kots
 * @since 8/20/2014.
 */
class Response {
    private List<DataWrapper> data;

    public List<DataWrapper> getData() {
        return data;
    }

    public void setData(List<DataWrapper> data) {
        this.data = data;
    }
}
