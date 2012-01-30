/*
 * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redblackit.web.server.mvc;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.core.MethodParameter;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

import com.redblackit.web.server.mvc.annotation.RequestUrl;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Test class for RequestUrlArgumentResolver, verifying correct parameter
 * objects are returned for supported parameter types
 * 
 * @author Dominic North
 * 
 */
@RunWith(Parameterized.class)
public class RequestUrlHandlerMethodArgumentResolverTest {

    /**
	 * Interface for test classes
	 */
	interface TestController<PARMTYPE> {
		void methodParameter0of1(PARMTYPE requestUrl);

		void methodParameter1of3(int someInt, PARMTYPE requestUrl, Model model);
	};

	/**
	 * Class used to test with un-annotated UrlChildLocation parameter
	 *
	 * @author djnorth
	 */
	static private class RequestUrlHelperParameterController implements
			TestController<UrlChildLocation> {
		public void methodParameter0of1(UrlChildLocation urlChildLocation) {
		}

		public void methodParameter1of3(int someInt,
				UrlChildLocation urlChildLocation, Model model) {
		}

	};

	/**
	 * Class used to test with annotated UrlChildLocation parameter
	 *
	 * @author djnorth
	 */
	static private class AnnotatedRequestUrlHelperParameterController implements
			TestController<UrlChildLocation> {

		public void methodParameter0of1(@RequestUrl UrlChildLocation requestUrl) {
		}

		public void methodParameter1of3(int someInt,
				@RequestUrl UrlChildLocation requestUrl, Model model) {
		}

	};

	/**
	 * Class used to test with annotated String parameter
	 * 
	 * @author djnorth
	 */
	static private class AnnotatedStringParameterController implements
			TestController<String> {

		public void methodParameter0of1(@RequestUrl String requestUrl) {
		}

		public void methodParameter1of3(int someInt,
				@RequestUrl String requestUrl, Model model) {
		}

	};

	/**
	 * Class used to test with annotated StringBuffer parameter
	 * 
	 * @author djnorth
	 */
	static private class AnnotatedStringBufferParameterController implements
			TestController<StringBuffer> {

		public void methodParameter0of1(@RequestUrl StringBuffer requestUrl) {
		}

		public void methodParameter1of3(int someInt,
				@RequestUrl StringBuffer requestUrl, Model model) {
		}

	};

	/**
	 * Class used to test with annotated StringBuilder parameter
	 * 
	 * @author djnorth
	 */
	static private class AnnotatedStringBuilderParameterController implements
			TestController<StringBuilder> {

		public void methodParameter0of1(@RequestUrl StringBuilder requestUrl) {
		}

		public void methodParameter1of3(int someInt,
				@RequestUrl StringBuilder requestUrl, Model model) {
		}

	};

	/**
	 * Class used to test with annotated URL parameter
	 * 
	 * @author djnorth
	 */
	static private class AnnotatedURLParameterController implements
			TestController<URL> {

		public void methodParameter0of1(@RequestUrl URL requestUrl) {
		}

		public void methodParameter1of3(int someInt,
				@RequestUrl URL requestUrl, Model model) {
		}

	};

	/**
	 * Class used to test with annotated URI parameter
	 * 
	 * @author djnorth
	 */
	static private class AnnotatedURIParameterController implements
			TestController<URI> {

		public void methodParameter0of1(@RequestUrl URI requestUrl) {
		}

		public void methodParameter1of3(int someInt,
				@RequestUrl URI requestUrl, Model model) {
		}

	};

	/**
	 * Class used to test with annotated Integer parameter (bad!!!)
	 * 
	 * @author djnorth
	 */
	static private class AnnotatedIntegerParameterController implements
			TestController<Integer> {

		public void methodParameter0of1(@RequestUrl Integer requestUrl) {
		}

		public void methodParameter1of3(int someInt,
				@RequestUrl Integer requestUrl, Model model) {
		}

	};

	/**
	 * Class used to test with non-annotated String parameter (OK)
	 * 
	 * @author djnorth
	 */
	static private class NonAnnotatedStringParameterController implements
			TestController<String> {

		public void methodParameter0of1(String requestUrl) {
		}

		public void methodParameter1of3(int someInt, String requestUrl,
				Model model) {
		}

	};

