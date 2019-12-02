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

import java.util.Objects;

/**
 * Defines configuration for Proxy connections.
 */
public class ProxyConfiguration {

    // Proxy Configuration
    private final String proxyHost;
    private final int proxyPort;
    private final String proxyScheme;

    // Optional Proxy Authentication.
    private final String proxyUsername;
    private final String proxyPassword;

    /**
     * Constructor.
     * @param proxyHost host of the proxy.
     * @param proxyPort port of the proxy.
     * @param proxyScheme Scheme to use talking to the proxy, typically "HTTP" or "HTTPS"
     * @param proxyUsername (optional) if proxy requires authentication, the username.
     * @param proxyPassword (optional) if proxy requires authentication, the password.
     */
    public ProxyConfiguration(
        final String proxyHost,
        final int proxyPort,
        final String proxyScheme,
        final String proxyUsername,
        final String proxyPassword) {
        this.proxyHost = Objects.requireNonNull(proxyHost);
        this.proxyPort = proxyPort;
        this.proxyScheme = Objects.requireNonNull(proxyScheme);
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
    }

    /**
     * Constructor.
     * @param proxyHost host of the proxy.
     * @param proxyPort port of the proxy.
     * @param proxyScheme Scheme to use talking to the proxy, typically "HTTP" or "HTTPS"
     */
    public ProxyConfiguration(
        final String proxyHost,
        final int proxyPort,
        final String proxyScheme) {
        this(proxyHost, proxyPort, proxyScheme, null, null);
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyScheme() {
        return proxyScheme;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * Is this configured with a proxy username and password.
     *
     * @return true if there is a proxy username defined.
     */
    public boolean isProxyAuthenticationEnabled() {
        return proxyUsername != null;
    }

    /**
     * Builder instance for ProxyConfiguration.
     * @return builder instance.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * ProxyConfiguration Builder.
     */
    public static final class Builder {
        // Proxy Configuration
        private String proxyHost;
        private int proxyPort;
        private String proxyScheme = "HTTP";
        // Optional Proxy Authentication.
        private String proxyUsername;
        private String proxyPassword;

        private Builder() {
        }

        /**
         * Allow setting optional proxy configuration.
         *
         * @param proxyHost Host for the proxy to use.
         * @param proxyPort Post for the proxy to use.
         * @param proxyScheme Scheme to use, HTTP/HTTPS
         * @return Configuration instance.
         */
        public Builder useProxy(final String proxyHost, final int proxyPort, final String proxyScheme) {
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
            this.proxyScheme = proxyScheme;
            return this;
        }

        /**
         * Allow setting credentials for a proxy that requires authentication.
         *
         * @param proxyUsername Username for proxy.
         * @param proxyPassword Password for proxy.
         * @return Configuration instance.
         */
        public Builder useProxyAuthentication(final String proxyUsername, final String proxyPassword) {
            this.proxyUsername = proxyUsername;
            this.proxyPassword = proxyPassword;
            return this;
        }

        /**
         * Create a new ProxyConfiguration instance.
         * @return new ProxyConfiguration instance.
         */
        public ProxyConfiguration build() {
            return new ProxyConfiguration(proxyHost, proxyPort, proxyScheme, proxyUsername, proxyPassword);
        }
    }
}
