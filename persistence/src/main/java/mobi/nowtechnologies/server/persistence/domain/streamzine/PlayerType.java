package mobi.nowtechnologies.server.persistence.domain.streamzine;

// @author Titov Mykhaylo (titov) on 06.10.2014.
public enum PlayerType {
    REGULAR_PLAYER_ONLY,
    MINI_PLAYER_ONLY;

    public static String[] getValues(){
        String[] values = new String[values().length];
        for (int i = 0; i < values.length; i++) {
            values[i] = values()[i].name();
        }
        return values;
    }

    public static PlayerType getDefaultPlayerType(){
        return REGULAR_PLAYER_ONLY;
    }
}
