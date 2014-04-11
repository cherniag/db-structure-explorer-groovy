package mobi.nowtechnologies.server.user.criteria;

import java.lang.reflect.Field;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class ReflectionHelper {

    public static Object getFieldValue(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
