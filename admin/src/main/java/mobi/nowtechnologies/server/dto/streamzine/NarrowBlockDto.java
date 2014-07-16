package mobi.nowtechnologies.server.dto.streamzine;

public class NarrowBlockDto extends BlockDto {
    private OrdinalBlockDto first;
    private OrdinalBlockDto second;

    public OrdinalBlockDto getFirst() {
        return first;
    }

    public void setFirst(OrdinalBlockDto first) {
        this.first = first;
    }

    public OrdinalBlockDto getSecond() {
        return second;
    }

    public void setSecond(OrdinalBlockDto second) {
        this.second = second;
    }

    @Override
    public int getPosition() {
        return first.getPosition();
    }

    @Override
    public String toString() {
        return "NarrowBlockDto{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
