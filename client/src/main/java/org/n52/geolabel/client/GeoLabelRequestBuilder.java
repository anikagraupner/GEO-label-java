/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.geolabel.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.n52.geolabel.client.GeoLabelClient.GeoLabelRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class GeoLabelRequestBuilder {

    private static Logger log = LoggerFactory.getLogger(GeoLabelRequestBuilder.class);

    protected DocumentReference.Base metadataDocument;
    protected DocumentReference.Base feedbackDocument;
    protected final GeoLabelRequestHandler requestHandler;
    protected Integer desiredSize = null;
    protected boolean forceDownload;
    protected boolean useCache;
    protected String serviceUrl;

    GeoLabelRequestBuilder(GeoLabelRequestHandler requestHandler, String serviceUrl) {
        this.requestHandler = requestHandler;
        this.serviceUrl = serviceUrl;

        log.debug("NEW {}", this);
    }

    // / METADATA / PRODUCER DOCUMENT

    public GeoLabelRequestBuilder setMetadataDocument(DocumentReference.Base reference) {
        if (this.metadataDocument != null)
            throw new IllegalStateException("Metadata document already set");
        this.metadataDocument = reference;
        return this;
    }

    public GeoLabelRequestBuilder setMetadataDocument(Document document) {
        return setMetadataDocument(new DocumentReference.XMLDocument(document));
    }

    public GeoLabelRequestBuilder setMetadataDocument(InputStream stream) {
        return setMetadataDocument(new DocumentReference.InputStreamDocument(stream));
    }

    public GeoLabelRequestBuilder setMetadataDocument(URL url) {
        return setMetadataDocument(new DocumentReference.URLDocument(url));
    }

    public GeoLabelRequestBuilder setMetadataDocument(String url) {
        try {
            return setMetadataDocument(new DocumentReference.URLDocument(url));
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url", e);
        }
    }

    // / FEEDBACK / USER DOCUMENT

    public GeoLabelRequestBuilder setFeedbackDocument(DocumentReference.Base reference) {
        if (this.feedbackDocument != null)
            throw new IllegalStateException("Feedback document already set");
        this.feedbackDocument = reference;
        return this;
    }

    public GeoLabelRequestBuilder setFeedbackDocument(Document document) {
        return setFeedbackDocument(new DocumentReference.XMLDocument(document));
    }

    public GeoLabelRequestBuilder setFeedbackDocument(InputStream stream) {
        return setFeedbackDocument(new DocumentReference.InputStreamDocument(stream));
    }

    public GeoLabelRequestBuilder setFeedbackDocument(URL url) {
        return setFeedbackDocument(new DocumentReference.URLDocument(url));
    }

    public GeoLabelRequestBuilder setFeedbackDocument(String url) {
        try {
            return setFeedbackDocument(new DocumentReference.URLDocument(url));
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url", e);
        }
    }

    /**
     * Desired GEO label size (accepted values are between 1 and 3000 pixels)
     *
     * @param desiredSize
     * @return
     */
    public GeoLabelRequestBuilder setDesiredSize(int desiredSize) {
        this.desiredSize = Integer.valueOf(desiredSize);
        return this;
    }

    public Integer getDesiredSize() {
        return this.desiredSize;
    }

    public InputStream getSVG() throws IOException {
        return this.requestHandler.getLabel(this);
    }

    public GeoLabelRequestBuilder setForceDownload(boolean enabled) {
        this.forceDownload = enabled;
        return this;
    }

    public GeoLabelRequestBuilder setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        return this;
    }

    public GeoLabelRequestBuilder setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GeoLabelRequestBuilder [");
        if (this.metadataDocument != null) {
            builder.append("metadataDocument=");
            builder.append(this.metadataDocument);
            builder.append(", ");
        }
        if (this.feedbackDocument != null) {
            builder.append("feedbackDocument=");
            builder.append(this.feedbackDocument);
            builder.append(", ");
        }
        if (this.requestHandler != null) {
            builder.append("requestHandler=");
            builder.append(this.requestHandler);
            builder.append(", ");
        }
        if (this.desiredSize != null) {
            builder.append("desiredSize=");
            builder.append(this.desiredSize);
            builder.append(", ");
        }
        builder.append("forceDownload=");
        builder.append(this.forceDownload);
        builder.append(", useCache=");
        builder.append(this.useCache);
        builder.append(", ");
        if (this.serviceUrl != null) {
            builder.append("serviceUrl=");
            builder.append(this.serviceUrl);
        }
        builder.append("]");
        return builder.toString();
    }

}