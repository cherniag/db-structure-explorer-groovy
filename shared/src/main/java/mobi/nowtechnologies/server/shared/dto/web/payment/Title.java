package mobi.nowtechnologies.server.shared.dto.web.payment;

public enum Title {
    MR("Mr"),
    MS("Ms"),
    MRS("Mrs"),
    MISS("Miss");

    private String value;

    private Title(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}