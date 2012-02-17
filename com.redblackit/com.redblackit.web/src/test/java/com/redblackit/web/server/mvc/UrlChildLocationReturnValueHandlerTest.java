package com.redblackit.web.server.mvc;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.junit.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: djnorth Date: 03/12/2011 Time: 12:41 To change this template use File | Settings |
 * File Templates.
 */
@RunWith(Parameterized.class)
public class UrlChildLocationReturnValueHandlerTest {

    /**
     * Interface for test controllers
     */
    interface TestController<RETURNTYPE> {
        RETURNTYPE testMethod();
    };


    /**
     * Controller with method returning UrlChildLocation
     */
    static private class UrlChildLocationReturnTypeController implements TestController<UrlChildLocation> {

        public UrlChildLocation testMethod() {
            return null;
        }

    };


    /**
     * Controller with method returning UrlChildLocation, annotated with ModelAttribute
     */
    static private class ModelAttributeAnnotatedUrlChildLocationReturnTypeController implements TestController<UrlChildLocation> {

        public @ModelAttribute("urlChildLocation") UrlChildLocation testMethod() {
            return null;
        }

    };


    /**
     * Controller with method returning UrlChildLocation, annotated with ResponseBody
     */
    static private class ResponseBodyAnnotatedUrlChildLocationReturnTypeController implements TestController<UrlChildLocation> {

        public @ResponseBody UrlChildLocation testMethod() {
            return null;
        }

    };


    /**
     * Controller with method returning String
     */
    static private class StringReturnTypeController implements TestController<String> {

        public String testMethod() {
            return null;
        }

    };

    /**
     * Test data
     */
    @Parameterized.Parameters
    public static List<Object[]> testData() {

        Object[][] parameters = {

                {new TestParameters(new UrlChildLocationReturnTypeController(), true, new UrlChildLocation("http:/url1/", "child1"))},
                {new TestParameters(new UrlChildLocationReturnTypeController(), true, new UrlChildLocation("http:/url2", null))},
                {new TestParameters(new UrlChildLocationReturnTypeController(), true, null)},
                {new TestParameters(new ModelAttributeAnnotatedUrlChildLocationReturnTypeController(), false, new UrlChildLocation("http:/url1/", "child1"))},
                {new TestParameters(new ResponseBodyAnnotatedUrlChildLocationReturnTypeController(), false, new UrlChildLocation("http:/url2", null))},
                {new TestParameters(new StringReturnTypeController(), false, "some-string")}

        };

        return Arrays.asList(parameters);

    }

    /**
     * test Parameters
     */
    private TestParameters testParameters;

    /**
     * Object under test
     */
     private UrlChildLocationReturnValueHandler returnValueHandler;

    /**
     * MethodParameter for test
     */
    private MethodParameter returnValueMethodParameter;

    /**
     * Mock ModelAndViewContainer
     */
    private ModelAndViewContainer mavContainer;

    /**
     * WebRequest for tests
     */
    protected NativeWebRequest nativeWebRequest;

    /**
     * Wrapped HttpServletRequest
     */
    protected HttpServletResponse httpServletResponse;

    /**
     * Constructor taking test parameters
     *
     * @param testParameters
     */
    public UrlChildLocationReturnValueHandlerTest(TestParameters testParameters) throws Exception {
        this.testParameters = testParameters;
        returnValueHandler = new UrlChildLocationReturnValueHandler();

        Method testMethod = testParameters.getController().getClass().getMethod("testMethod");
        returnValueMethodParameter = new MethodParameter(testMethod, -1);
    }

    /**
     * Test returnType isSupported as expected
     */
    @Test
    public void testSupportsReturnType() {
        Assert.assertEquals(assertMsg("supportsReturnType"),
                            testParameters.isReturnTypeSupported(),
                            returnValueHandler.supportsReturnType(returnValueMethodParameter));
    }

    /**
     * Test handling return value
     */
    @Test
    public void testHandleReturnValue() throws Exception {

        mavContainer = new ModelAndViewContainer();
        nativeWebRequest = EasyMock.createMock(NativeWebRequest.class);
        httpServletResponse = EasyMock.createMock(HttpServletResponse.class);

        if (testParameters.isReturnTypeSupported() && testParameters.getReturnObject() != null) {
            EasyMock.expect(nativeWebRequest.getNativeResponse(HttpServletResponse.class)).andReturn(httpServletResponse);
            final String expectedLocation = testParameters.getReturnUrlChildLocation().getLocationUrl();
            httpServletResponse.setHeader("LOCATION", expectedLocation);
        }

        EasyMock.replay(nativeWebRequest, httpServletResponse);

        returnValueHandler.handleReturnValue(testParameters.getReturnObject(), returnValueMethodParameter, mavContainer, nativeWebRequest);

        Assert.assertEquals(assertMsg("mavContainer.isRequestHandled()"),
                            mavContainer.isRequestHandled(),
                            testParameters.isReturnTypeSupported() && testParameters.getReturnObject() != null);

        EasyMock.verify(nativeWebRequest, httpServletResponse);
    }

    /**
     * Helper to add context to assertion message
     *
     * @param msg to enhance
     * @return result
     */
    private String assertMsg(String msg) {
        StringBuilder sb = new StringBuilder(msg);

        sb.append(':').append(this);
        return sb.toString();
    }

    /**
     * Test parameter class
     */
    private static class TestParameters {

        /**
         * Controller
         */
        private final TestController<?> controller;

        /**
         * Expecting return type to be supported
         */
        private final boolean returnTypeSupported;

        /**
         * URL child location to use (null for unsupported type)
         */
        private final Object returnObject;

        /**
         * Constructor taking parameters to be used
         *
         * @param controller
         * @param returnTypeSupported
         * @param returnObject
         */
        private TestParameters(TestController<?> controller, boolean returnTypeSupported, Object returnObject) {
            this.controller = controller;
            this.returnTypeSupported = returnTypeSupported;
            this.returnObject = returnObject;
        }

        /**
         * @return controller we're using
         */
        public TestController<?> getController() {
            return controller;
        }

        /**
         * @return expected return type support
         */
        public boolean isReturnTypeSupported() {
            return returnTypeSupported;
        }

        /**
         * @return return object
         */
        public Object getReturnObject() {
            return returnObject;
        }

        /**
         * Get return object as UrlChildLocation (if possible), otherwise as null
         *
         * @return returnObject cast to UrlChildLocation
         */
        public UrlChildLocation getReturnUrlChildLocation() {
            return (returnObject != null && returnObject instanceof UrlChildLocation ?
                    (UrlChildLocation) returnObject : null);
        }

        /**
         * toString
         *
         * @return stringified field values
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("RequestResponseParameters");
            sb.append("{controller=").append(controller);
            sb.append(", returnTypeSupported=").append(returnTypeSupported);
            sb.append(", returnObject=").append(returnObject);
            if (returnObject != null) {
                sb.append(" (").append(returnObject.getClass()).append(')');
            }
            sb.append('}');
            return sb.toString();
        }
    }
}
