package mobi.nowtechnologies.server.shared;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/4/13
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Parser<IN,OUT> {
    OUT parse(IN data);
}
