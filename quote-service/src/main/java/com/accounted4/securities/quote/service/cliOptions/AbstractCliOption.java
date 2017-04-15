package com.accounted4.securities.quote.service.cliOptions;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 *
 * @author gheinze
 */
public abstract class AbstractCliOption extends Option {

    public AbstractCliOption(String opt, String description) throws IllegalArgumentException {
        super(opt, description);
    }

    public abstract boolean apply(CommandLine cmdLine);


    public boolean accepts(CommandLine cmdLine) {
        return cmdLine.hasOption(getOpt());
    }


}
