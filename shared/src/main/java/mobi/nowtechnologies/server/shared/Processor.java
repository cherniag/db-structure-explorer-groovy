package mobi.nowtechnologies.server.shared;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/4/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Processor<T> {
    void process(T data);
}
