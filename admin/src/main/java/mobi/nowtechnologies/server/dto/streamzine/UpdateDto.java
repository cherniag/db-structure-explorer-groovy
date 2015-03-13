package mobi.nowtechnologies.server.dto.streamzine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UpdateDto {

    private Date date;
    private long id;
    private List<String> userNames = new ArrayList<String>();

    private Set<BlockDto> blocks = new LinkedHashSet<BlockDto>();
    private boolean canEdit;

    public UpdateDto() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<BlockDto> getBlocks() {
        return blocks;
    }

    public void addAllBlocks(Collection<? extends BlockDto> blockDtos) {
        blocks.addAll(blockDtos);
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void addUserName(String userName) {
        this.userNames.add(userName);
    }
}
