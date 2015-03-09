package org.isk.plume.exception;

public class MappedCodePointsException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public MappedCodePointsException(final String message) {
    super(message);
  }
}
