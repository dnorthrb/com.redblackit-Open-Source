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

package com.redblackit.util.converter.string;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.format.number.NumberFormatter;

/**
 * Tests for QueriableNumberFormatter
 * 
 * @author djnorth
 */
@RunWith(Parameterized.class)
public class QueriableNumberFormatterTest extends
		QueriableFormatterTestBase<Number> {

	/**
	 * Create and return parameters
	 */
	@Parameters
	public static List<Object[]> getParameters() {

		Object[][] parameters = { { "3", BigDecimal.valueOf(3), Locale.UK,
				new NumberFormatter("#") } };

		return Arrays.asList(parameters);
	}

	/**
	 * Formatter under test
	 */
	private QueriableNumberFormatter formatter;
	
	
	/**
	 * @param textToParse
	 * @param objectToPrint
	 * @param locale
	 * @param delegateFormatter
	 */
	public QueriableNumberFormatterTest(String textToParse,
			Object objectToPrint, Locale locale,
			NumberFormatter delegateFormatter) {
		super(textToParse, objectToPrint, locale);
		this.formatter = new QueriableNumberFormatter(delegateFormatter);
	}


	/**
	 * Get specific formatter to test.
	 * 
	 * We use template getter so that we can do additional class-specific tests
	 * in concrete test classes
	 * 
	 * @return formatter
	 * @see QueriableFormatterTestBase#getFormatter
	 */
	@Override
	protected QueriableFormatterBase<Number> getFormatter() {
		return formatter;
	}

}
