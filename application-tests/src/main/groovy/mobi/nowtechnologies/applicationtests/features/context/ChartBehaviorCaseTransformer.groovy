package mobi.nowtechnologies.applicationtests.features.context

import cucumber.api.Transformer

class ChartBehaviorCaseTransformer extends Transformer<ChartBehaviorCase> {
    static String NOW_TOKEN = "(NOW)"
    static String FT_TOKEN = "(FT)"
    static String RM_TOKEN = "(Rm)"
    static String RE_TOKEN = "(Re)"

    @Override
    ChartBehaviorCase transform(String value) {
        def chartBehaviorCase = new ChartBehaviorCase()
        chartBehaviorCase.info = value
        chartBehaviorCase.nowIndex = value.indexOf(NOW_TOKEN)
        chartBehaviorCase.ftIndex = value.indexOf(FT_TOKEN)
        chartBehaviorCase.rmIndex = value.indexOf(RM_TOKEN)
        chartBehaviorCase.reIndex = value.indexOf(RE_TOKEN)

        assert chartBehaviorCase.rmIndex >= 0, 'Matching Date marker is required'
        assert chartBehaviorCase.rmIndex == chartBehaviorCase.info.indexOf('('), 'Matching Date should go first'

        return chartBehaviorCase
    }
}
