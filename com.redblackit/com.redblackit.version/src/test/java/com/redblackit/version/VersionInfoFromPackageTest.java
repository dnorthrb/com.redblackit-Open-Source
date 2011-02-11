package com.redblackit.version;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(Parameterized.class)
public class VersionInfoFromPackageTest {
	
	/**
	 * VersionInfo under test
	 */
	private VersionInfo versionInfoUnderTest;

	/**
	 * Expected title
	 */
	private String expectedTitle;

	/**
	 * Expected version
	 */
	private String expectedVersion;

	/**
	 * Expected vendor
	 */
	private String expectedVendor;

	/**
	 * Expected config version
	 */
	private String expectedConfigVersion;
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Parameters
	public static List<Object[]> testData() {
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {"VersionInfoFromPackageTest-context.xml"});
		List<List<Object>> testList = applicationContext.getBean("testList", List.class);
		List<Object[]> dataList = new ArrayList<Object[]>();
		for (List<Object> test : testList)
		{
			dataList.add(test.toArray());
		}
		
		return dataList;
	}

	/**
	 * Constructor taking instance to test and expected results.
	 * 
	 * @param versionInfoUnderTest
	 * @param expectedTitle
	 * @param expectedVersion
	 * @param expectedVendor
	 * @param expectedConfigVersion
	 */
	public VersionInfoFromPackageTest(VersionInfo versionInfoUnderTest,
			String expectedTitle, String expectedVersion,
			String expectedVendor, String expectedConfigVersion) {

		this.versionInfoUnderTest = versionInfoUnderTest;
		this.expectedTitle = expectedTitle;
		this.expectedVersion = expectedVersion;
		this.expectedVendor = expectedVendor;
		this.expectedConfigVersion = expectedConfigVersion;
		
	}

	/**
	 * Test get title
	 */
	@Test
	public void getTitle() {
		Assert.assertEquals(versionInfoUnderTest.getClass() + ":title",
				expectedTitle, versionInfoUnderTest.getImplementationTitle());
	}

	/**
	 * Test get inmplementation version
	 */
	@Test
	public void getVersion() {
		Assert.assertEquals(versionInfoUnderTest.getClass()
				+ ":implementation version", expectedVersion,
				versionInfoUnderTest.getImplementationVersion());
	}

	/**
	 * Test get vendor
	 */
	@Test
	public void getVendor() {
		Assert.assertEquals(versionInfoUnderTest.getClass() + ":vendor",
				expectedVendor, versionInfoUnderTest.getImplementationVendor());
	}

	/**
	 * Test get config version
	 */
	@Test
	public void getConfigVersion() {
		Assert.assertEquals(
				versionInfoUnderTest.getClass() + ":config version",
				expectedConfigVersion, versionInfoUnderTest
						.getConfigurationVersion());
	}

}
