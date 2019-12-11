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

package org.sourcelab.http.rest.request;

import org.sourcelab.http.rest.request.body.RequestBodyContent;
import org.sourcelab.http.rest.request.body.UrlEncodedFormBodyContent;

/**
 * Defines interface for GET requests.
 * @param <T> Defines the return type of the request.
 */
public interface GetRequest<T> extends Request<T> {

    /**
     * All GET requests use GET.
     * @return RequestMethod.GET
     */
    @Override
    default RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }

    /**
     * Object to be submitted as the body of the request.
     * The request body will be populated by calling .toString() on the returned object.
     * Return null if no request body should be sent.
     *
     * @return Object representing request body content, or null if none required.
     */
    @Override
    default RequestBodyContent getRequestBody() {
        return new UrlEncodedFormBodyContent();
    }
}
