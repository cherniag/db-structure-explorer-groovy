package mobi.nowtechnologies.server.interceptor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

public class LoggerInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!request.getPathInfo().contains("REGISTER_USER")) {
			request.getParameterMap();
		}
		request = new RequestCachingRequestWrapper(request);
		
		String remoteAddr = Utils.getIpFromRequest(request);
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		
		LogUtils.putGlobalMDC(null, null, request.getParameter("USER_NAME"), request.getParameter("COMMUNITY_NAME"), request.getPathInfo().replaceFirst("/", ""), handlerMethod.getBean().getClass(), remoteAddr);
		
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
		LogUtils.removeGlobalMDC();
		LogUtils.remove3rdParyRequestProfileMDC();
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
