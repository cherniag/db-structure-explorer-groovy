package mobi.nowtechnologies.server.shared;

public class ObjectUtils {
    public static <T> boolean  isNull(T o) {
        return o == null ? true : false;
    }

    public static boolean isNotNull(Object o) {
        return o != null ? true : false;
    }

    public static <T> boolean different(T one, T two) {
        if(one == null && two == null)
            return false;
        if(one == null && two != null)
            return true;
        if(one.equals(two))
            return false;
        return true;
    }

    public static <T> String toStringIfNull(T obj) {
        if(obj == null)
            return  null;
        return obj.toString();
    }
}
