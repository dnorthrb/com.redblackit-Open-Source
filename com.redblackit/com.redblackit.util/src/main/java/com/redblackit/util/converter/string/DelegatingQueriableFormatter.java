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

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

/**
 * Queriable date formatter, wrapping the Spring DateFormatter
 * 
 * @author djnorth
 */
public abstract class DelegatingQueriableFormatter<FMR extends Formatter<T>, T> extends QueriableFormatterBase<T> {
	
	/**
	 * Our delegate Formatter
	 */
	private final FMR delegateFormatter;

	/**
	 * Constructor taking our delegate
	 * 
	 * @param supportedType
	 * @param delegateFormatter
	 */
	public DelegatingQueriableFormatter(Class<T> supportedType,
			FMR delegateFormatter) {
		super(supportedType);
		this.delegateFormatter = delegateFormatter;
	}

	/**
	 * Print using our formatter
	 * 
	 * @param object
	 * @param locale
	 * @return string
	 * @see org.springframework.format.Printer#print(java.lang.Object, java.util.Locale)
	 */
	@Override
	public String print(T object, Locale locale) {
		return delegateFormatter.print(object, locale);
	}

	/**
	 * Parse using our formatter
	 * 
	 * @param text
	 * @param locale
	 * @return date
	 * @throws ParseException
	 * @see org.springframework.format.Parser#parse(java.lang.String, java.util.Locale)
	 */
	@Override
	public T parse(String text, Locale locale) throws ParseException {
		return delegateFormatter.parse(text, locale);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append(" [delegateFormatter=");
		builder.append(delegateFormatter);
		builder.append("]");
		return builder.toString();
	}
	

}
