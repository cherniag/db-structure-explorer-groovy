package mobi.nowtechnologies.server.shared.dto.admin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ChartItemPositionDto {

    public static final String CHART_ITEMS_POSITIONS_DTO = "CHART_ITEMS_POSITIONS_DTO";

    private Map<Integer, Byte> positionMap = new HashMap<Integer, Byte>();

    public Map<Integer, Byte> getPositionMap() {
        return positionMap;
    }

    public void setPositionMap(Map<Integer, Byte> positionMap) {
        this.positionMap = positionMap;
    }

    @Override
    public String toString() {
        return "ChartItemPositionDto [positionMap=" + positionMap + "]";
    }

}
