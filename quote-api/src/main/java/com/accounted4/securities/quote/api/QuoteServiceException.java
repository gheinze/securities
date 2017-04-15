package com.accounted4.securities.quote.api;


/**
 * QuoteService failures should be wrapped in a QuoteServiceException.
 *
 * @author gheinze
 */
public class QuoteServiceException extends RuntimeException {


    public QuoteServiceException(Exception ex) {
        super(ex);
    }


    public QuoteServiceException(String msg) {
        super(msg);
    }


}
