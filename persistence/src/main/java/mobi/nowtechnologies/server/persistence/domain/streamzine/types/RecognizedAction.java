package mobi.nowtechnologies.server.persistence.domain.streamzine.types;

public enum RecognizedAction {
    SUBSCRIBE("opensubscription");

    private String id;

    RecognizedAction(String id) {
        this.id = id;
    }

    public static RecognizedAction recongnize(String actionId) {
        for (RecognizedAction action : values()) {
            if (action.id.equals(actionId)) {
                return action;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
