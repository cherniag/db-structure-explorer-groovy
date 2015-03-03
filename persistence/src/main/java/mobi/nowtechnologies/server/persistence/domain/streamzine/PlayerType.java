package mobi.nowtechnologies.server.persistence.domain.streamzine;

// @author Titov Mykhaylo (titov) on 06.10.2014.
public enum PlayerType {
    REGULAR_PLAYER_ONLY("regular"),
    MINI_PLAYER_ONLY("mini");

    private String id;

    PlayerType(String id) {
        this.id = id;
    }

    public static PlayerType getDefaultPlayerType() {
        return REGULAR_PLAYER_ONLY;
    }

    public String getId() {
        return id;
    }
}