	/**
	 * Class used to test with non-annotated Integer parameter (OK)
	 * 
	 * @author djnorth
	 */
	static private class NonAnnotatedIntegerParameterController implements
			TestController<Integer> {

		public void methodParameter0of1(Integer requestUrl) {
		}

		public void methodParameter1of3(int someInt, Integer requestUrl,
				Model model) {
		}

	};

	/**
	 * Test parameter creation
	 */
	@Parameters
	public static List<Object[]> getParameters() throws Exception {
		final String url0 = "https://test.com/url0";
		final String url1 = "https://test.com:8443/ur10";
		Object[][] parameters = {
				{ new TestParameters(UrlChildLocation.class, true, true, url0,
						new UrlChildLocation(url0),
						new RequestUrlHelperParameterController()) },
				{ new TestParameters(UrlChildLocation.class, true, true, url0,
						new UrlChildLocation(url0),
						new AnnotatedRequestUrlHelperParameterController()) },
				{ new TestParameters(String.class, true, true, url0, url0,
						new AnnotatedStringParameterController()) },
				{ new TestParameters(StringBuffer.class, true, true, url0,
						url0, new AnnotatedStringBufferParameterController()) },
				{ new TestParameters(StringBuilder.class, true, true, url0,
						url0, new AnnotatedStringBuilderParameterController()) },
				{ new TestParameters(URL.class, true, true, url0,
						new URL(url0), new AnnotatedURLParameterController()) },
				{ new TestParameters(URI.class, true, true, url0,
						new URI(url0), new AnnotatedURIParameterController()) },
				{ new TestParameters(Integer.class, true, false, url0, url0,
						new AnnotatedIntegerParameterController()) },
				{ new TestParameters(String.class, false, true, url0, null,
						new NonAnnotatedStringParameterController()) },
				{ new TestParameters(Integer.class, false, false, url0, null,
						new NonAnnotatedIntegerParameterController()) },
				{ new TestParameters(UrlChildLocation.class, true, true, url1,
						new UrlChildLocation(url1),
						new RequestUrlHelperParameterController()) },
				{ new TestParameters(UrlChildLocation.class, true, true, url1,
						new UrlChildLocation(url1),
						new AnnotatedRequestUrlHelperParameterController()) },
				{ new TestParameters(String.class, true, true, url1, url1,
						new AnnotatedStringParameterController()) },
				{ new TestParameters(StringBuffer.class, true, true, url1,
						url1, new AnnotatedStringBufferParameterController()) },
				{ new TestParameters(StringBuilder.class, true, true, url1,
						url1, new AnnotatedStringBuilderParameterController()) },
				{ new TestParameters(URL.class, true, true, url1,
						new URL(url1), new AnnotatedURLParameterController()) },
				{ new TestParameters(URI.class, true, true, url1,
						new URI(url1), new AnnotatedURIParameterController()) },
				{ new TestParameters(Integer.class, true, false, url1, url1,
						new AnnotatedIntegerParameterController()) },
				{ new TestParameters(String.class, false, true, url1, null,
						new NonAnnotatedStringParameterController()) },
				{ new TestParameters(Integer.class, false, false, url1, null,
						new NonAnnotatedIntegerParameterController()) } };

		return Arrays.asList(parameters);
	}

	/**
	 * Logger
	 */
	private final Logger logger = Logger.getLogger("web.server");

	/**
	 * Test parameters
	 */
	private TestParameters testParameters;

	/**
	 * WebRequest for tests
	 */
	protected NativeWebRequest nativeWebRequest;

	/**
	 * Wrapped HttpServletRequest
	 */
	protected HttpServletRequest httpServletRequest;

    /**
     * Object under test
     */
    private RequestUrlHandlerMethodArgumentResolver resolver;

    /**
     * Mock ModelAndViewContainer
     */
    private ModelAndViewContainer mavContainer;

    /**
     * Mock WebDataBinderFactory
     */
    private WebDataBinderFactory webDataBinderFactory;

	/**
	 * @param testParameters
	 */
	public RequestUrlHandlerMethodArgumentResolverTest(TestParameters testParameters) {
		super();
		this.testParameters = testParameters;
        this.resolver = new RequestUrlHandlerMethodArgumentResolver();
    }

