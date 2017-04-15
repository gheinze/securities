package com.accounted4.securities.quote.service.cliOptions;

import org.apache.commons.cli.CommandLine;


/**
 * A command line option implementation for showing "version" info of this cli.
 *
 * @author gheinze
 */
public class VersionOption extends AbstractCliOption {

    private static final String VERSION = "0.3";

    private static final String OPTION_FIELD = "version";
    private static final String OPTION_DESCRIPTION = "print the version information and exit";

    public VersionOption() {
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
            System.out.println("version: " + VERSION);
        }
        return canProcess;
    }


}
