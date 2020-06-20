package tho.nill.datenlieferung;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogHeaderFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(LogHeaderFilter.class);

	public LogHeaderFilter() {
		LOGGER.debug("LogHeaderFilter wurde erzeugt");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain cain)
			throws IOException, SecurityException, ServletException {
		if (request instanceof HttpServletRequest) {

			String header = ((HttpServletRequest) request).getHeader("Authorization");
			LOGGER.debug("servletRequest hat folgenden Authorization Header {}", header);

			HttpServletRequest httpRequest = (HttpServletRequest) request;
			Enumeration<String> headerNames = httpRequest.getHeaderNames();

			if (headerNames != null) {
				LOGGER.debug("servletRequest hat folgende Header");
				Iterator<String> i = headerNames.asIterator();
				while (i.hasNext()) {
					String headerName = i.next();
					LOGGER.debug("Header: " + headerName + "=" + httpRequest.getHeader(headerName));

				}
				LOGGER.debug("Ende der Header");
			} else {
				LOGGER.debug("servletRequest hat keine Header");
			}
		} else {
			LOGGER.debug("servletRequest ist kein HttpRequest");
		}

		cain.doFilter(request, response);

	}

}
