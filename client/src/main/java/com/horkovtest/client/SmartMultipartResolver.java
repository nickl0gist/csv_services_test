package com.horkovtest.client;

import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Created in order to overwrite isMultipart method to be able to handle multipart payloads conditionally.
 */
public class SmartMultipartResolver extends StandardServletMultipartResolver {
    @Override
    public boolean isMultipart(HttpServletRequest request) {
        if ("large".equals(request.getHeader("X-Large-Upload"))) {
            return false;
        }
        return super.isMultipart(request);
    }
}
