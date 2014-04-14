package mobi.nowtechnologies.server.user.criteria;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/11/2014
 */
public abstract class ExpectedValueHolder<T> {

    public abstract T getValue();

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

    public static <T> ExpectedValueHolder<T> valueOf(final T value){
        return new ExpectedValueHolder<T>(){

            @Override
            public T getValue() {
                return value;
            }
        };
    }

    public static  ExpectedValueHolder<Long> currentTimestamp() {
        return new ExpectedValueHolder<Long>() {
            @Override
            public Long getValue() {
                return System.currentTimeMillis();
            }
        };
    }
}
