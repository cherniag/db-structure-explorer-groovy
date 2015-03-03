package mobi.nowtechnologies.server.shared;

/**
 * User: Alexsandr_Kolpakov Date: 10/4/13 Time: 6:23 PM
 */
public interface Parser<IN, OUT> {

    OUT parse(IN data);
}
