package mobi.nowtechnologies.server.shared.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlowRequestFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlowRequestFilter.class);
    private final long threshold;

    public SlowRequestFilter() {
        this.threshold = TimeUnit.SECONDS.toNanos(1);
    }

    public SlowRequestFilter(long millisecond) {
        this.threshold = TimeUnit.MILLISECONDS.toNanos(millisecond);
    }

    public static String getFullUrl(HttpServletRequest request) {
        final StringBuilder url = new StringBuilder(100).append(request.getRequestURI());
        if (request.getQueryString() != null) {
            url.append('?').append(request.getQueryString());
        }
        return url.toString();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { /* unused */ }

    @Override
    public void destroy() { /* unused */ }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final long startTime = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            final long elapsedMS = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            if (elapsedMS >= threshold) {
                LOGGER.warn("Slow request: {} {} ({}ms)", req.getMethod(), getFullUrl(req), elapsedMS);
            }
        }
    }
}