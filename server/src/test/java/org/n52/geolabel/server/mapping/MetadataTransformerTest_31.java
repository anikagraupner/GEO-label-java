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

package org.n52.geolabel.server.mapping;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumSet;

import org.junit.Test;
import org.n52.geolabel.commons.Label;
import org.n52.geolabel.commons.LabelFacet;
import org.n52.geolabel.commons.LabelFacet.Availability;
import org.n52.geolabel.commons.test.Facet;
import org.n52.geolabel.server.config.GeoLabelObjectMapper;
import org.n52.geolabel.server.config.TransformationDescriptionLoader;
import org.n52.geolabel.server.config.TransformationDescriptionResources;

public class MetadataTransformerTest_31 {

    public static MetadataTransformer newMetadataTransformer() {
        TransformationDescriptionResources res = new TransformationDescriptionResources("http://geoviqua.github.io/geolabel/mappings/transformer.json=/transformations/transformer.json");
        return new MetadataTransformer(new TransformationDescriptionLoader(res, new GeoLabelObjectMapper(res), false));
    }

    // @Test
    public void testParseMetadata() throws IOException {

        MetadataTransformer metadataTransformer = newMetadataTransformer();

        InputStream metadataStream = getClass().getResourceAsStream("/3.1/GLC2000.xml");
        // StringWriter writer = new StringWriter();
        // IOUtils.copy(metadataStream, writer, "utf-8");
        // String theString = writer.toString();
        // System.out.println(theString);

        Label geoLabel = metadataTransformer.updateGeoLabel(metadataStream, new Label());

        assertTrue(geoLabel.getProducerProfileFacet().getAvailability() == Availability.AVAILABLE);
        assertTrue(geoLabel.getProducerProfileFacet().getOrganizationNames().contains("JRC"));

        assertTrue(geoLabel.getLineageFacet().getAvailability() == Availability.AVAILABLE);
        assertTrue(geoLabel.getLineageFacet().getTotalProcessSteps() == 3);

        assertTrue(geoLabel.getProducerCommentsFacet().getAvailability() == Availability.AVAILABLE);
        assertTrue(geoLabel.getProducerCommentsFacet().getKnownProblems() != null);

        assertTrue(geoLabel.getStandardsComplianceFacet().getAvailability() == Availability.AVAILABLE);
        assertTrue(geoLabel.getStandardsComplianceFacet().getStandards().size() == 1);

        assertTrue(geoLabel.getQualityInformationFacet().getAvailability() == Availability.AVAILABLE);
        assertTrue(geoLabel.getQualityInformationFacet().getScopeLevels().size() == 1);

        assertTrue(geoLabel.getUserFeedbackFacet().getAvailability() == Availability.NOT_AVAILABLE);
        assertTrue(geoLabel.getUserFeedbackFacet().getAverageRating() == 0);
        assertTrue(geoLabel.getUserFeedbackFacet().getTotalItems() == 0);
        assertTrue(geoLabel.getUserFeedbackFacet().getRatingCount() == 0);

        assertTrue(geoLabel.getExpertFeedbackFacet().getAvailability() == Availability.AVAILABLE);
        assertTrue(geoLabel.getExpertFeedbackFacet().getAverageRating() == 2.5);
        assertTrue(geoLabel.getExpertFeedbackFacet().getTotalItems() == 2);
        assertTrue(geoLabel.getExpertFeedbackFacet().getRatingCount() == 2);

        assertTrue(geoLabel.getCitationsFacet().getAvailability() == Availability.AVAILABLE);
        assertTrue(geoLabel.getCitationsFacet().getTotalCitations() == 7);
    }

    private class LabelControlHolder {

        protected EnumSet<Facet> availableFacets;

        protected String[] standards;
        protected String[] scopeLevels;
        protected String[] organizationsNames;
        protected String[] producerCommentsStart;

        protected Integer processStepCount;

        protected Integer citationCount;

        protected Integer expertReviewCount;
        protected Double expertRating;
        protected Integer expertRatingCount;

