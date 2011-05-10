package org.jboss.seam.rest.test;

import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.arquillian.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.seam.rest.SeamRestConfiguration;
import org.jboss.seam.rest.SeamRestExtension;
import org.jboss.seam.rest.client.RestClient;
import org.jboss.seam.rest.exceptions.ErrorMessageWrapper;
import org.jboss.seam.rest.exceptions.ExceptionMapping;
import org.jboss.seam.rest.exceptions.ExceptionMappingExtension;
import org.jboss.seam.rest.exceptions.Mapping;
import org.jboss.seam.rest.exceptions.ResponseBuilderProducer;
import org.jboss.seam.rest.exceptions.RestRequest;
import org.jboss.seam.rest.exceptions.RestResource;
import org.jboss.seam.rest.exceptions.UnhandledException;
import org.jboss.seam.rest.exceptions.integration.CatchValidationExceptionHandler;
import org.jboss.seam.rest.util.Annotations;
import org.jboss.seam.rest.util.Interpolator;
import org.jboss.seam.rest.util.Utils;
import org.jboss.seam.rest.validation.ValidateRequest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@RunAsClient
public abstract class SeamRestClientTest {
    public static final File LIBRARY_SEAM_SOLDER_API = new File("target/lib/seam-solder-api.jar");
    public static final File LIBRARY_SEAM_SOLDER_IMPL = new File("target/lib/seam-solder-impl.jar");
    public static final File LIBRARY_SEAM_SOLDER = new File("target/lib/seam-solder.jar");
    public static final File LIBRARY_JBOSS_LOGGING = new File("target/lib/jboss-logging.jar");
    public static final File LIBRARY_SEAM_SERVLET_API = new File("target/lib/seam-servlet-api.jar");
    public static final File LIBRARY_SEAM_SERVLET_IMPL = new File("target/lib/seam-servlet-impl.jar");
    public static final File LIBRARY_SEAM_CATCH = new File("target/lib/seam-catch.jar");

    protected HttpClient client = new HttpClient();

    protected void test(String url, int expectedStatus, String expectedBody, String accept) throws Exception {
        GetMethod get = new GetMethod(url);
        get.setRequestHeader("Accept", accept);
        assertEquals(expectedStatus, client.executeMethod(get));
        if (expectedBody != null) {
            assertEquals(expectedBody, get.getResponseBodyAsString());
        }
    }

    protected void test(String url, int expectedStatus, String expectedBody) throws Exception {
        test(url, expectedStatus, expectedBody, "text/plain");
    }

    public static JavaArchive getLoggingJar() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "logging.jar");
        jar.addPackage(Logger.class.getPackage());
        return jar;
    }

    /**
     * Simulates combined seam-rest.jar. No extensions are enabled. Templating support is not added. SeamExceptionMapper and
     * CatchExceptionMapper is not added.
     */
    public static JavaArchive createSeamRest() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "seam-rest.jar");
        jar.addClasses(SeamRestConfiguration.class, SeamRestExtension.class);
        jar.addPackage(RestClient.class.getPackage()); // .client
        jar.addClasses(ExceptionMapping.class, Mapping.class, RestRequest.class, RestResource.class, UnhandledException.class); // .exceptions
        // api
        jar.addClasses(ErrorMessageWrapper.class, ExceptionMappingExtension.class, ResponseBuilderProducer.class); // .exceptions
        // impl
        jar.addClass(CatchValidationExceptionHandler.class); // .exceptions.integration
        jar.addClasses(Annotations.class, Interpolator.class, Utils.class); // .utils
        jar.addPackage(ValidateRequest.class.getPackage());
        jar.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        // mock SeamRestStartup
        jar.addClass(MockStartup.class);
        return jar;
    }
}
