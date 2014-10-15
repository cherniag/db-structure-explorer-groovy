package mobi.nowtechnologies.applicationtests.services.repeat;

public interface Repeatable<T> {
    boolean again();

    T result();
}