        protected Integer userReviewCount;
        protected Double userRating;
        protected Integer userRatingCount;

        public LabelControlHolder() {
            //
        }
    }

    // @Test
    public void testFAO_GEONETWORK() throws IOException {
        testMetadataExample("FAO_GEO_Network_iso19139.xml", new LabelControlHolder() {
            {
                this.availableFacets = EnumSet.of(Facet.PRODUCER_PROFILE,
                                                  Facet.PRODUCER_COMMENTS,
                                                  Facet.LINEAGE,
                                                  Facet.QUALITY_INFORMATION,
                                                  Facet.STANDARDS_COMPLIANCE);
                this.organizationsNames = new String[] {"FAO - NRL"};
                this.producerCommentsStart = new String[] {"The EPSMO Project was initiated"};
                this.processStepCount = Integer.valueOf(0);
                this.standards = new String[] {"ISO 19115:2003/19139, 1.0"};
                this.scopeLevels = new String[] {"dataset"};
            }
        });
    }

    // @Test
    public void testFGDC_Producer() throws IOException {
        testMetadataExample("FGDC_Producer.xml", new LabelControlHolder() {
            {
                this.availableFacets = EnumSet.of(Facet.PRODUCER_PROFILE, Facet.STANDARDS_COMPLIANCE);
                this.organizationsNames = new String[] {"Esri"};
                this.standards = new String[] {"FGDC-STD-001-1998"};
            }
        });
    }

    // @Test
    public void testGVQ_Aggregated_All() throws IOException {
        testMetadataExample("GVQ_Aggregated_All_Available.xml", new LabelControlHolder() {
            {
                this.availableFacets = EnumSet.complementOf(EnumSet.of(Facet.USER_FEEDBACK, Facet.EXPERT_REVIEW));
                this.organizationsNames = new String[] {"JRC"};
                this.producerCommentsStart = new String[] {"The GVM unit"};
                this.processStepCount = Integer.valueOf(3);
                this.standards = new String[] {"ISO 19115:2003/19139, 1.0"};
                this.scopeLevels = new String[] {"dataset"};
                this.citationCount = Integer.valueOf(7);
                this.expertReviewCount = Integer.valueOf(2);
                this.expertRating = Double.valueOf(2.5);
                this.expertRatingCount = Integer.valueOf(2);
            }
        });
    }

    // @Test
    public void testGVQ_Feedback_All() throws MalformedURLException, IOException {
        testMetadataExample("GVQ_Feedback_All_Available.xml", new LabelControlHolder() {
            {
                this.availableFacets = EnumSet.of(Facet.CITATIONS_INFORMATION, Facet.EXPERT_REVIEW, Facet.USER_FEEDBACK);
                this.citationCount = Integer.valueOf(4);

                this.expertReviewCount = Integer.valueOf(1);
                this.expertRating = Double.valueOf(3d);
                this.expertRatingCount = Integer.valueOf(1);

                this.userReviewCount = Integer.valueOf(3);
                this.userRating = Double.valueOf(3d);
                this.userRatingCount = Integer.valueOf(2);
            }
        });
    }

    // @Test
    public void testGVQ_Feedback_No_Expert() throws MalformedURLException, IOException {
        testMetadataExample("GVQ_Feedback_No_Expert_Review.xml", new LabelControlHolder() {
            {
                this.availableFacets = EnumSet.of(Facet.CITATIONS_INFORMATION);
                this.citationCount = Integer.valueOf(0);

                this.userReviewCount = Integer.valueOf(0);
                this.userRating = Double.valueOf(0d);
                this.userRatingCount = Integer.valueOf(0);
            }
        });
    }

