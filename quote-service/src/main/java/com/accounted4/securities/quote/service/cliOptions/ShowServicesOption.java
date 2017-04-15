package com.accounted4.securities.quote.service.cliOptions;

import com.accounted4.securities.quote.api.QuoteService;
import com.accounted4.securities.quote.service.QueryBuilder;
import java.util.List;
import org.apache.commons.cli.CommandLine;


/**
 * A command line option implementation for showing quote services available for this cli.
 *
 * @author gheinze
 */
public class ShowServicesOption extends AbstractCliOption {

    private final QueryBuilder queryBuilder;

    private static final String OPTION_FIELD = "showServices";
    private static final String OPTION_DESCRIPTION = "list the stock quoting services available for queries";

    public ShowServicesOption(QueryBuilder queryBuilder) {
        super(OPTION_FIELD, OPTION_DESCRIPTION);
        this.queryBuilder = queryBuilder;
    }


    /**
     * If this handler can process the command line option, then print out QuoteServices registered
     * via the ServiceLoader.
     *
     * @param cmdLine command line parameters received by end user
     * @return true if this handler took action based on the command line parameters
     */
    @Override
    public boolean apply(CommandLine cmdLine) {
        boolean canProcess = accepts(cmdLine);
        if (canProcess) {
            System.out.println("Discovered services: ");
            queryBuilder.getSupportedQuoteServices()
                    .stream()
                    .map(s -> "  " + s.getServiceName())
                    .forEach(System.out::println)
                    ;
        }
        return canProcess;
    }


}
