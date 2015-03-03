package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.enums.IngestStatus;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * User: sanya Date: 7/15/13 Time: 2:41 PM
 */
public class IngestWizardDataDto {

    public static final String INGEST_WIZARD_DATA_DTO = "ingestWizardDataDto";
    public static final String ACTION = "action";
    protected List<DropDto> drops;
    private String suid;
    private IngestStatus status;

    public List<DropDto> getDrops() {
        return drops;
    }

    public void setDrops(List<DropDto> drops) {
        this.drops = drops;
    }

    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public IngestStatus getStatus() {
        return status;
    }

    public void setStatus(IngestStatus status) {
        this.status = status;
    }

    public boolean hasAnySelected() {
        for (DropDto drop : drops) {
            if (drop.getSelected() != null && drop.getSelected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append("suid", suid).append("status", status).append("drops", drops).toString();
    }
}
