package mobi.nowtechnologies.server.trackrepo.dto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/15/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class IngestWizardDataDto {
    public static final String INGEST_WIZARD_DATA_DTO = "IngestWizardDataDto";

    private String suid;
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

    @Override
    public String toString() {
        return "IngestWizardDataDto{" +
                "suid='" + suid + '\'' +
                ", drops=" + drops +
                "} " + super.toString();
    }
}
