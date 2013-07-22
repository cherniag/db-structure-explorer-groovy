package mobi.nowtechnologies.server.trackrepo.ingest;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 7/16/13
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class IngestSessionClosedException extends RuntimeException {
    public IngestSessionClosedException(){
        super("Ingest session is closed");
    }
}
