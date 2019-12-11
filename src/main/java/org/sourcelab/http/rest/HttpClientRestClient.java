/**
 * Copyright 2019 SourceLab.org https://github.com/SourceLabOrg/http-client-wrapper
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sourcelab.http.rest.configuration.Configuration;
import org.sourcelab.http.rest.configuration.ProxyConfiguration;
import org.sourcelab.http.rest.request.RequestHeader;
import org.sourcelab.http.rest.exceptions.ConnectionException;
import org.sourcelab.http.rest.exceptions.ResultParsingException;
import org.sourcelab.http.rest.handlers.RestResponseHandler;
import org.sourcelab.http.rest.interceptor.HeaderRequestInterceptor;
import org.sourcelab.http.rest.interceptor.RequestContext;
import org.sourcelab.http.rest.interceptor.RequestInterceptor;
import org.sourcelab.http.rest.request.Request;
import org.sourcelab.http.rest.request.RequestMethod;
import org.sourcelab.http.rest.request.RequestParameter;
import org.sourcelab.http.rest.request.body.RequestBodyContent;
import org.sourcelab.http.rest.request.body.UrlEncodedFormBodyContent;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RestClient implementation using HTTPClient.
 */
public class HttpClientRestClient implements RestClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientRestClient.class);

    /**
     * Save a copy of the configuration.
     */
    private Configuration configuration;

    /**
     * Our underlying Http Client.
     */
    private CloseableHttpClient httpClient;

    private HttpClientContext httpClientContext;

    /**
     * To allow for custom modifications to request prior to submitting it.
     */
    private final List<RequestInterceptor> requestInterceptors = new ArrayList<>();

    /**
     * Constructor.
     */
    public HttpClientRestClient() {
    }

    /**
     * Initialization method.  This takes in the configuration and sets up the underlying
     * http client appropriately.
     * @param configuration The user defined configuration.
     */
    @Override
    public void init(final Configuration configuration) {
        // Save reference to configuration
        this.configuration = configuration;

        // Load default headers
        if (configuration.getRequestHeaders() != null && !configuration.getRequestHeaders().isEmpty()) {
            // Add interceptor to add headers to all requests.
            requestInterceptors.add(
                new HeaderRequestInterceptor(configuration.getRequestHeaders())
            );
        }

        // Load RequestMutator instance from configuration.
        requestInterceptors.addAll(configuration.getRequestInterceptors());

        // Create https context builder utility.
        final HttpsContextBuilder httpsContextBuilder = new HttpsContextBuilder(configuration);

        // Setup client builder
        final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder
            // Define timeout
            .setConnectionTimeToLive(configuration.getRequestTimeoutInSeconds(), TimeUnit.SECONDS)

            // Define SSL Socket Factory instance.
            .setSSLSocketFactory(httpsContextBuilder.createSslSocketFactory());

        // Define our RequestConfigBuilder
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        // Define our Credentials Provider
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();

        // Define our context
        httpClientContext = HttpClientContext.create();

        // Define our auth cache
        final AuthCache authCache = new BasicAuthCache();

        // If we have a configured proxy host
        if (configuration.getProxyConfiguration() != null) {
            final ProxyConfiguration proxyConfiguration = configuration.getProxyConfiguration();

            // Define proxy host
            final HttpHost proxyHost = new HttpHost(
                proxyConfiguration.getProxyHost(),
                proxyConfiguration.getProxyPort(),
                proxyConfiguration.getProxyScheme()
            );

            // If we have proxy auth enabled
            if (proxyConfiguration.isProxyAuthenticationEnabled()) {
                // Add proxy credentials
                credsProvider.setCredentials(
                    new AuthScope(proxyConfiguration.getProxyHost(), proxyConfiguration.getProxyPort()),
                    new UsernamePasswordCredentials(proxyConfiguration.getProxyUsername(), proxyConfiguration.getProxyPassword())
                );

                // Preemptive load context with authentication.
                authCache.put(
                    new HttpHost(
                        proxyConfiguration.getProxyHost(),
                        proxyConfiguration.getProxyPort(),
                        proxyConfiguration.getProxyScheme()
                    ), new BasicScheme()
                );
            }

            // Attach Proxy to request config builder
            requestConfigBuilder.setProxy(proxyHost);
        }

        // If BasicAuth credentials are configured.
        if (configuration.getBasicAuthUsername() != null) {
            try {
                // parse ApiHost for Hostname and port.
                final URL apiUrl = new URL(configuration.getApiHost());

                // Add credentials
                credsProvider.setCredentials(
                    new AuthScope(apiUrl.getHost(), apiUrl.getPort()),
                    new UsernamePasswordCredentials(
                        configuration.getBasicAuthUsername(),
                        configuration.getBasicAuthPassword()
                    )
                );

                // Preemptive load context with authentication.
                authCache.put(
                    new HttpHost(apiUrl.getHost(), apiUrl.getPort(), apiUrl.getProtocol()), new BasicScheme()
                );
            } catch (final MalformedURLException exception) {
                throw new RuntimeException(exception.getMessage(), exception);
            }
        }

        // Configure context.
        httpClientContext.setAuthCache(authCache);
        httpClientContext.setCredentialsProvider(credsProvider);

        // Attach Credentials provider to client builder.
        clientBuilder.setDefaultCredentialsProvider(credsProvider);

        // Attach default request config
        clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

        // build http client
        httpClient = clientBuilder.build();
    }

    @Override
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (final IOException e) {
                logger.error("Error closing: {}", e.getMessage(), e);
            }
        }
        httpClient = null;
    }

    /**
     * Make a request against the Pardot API.
     * @param request The request to submit.
     * @return The response, in UTF-8 String format.
     * @throws RestException if something goes wrong.
     */
    @Override
    public RestResponse submitRequest(final Request request) throws RestException {
        final String url = constructApiUrl(request.getApiEndpoint());
        final ResponseHandler<RestResponse> responseHandler = new RestResponseHandler();

        try {
            switch (request.getRequestMethod()) {
                case GET:
                    return submitGetRequest(url, request.getRequestBody(), responseHandler);
                case POST:
                    return submitPostRequest(url, request.getRequestBody(), responseHandler);
                case PUT:
                    return submitPutRequest(url, request.getRequestBody(), responseHandler);
                case DELETE:
                    return submitDeleteRequest(url, request.getRequestBody(), responseHandler);
                default:
                    throw new IllegalArgumentException("Unknown Request Method: " + request.getRequestMethod());
            }
        } catch (final IOException exception) {
            throw new RestException(exception.getMessage(), exception);
        }
    }

    /**
     * Internal GET method.
     * @param url Url to GET to.
     * @param requestBodyContent parameters to include in the request
     * @param responseHandler The response Handler to use to parse the response
     * @param <T> The type that ResponseHandler returns.
     * @return Parsed response.
     */
    private <T> T submitGetRequest(
        final String url,
        final RequestBodyContent requestBodyContent,
        final ResponseHandler<T> responseHandler
    ) throws IOException {
        final RequestContext requestContext = new RequestContext(url, RequestMethod.GET);

        try {
            // Construct URI including our request parameters.
            final URIBuilder uriBuilder = new URIBuilder(url)
                .setCharset(StandardCharsets.UTF_8);

            if (requestBodyContent instanceof UrlEncodedFormBodyContent) {
                final List<RequestParameter> requestParameters = processRequestParameters(
                    ((UrlEncodedFormBodyContent) requestBodyContent).getRequestParameters(),
                    requestContext
                );

                // Attach submitRequest params
                for (final RequestParameter requestParameter : requestParameters) {
                    uriBuilder.setParameter(requestParameter.getName(), requestParameter.getValue());
                }
            }

            // Build Get Request
            final HttpGet get = new HttpGet(uriBuilder.build());

            // Add headers.
            buildHeaders(get, requestContext);

            logger.debug("Executing request {}", get.getRequestLine());

            // Execute and return
            return httpClient.execute(get, responseHandler, httpClientContext);
        } catch (final ClientProtocolException | SocketException | URISyntaxException | SSLHandshakeException connectionException) {
            // Typically this is a connection or certificate issue.
            throw new ConnectionException(connectionException.getMessage(), connectionException);
        } catch (final IOException ioException) {
            // Typically this is a parse error.
            throw new ResultParsingException(ioException.getMessage(), ioException);
        }
    }

    /**
     * Internal POST method.
     * @param url Url to POST to.
     * @param requestBodyContent POST entity include in the request body
     * @param responseHandler The response Handler to use to parse the response
     * @param <T> The type that ResponseHandler returns.
     * @return Parsed response.
     */
    private <T> T submitPostRequest(
        final String url,
        final RequestBodyContent requestBodyContent,
        final ResponseHandler<T> responseHandler
    ) throws IOException {
        final RequestContext requestContext = new RequestContext(url, RequestMethod.POST);

        try {
            final HttpPost post = new HttpPost(url);

            // Pass headers through interceptor interface
            buildHeaders(post, requestContext);

            // Build request entity
            post.setEntity(
                buildEntity(requestBodyContent, requestContext)
            );
            logger.debug("Executing request {} with {}", post.getRequestLine(), requestBodyContent);

            // Execute and return
            return httpClient.execute(post, responseHandler, httpClientContext);
        } catch (final ClientProtocolException | SocketException | SSLHandshakeException connectionException) {
            // Typically this is a connection issue.
            throw new ConnectionException(connectionException.getMessage(), connectionException);
        } catch (final IOException ioException) {
            // Typically this is a parse error.
            throw new ResultParsingException(ioException.getMessage(), ioException);
        }
    }

    /**
     * Internal PUT method.
     * @param url Url to POST to.
     * @param requestBodyContent POST entity include in the request body
     * @param responseHandler The response Handler to use to parse the response
     * @param <T> The type that ResponseHandler returns.
     * @return Parsed response.
     */
    private <T> T submitPutRequest(
        final String url,
        final RequestBodyContent requestBodyContent,
        final ResponseHandler<T> responseHandler
    ) throws IOException {
        final RequestContext requestContext = new RequestContext(url, RequestMethod.PUT);

        try {
            final HttpPut put = new HttpPut(url);

            // Pass headers through interceptor interface
            buildHeaders(put, requestContext);

            // Build request entity
            put.setEntity(
                buildEntity(requestBodyContent, requestContext)
            );
            logger.debug("Executing request {} with {}", put.getRequestLine(), requestBodyContent);

            // Execute and return
            return httpClient.execute(put, responseHandler, httpClientContext);
        } catch (final ClientProtocolException | SocketException | SSLHandshakeException connectionException) {
            // Typically this is a connection issue.
            throw new ConnectionException(connectionException.getMessage(), connectionException);
        } catch (final IOException ioException) {
            // Typically this is a parse error.
            throw new ResultParsingException(ioException.getMessage(), ioException);
        }
    }

    /**
     * Internal DELETE method.
     * @param url Url to DELETE to.
     * @param requestBody POST entity include in the request body
     * @param responseHandler The response Handler to use to parse the response
     * @param <T> The type that ResponseHandler returns.
     * @return Parsed response.
     */
    private <T> T submitDeleteRequest(final String url, final Object requestBody, final ResponseHandler<T> responseHandler) throws IOException {
        final RequestContext requestContext = new RequestContext(url, RequestMethod.DELETE);

        try {
            final HttpDelete delete = new HttpDelete(url);

            // Pass headers through interceptor interface
            buildHeaders(delete, requestContext);

            // Delete requests have no request body.

            // Execute and return
            return httpClient.execute(delete, responseHandler, httpClientContext);
        } catch (final ClientProtocolException | SocketException | SSLHandshakeException connectionException) {
            // Typically this is a connection issue.
            throw new ConnectionException(connectionException.getMessage(), connectionException);
        } catch (final IOException ioException) {
            // Typically this is a parse error.
            throw new ResultParsingException(ioException.getMessage(), ioException);
        }
    }

    /**
     * Internal helper method for generating URLs w/ the appropriate API host and API version.
     * @param endPoint The end point you want to hit.
     * @return Constructed URL for the end point.
     */
    private String constructApiUrl(final String endPoint) {
        return configuration.getApiHost() + endPoint;
    }

    private HttpEntity buildEntity(final RequestBodyContent requestBodyContent, final RequestContext requestContext) throws UnsupportedEncodingException {
        if (requestBodyContent instanceof UrlEncodedFormBodyContent) {
            List<RequestParameter> requestParameters = processRequestParameters(
                ((UrlEncodedFormBodyContent) requestBodyContent).getRequestParameters(), requestContext
            );

            // Build Form parameters
            final List<NameValuePair> params = new ArrayList<>();

            // Attach submitRequest params
            requestParameters
                .forEach(parameter ->  params.add(new BasicNameValuePair(parameter.getName(), parameter.getValue()))
            );
            return new UrlEncodedFormEntity(params);
        } else {
            return new StringEntity(requestBodyContent.toString());
        }
    }

    /**
     * Process headers through requestInteceptor instances.
     * @param requestBase The underlying request object.
     * @param requestContext Contextual details about the request.
     */
    private void buildHeaders(final HttpRequestBase requestBase, final RequestContext requestContext) {
        // Pass headers through interceptor interface
        List<RequestHeader> headers = new ArrayList<>();
        for (final RequestInterceptor requestInterceptor : requestInterceptors) {
            headers = requestInterceptor.modifyHeaders(headers, requestContext);
        }

        // Add headers to the request instance.
        headers
            .stream()
            .map((entry) -> new BasicHeader(entry.getName(), entry.getValue()))
            .forEach(requestBase::addHeader);
    }

    /**
     * Process request parameters through request interceptors.
     *
     * @param incomingParameters The defined request parameters.
     * @param requestContext Contextual details about the request.
     * @return Modified request parameters.
     */
    private List<RequestParameter> processRequestParameters(final List<RequestParameter> incomingParameters, final RequestContext requestContext) {
        // Copy parameters
        List<RequestParameter> requestParameters = new ArrayList<>(incomingParameters);

        // Loop over each interceptor
        for (final RequestInterceptor requestInterceptor : requestInterceptors) {
            // Pass in the parameters and get the returned list.
            requestParameters = requestInterceptor.modifyRequestParameters(requestParameters, requestContext);
        }
        return requestParameters;
    }
}
