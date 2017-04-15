package com.accounted4.securities.quote.yahoo;


/**
 * Configuration information for accessing the Yahoo quote service.
 * 
 * Configuration lookup is performed in a hierarchical manner until a value is found:
 *   o check if the property was set at jvm startup via the -D setting
 *   o if not found, use a default value
 *
 * @author gheinze
 */
public enum YahooQuoteConfiguration {

     serviceName("name", "Yahoo")
    ,baseUrl("url", "http://finance.yahoo.com/d/quotes.csv")
    ,securitySeparator("securitySeparator", "+")
    ,responseSeparator("responseSeparator", ",")
    ;

    private static final String KEY_PREFIX = "quoteService.yahoo.";


    private final String configPropertyName;
    private final String defaultValue;

    private YahooQuoteConfiguration(String configPropertyName, String defaultValue) {
        this.configPropertyName = configPropertyName;
        this.defaultValue = defaultValue;
    }


    public String getActiveConfigValue() {
        String value = System.getProperty(KEY_PREFIX + configPropertyName);
        return null == value ? defaultValue : value;
    }


    public String getConfigPropertyName() {
        return KEY_PREFIX + configPropertyName;
    }


    public String getDefaultValue() {
        return defaultValue;
    }

}
