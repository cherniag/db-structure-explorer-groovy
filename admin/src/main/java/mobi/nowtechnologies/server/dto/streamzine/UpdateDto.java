package mobi.nowtechnologies.server.dto.streamzine;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class UpdateDto {
    private Date date;
    private long id;
    private String userName;

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

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
