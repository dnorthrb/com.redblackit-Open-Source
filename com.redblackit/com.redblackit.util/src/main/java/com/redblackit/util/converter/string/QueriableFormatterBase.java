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


/**
 * Queriable formatter base class
 * 
 * @author djnorth
 */
public abstract class QueriableFormatterBase<T> implements QueriableFormatter<T> {
	
	/**
	 * Supported type
	 */
	private final Class<T> supportedType;

	/**
	 * Constructor taking pattern
	 * 
	 * @param supportedType
	 */
	public QueriableFormatterBase(Class<T> supportedType) {
		super();
		this.supportedType = supportedType;
	}
	


	/**
	 * Test type is a date
	 * 
	 * @param type
	 * @return true or false accordingly
	 * @see com.redblackit.util.converter.string.QueriableFormatter#canFormatType(Class)
	 */
	@Override
	public boolean canFormatType(Class<?> type) {
		return getSupportedType().isAssignableFrom(type);
	}


	/**
	 * Test object is printable
	 * 
	 * @param object
	 * @return true or false accordingly
	 * @see com.redblackit.util.converter.string.QueriableFormatter#canPrintObject(Object)
	 */
	@Override
	public boolean canPrintObject(Object object) {
		return (object == null ? true : canFormatType(object.getClass()));
	}
	
	
	/**
	 * Test text can be parsed using the default locale by trying to parse it. We don't care why it fails if it does.
	 * 
	 * @param text
	 * @return true or false accordingly
	 * @see com.redblackit.util.converter.string.QueriableFormatter#canParseText(java.lang.String)
	 */
	@Override
	public boolean canParseText(String text, Locale locale) {
		boolean canParse = false;
		try
		{
			@SuppressWarnings("unused")
			T textObj = parse(text, locale);
			canParse = true;
		}
		catch (Throwable t) {
			// OK, we can't!
		}
		
		return canParse;
	}
	

	/**
	 * Print object using default locale
	 * 
	 * @return string
	 * @see com.redblackit.util.converter.string.QueriableFormatter#print(java.lang.Object)
	 */
	@Override
	public String print(T object) {
		return print(object, Locale.getDefault());
	}


	/**
	 * Parse text using default locale
	 * 
	 * @param text
	 * @return object
	 * @throws ParseException
	 * @see com.redblackit.util.converter.string.QueriableFormatter#parse(java.lang.String)
	 */
	@Override
	public T parse(String text) throws ParseException {
		return parse(text, Locale.getDefault());
	}



	/**
	 * Test if we can parse text using default locale
	 * 
	 * @param text
	 * @return true or false
	 * @see com.redblackit.util.converter.string.QueriableFormatter#canParseText(java.lang.String)
	 */
	@Override
	public boolean canParseText(String text) {
		return canParseText(text, Locale.getDefault());
	}



	/**
	 * @return the supportedType
	 */
	public Class<T> getSupportedType() {
		return supportedType;
	}


	/**
	 * toString
	 * 
	 * @return state as string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getClass().getName());
		builder.append(" [supportedType=");
		builder.append(supportedType);
		builder.append("]");
		return builder.toString();
	}


	/**
	 * Throw exception if object cannot be formatted
	 * 
	 * @param object
	 */
	protected void verifyCanPrintObject(Object object) {
		if (!canPrintObject(object))
		{
			throw new IllegalArgumentException(getClass().getName() + ":object should be of class " + getSupportedType());
		}
	}


}
