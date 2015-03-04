package mobi.nowtechnologies.server.persistence.domain.streamzine.types;

/**
 * @author kots
 * @since 8/18/2014.
 */
public enum RecognizedPage {
    GENERIC_NEWS("news");

    private String name;

    RecognizedPage(String name) {
        this.name = name;
    }

    public static RecognizedPage recognize(String name) {
        for (RecognizedPage page : values()) {
            if (page.name.equals(name)) {
                return page;
            }
        }
        return null;
    }
}
