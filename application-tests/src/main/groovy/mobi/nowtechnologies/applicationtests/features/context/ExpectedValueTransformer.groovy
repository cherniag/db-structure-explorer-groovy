package mobi.nowtechnologies.applicationtests.features.context
import cucumber.api.Transformer

class ExpectedValueTransformer extends Transformer<ExpectedValue> {
    @Override
    ExpectedValue transform(String value) {
        def list = value.split(';')

        ExpectedValue expectedValue = new ExpectedValue()
        expectedValue.expected['validFrom'] = list[0]
        expectedValue.expected['behavior'] = list[1]
        if(!"null".equalsIgnoreCase(list[2])) {
            expectedValue.expected['lockedAction'] = list[2]
        }
        return expectedValue
    }
}
