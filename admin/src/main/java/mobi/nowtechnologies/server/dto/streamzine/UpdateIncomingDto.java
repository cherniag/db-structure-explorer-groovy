package mobi.nowtechnologies.server.dto.streamzine;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class UpdateIncomingDto {
    @JsonProperty(value = "id")
    private long id;
    @JsonProperty(value = "timestamp")
    private long timestamp;
    @JsonProperty(value = "userName")
    private String userName;

    @JsonProperty(value = "blocks")
    private List<OrdinalBlockDto> blocks = new ArrayList<OrdinalBlockDto>();

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<OrdinalBlockDto> getBlocks() {
        return blocks;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("timestamp", timestamp)
                .append("userName", userName)
                .append("blocks", blocks)
                .toString();
    }
}
