/**
 * Copyright 2019 SourceLab.org https://github.com/SourceLabOrg/HttpClientWrapper
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.sourcelab.http.rest.configuration;

import org.sourcelab.http.rest.interceptor.NoopRequestInterceptor;
import org.sourcelab.http.rest.interceptor.RequestInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Basic implementation of Configuration interface.  It's possible to extend this class with your own more specific
 * configuration.
 *
 * @param <SELF> The class extending this instance.
 */
public class BasicConfiguration<SELF extends BasicConfiguration> implements Configuration {
    // Defines the URL/Hostname of the API.
    private final String apiHost;

    // Optional Connection options
    private int requestTimeoutInSeconds = 300;

    // Optional BasicAuth options
    private String basicAuthUsername = null;
    private String basicAuthPassword = null;

    // Optional settings to validate SSL certificate.
    private boolean ignoreInvalidSslCertificates = false;
    private File trustStoreFile = null;
    private String trustStorePassword = null;

    // Optional settings to send a client certificate with request.
    private File keyStoreFile = null;
    private String keyStorePassword = null;

    // Optional Proxy Configuration
    private ProxyConfiguration proxyConfiguration =  null;

    /**
     * Request interceptor.
     */
    private RequestInterceptor requestInterceptor = new NoopRequestInterceptor();

    /**
     * Request headers added to every request.
     */
    private final List<RequestHeader> requestHeaders = new ArrayList<>();

    /**
     * Default Constructor.
     * @param apiHost Hostname of API
     */
    public BasicConfiguration(final String apiHost) {
        if (apiHost == null) {
            throw new NullPointerException("apiHost parameter cannot be null!");
        }

        // Normalize into "http://<hostname>" if not specified.
        if (apiHost.startsWith("http://") || apiHost.startsWith("https://")) {
            this.apiHost = apiHost;
        } else {
            // Assume http protocol
            this.apiHost = "http://" + apiHost;
        }
    }

    /**
     * Allow setting http Basic-Authentication username and password to authenticate requests.
     *
     * @param username username to authenticate requests with.
     * @param password password to authenticate requests with.
     * @return Configuration instance.
     */
    public SELF useBasicAuth(final String username, final String password) {
        this.basicAuthUsername = username;
        this.basicAuthPassword = password;
        return (SELF) this;
    }

    /**
     * Allow setting optional proxy configuration.
     *
     * @param proxyConfig configuration for proxy.
     * @return Configuration instance.
     */
    public SELF useProxy(final ProxyConfiguration proxyConfig) {
        this.proxyConfiguration = proxyConfig;
        return (SELF) this;
    }

    /**
     * Skip all validation of SSL Certificates.  This is insecure and highly discouraged!
     *
     * @return Configuration instance.
     */
    public SELF useInsecureSslCertificates() {
        this.ignoreInvalidSslCertificates = true;
        return (SELF) this;
    }

    /**
     * (Optional) Supply a path to a JKS trust store to be used to validate SSL certificates with.  You'll need this
     * if you're using Self Signed certificates.
     *
     * Alternatively you can can explicitly add your certificate to the JVM's truststore using a command like:
     * keytool -importcert -keystore truststore.jks -file servercert.pem
     *
     * @param trustStoreFile file path to truststore.
     * @param password (optional) Password for truststore. Pass null if no password.
     * @return Configuration instance.
     */
    public SELF useTrustStore(final File trustStoreFile, final String password) {
        this.trustStoreFile = Objects.requireNonNull(trustStoreFile);
        this.trustStorePassword = password;
        return (SELF) this;
    }

    /**
     * (Optional) Supply a path to a JKS key store to be used for client validation.  You'll need this if your
     * Kafka-Connect instance is configured to only accept requests from clients with a valid certificate.
     *
     * @param keyStoreFile file path to keystore.
     * @param password (optional) Password for keystore. Pass null if no password.
     * @return Configuration instance.
     */
    public SELF useKeyStore(final File keyStoreFile, final String password) {
        this.keyStoreFile = Objects.requireNonNull(keyStoreFile);
        this.keyStorePassword = password;
        return (SELF) this;
    }

    /**
     * Set the request timeout value, in seconds.
     * @param requestTimeoutInSeconds How long before a request times out, in seconds.
     * @return Configuration instance.
     */
    public SELF useRequestTimeoutInSeconds(final int requestTimeoutInSeconds) {
        this.requestTimeoutInSeconds = requestTimeoutInSeconds;
        return (SELF) this;
    }

    /**
     * Set request interceptor instance.
     * @param requestInterceptor instance.
     * @return Configuration instance.
     */
    public SELF useRequestInteceptor(final RequestInterceptor requestInterceptor) {
        this.requestInterceptor = Objects.requireNonNull(requestInterceptor);
        return (SELF) this;
    }

    /**
     * Add additional request header to add to each request.
     *
     * @param name name of header.
     * @param value value of header.
     * @return Configuration instance.
     */
    public SELF withRequestHeader(final String name, final String value) {
        requestHeaders.add(new RequestHeader(name, value));
        return (SELF) this;
    }

    public String getApiHost() {
        return apiHost;
    }

    public boolean getIgnoreInvalidSslCertificates() {
        return ignoreInvalidSslCertificates;
    }

    public File getTrustStoreFile() {
        return trustStoreFile;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public int getRequestTimeoutInSeconds() {
        return requestTimeoutInSeconds;
    }

    public File getKeyStoreFile() {
        return keyStoreFile;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getBasicAuthUsername() {
        return basicAuthUsername;
    }

    public String getBasicAuthPassword() {
        return basicAuthPassword;
    }

    @Override
    public ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }

    @Override
    public RequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    @Override
    public List<RequestHeader> getRequestHeaders() {
        return Collections.unmodifiableList(requestHeaders);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("Configuration{")
            .append("apiHost='").append(apiHost).append('\'')
            .append(", requestTimeout='").append(requestTimeoutInSeconds).append('\'');
        if (getProxyConfiguration() != null) {
            stringBuilder
                .append(", proxy='").append(getProxyConfiguration().getProxyScheme()).append("://");

            // Append configured proxy auth details
            if (getProxyConfiguration().getProxyUsername() != null) {
                stringBuilder.append(getProxyConfiguration().getProxyUsername()).append(':').append("XXXXXXX@");
            }

            stringBuilder.append(getProxyConfiguration().getProxyHost()).append(":").append(getProxyConfiguration().getProxyPort()).append('\'');
        }
        stringBuilder.append(", ignoreInvalidSslCertificates='").append(ignoreInvalidSslCertificates).append('\'');
        if (trustStoreFile != null) {
            stringBuilder.append(", sslTrustStoreFile='").append(trustStoreFile).append('\'');
            if (trustStorePassword != null) {
                stringBuilder.append(", sslTrustStorePassword='******'");
            }
        }
        if (basicAuthUsername != null) {
            stringBuilder
                .append(", basicAuthUsername='").append(basicAuthUsername).append('\'')
                .append(", basicAuthPassword='******'");
        }
        stringBuilder.append('}');

        return stringBuilder.toString();
    }
}
