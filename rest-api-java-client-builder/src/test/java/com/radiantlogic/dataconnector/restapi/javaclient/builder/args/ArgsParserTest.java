package com.radiantlogic.dataconnector.restapi.javaclient.builder.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.radiantlogic.dataconnector.restapi.javaclient.builder.properties.Props;
import org.junit.jupiter.api.Test;

public class ArgsParserTest {
  final Props props = new Props("my-artifact", "1.0.0");
  private final ArgsParser argsParser = new ArgsParser(props);

  @Test
  void itShowsHelpMenu() {
    final String[] args = {"-h"};
    final Args parsedArgs = argsParser.parse(args);
    final Args expectedArgs = new Args(ProgramArgStatus.EXIT, "", "");
    assertThat(parsedArgs).isEqualTo(expectedArgs);
  }

  @Test
  void itParsesWithDefaults() {
    final String path = "http://localhost:8080/openapi.json";
    final String[] args = {"-p=%s".formatted(path)};
    final Args parsedArgs = argsParser.parse(args);
    final Args expectedArgs = new Args(ProgramArgStatus.PROCEED, path, ArgsParser.DEFAULT_GROUP_ID);
    assertThat(parsedArgs).isEqualTo(expectedArgs);
  }

  @Test
  void itParsesWithAllArgsProvided() {
    final String path = "http://localhost:8080/openapi.json";
    final String groupId = "org.something";
    final String[] args = {"-p=%s".formatted(path), "-g=%s".formatted(groupId)};
    final Args parsedArgs = argsParser.parse(args);
    final Args expectedArgs = new Args(ProgramArgStatus.PROCEED, path, groupId);
    assertThat(parsedArgs).isEqualTo(expectedArgs);
  }

  @Test
  void itParsesNoArgs() {
    assertThatThrownBy(() -> argsParser.parse(new String[] {}))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Missing required arguments. Please run with -h for more information.");
  }

  @Test
  void itParsesWithMissingOpenapiPathArg() {
    final String groupId = "org.something";
    final String[] args = {"-g=%s".formatted(groupId)};
    assertThatThrownBy(() -> argsParser.parse(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Missing required OpenAPI path argument. Please use the -h option to see usage instructions.");
  }
}
