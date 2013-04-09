package mobi.nowtechnologies.server.shared.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SlowRequestFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlowRequestFilter.class);
    private final long threshold;

    public SlowRequestFilter() {
        this.threshold = TimeUnit.SECONDS.toNanos(1);
    }

    public SlowRequestFilter(long millisecond) {
        this.threshold = TimeUnit.MILLISECONDS.toNanos(millisecond);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { /* unused */ }

    @Override
    public void destroy() { /* unused */ }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final long startTime = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            final long elapsedMS = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            if (elapsedMS >= threshold)
                LOGGER.warn("Slow request: {} {} ({}ms)", req.getMethod(), getFullUrl(req), elapsedMS);
        }
    }

    public static String getFullUrl(HttpServletRequest request) {
        final StringBuilder url = new StringBuilder(100).append(request.getRequestURI());
        if (request.getQueryString() != null) {
            url.append('?').append(request.getQueryString());
        }
        return url.toString();
    }
}
