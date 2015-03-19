package mobi.nowtechnologies.server.transport.mvc.mapping;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * User: Alexsandr_Kolpakov Date: 12/10/13 Time: 11:02 AM
 */
public class RESTRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestMappingInfo getMatchingMapping(RequestMappingInfo info, HttpServletRequest request) {
        String[] patterns = info.getPatternsCondition().getPatterns().toArray(new String[0]);
        RESTPatternsRequestCondition restPatternsRequestCondition = new RESTPatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(), useSuffixPatternMatch(), useTrailingSlashMatch());

        RequestMethodsRequestCondition methods = info.getMethodsCondition().getMatchingCondition(request);
        ParamsRequestCondition params = info.getParamsCondition().getMatchingCondition(request);
        HeadersRequestCondition headers = info.getHeadersCondition().getMatchingCondition(request);
        ConsumesRequestCondition consumes = info.getConsumesCondition().getMatchingCondition(request);
        ProducesRequestCondition produces = info.getProducesCondition().getMatchingCondition(request);

        if (methods == null || params == null || headers == null || consumes == null || produces == null) {
            return null;
        }

        PatternsRequestCondition patternsRequestCondition = restPatternsRequestCondition.getMatchingCondition(request);
        if (patternsRequestCondition == null) {
            return null;
        }

        RequestConditionHolder custom = new RequestConditionHolder(info.getCustomCondition()).getMatchingCondition(request);
        if (custom == null) {
            return null;
        }

        return new RequestMappingInfo(patternsRequestCondition, methods, params, headers, consumes, produces, custom.getCondition());
    }
}
