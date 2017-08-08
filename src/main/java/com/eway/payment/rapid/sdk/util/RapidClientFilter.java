package com.eway.payment.rapid.sdk.util;

import com.eway.payment.rapid.sdk.RapidClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Properties;

/**
 * Filter for WebResource to add Rapid headers
 */
public class RapidClientFilter implements ClientRequestFilter {

    private String apiVersion;
    private static final Logger LOGGER = LoggerFactory.getLogger(RapidClient.class);

    public void setVersion(String version) {
        this.apiVersion = version;
    }


    public void filter(ClientRequestContext aContext) throws IOException {

        String userAgent = "";
        try {
            Properties prop = ResourceUtil.loadProperiesOnResourceFolder(Constant.RAPID_API_RESOURCE);
            userAgent = prop.getProperty(Constant.RAPID_SDK_USER_AGENT_PARAM);
            if (StringUtils.isBlank(userAgent)) {
                throw new Exception("Resource file " + Constant.RAPID_API_RESOURCE + " is invalid.");
            }
        } catch (Exception e) {
            LOGGER.error("User Agent could not be loaded", e);
        }

        aContext.getHeaders().putSingle(HttpHeaders.USER_AGENT, userAgent);
        if (this.apiVersion != null) {
            aContext.getHeaders().putSingle("X-EWAY-APIVERSION", this.apiVersion);
        }
    }
}
