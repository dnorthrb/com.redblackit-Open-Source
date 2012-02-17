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

import org.springframework.format.Formatter;

/**
 * Queriable date formatter, wrapping the Spring DateFormatter
 * 
 * @author djnorth
 */
public class QueriableNumberFormatter extends DelegatingQueriableFormatter<Formatter<Number>, Number> {
	

	/**
	 * Constructor taking the Spring Formatter<Number> (probably DateFormatter) we'll use as our delegate, ready configured
	 * 
	 * @param delegateFormatter
	 */
	public QueriableNumberFormatter(Formatter<Number> delegateFormatter) {
		super(Number.class, delegateFormatter);
	}

}
