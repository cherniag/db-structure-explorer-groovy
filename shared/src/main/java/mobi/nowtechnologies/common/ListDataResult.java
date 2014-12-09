package mobi.nowtechnologies.common;


import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Gennadii Cherniaiev
 * Date: 5/23/2014
 */
public class ListDataResult<T> {
    private List<T> data = new ArrayList<T>();
    private long total;

    public ListDataResult(List<T> data) {
        this.data.addAll(data);
        this.total = data.size();
    }

    public void setTotal(long total) {
        this.total = total;
        Assert.isTrue(total >= data.size());
    }

    public List<T> getData() {
        return new ArrayList<T>(data);
    }

    public long getTotal() {
        return total;
    }

    public long getSize(){
        return data.size();
    }

    public boolean totalExceedsDataSize() {
        return total > data.size();
    }
}
