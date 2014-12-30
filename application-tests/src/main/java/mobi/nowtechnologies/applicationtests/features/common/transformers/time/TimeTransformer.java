package mobi.nowtechnologies.applicationtests.features.common.transformers.time;

import cucumber.api.Transformer;

import java.util.Date;

/**
 * Author: Gennadii Cherniaiev
 * Date: 12/5/2014
 */
public class TimeTransformer extends Transformer<Long> {


    @Override
    public Long transform(String value) {
        if(value.contains("past")){
            return new Date().getTime() - 10000L;
        }
        return null;
    }
}
