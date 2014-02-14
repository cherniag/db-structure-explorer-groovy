package mobi.nowtechnologies.server.shared;

public class ObjectUtils {
    public static <T> boolean  isNull(T o) {
        return o == null ? true : false;
    }

    public static boolean isNotNull(Object o) {
        return o != null ? true : false;
    }

}
