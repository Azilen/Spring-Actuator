package com.azilen.web.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This filter dumps incoming request headers and parameters This should only be
 * used for debugging purpose ,must be turned off in production environment by
 * defining production mode in filter config.
 *
 * By default its off (production_mode=true)
 *
 * @author hupadhyay
 */
public class RequestDumper implements Filter {

    private static final Logger log = LogManager.getLogger(RequestDumper.class);

    private boolean isProductionMode = true;
    private List<String> ignoreExtnList = new ArrayList<String>(5);

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = ((HttpServletRequest) req);
        HttpServletResponse httpResp = (HttpServletResponse) resp;
        try {
            if (!isProductionMode) {
                for (String extn : ignoreExtnList) {
                    if (!httpReq.getRequestURI().contains(extn)) {
                        logRequest(req, resp, chain);
                    }
                }
            }
            chain.doFilter(req, resp);
        }
        catch (SecurityException ex) {
            log.error("Security exception while accessing the resource:" + ex.getMessage(), ex);
            httpResp.sendError(HttpStatus.SC_UNAUTHORIZED, ex.getMessage());
        }
    }

    /**
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    private void logRequest(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest hRequest = null;
        StringBuilder sb = new StringBuilder(500);

        if ((request instanceof HttpServletRequest)) {
            hRequest = (HttpServletRequest) request;
        }

        if (hRequest == null) {
            logAppend("requestURI", "Not available. Non-http request.", sb);
            return;
        }
        else {
            logAppend("requestURI", hRequest.getRequestURI(), sb);
            logAppend("authType", hRequest.getAuthType(), sb);
        }

        logAppend("characterEncoding", request.getCharacterEncoding(), sb);
        logAppend("contentLength", Integer.valueOf(request.getContentLength()).toString(), sb);

        logAppend("contentType", request.getContentType(), sb);
        logAppend("contextPath", hRequest.getContextPath(), sb);
        Cookie[] cookies = hRequest.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                logAppend("cookie", cookies[i].getName() + "=" + cookies[i].getValue(), sb);
            }
        }

        Enumeration hnames = hRequest.getHeaderNames();
        while (hnames.hasMoreElements()) {
            String hname = (String) hnames.nextElement();
            Enumeration hvalues = hRequest.getHeaders(hname);
            while (hvalues.hasMoreElements()) {
                String hvalue = (String) hvalues.nextElement();
                logAppend("header", hname + "=" + hvalue, sb);
            }
        }

        logAppend("locale", request.getLocale().toString(), sb);
        logAppend("method", hRequest.getMethod(), sb);

        Enumeration pnames = request.getParameterNames();
        while (pnames.hasMoreElements()) {
            String pname = (String) pnames.nextElement();
            String[] pvalues = request.getParameterValues(pname);
            StringBuilder result = new StringBuilder(pname);
            result.append('=');
            for (int i = 0; i < pvalues.length; i++) {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(pvalues[i]);
            }
            logAppend("parameter", result.toString(), sb);
        }

        logAppend("pathInfo", hRequest.getPathInfo(), sb);
        logAppend("protocol", request.getProtocol(), sb);
        logAppend("queryString", hRequest.getQueryString(), sb);
        logAppend("remoteAddr", request.getRemoteAddr(), sb);
        logAppend("remoteHost", request.getRemoteHost(), sb);
        logAppend("remoteUser", hRequest.getRemoteUser(), sb);
        logAppend("requestedSessionId", hRequest.getRequestedSessionId(), sb);
        logAppend("scheme", request.getScheme(), sb);
        logAppend("serverName", request.getServerName(), sb);
        logAppend("serverPort", Integer.toString(request.getServerPort()), sb);
        logAppend("servletPath", hRequest.getServletPath(), sb);
        logAppend("isSecure", Boolean.toString(request.isSecure()), sb);

        logAppend("------------------", "--------------------------------------------", sb);
        
        if (log.isDebugEnabled()) {
            log.debug(sb.toString());
        }
    }

    private void logAppend(String attribute, String value, StringBuilder sb) {
        if (log.isDebugEnabled()) {
            sb.append(attribute);
            sb.append('=');
            sb.append(value).append("\n");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            isProductionMode = Boolean.valueOf(filterConfig.getServletContext().getInitParameter("productionMode"));
            if (filterConfig.getInitParameter("ignoreExtn") != null) {
                ignoreExtnList = Arrays.<String>asList(filterConfig.getInitParameter("ignoreExtn").split(","));
            }
        }
        catch (Exception ex) {
            log.warn("Failed to set filter config param 'production-mode',fallbacks to default:true", ex);
        }
    }

    @Override
    public void destroy() {
    }
}
