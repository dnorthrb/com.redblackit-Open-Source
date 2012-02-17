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
 * Extension of spring Formatter<T> interface, also allowing clients to test types or
 * objects for support on an individual formatter, and also implicit use of default locale.
 * 
 * @author djnorth
 */
public interface QueriableFormatter<T> extends Formatter<T> {

	/**
	 * Print with default locale
	 * 
	 * @param object to print as String
	 * @return resulting String
	 */
	String print(T object);
	
	/**
	 * Parse using default locale
	 * 
	 * @param text to parse
	 * @return resulting object
	 * @throws ParseException
	 */
	T parse(String text) throws ParseException;
	
	/**
	 * Indicate whether type is supported for formatting (print/parse)
	 * 
	 * @param type
	 */
	boolean canFormatType(Class<?> type);

	/**
	 * Indicate whether object can be printed
	 * 
	 * <ul>
	 * <li>If object is not null, this should return the same as
	 * canFormatType(obj.getClass())</li>
	 * <li>If object is null, return true, as we can always return null</li>
	 * <ul>
	 * 
	 * @param object
	 * @return true or false as above
	 */
	boolean canPrintObject(Object object);

	/**
	 * Indicate whether text can be parsed successfully for the given Locale.
	 * 
	 * @param text
	 * @param locale
	 * @return true or false 
	 */
	boolean canParseText(String text, Locale locale);

	/**
	 * Indicate whether text can be parsed successfully for the default locale
	 * 
	 * @param text
	 * @return true or false 
	 */
	boolean canParseText(String text);

}
