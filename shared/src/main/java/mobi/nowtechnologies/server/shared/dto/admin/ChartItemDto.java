package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ChartItemDto {
	
	public static final String CHART_ITEM_DTO = "CHART_ITEM_DTO";

	public static final String CHART_ITEM_DTO_LIST = "CHART_ITEM_DTO_LIST";
	
	private Integer id;
	
	private Byte chartId;
	
	private MediaDto mediaDto;
	
	@NotNull
	private String info;
	
	private byte position;

	private byte prevPosition;
	
	private ChgPosition chgPosition;
	
	private String channel;
    private String isrc;

    @DateTimeFormat(iso=ISO.DATE_TIME)
	private Date publishTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Byte getChartId() {
		return chartId;
	}

	public void setChartId(Byte chartId) {
		this.chartId = chartId;
	}

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

	public MediaDto getMediaDto() {
		return mediaDto;
	}

	public void setMediaDto(MediaDto mediaDto) {
		this.mediaDto = mediaDto;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public byte getPrevPosition() {
		return prevPosition;
	}

	public void setPrevPosition(byte prevPosition) {
		this.prevPosition = prevPosition;
	}

	public ChgPosition getChgPosition() {
		return chgPosition;
	}

	public void setChgPosition(ChgPosition chgPosition) {
		this.chgPosition = chgPosition;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "ChartItemDto [channel=" + channel + ", chartId=" + chartId + ", chgPosition=" + chgPosition + ", id=" + id + ", info=" + info + ", mediaDto="
				+ mediaDto + ", position=" + position + ", prevPosition=" + prevPosition + ", publishTime=" + publishTime + "]";
	}

}
