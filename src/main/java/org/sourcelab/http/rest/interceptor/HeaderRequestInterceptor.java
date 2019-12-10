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

package org.sourcelab.http.rest.interceptor;

import org.sourcelab.http.rest.request.RequestHeader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation that allows for adding a static set of headers to all requests.
 */
public class HeaderRequestInterceptor implements RequestInterceptor {

    /**
     * Holds our default Headers.
     */
    private final List<RequestHeader> headers;

    /**
     * Constructor.
     * @param headers map of header names to values.
     */
    public HeaderRequestInterceptor(final Map<String, String> headers) {
        final List<RequestHeader> listOfHeaders = new ArrayList<>();
        headers
            .forEach((name, value) -> listOfHeaders.add(new RequestHeader(name, value)));

        this.headers = Collections.unmodifiableList(listOfHeaders);
    }

    /**
     * Constructor.
     * @param headers list of request headers.
     */
    public HeaderRequestInterceptor(final List<RequestHeader> headers) {
        final List<RequestHeader> listOfHeaders = new ArrayList<>(headers);
        this.headers = Collections.unmodifiableList(listOfHeaders);
    }

    /**
     * Passed a mutable Map of request headers prior to sending the request.
     * Adding, removing, or modifying any members in this list will alter the values
     * sent in the request headers.
     *
     * @param requestHeaders list representing request header values.
     * @return The modified list.
     */
    @Override
    public List<RequestHeader> modifyHeaders(final List<RequestHeader> requestHeaders, final RequestContext requestContext) {
        requestHeaders.addAll(headers);
        return requestHeaders;
    }
}
