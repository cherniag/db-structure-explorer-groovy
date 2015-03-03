package mobi.nowtechnologies.server.trackrepo.ingest;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DropData {

    public String name;
    public Date date;

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("date", date).toString();
    }
}
