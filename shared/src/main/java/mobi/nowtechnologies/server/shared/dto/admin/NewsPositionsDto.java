package mobi.nowtechnologies.server.shared.dto.admin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kolpakov Alexander (akolpakov)
 */
public class NewsPositionsDto {

    public static final String NEWS_POSITIONS_DTO = "NEWS_POSITIONS_DTO";

    public static final String NEWS_IDS = "ids";

    private Map<Integer, Integer> positionMap = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getPositionMap() {
        return positionMap;
    }

    public void setPositionMap(Map<Integer, Integer> positionMap) {
        this.positionMap = positionMap;
    }

    @Override
    public String toString() {
        return "NewsPositionsDto [positionMap=" + positionMap + "]";
    }
}