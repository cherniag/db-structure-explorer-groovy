package mobi.nowtechnologies.server.dto.streamzine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UpdateIncomingDto {

    @JsonProperty(value = "id")
    private long id;
    @JsonProperty(value = "timestamp")
    private long timestamp;
    @JsonProperty(value = "userNames")
    private List<String> userNames = new ArrayList<String>();

    @JsonProperty(value = "blocks")
    private List<OrdinalBlockDto> blocks = new ArrayList<OrdinalBlockDto>();

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<OrdinalBlockDto> getBlocks() {
        return blocks;
    }

    public List<String> getUserNames() {
        return new ArrayList<String>(userNames);
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public void addUserName(String userName) {
        this.userNames.add(userName);
    }

    public void removeUserNameDuplicates() {
        this.userNames = new ArrayList<String>(new HashSet<String>(userNames));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("timestamp", timestamp).append("userNames", userNames).append("blocks", blocks).toString();
    }
}