	/**
	 * Setup our mocks
	 */
	@Before
	public void setupMocks() {

		httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
		nativeWebRequest = EasyMock.createMock(NativeWebRequest.class);
        webDataBinderFactory = EasyMock.createMock(WebDataBinderFactory.class);
		if (testParameters.isParameterSupported()
				&& testParameters.isParameterTypeValid()) {
			EasyMock.expect(httpServletRequest.getRequestURL()).andReturn(
					new StringBuffer(testParameters.requestUrl));
			EasyMock.expect(
					nativeWebRequest.getNativeRequest(HttpServletRequest.class))
					.andReturn(httpServletRequest);
		}
		EasyMock.replay(httpServletRequest, nativeWebRequest, webDataBinderFactory);

        mavContainer = new ModelAndViewContainer();
    }

	/**
	 * Test call using parameter 0
	 * 
	 * Test method for
	 * {@link com.redblackit.web.server.mvc.RequestUrlHandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)}
	 */
	@Test
	public void testResolveArgumentParm0of1() throws Exception {
		Method method = null;
		try {
			method = testParameters.testController.getClass().getMethod(
					"methodParameter0of1", testParameters.parameterType);
		} catch (NoSuchMethodException nsme) {
			logger.fatal(assertMsg("controller method", null));
			throw nsme;
		}
		verifyParameter(method, 0);
	}

