package com.accounted4.securities.quote.service.cliOptions;

import com.accounted4.securities.quote.api.QueryField;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;


/**
 * A command line option implementation for showing the attributes that can be used for querying
 * the quote service.
 *
 * @author gheinze
 */
public class ShowAttributesOption extends AbstractCliOption {

    private static final String OPTION_FIELD = "showAttributes";
    private static final String OPTION_DESCRIPTION = "list attributes that may be retrieved";

    public ShowAttributesOption() {
        super(OPTION_FIELD, OPTION_DESCRIPTION);
    }


    /**
     * If this handler can process the command line option, then print out version information.
     *
     * @param cmdLine command line parameters received by end user
     * @return true if this handler took action based on the command line parameters
     */
    @Override
    public boolean apply(CommandLine cmdLine) {
        boolean canProcess = accepts(cmdLine);
        if (canProcess) {
            System.out.println("Supported query attributes: ");
            Arrays.stream(QueryField.values())
                    .map(f -> "  " + f)
                    .forEach(System.out::println)
                    ;
        }
        return canProcess;
    }


}
