package com.accounted4.securities.quote.api;

import static com.accounted4.securities.quote.api.QueryFieldType.*;


/**
 * Supported attributes for which to query a stock quote service.
 *
 * @author gheinze
 */
public enum QueryField {

     symbol(TEXT)
    ,companyName(TEXT)
    ,lastTradePrice(NUMERIC)
    ,bookValue(NUMERIC)
    ,earningsPerShare(NUMERIC)
    ,dividendPerShare(NUMERIC)
    ,exDividendDate(DATE)
    ,dividendDate(DATE)
    ,dividendYield(NUMERIC)
    ;

    private final QueryFieldType type;


    private QueryField(QueryFieldType type) {
        this.type = type;
    }

    public QueryFieldType getType() {
        return type;
    }

}
