package mobi.nowtechnologies.server.shared;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/4/13
 * Time: 5:43 PM
 */
public interface Processor<OUT> {
    void process(OUT data);
}
