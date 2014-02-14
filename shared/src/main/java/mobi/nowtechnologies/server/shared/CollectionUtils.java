package mobi.nowtechnologies.server.shared;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

/**
 * Created by oar on 2/14/14.
 */
public class CollectionUtils {

    public static <T> T unique(Collection<T> collection, Predicate<T> predicate) {
        if (!isEmpty(collection)) {
            Collection<T> filteredValues = Collections2.filter(collection, predicate);
            if (!filteredValues.isEmpty()) {
                Assert.isTrue(filteredValues.size() == 1);
                return Iterables.get(filteredValues, 0);
            }
        }
        return null;
    }


    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map content) {
        return (content == null || content.isEmpty());
    }




}
