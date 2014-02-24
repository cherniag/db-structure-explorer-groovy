package mobi.nowtechnologies.server.trackrepo.ingest;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

public class DropData {

	public String name;
	public Date date;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("date", date)
                .toString();
    }
}
