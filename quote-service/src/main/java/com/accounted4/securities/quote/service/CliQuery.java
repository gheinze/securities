package com.accounted4.securities.quote.service;

import com.accounted4.securities.quote.api.QueryField;
import com.accounted4.securities.quote.api.QuoteService;
import com.accounted4.securities.quote.api.QuoteServiceException;
import com.accounted4.securities.quote.service.cliOptions.AbstractCliOption;
import com.accounted4.securities.quote.service.cliOptions.HelpOption;
import com.accounted4.securities.quote.service.cliOptions.ShowAttributesOption;
import com.accounted4.securities.quote.service.cliOptions.ShowServicesOption;
import com.accounted4.securities.quote.service.cliOptions.VersionOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;



/**
 * Command Line Interface utility for interactive quick quotes.
 *
 * @author Glenn Heinze <glenn@gheinze.com>
 */
public class CliQuery {


    List<AbstractCliOption> metaServiceCommandLineOptions;

    private final Option serviceOption = Option.builder("service")
            .desc("use the named service for obtaining quote.")
            .hasArg()
            .argName("serviceName")
            .build()
            ;

    private final Option symbolsOption = Option.builder("symbols")
            .desc("comma separated list of ticker symbols to query for (example: ORCL,BMO.TO")
            .hasArg()
            .argName("tickerSymbols")
            .build()
            ;

    private final Option attributesOption = Option.builder("attributes")
            .desc("comma separated list of attributes to query for (example: lastTradePrice,dividendPerShare)")
            .hasArg()
            .argName("queryAttributes")
            .build()
            ;



    private final HelpFormatter formatter = new HelpFormatter();
    private final CommandLineParser parser = new GnuParser();
    private final QueryBuilder queryBuilder = new QueryBuilder();


    private final Options options;


    public CliQuery() {

        this.options = new Options();

        // Meta commandline options form an OptionGroup of mutually exclusive command line options
        metaServiceCommandLineOptions = new ArrayList<>();
        metaServiceCommandLineOptions.add(new HelpOption(options));
        metaServiceCommandLineOptions.add(new VersionOption());
        metaServiceCommandLineOptions.add(new ShowServicesOption(queryBuilder));
        metaServiceCommandLineOptions.add(new ShowAttributesOption());

        options.addOptionGroup(createOptionGroup());

        // Service query options should all be on the commandline in order to query the quote service
        options.addOption(serviceOption);
        options.addOption(symbolsOption);
        options.addOption(attributesOption);

    }


    private OptionGroup createOptionGroup() {
        OptionGroup group = new OptionGroup();
        metaServiceCommandLineOptions
                .stream()
                .forEach(o -> group.addOption(o))
                ;
        return group;
    }



    public void processCommandLine(String[] args) {

        CommandLine cmdLine = parseCommandLine(args);

        Optional<AbstractCliOption> optionHandler = metaServiceCommandLineOptions
                .stream()
                .filter(o -> o.apply(cmdLine))
                .findFirst()
                ;

        if (!optionHandler.isPresent()) {
            executeQuery(cmdLine);
        }

    }



    private CommandLine parseCommandLine(String[] args) {
        try {
            return parser.parse(options, args);
        } catch(ParseException pe) {
            System.out.println(pe.getMessage());
            throw new QuoteServiceException(pe);
        }
    }



    private void executeQuery(CommandLine cmdLine) throws QuoteServiceException {

        String tickerItems = cmdLine.getOptionValue(symbolsOption.getOpt());

        List<String> enteredSymbols = Arrays.stream(tickerItems.split(","))
                .filter(s -> null != s && !s.isEmpty())
                .map(s -> s.trim())
                .collect(Collectors.toList())
                ;

        ArrayList<QueryField> attrList = extractQueryFieldsFromCmdLine(cmdLine);

        QuoteService quoteService = findRequestedService(cmdLine);

        Map<String, EnumMap<QueryField, String>> result = quoteService.executeQuery(enteredSymbols, attrList);

        dumpQuoteServiceResponse(result);

    }


    private static final String DEFAULT_QUERY_FIELD = QueryField.lastTradePrice.toString();

    private ArrayList<QueryField> extractQueryFieldsFromCmdLine(CommandLine cmdLine) {

        ArrayList<QueryField> attrList = new ArrayList<>();

        String queryFields = cmdLine.getOptionValue(attributesOption.getOpt(), DEFAULT_QUERY_FIELD);

        for (String s : queryFields.split(",")) {
            try {
                attrList.add(QueryField.valueOf(s));
            } catch (IllegalArgumentException iae) {
                System.out.println("Ignoring unrecognized attribute: " + s);
            }
        }

        return attrList;
    }


    private void dumpQuoteServiceResponse(Map<String, EnumMap<QueryField, String>> result) {

        result.entrySet()
                .stream()
                .map((line) -> {
                    System.out.println();
                    System.out.print("symbol=\"" + line.getKey() + "\"");
                    return line;
                })
                .forEach((line) -> {
                    line.getValue().entrySet().stream()
                    .forEach((attr) -> {
                        System.out.print(", " + attr.getKey().toString() + "=\"" + attr.getValue() + "\"");
            });
        });

        System.out.println("\n");

    }


    private static final String DEFAULT_QUUOTE_SERVICE = "Yahoo";

    private QuoteService findRequestedService(CommandLine cmdLine) throws QuoteServiceException {

        String requestedService = cmdLine.getOptionValue(serviceOption.getOpt(), DEFAULT_QUUOTE_SERVICE);

        return queryBuilder.getSupportedQuoteServices()
                .stream()
                .filter(s -> s.getServiceName().equalsIgnoreCase(requestedService))
                .findAny()
                .orElseThrow(() -> new QuoteServiceException(
                        String.format(
                                "Service %s was not discovered. Show available services with -showServices or check classpath?",
                                requestedService
                )));

    }



    /* ---------------------------
     * Interactive
     *     -showServices
     *     -showAtributes
     *     -service <serviceName> -symbols <symbol>[,<symbol>] -attributes <attr>[,<attr>]
     *     -help
     * ---------------------------
     */
    public static void main(String[] args) {

        CliQuery query = new CliQuery();

        try {
//            String[] x = {"-symbols=bmo.to"};
//            query.processCommandLine(x);
            query.processCommandLine(args);
        } catch(QuoteServiceException qse) {
        }

    }


}

