package mobi.nowtechnologies.applicationtests.services.http.domain.common;

import java.util.List;

/**
 * Created by kots on 9/11/2014.
 */
public class Response {

    private List<DataWrapper> data;

    public List<DataWrapper> getData() {
        return data;
    }

    public void setData(List<DataWrapper> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }
}
