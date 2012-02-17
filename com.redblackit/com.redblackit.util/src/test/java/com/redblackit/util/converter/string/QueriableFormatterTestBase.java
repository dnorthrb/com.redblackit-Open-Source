package com.redblackit.util.converter.string;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.redblackit.util.converter.string.QueriableFormatterBase;

/**
 * Base class for tests on QueriableFormatter implementations
 */
public abstract class QueriableFormatterTestBase<T> {

	/**
	 * Logger
	 */
	private final Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Text to parse
	 */
	private final String textToParse;

	/**
	 * Object to print
	 */
	private final Object objectToPrint;
	
	/**
	 * Locale
	 */
	private final Locale locale;

	/**
	 * Constructor taking text to parse, or an equivalent object to print, or
	 * both
	 * <ul>
	 * <li>If textToParse is not-null, and parsable, then an equivalent
	 * objectToPrint should be provided. In this case
	 * <ul>
	 * <li>null values will also be checked to ensure they work consistently
	 * with the corresponding query methods</li>
	 * <li>The supplied locale works consistently as default or when explicitly
	 * passed in</li>
	 * </ul>
	 * </li>
	 * <li>If the textToParse is not-null, but not parsable, then objectToPrint
	 * should be null</li>
	 * <li>If the textToParse is null, objectToPrint should not be null should
	 * be of an unsupported type</li>
	 * </ul>
	 * 
	 * @param textToParse
	 * @param objectToPrint
	 * @param locale
	 */
	public QueriableFormatterTestBase(String textToParse, Object objectToPrint,
			Locale locale) {
		this.textToParse = textToParse;
		this.objectToPrint = objectToPrint;
		this.locale = locale;
		Assert.assertNotNull("locale:" + this, locale);
		Assert.assertTrue(
				"one of textToParse and objectToPrint must be not-null:" + this,
				(textToParse != null || objectToPrint != null));
		
		Locale.setDefault(locale);
	}

	/**
	 * Test method for implementation of 
	 * {@link com.redblackit.util.converter.string.QueriableFormatter#canFormatType(java.lang.Class)}
	 * 
	 * We skip test if object is null
	 */
	@Test
	public void testCanFormatType() {
		if (getObjectToPrint() != null) {
			Assert.assertEquals("canFormatType:" + this,
					nonNullObjectSupportedType(),
					getFormatter().canFormatType(getObjectToPrint().getClass()));
		}
	}

	/**
	 * Test method for implementation of 
	 * {@link com.redblackit.util.converter.string.QueriableFormatter#canPrintObject(java.lang.Object)}
	 * 
	 * We skip test if object is null
	 */
	@Test
	public void testCanPrintObject() {
		if (getObjectToPrint() != null) {
			Assert.assertEquals("canPrintObject:" + this,
					nonNullObjectSupportedType(), getFormatter()
							.canPrintObject(getObjectToPrint()));
		}
	}

	/**
	 * Test method for implementation of 
	 * {@link com.redblackit.util.converter.string.QueriableFormatter#canParseText(java.lang.String)}
	 * 
	 * We skip test if text is null
	 */
	@Test
	public void testCanParseText() {
		if (getTextToParse() != null) {
			Assert.assertEquals("canParseText:" + this, nonNullTextParsable(),
					getFormatter().canParseText(getTextToParse()));
			Assert.assertEquals("canParseText with locale:" + this, nonNullTextParsable(),
					getFormatter().canParseText(getTextToParse(), getLocale()));
		}
	}

	/**
	 * Test method for implementation of 
	 * {@link com.redblackit.util.converter.string.QueriableFormatter#canParseText(java.lang.String,java.util.Locale)}
	 * 
	 * We skip test if text is null
	 */
	@Test
	public void testCanParseTextWithLocale() {
		if (getTextToParse() != null) {
			Assert.assertEquals("canParseText with locale:" + this, nonNullTextParsable(),
					getFormatter().canParseText(getTextToParse(), getLocale()));
		}
	}
	
	/**
	 * Test method for {@link com.redblackit.util.converter.string.QueriableFormatter#parse(java.lang.String)}
	 * 
	 * We skip test if text is null
	 */
	@Test
	public void testParseText() throws Throwable
	{
		T parsedObject = null;
		if (getTextToParse() != null) {
			try
			{
				parsedObject = getFormatter().parse(getTextToParse());
				Assert.assertTrue("expected failure for non-parsable text:" + this, nonNullTextParsable());
				Assert.assertEquals("objectToPrint and parsedObject:" + this, getObjectToPrint(), parsedObject);
			}
			catch (Throwable t)
			{
				if (nonNullTextParsable())
				{
					throw t;
				}
				else
				{
					logger.debug("expected exception:" + this, t);
				}
			}
		}
	}
	
	/**
	 * Test method for {@link com.redblackit.util.converter.string.QueriableFormatter#parse(java.lang.String,java.util.Locale)}
	 * 
	 * We skip test if text is null
	 */
	@Test
	public void testParseTextWithLocale() throws Throwable
	{
		T parsedObject = null;
		if (getTextToParse() != null) {
			try
			{
				parsedObject = getFormatter().parse(getTextToParse(), getLocale());
				Assert.assertTrue("expected failure for non-parsable text:" + this, nonNullTextParsable());
				Assert.assertEquals("objectToPrint and parsedObject:" + this, getObjectToPrint(), parsedObject);
			}
			catch (Throwable t)
			{
				if (nonNullTextParsable())
				{
					throw t;
				}
				else
				{
					logger.debug("expected exception:" + this, t);
				}
			}
		}
	}

	/**
	 * toString
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getClass().getName());
		builder.append(" [textToParse=");
		builder.append(getTextToParse());
		builder.append(", objectToPrint=");
		builder.append(objectToPrint);
		builder.append(", locale=");
		builder.append(locale);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the logger
	 */
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * @return the textToParse
	 */
	protected String getTextToParse() {
		return textToParse;
	}

	/**
	 * @return the objectToFormat
	 */
	protected Object getObjectToPrint() {
		return objectToPrint;
	}

	/**
	 * @return the locale
	 */
	protected Locale getLocale() {
		return locale;
	}

	/**
	 * Test if we expect non-null text to be parsable i.e. we have an
	 * objectToPrint as well
	 * 
	 * @return true or false
	 */
	protected boolean nonNullTextParsable() {
		return (getObjectToPrint() != null);
	}

	/**
	 * Test if we expect non-null object to be printable and of supported type
	 * i.e. we have an textToParse as well
	 * 
	 * @return true or false
	 */
	protected boolean nonNullObjectSupportedType() {
		return (getTextToParse() != null);
	}

	/**
	 * Get specific formatter to test.
	 * 
	 * We use template getter so that we can do additional class-specific tests
	 * in concrete test classes
	 * 
	 * @return the formatter
	 */
	protected abstract QueriableFormatterBase<T> getFormatter();

}