    // @Test
    public void testGVQ_Producer_All() throws IOException {
        testMetadataExample("GVQ_Producer_All_Available.xml", new LabelControlHolder() {
            {
                this.availableFacets = EnumSet.complementOf(EnumSet.of(Facet.USER_FEEDBACK, Facet.EXPERT_REVIEW));
                this.organizationsNames = new String[] {"JRC"};
                this.producerCommentsStart = new String[] {"The GVM unit"};
                this.processStepCount = Integer.valueOf(3);
                this.standards = new String[] {"ISO 19115:2003/19139, 1.0"};
                this.scopeLevels = new String[] {"dataset"};
                this.citationCount = Integer.valueOf(5);
            }
        });
    }

    // @Test
    public void testIndia19139() throws IOException {
        testMetadataExample("india19139.xml", new LabelControlHolder() {
            {
                this.availableFacets = EnumSet.of(Facet.PRODUCER_PROFILE,
                                                  Facet.PRODUCER_COMMENTS,
                                                  Facet.STANDARDS_COMPLIANCE);
                this.organizationsNames = new String[] {"FAO - UN AGL Documentation Center"};
                this.producerCommentsStart = new String[] {"ISIS Identifier"};
                this.standards = new String[] {"ISO 19115:2003/19139, 1.0"};
            }
        });
    }

    // @Test
    public void testIndiaGVQ() throws IOException {
        testMetadataExample("indiaGVQ.xml", new LabelControlHolder() {
            {
                this.availableFacets = EnumSet.of(Facet.PRODUCER_PROFILE,
                                                  Facet.PRODUCER_COMMENTS,
                                                  Facet.STANDARDS_COMPLIANCE);
                this.organizationsNames = new String[] {"FAO - UN AGL Documentation Center"};
                this.producerCommentsStart = new String[] {"ISIS Identifier"};
                this.standards = new String[] {"ISO 19115:2003/19139, 1.0"};
            }
        });
    }

    private void testMetadataExample(String exampleFile, LabelControlHolder control) throws IOException {
        MetadataTransformer metadataTransformer = newMetadataTransformer();
        InputStream metadataStream = getClass().getClassLoader().getResourceAsStream("3.1/" + exampleFile);
        Label l = new Label();
        l.setMetadataUrl(new URL("http://not.available.net"));
        l.setFeedbackUrl(new URL("http://not.available.net"));
        Label label = metadataTransformer.updateGeoLabel(metadataStream, l);

        // Check facet availability
        if (control.availableFacets != null)
            for (Facet facet : EnumSet.allOf(Facet.class)) {
                boolean contained = control.availableFacets.contains(facet);
                Availability expected = contained ? Availability.AVAILABLE : Availability.NOT_AVAILABLE;
                Availability actual = facet.getFacet(label).getAvailability();
                assertEquals("Facet " + facet.name() + " availability", expected, actual);
            }
        if (control.organizationsNames != null)
            label.getProducerProfileFacet().getOrganizationNames().containsAll(Arrays.asList(control.organizationsNames));

        if (control.processStepCount != null)
            assertEquals("Lineage processing steps",
                         control.processStepCount.intValue(),
                         label.getLineageFacet().getTotalProcessSteps());

        if (control.standards != null)
            assertTrue("Standards",
                       label.getStandardsComplianceFacet().getStandards().containsAll(Arrays.asList(control.standards)));

        if (control.scopeLevels != null)
            assertTrue("Scope Levels",
                       label.getQualityInformationFacet().getScopeLevels().containsAll(Arrays.asList(control.scopeLevels)));

        if (control.expertRating != null)
            assertEquals("Expert rating",
                         control.expertRating.doubleValue(),
                         label.getExpertFeedbackFacet().getAverageRating(),
                         0.05);
        if (control.expertRatingCount != null)
            assertEquals("Expert rating count",
                         control.expertRatingCount.intValue(),
                         label.getExpertFeedbackFacet().getRatingCount());
        if (control.expertReviewCount != null)
            assertEquals("Expert review count",
                         control.expertReviewCount.intValue(),
                         label.getExpertFeedbackFacet().getTotalItems());

        if (control.userRating != null)
            assertEquals("User rating",
                         control.userRating.doubleValue(),
                         label.getUserFeedbackFacet().getAverageRating(),
                         0.05);
        if (control.userRatingCount != null)
            assertEquals("User rating count",
                         control.userRatingCount.intValue(),
                         label.getUserFeedbackFacet().getRatingCount());
        if (control.userReviewCount != null)
            assertEquals("User review count",
                         control.userReviewCount.intValue(),
                         label.getUserFeedbackFacet().getTotalItems());

        if (control.citationCount != null)
            assertEquals("citations", control.citationCount.intValue(), label.getCitationsFacet().getTotalCitations());

        // check drilldown urls
        checkDrilldownUrl(label.getCitationsFacet(), false);
        checkDrilldownUrl(label.getExpertFeedbackFacet(), false);
        checkDrilldownUrl(label.getLineageFacet(), false);
        checkDrilldownUrl(label.getProducerCommentsFacet(), false);
        checkDrilldownUrl(label.getProducerProfileFacet(), false);
        checkDrilldownUrl(label.getQualityInformationFacet(), false);
        checkDrilldownUrl(label.getStandardsComplianceFacet(), false);
        checkDrilldownUrl(label.getUserFeedbackFacet(), false);
    }

