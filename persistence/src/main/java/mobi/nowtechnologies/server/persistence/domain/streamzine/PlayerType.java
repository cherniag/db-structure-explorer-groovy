package mobi.nowtechnologies.server.persistence.domain.streamzine;

// @author Titov Mykhaylo (titov) on 06.10.2014.
public enum PlayerType {
    REGULAR_PLAYER_ONLY,
    MINI_PLAYER_ONLY;

    public static PlayerType getDefaultPlayerType(){
        return REGULAR_PLAYER_ONLY;
    }
}
