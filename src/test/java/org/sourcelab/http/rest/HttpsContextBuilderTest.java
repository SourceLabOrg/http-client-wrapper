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