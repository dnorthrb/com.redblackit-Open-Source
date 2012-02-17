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

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.format.datetime.DateFormatter;

import com.redblackit.util.converter.string.QueriableDateFormatter;

/**
 * Tests for ThreadsafeDecimalFormatter
 * 
 * @author djnorth
 */
@RunWith(Parameterized.class)
public class QueriableDateFormatterTest extends
		QueriableFormatterTestBase<Date> {

	/**
	 * Create and return parameters
	 */
	@Parameters
	public static List<Object[]> getParameters() {

		Object[][] parameters = { { "2011-03-02",
				new GregorianCalendar(2011, 2, 2).getTime(), Locale.UK,
				new DateFormatter("yyyy-MM-dd") } };

		return Arrays.asList(parameters);
	}

	/**
	 * Formatter under test
	 */
	private QueriableDateFormatter formatter;

	/**
	 * @param textToParse
	 * @param objectToPrint
	 * @param locale
	 * @param delegateFormatter
	 */
	public QueriableDateFormatterTest(String textToParse, Object objectToPrint,
			Locale locale, DateFormatter delegateFormatter) {
		super(textToParse, objectToPrint, locale);
		this.formatter = new QueriableDateFormatter(delegateFormatter);
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
	protected QueriableFormatterBase<Date> getFormatter() {
		return formatter;
	}

}
