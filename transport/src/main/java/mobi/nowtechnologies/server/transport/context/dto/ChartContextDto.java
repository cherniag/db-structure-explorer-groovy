package mobi.nowtechnologies.server.transport.context.dto;

import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.NONE)
public class ChartContextDto {

    @XmlElement(name = "behaviorTemplates")
    @JsonProperty(value = "behaviorTemplates")
    private HashMap<ChartBehaviorType, BehaviorTemplateDto> chartTemplateBehaviors = new HashMap<ChartBehaviorType, BehaviorTemplateDto>();

    @XmlElement(name = "instructions")
    @JsonProperty(value = "instructions")
    private Set<ChartBehaviorsDto> chartBehaviors = new TreeSet<>();

    protected ChartContextDto() {
        chartTemplateBehaviors.putAll(createBehaviorTemplates());
    }

    private Map<ChartBehaviorType, BehaviorTemplateDto> createBehaviorTemplates() {
        Map<ChartBehaviorType, BehaviorTemplateDto> behaviors = new HashMap<ChartBehaviorType, BehaviorTemplateDto>();

        for (ChartBehaviorType chartBehaviorType : ChartBehaviorType.values()) {
            behaviors.put(chartBehaviorType, new BehaviorTemplateDto());
        }

        return behaviors;
    }

    public BehaviorTemplateDto getChartTemplateBehaviorsDto(ChartBehaviorType type) {
        return chartTemplateBehaviors.get(type);
    }

    public Collection<ChartBehaviorsDto> getChartBehaviors() {
        return chartBehaviors;
    }
}
