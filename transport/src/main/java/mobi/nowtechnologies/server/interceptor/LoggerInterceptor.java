package mobi.nowtechnologies.server.interceptor;

import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.transport.controller.CommonController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoggerInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!request.getPathInfo().contains("REGISTER_USER")) {
			request.getParameterMap();
		}
		request = new RequestCachingRequestWrapper(request);

		HandlerMethod handlerMethod = (HandlerMethod) handler;
        CommonController controller = (CommonController)handlerMethod.getBean();
		
		LogUtils.putGlobalMDC(null, null, request.getParameter("USER_NAME"), controller.getCurrentCommunityUri(), request.getPathInfo().replaceFirst("/", ""), handlerMethod.getBean().getClass(), controller.getCurrentRemoteAddr());

		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
		LogUtils.removeGlobalMDC();
		LogUtils.removeAll3rdParyRequestProfileMDC();
		super.afterCompletion(request, response, handler, ex);
	}
	
	private static class RequestCachingRequestWrapper extends HttpServletRequestWrapper {

		private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		private final ServletInputStream inputStream;

		private BufferedReader reader;

		private RequestCachingRequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
			this.inputStream = new RequestCachingInputStream(request.getInputStream());
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			return inputStream;
		}

		@Override
		public String getCharacterEncoding() {
			return super.getCharacterEncoding() != null ? super.getCharacterEncoding() :
					WebUtils.DEFAULT_CHARACTER_ENCODING;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			if (this.reader == null) {
				this.reader = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
			}
			return this.reader;
		}

		private byte[] toByteArray() {
			return this.bos.toByteArray();
		}

		private class RequestCachingInputStream extends ServletInputStream {

			private final ServletInputStream is;

			private RequestCachingInputStream(ServletInputStream is) {
				this.is = is;
			}

			@Override
			public int read() throws IOException {
				int ch = is.read();
				if (ch != -1) {
					bos.write(ch);
				}
				return ch;
			}

		}

	}

}
