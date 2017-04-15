package com.accounted4.securities.quote.yahoo;


import static com.accounted4.securities.quote.yahoo.YahooQuoteConfiguration.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;


/**
 *
 * @author gheinze
 */
public class YahooQuoteConfigurationTest {


    /**
     * Test that the default configuration values for the Yahoo service exist
     * and are returned when there are no overrides (-D command line property, environment settings).
     */
    @Test
    public void testDefaultValues() {
        System.out.println("defaultValues");

        String expected = serviceName.getDefaultValue();
        String actual = serviceName.getActiveConfigValue();
        assertThat("Service name", actual, is(equalTo(expected)));

        expected = baseUrl.getDefaultValue();
        actual = YahooQuoteConfiguration.baseUrl.getActiveConfigValue();
        assertThat("Base url", actual, is(equalTo(expected)));

        expected = securitySeparator.getDefaultValue();
        actual = YahooQuoteConfiguration.securitySeparator.getActiveConfigValue();
        assertThat("Security separator", actual, is(equalTo(expected)));

        expected = responseSeparator.getDefaultValue();
        actual = YahooQuoteConfiguration.responseSeparator.getActiveConfigValue();
        assertThat("Response separator", actual, is(equalTo(expected)));

    }


    @Test
    public void testOverrideValues() {
        System.out.println("overrideValues");

        String configPropertyName = serviceName.getConfigPropertyName();
        String expected = "TestYahoo";
        System.setProperty(configPropertyName, expected);
        String actual = serviceName.getActiveConfigValue();
        System.clearProperty(configPropertyName);
        assertThat("Service name", actual, is(equalTo(expected)));

        configPropertyName = baseUrl.getConfigPropertyName();
        expected = "TESThttp://finance.yahoo.com/d/quotes.csv";
        System.setProperty(configPropertyName, expected);
        actual = YahooQuoteConfiguration.baseUrl.getActiveConfigValue();
        System.clearProperty(configPropertyName);
        assertThat("Base url", actual, is(equalTo(expected)));

        configPropertyName = securitySeparator.getConfigPropertyName();
        expected = "+";
        System.setProperty(configPropertyName, expected);
        actual = YahooQuoteConfiguration.securitySeparator.getActiveConfigValue();
        System.clearProperty(configPropertyName);
        assertThat("Security separator", actual, is(equalTo(expected)));

        configPropertyName = responseSeparator.getConfigPropertyName();
        expected = ",";
        System.setProperty(configPropertyName, expected);
        actual = YahooQuoteConfiguration.responseSeparator.getActiveConfigValue();
        System.clearProperty(configPropertyName);
        assertThat("Response separator", actual, is(equalTo(expected)));

    }


//    @Test
//    public void testValues() {
//        System.out.println("values");
//        YahooQuoteConfiguration[] expResult = null;
//        YahooQuoteConfiguration[] result = YahooQuoteConfiguration.values();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testValueOf() {
//        System.out.println("valueOf");
//        String name = "";
//        YahooQuoteConfiguration expResult = null;
//        YahooQuoteConfiguration result = YahooQuoteConfiguration.valueOf(name);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetConfigValue() {
//        System.out.println("getActiveConfigValue");
//        YahooQuoteConfiguration instance = null;
//        String expResult = "";
//        String result = instance.getActiveConfigValue();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
