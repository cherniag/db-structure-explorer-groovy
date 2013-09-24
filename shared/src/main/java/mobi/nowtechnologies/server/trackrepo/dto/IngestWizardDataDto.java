package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.enums.IngestStatus;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/15/13
 * Time: 2:41 PM
 */
public class IngestWizardDataDto {
    public static final String INGEST_WIZARD_DATA_DTO = "ingestWizardDataDto";
    public static final String ACTION = "action";

    private String suid;
    private IngestStatus status;
    protected List<DropDto> drops;

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
             if(drop.getSelected() != null && drop.getSelected())
                 return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "IngestWizardDataDto{" +
                "suid='" + suid + '\'' +
                ", status=" + status +
                ", drops=" + drops +
                "} " + super.toString();
    }
}
