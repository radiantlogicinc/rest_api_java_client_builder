package com.radiantlogic.dataconnector.restapi.javaclient.builder.args;

import com.radiantlogic.dataconnector.restapi.javaclient.builder.properties.Props;
import java.util.Arrays;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

/** Parse the arguments supplied to this program using the commons-cli library. */
@Slf4j
@RequiredArgsConstructor
public class ArgsParser {
  public static final String DEFAULT_GROUP_ID = "com.radiantlogic.custom.dataconnector";

  private static final Option PATH_OPTION =
      Option.builder("p")
          .argName("OpenAPI Path")
          .longOpt("path")
          .desc("The path to the OpenAPI specification. Either a file path or URL.")
          .hasArg()
          .valueSeparator('=')
          .build();
  private static final Option GROUP_ID_OPTION =
      Option.builder("g")
          .argName("Group ID")
          .longOpt("groupId")
          .desc(
              "The groupId to use for the generated artifact. If not provided, a default groupId will be set")
          .hasArg()
          .valueSeparator('=')
          .build();
  private static final Option HELP_OPTION =
      Option.builder("h")
          .argName("Help")
          .longOpt("help")
          .desc("Print the help information for this CLI")
          .build();
  private static final Options OPTIONS = new Options();

  static {
    OPTIONS.addOption(PATH_OPTION);
    OPTIONS.addOption(HELP_OPTION);
    OPTIONS.addOption(GROUP_ID_OPTION);
  }

  @NonNull private final Props props;

  public Args parse(final String[] args) {
    if (args.length < 1) {
      throw new IllegalArgumentException(
          "Missing required arguments. Please run with -h for more information.");
    }

    log.debug("Parsing cli arguments {}", Arrays.toString(args));

    try {
      final CommandLineParser parser = new DefaultParser();
      final CommandLine commandLine = parser.parse(OPTIONS, args);

      if (commandLine.hasOption(HELP_OPTION.getOpt())) {
        return handleHelp();
      }

      final String openapiPath = commandLine.getOptionValue(PATH_OPTION.getOpt(), "");
      if (StringUtils.isBlank(openapiPath)) {
        throw new IllegalArgumentException(
            "Missing required OpenAPI path argument. Please use the -h option to see usage instructions.");
      }

      final String groupId = commandLine.getOptionValue(GROUP_ID_OPTION.getOpt(), DEFAULT_GROUP_ID);
      return new Args(ProgramArgStatus.PROCEED, openapiPath, groupId);
    } catch (final ParseException ex) {
      throw new IllegalStateException(
          "Failed to parse command line arguments, cannot proceed.", ex);
    }
  }

  private Args handleHelp() {
    final HelpFormatter helpFormatter = new HelpFormatter();

    final String header =
        """

            NOTE: arguments with values are separated by an '=' sign, ie -p=http://localhost:8080/openapi.json

            """
            .stripIndent();

    helpFormatter.printHelp(
        "%s %s".formatted(props.artifactId(), props.version()), header, OPTIONS, "Footer", true);
    return new Args(ProgramArgStatus.EXIT, "", "");
  }
}
