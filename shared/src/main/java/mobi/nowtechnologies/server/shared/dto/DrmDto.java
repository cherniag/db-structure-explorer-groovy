package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Arrays;

@XmlRootElement(name = "drm")
public class DrmDto {

    private DrmItemDto[] drmItemDtos;

    @XmlAnyElement
    public DrmItemDto[] getDrmItemDtos() {
        return drmItemDtos;
    }

    public void setDrmItemDtos(DrmItemDto[] drmDtos) {
        this.drmItemDtos = drmDtos;
    }

    @Override
    public String toString() {
        return "DrmDto [drmItemDtos=" + Arrays.toString(drmItemDtos) + "]";
    }
}