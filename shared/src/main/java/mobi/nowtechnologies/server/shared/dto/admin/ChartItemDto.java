package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
	
	private Integer chartId;
	
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
    
    private Boolean locked;

    private String code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
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

    public Integer getChartId() {
        return chartId;
    }

    public void setChartId(Integer chartId) {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("chartId", chartId)
                .append("mediaDto", mediaDto)
                .append("info", info)
                .append("position", position)
                .append("prevPosition", prevPosition)
                .append("chgPosition", chgPosition)
                .append("channel", channel)
                .append("isrc", isrc)
                .append("publishTime", publishTime)
                .append("locked", locked)
                .append("code", code)
                .toString();
    }
}
