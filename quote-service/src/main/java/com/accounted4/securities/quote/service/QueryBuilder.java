package com.accounted4.securities.quote.service;

import com.accounted4.securities.quote.api.QueryField;
import com.accounted4.securities.quote.api.QuoteService;
import com.accounted4.securities.quote.api.QuoteServiceException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;


/**
 * Prepare inputs and invoke an appropriate Stock Quoting Service.
 *
 * Query criteria include:
 *
 * <ul>
 *   <li>serviceName</li>
 *   <li>list of securities</li>
 *   <li>list of attributes to query for each security</li>
 * </ul>
 *
 * @author Glenn Heinze <glenn@gheinze.com>
 */
public class QueryBuilder {


    private QuoteService selectedQuoteService;
    private final List<QueryField> queryFields;

    // Securities for which to query for information. Note that the security name is dependent
    // on the service and the exchange. For example, quoting Yahoo for BMO would query the
    // NYSE by default. One would need to use BMO.TO for the Yahoo service to check at the TSX
    private final List<String> securityList;

    // Available quote services are discovered on the classpath via meta-inf/services
    private static ServiceLoader<QuoteService> quoteServiceLoader;



    /**
     * Initialize a new Query builder with a default query service.
     */
    public QueryBuilder() {

        securityList = new ArrayList<>();
        queryFields = new ArrayList<>();

        quoteServiceLoader = ServiceLoader.load(QuoteService.class);

        // Select the first service we can find as a default service, if it exists
        Iterator<QuoteService> serviceIterator = quoteServiceLoader.iterator();
        if (serviceIterator.hasNext()) {
            selectedQuoteService = serviceIterator.next();
        }

    }


    /* ---------------------------
     * Quote Service API
     * ---------------------------
     */


    /**
     * Discover Quote services on the classpath.
     *
     * @return A list of available services
     */
    public List<QuoteService> getSupportedQuoteServices() {

        ArrayList<QuoteService> result = new ArrayList<>();

        Iterator<QuoteService> serviceIterator = QueryBuilder.quoteServiceLoader.iterator();
        while (serviceIterator.hasNext()) {
            result.add(serviceIterator.next());
        }

        return result;

    }


    /**
     * The quote service which will be used if the query were to be
     * executed at this stage.
     *
     * @return The quote service to be used for requesting quotes. It is possible
     * for the result to be null;
     */
    public QuoteService getSelectedQuoteService() {
        return selectedQuoteService;
    }


    /**
     * Specify the quoting service to use for queries.
     *
     * @param service The quoting service to use
     */
    public void setSelectedQuoteService(QuoteService service) {
        selectedQuoteService = service;
    }


    /**
     * Specify the name of the quoting service to use for queries.
     *
     * @param serviceName The name of the quoting service
     *
     * @throws QuoteServiceException If no quoting service matching the given
     * name could be found among the services on the classpath.
     */
    public void setSelectedQuoteService(String serviceName) throws QuoteServiceException {

        QuoteService service = getSupportedQuoteServices()
                .stream()
                .filter(s -> s.getServiceName().equalsIgnoreCase(serviceName))
                .findAny()
                .orElseThrow(() -> new QuoteServiceException(
                        "Unknown service name: " + serviceName + "\n" +
                        "Known services: " + getSupportedQuoteServices()
                ));

        setSelectedQuoteService(service);

    }



    /* ---------------------------
     * Attribute API
     * ---------------------------
     */

    /**
     * The list of attributes for which the quote service will be queried. The list is ordered
     * and could contain the same attribute multiple times if the list was build in that manner.
     *
     * @return The list of query attributes (ex last price, company name, ...).
     */
    public List<QueryField> getQuoteAttributes() {
        return new ArrayList<>(queryFields);
    }


    /**
     * Adds an attribute to the list of quote criteria to query for..
     *
     * @param attribute The attribute specifying the information to retrieve from the quoting
     * service when the query is executed.
     */
    public void addQuoteAttribute(QueryField attribute) {
        queryFields.add(attribute);
    }


    /**
     * Remove all attributes from the query builder. Perhaps useful when re-using the
     * QueryBuilder for multiple queries with different attribute lists: clear the list
     * between queries.
     */
    public void clearQuoteAttributes() {
        queryFields.clear();
    }


    /* ---------------------------
     * Security API
     * ---------------------------
     */

    /**
     * The list of ticker symbols to use when querying the quoting service.
     *
     * @return  A list of strings representing ticker symbols in a format that is
     * should be structured for the underlying query service.
     */
    public List<String> getSecurityList() {
        return new ArrayList<>(securityList);
    }


    /**
     * The ticker symbol to use when querying the service.
     *
     * @param security A ticker symbol to add to the list of securities to query.
     */
    public void addSecurity(String security) {
        securityList.add(security);
    }


    /**
     * Remove all ticker symbols from the query builder. Perhaps useful when re-using the
     * QueryBuilder for multiple queries with different security lists: clear the list
     * between queries.
     */
    public void clearSecurityList() {
        securityList.clear();
    }


    /* ---------------------------
     * Execute
     * ---------------------------
     */

    /**
     * After building the query by choosing a service, adding securities, and specifying the
     * attributes to query for, the service can be queried for the information.
     *
     * @return a list of results for each ticker symbol. Each list item is itself a
     * list of the resultant values for the queried attributes. Example:
     *
     *     {
     *         BMO.TO -> {lastTradePrice -> 60, earningsPerShare -> 2},
     *         SLF.TO -> {lastTradePrice -> 40, earningsPerShare -> 1.5}
     *     }
     *
     * @throws QuoteServiceException
     */
    public Map<String, EnumMap<QueryField, String>> executeQuery() throws QuoteServiceException {

        if (null == selectedQuoteService) {
            throw new QuoteServiceException("No quote service selected");
        }

        if (queryFields.isEmpty()) {
            throw new QuoteServiceException("No query attributes selected");
        }

        if (securityList.isEmpty()) {
            throw new QuoteServiceException("No securities selected");
        }

        return selectedQuoteService.executeQuery(securityList, queryFields);

    }


}
