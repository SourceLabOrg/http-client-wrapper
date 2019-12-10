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

package org.sourcelab.http.rest.request.body;

import org.sourcelab.http.rest.request.RequestParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents URL Encoded Form Parameters request body.
 */
public class UrlEncodedFormBodyContent implements RequestBodyContent {
    private final List<RequestParameter> requestParameters = new ArrayList<>();

    /**
     * Default constructor.
     */
    public UrlEncodedFormBodyContent() {
    }

    /**
     * Constructor.
     * @param requestParameters Request parameters to add.
     */
    public UrlEncodedFormBodyContent(final Collection<RequestParameter> requestParameters) {
        requestParameters.addAll(requestParameters);
    }

    /**
     * Add request parameter to body.
     *
     * @param parameter the parameter to add.
     * @return self.
     */
    public UrlEncodedFormBodyContent addParameter(final RequestParameter parameter) {
        requestParameters.add(parameter);
        return this;
    }

    /**
     * Add request parameter to body.
     *
     * @param name name of the form field.
     * @param value value of the form field.
     * @return self.
     */
    public UrlEncodedFormBodyContent addParameter(final String name, final String value) {
        return addParameter(
            new RequestParameter(name, value)
        );
    }

    /**
     * The configured parameters.
     * @return The fields to submit.
     */
    public List<RequestParameter> getRequestParameters() {
        return Collections.unmodifiableList(requestParameters);
    }
}
