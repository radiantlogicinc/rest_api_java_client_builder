package com.radiantlogic.dataconnector.restapi.javaclient.builder.exceptions;

// TODO refactor all exceptions to be a sub-type of this
public class JavaClientBuilderException extends RuntimeException {
  public JavaClientBuilderException() {}

  public JavaClientBuilderException(final String message) {
    super(message);
  }

  public JavaClientBuilderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public JavaClientBuilderException(final Throwable cause) {
    super(cause);
  }
}
