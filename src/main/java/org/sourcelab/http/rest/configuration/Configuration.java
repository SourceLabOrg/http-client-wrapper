package org.sourcelab.http.rest.configuration;

import org.sourcelab.http.rest.interceptor.RequestInterceptor;

import java.io.File;
import java.util.List;

/**
 * Configuration interface.
 */
public interface Configuration {

    /**
     * Proxy configuration properties if defined.
     * @return NULL if not configured, or Proxy configuration properties if defined.
     */
    ProxyConfiguration getProxyConfiguration();

    /**
     * Hostname to make API requests against.
     * @return Hostname to make API requests against.
     */
    String getApiHost();

    /**
     * Skip all validation of SSL Certificates.  This is insecure and highly discouraged!
     *
     * @return boolean if invalid SSL certificates should be allowed.
     */
    boolean getIgnoreInvalidSslCertificates();

    /**
     * (Optional) Supply a path to a JKS trust store to be used to validate SSL certificates with.  You'll need this
     * if you're using Self Signed certificates.
     *
     * Alternatively you can can explicitly add your certificate to the JVM's truststore using a command like:
     * keytool -importcert -keystore truststore.jks -file servercert.pem
     *
     * @return file path to truststore.
     */
    File getTrustStoreFile();

    /**
     * If set, the truststore password.
     * @return NULL if no password set.
     */
    String getTrustStorePassword();

    /**
     * Get the request timeout value, in seconds.
     * @return Request timeout value represented in seconds.
     */
    int getRequestTimeoutInSeconds();

    /**
     * Path to a JKS key store to be used for client validation.  If your requesting host
     * is configured to only accept requests from clients with a valid client certificate.
     * @return NULL if not configured, otherwise path to JKS keystore.
    */
    File getKeyStoreFile();

    /**
     * Password for keystore.
     * @return NULL if not configured, otherwise password for JKS keystore.
     */
    String getKeyStorePassword();

    /**
     * Basic Auth username.
     * @return NULL if not configured, otherwise basic auth username.
     */
    String getBasicAuthUsername();

    /**
     * Basic Auth password.
     * @return NULL if not configured, otherwise basic auth username.
     */
    String getBasicAuthPassword();

    /**
     * Implementation for intercepting requests.
     * @return instance.
     */
    RequestInterceptor getRequestInterceptor();

    /**
     * Immutable list of request headers to be sent with every request.
     * @return Immutable list of request headers.
     */
    List<RequestHeader> getRequestHeaders();
}
