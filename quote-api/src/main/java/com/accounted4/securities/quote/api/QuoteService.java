package com.accounted4.securities.quote.api;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * Implementors of a a stock quote service should:
 *
 * <ul>
 *     <li>implement the QuoteService interface</li>
 *     <li>add the fully qualified name of the implementing class into the file:
 *       META-INF.services.com.accounted4.securities.quote.api.QuoteService</li>
 *     <li>add the jar to the classpath of the stock-quote executable</li>
 * </ul>
 *
 * See quote-yahoo for an example implementation.
 *
 * @author gheinze
 */
public interface QuoteService {


    /**
     * Provide a name for the service that provides stock quotes.
     * The name is used to distinguish it from other possible implementations.
     *
     * @return Name of the service.
     */
    String getServiceName();


    /**
     * Invokes the query to the stock quote service.
     *
     * @param securities The set of securities (i.e. ticker symbols) for which information should be retrieved.
     * @param queryFields A set of the attributes to be retrieved for each security.
     *
     * @return A map keyed on the security (ticker symbol) and associated with a mapping of the
     * attribute to the attribute value.  For example:
     *
     *     {
     *         BMO.TO -> {lastTradePrice -> 60, earningsPerShare -> 2},
     *         SLF.TO -> {lastTradePrice -> 40, earningsPerShare -> 1.5}
     *     }
     *
     * The result list may be smaller than the supplied list of securities if the security was not found at the quote service.
     */
    Map<String, EnumMap<QueryField, String>> executeQuery(
             List<String> securities
            ,List<QueryField> queryFields
    ) throws QuoteServiceException;

}