    private void checkDrilldownUrl(LabelFacet facet, boolean asReference) {
        assertThat("drilldown does not contain string placeholders in " + facet.getClass().getSimpleName(),
                   facet.getHref(),
                   not(containsString("%s")));
        if (asReference)
            // sending documents directly, cannot check for geolabel.net
            assertThat("drilldown URL is not set in " + facet.getClass().getSimpleName(),
                       facet.getHref(),
                       is(nullValue()));
        else
            assertThat("drilldown contains URLs to geolabel.net in " + facet.getClass().getSimpleName(),
                       facet.getHref(),
                       containsString("http://not.available.net"));
    }

    @SuppressWarnings("unused")
    @Test
    public void testLabelUrlKey() throws IOException {
        TransformationDescriptionResources res = new TransformationDescriptionResources("http://geoviqua.github.io/geolabel/mappings/transformer.json=/transformations/transformer.json");
        new MetadataTransformer(new TransformationDescriptionLoader(res, new GeoLabelObjectMapper(res), false)) {
            {
                URL testURL1 = new URL("http://test1.resource1");
                URL testURL2 = new URL("http://test2.resource2");

                assertEquals(new LabelUrlKey(testURL1, testURL2), new LabelUrlKey(testURL1, testURL2));
                assertEquals(new LabelUrlKey(testURL2, testURL1), new LabelUrlKey(testURL1, testURL2));

                assertEquals(new LabelUrlKey(null, testURL1), new LabelUrlKey(testURL1, null));
                assertEquals(new LabelUrlKey(testURL1, null), new LabelUrlKey(null, testURL1));

                assertNotEquals(new LabelUrlKey(testURL1, testURL2), new LabelUrlKey(testURL1, null));
                assertNotEquals(new LabelUrlKey(testURL1, testURL2), new LabelUrlKey(null, testURL2));

                assertTrue(new LabelUrlKey(testURL1, testURL2).hashCode() == new LabelUrlKey(testURL2, testURL1).hashCode());
            }
        };
    }

    // TODO make this an integration test:
    // @Test
    // public void testResourceNotFoundErrorMessage() throws IOException {
    // MetadataTransformer metadataTransformer = newMetadataTransformer();
    // Label label = metadataTransformer.createGeoLabel(new URL("http://does.not/exist.xml"));
    //
    // ErrorFacet ef = label.getErrorFacet();
    // assertEquals("error facet availability", Availability.AVAILABLE, ef.getAvailability());
    // assertNotNull("error message null", ef.getErrorMessage());
    //
    // StringWriter sw = new StringWriter();
    // label.toSVG(sw, "testid", 100);
    // String svgString = sw.toString();
    //
    // assertTrue("error string is given", svgString.contains("does.not"));
    // assertTrue("error string is given", svgString.contains("Error:"));
    // assertTrue("error string is given", svgString.contains("Message:"));
    // }

}
