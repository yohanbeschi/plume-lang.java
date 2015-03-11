package org.isk.plume.inputstream;

import java.io.Closeable;

public interface QuietAutoCloseable extends Closeable {
  @Override
  void close() throws QuietAutoCloseableException;
}
