package mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype;

import mobi.nowtechnologies.server.admin.settings.asm.dto.duration.DurationInfoDto;

public class TracksInfoDto {
    private int number;
    private DurationInfoDto durationInfoDto = new DurationInfoDto();

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public DurationInfoDto getDurationInfoDto() {
        return durationInfoDto;
    }
}
