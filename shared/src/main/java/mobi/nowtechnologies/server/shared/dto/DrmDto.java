package mobi.nowtechnologies.server.shared.dto;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "drm")
public class DrmDto {

	private DrmItemDto[] drmItemDtos;
	
	public void setDrmItemDtos(DrmItemDto[] drmDtos) {
		this.drmItemDtos = drmDtos;
	}

	@XmlAnyElement
	public DrmItemDto[] getDrmItemDtos() {
		return drmItemDtos;
	}

	@Override
	public String toString() {
		return "DrmDto [drmItemDtos=" + Arrays.toString(drmItemDtos) + "]";
	}
}