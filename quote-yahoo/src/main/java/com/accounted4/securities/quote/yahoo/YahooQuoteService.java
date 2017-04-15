package com.accounted4.securities.quote.yahoo;


import com.accounted4.securities.quote.api.QueryField;
import com.accounted4.securities.quote.api.QueryFieldType;
import com.accounted4.securities.quote.api.QuoteService;
import com.accounted4.securities.quote.api.QuoteServiceException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An implementation of the stock quote api using a Yahoo REST service.
 *
 * @author gheinze
 */
public class YahooQuoteService implements QuoteService {


    private final Logger LOG = LoggerFactory.getLogger(YahooQuoteService.class);

    private static final String SERVICE_NAME = YahooQuoteConfiguration.serviceName.getActiveConfigValue();;
    private static final String BASE_URL = YahooQuoteConfiguration.baseUrl.getActiveConfigValue();
    private static final String SECURITY_SEPARATOR = YahooQuoteConfiguration.securitySeparator.getActiveConfigValue();
    private static final String RESPONSE_SEPARATOR = YahooQuoteConfiguration.responseSeparator.getActiveConfigValue();


    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, EnumMap<QueryField, String>> executeQuery(
            List<String> securities,
            List<QueryField> queryFields
    ) throws QuoteServiceException {

        String queryUrlString = generateQueryString(securities, queryFields);
        String response = queryYahooService(queryUrlString);

        return processResponse(response, securities, queryFields);
    }


    private String queryYahooService(String queryUrlString) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(queryUrlString);

        try {
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return null == entity ? "" : EntityUtils.toString(entity);
        } catch (IOException ex) {
            LOG.error("Failure communicating with Yahoo quote service");
            throw new QuoteServiceException(ex);
        }

    }


    /*
     * The response comes back as a csv. Parse it out
     *
     */
    private Map<String, EnumMap<QueryField, String>> processResponse(
            String response,
            List<String> securityList,
            List<QueryField> attributeList
    ) {

        Map<String, EnumMap<QueryField, String>> result = new LinkedHashMap<>();

        if (response.isEmpty()) {
            return result;
        }

        // Each line is the response for one security
        String[] lines = response.split("\n");
        int lineNumber = 0;

        for (String line : lines) {
            result.put(securityList.get(lineNumber++), parseResponseLine(line, attributeList));
        }

        return result;

    }


    private EnumMap<QueryField, String> parseResponseLine(String line, List<QueryField> attributeList) {

        EnumMap<QueryField, String> lineItems = new EnumMap<>(QueryField.class);

        // Results for the attributes come back in the order requested, each separated by a comma
        String[] items = line.split(RESPONSE_SEPARATOR);
        int itemNumber = 0;

        for (String item : items) {
            QueryField field = attributeList.get(itemNumber);
            String value = attributeValueFormat(item, field.getType());
            lineItems.put(field, value);
            itemNumber++;
        }

        return lineItems;
    }


    private String attributeValueFormat(String item, QueryFieldType type) {

        if (null == item || item.isEmpty()) {
            return item;
        }
        
        switch(type) {

            case DATE:
                // Yahoo dates respond in quoted day/month/year format: "4/26/2017"
                // Return standard format:  YYYY-MM-DD
                String[] dateParts = item.substring(1, item.length() - 1).split("/");
                return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

            case TEXT:
                // Yahoo text is double quoted:  companyName="Oracle Corporation"
                // Return with quotes removed
                return item.substring(1, item.length() - 1);
        }

        return item;
    }


    private String generateQueryString(List<String> securities, List<QueryField> queryFields) {

        String tickerList = securityListToString(securities);
        String attributeList = attributeListToString(queryFields);

        String query = String.format(BASE_URL + "?s=%s&f=%s", tickerList, attributeList);
        LOG.info("Query url: {}", query);

        return query;

    }



    /*
     * Convert the list of securities provided by the user into a Yahoo-formated list
     * that can be sent to the Yahoo service: a "+" separated list of Strings
     */
    private String securityListToString(List<String> securityList) {
        return securityList.stream().collect(Collectors.joining(SECURITY_SEPARATOR));
    }


    /*
     * Translate the list of attributes requested into a Yahoo-specific query string to
     * ship off to the Yahoo stock quote service
     *
     * @param quoteAttributes List of generic quote attributes
     * @return Query string to send to Yahoo in order to query for requested attributes.
     */
    private String attributeListToString(List<QueryField> queryFields) {
        return queryFields.stream()
                .map(f -> getYahooQueryString(f))
                .collect(Collectors.joining())
                ;
    }


    private String getYahooQueryString(QueryField queryField) {
        switch (queryField) {
                case symbol:            return  "s";
                case companyName:       return  "n";
                case lastTradePrice:    return  "l1";
                case bookValue:         return  "b4";
                case earningsPerShare:  return  "e";
                case dividendPerShare:  return  "d";
                case exDividendDate:    return  "q";
                case dividendDate:      return  "r1";
                case dividendYield:     return  "y";

                default:
                    LOG.warn("Unsupported query attribute: {}", queryField);
                    return "";
        }
    }


}