	/**
	 * Test call using parameter 1
	 * 
	 * Test method for
     * {@link com.redblackit.web.server.mvc.RequestUrlHandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)}
	 */
	@Test
	public void testResolveArgumentParm1of3() throws Exception {
		Method method = null;
		try {
			method = testParameters.testController.getClass().getMethod(
					"methodParameter1of3", Integer.TYPE,
					testParameters.parameterType, Model.class);
		} catch (NoSuchMethodException nsme) {
			logger.fatal(assertMsg("controller method", null));
			throw nsme;
		}
		verifyParameter(method, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RequestUrlHandlerMethodArgumentResolverTest");
        sb.append("{resolver=").append(resolver);
        sb.append(", mavContainer=").append(mavContainer);
        sb.append(", webDataBinderFactory=").append(webDataBinderFactory);
        sb.append(", testParameters=").append(testParameters);
        sb.append(", nativeWebRequest=").append(nativeWebRequest);
        sb.append(", httpServletRequest=").append(httpServletRequest);
        sb.append('}');
        return sb.toString();
    }

    /**
	 * Common method for validating method under test
	 * 
	 * @param method
	 * @param index
	 * @throws Exception
	 */
	protected void verifyParameter(Method method, int index) throws Exception {
		MethodParameter methodParameter = new MethodParameter(method, index);
		Object parameterValue = null;
		try {

			parameterValue = invokeResolveArgument(methodParameter);

			if (testParameters.isParameterSupported()) {
				Assert.assertTrue(
						assertMsg(
								"invalid parameter type on supported parameter should cause exception",
								parameterValue), testParameters
								.isParameterTypeValid());
			}

			if (testParameters.getExpectedParameterValue() == null) {
				Assert.assertNull(
						assertMsg("unresolved parameter value", parameterValue),
						parameterValue);
			} else {
				Assert.assertEquals(
						assertMsg("parameter type", parameterValue),
						testParameters.getExpectedParameterType(),
						parameterValue.getClass());

				if (testParameters.getExpectedParameterValue() instanceof String
						&& !(parameterValue instanceof String)) {
					// Sadly the inventors of Java did not implement equals for
					// either
					// StringBuffer or StringBuilder (grrrh !!!!)
					Assert.assertEquals(
							assertMsg("parameter value", parameterValue),
							testParameters.expectedParameterValue,
							parameterValue.toString());
				} else {
					Assert.assertEquals(
							assertMsg("parameter value", parameterValue),
							testParameters.getExpectedParameterValue(),
							parameterValue);
				}
			}

			verifyExpectations(parameterValue);

		} catch (IllegalStateException ise) {
			if (testParameters.parameterTypeValid) {
				logger.fatal(assertMsg("exception on valid parameter type",
						null));

				verifyExpectations(parameterValue);

				throw ise;
			} else {
				logger.trace(
						assertMsg(
								"expected exception on invalid parameter type",
								null), ise);

				verifyExpectations(parameterValue);

			}
		}
	}

	/**
	 * Helper to add context to assertion message
	 * 
	 * @param msg to enhance
     * @param parameterValue
	 * @return result
	 */
	protected String assertMsg(String msg, Object parameterValue) {
		StringBuilder sb = new StringBuilder(msg);
		sb.append(":parameterValue=").append(parameterValue);

		if (parameterValue != null) {
			sb.append(" (").append(parameterValue.getClass()).append(')');
		}

		sb.append(':').append(this);
		return sb.toString();
	}

	/**
	 * @return the nativeWebRequest
	 */
	protected NativeWebRequest getNativeWebRequest() {
		return nativeWebRequest;
	}

	/**
	 * @return the httpServletRequest
	 */
	protected HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	/**
	 * @return the logger
	 */
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * Verify expectations in EasyMock
	 * 
	 * @param parameterValue
	 */
	private void verifyExpectations(Object parameterValue) {
		EasyMock.verify(httpServletRequest, nativeWebRequest);
        ModelMap modelMap = mavContainer.getModel();
        Assert.assertEquals(
                assertMsg("modelAndViewContainer.modelMap.size()",
                        parameterValue), 0, modelMap.size());
        Assert.assertNull(
                assertMsg("modelAndViewContainer.getView() should be null" ,
                        parameterValue), mavContainer.getView());
        Assert.assertNull(
                assertMsg("modelAndViewContainer.getViewName() should be null",
                        parameterValue), mavContainer.getViewName());

        EasyMock.verify(webDataBinderFactory);
    }

    /**
     * Invoke our HandlerMethodArgumentResolver
     *
     * @param methodParameter
     * @return parameter object
     * @throws Exception
     */
    private Object invokeResolveArgument(MethodParameter methodParameter)
            throws Exception {
        return resolver.resolveArgument(methodParameter, mavContainer,
                getNativeWebRequest(), webDataBinderFactory);
    }

	/**
	 * Test parameter encapsulation class
	 * 
	 * @author djnorth
	 */
	static final class TestParameters {

		/**
		 * Type for parameter
		 */
		private final Class<?> parameterType;

		/**
		 * Parameter is supported (or not)
		 */
		private final boolean parameterSupported;

		/**
		 * Parameter type is valid (or not)
		 */
		private final boolean parameterTypeValid;

		/**
		 * Expected value for requestUrl
		 */
		private final String requestUrl;

		/**
		 * Expected value for parameter
		 */
		private final Object expectedParameterValue;

		/**
		 * testController with whose methods we test
		 */
		private final TestController<?> testController;

		/**
		 * @param parameterType
		 * @param requestUrl
		 * @param expectedParameterValue
		 * @param parameterSupported
		 * @param parameterTypeValid
		 * @param testController
		 */
		public TestParameters(Class<?> parameterType,
				boolean parameterSupported, boolean parameterTypeValid,
				String requestUrl, Object expectedParameterValue,
				TestController<?> testController) {
			super();
			this.parameterType = parameterType;
			this.requestUrl = requestUrl;
			this.expectedParameterValue = expectedParameterValue;
			this.parameterSupported = parameterSupported;
			this.parameterTypeValid = parameterTypeValid;
			this.testController = testController;
		}

		/**
		 * @return the expectedParameterType
		 */
		public Class<?> getExpectedParameterType() {
			return parameterType;
		}

		/**
		 * @return the expectedParameterValue
		 */
		public Object getExpectedParameterValue() {
			return expectedParameterValue;
		}

		/**
		 * @return the parameterSupported
		 */
		public boolean isParameterSupported() {
			return parameterSupported;
		}

		/**
		 * @return the parameterTypeValid
		 */
		public boolean isParameterTypeValid() {
			return parameterTypeValid;
		}

		/**
		 * @return the testController
		 */
		public TestController<?> getTestController() {
			return testController;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("RequestResponseParameters [expectedParameterType=");
			builder.append(parameterType);
			builder.append(", requestUrl=");
			builder.append(requestUrl);
			builder.append(", expectedParameterValue=");
			builder.append(expectedParameterValue);
			builder.append(", parameterSupported=");
			builder.append(parameterSupported);
			builder.append(", parameterTypeValid=");
			builder.append(parameterTypeValid);
			builder.append(", testController=");
			builder.append(testController);
			builder.append("]");
			return builder.toString();
		}

	}
}
