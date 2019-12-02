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
package org.sourcelab.http.rest;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.junit.jupiter.api.Test;
import org.sourcelab.http.rest.configuration.BasicConfiguration;

import javax.net.ssl.HostnameVerifier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpsContextBuilderTest {
    /**
     * Constructor should require non-null arguments.
     */
    @Test
    void testConstructorNullArguments() {
        assertThrows(NullPointerException.class, () -> new HttpsContextBuilder(null));
    }

    /**
     * When configured to validate SSL certificates, should not get NoopHostnameVerifier back.
     */
    @Test
    void getHostnameVerifier_validateSslCertificates() {
        final HttpsContextBuilder builder = new HttpsContextBuilder(new BasicConfiguration<>("http://localhost"));

        final HostnameVerifier verifier = builder.getHostnameVerifier();
        assertNotNull(verifier);
        assertFalse(verifier instanceof NoopHostnameVerifier, "Should not be an instance of NoopHostnameVerifier");
    }

    /**
     * When configured to skip validating SSL certificates, should get NoopHostnameVerifier back.
     */
    @Test
    void getHostnameVerifier_acceptInvalidSslCertificates() {
        final HttpsContextBuilder builder = new HttpsContextBuilder(
            new BasicConfiguration("http://localhost").useInsecureSslCertificates()
        );

        final HostnameVerifier verifier = builder.getHostnameVerifier();
        assertNotNull(verifier);
        assertTrue(verifier instanceof NoopHostnameVerifier, "Should be an instance of NoopHostnameVerifier");
    }
}