package com.accounted4.securities.quote.service.cliOptions;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;


/**
 * A command line option implementation for showing "help" functionality for the stock quote applications.
 *
 * @author gheinze
 */
public class HelpOption extends AbstractCliOption {

    private final Options options;
    private final HelpFormatter formatter;

    private static final String USAGE_EXAMPLES =
            "\n  1. stockquote.sh -service Yahoo -symbols ORCL,SLF.TO -attributes lastTradePrice,dividendPerShare" +
            "\n  2. stockquote.sh -showServices" +
            "\n  4. stockquote.sh -showAttributes" +
            "\n\n";
    private static final String OPTION_FIELD = "help";
    private static final String OPTION_DESCRIPTION = "print this message";

    public HelpOption(final Options options) {
        super(OPTION_FIELD, OPTION_DESCRIPTION);
        this.options = options;
        formatter = new HelpFormatter();
    }


    /**
     * If this handler can process the command line option, then print out a help message.
     *
     * @param cmdLine command line parameters received by end user
     * @return true if this handler took action based on the command line parameters
     */
    @Override
    public boolean apply(CommandLine cmdLine) {
        boolean canProcess = accepts(cmdLine);
        if (canProcess) {
            formatter.printHelp(USAGE_EXAMPLES, options);
        }
        return canProcess;
    }


    @Override
    public boolean accepts(CommandLine cmdLine) {
        return cmdLine.getOptions().length <= 0 || super.accepts(cmdLine);
    }

}
