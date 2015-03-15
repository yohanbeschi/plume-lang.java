package org.isk.plume.parser;

import java.util.ArrayDeque;
import java.util.Deque;

import org.isk.plume.parser.exception.ReaderException;
import org.isk.plume.unicode.CodePoints.Charset;
import org.isk.plume.unicode.MappedCodePoints;
import org.isk.plume.unicode.UnicodeInputStream;
import org.isk.plume.unicode.exception.MappedCodePointsException;

/**
 * <p>
 * A Reader offers the ability to read a {@link MappedCodePoints} one code point at a time, forward or backward, to save
 * the position of the next code point and to use all useful methods from {@link MappedCodePoints}.
 */
public class Reader {
  /**
   * The stream to go through.
   */
  final private MappedCodePoints stream;

  /**
   * The current position in the stream as an array of code points.
   */
  private int position = 0;

  /**
   * Saved positions.<br>
   * For mark and reset.
   */
  final private Deque<Integer> mark = new ArrayDeque<>();

  /**
   * Instantiates a new <code>Reader</code> from an {@link UnicodeInputStream} containing code units of a specified
   * encoding.
   * 
   * @param filename
   *          is the name of the file to be mapped.
   * @param charset
   *          is the encoding of the code units.
   * @param inputStream
   *          is an {@link UnicodeInputStream} containing code units.
   */
  public Reader( //
      final String filename, //
      final Charset charset, //
      final UnicodeInputStream inputStream) {
    this.stream = new MappedCodePoints(filename, charset, inputStream);
  }

  /**
   * Instantiates a new <code>Reader</code> from an {@link UnicodeInputStream} containing code units of a specified
   * encoding.
   * 
   * @param filename
   *          is the name of the file to be mapped.
   * @param charset
   *          is the encoding of the code units.
   * @param inputStream
   *          is an {@link UnicodeInputStream} containing code units.
   * @param debugLinesBefore
   *          is the number of lines before the line in error to be displayed
   * @param debugLinesAfter
   *          is the number of lines after the line in error to be displayed
   * @throws MappedCodePointsException
   *           if the stream is null or empty.
   */
  public Reader( //
      final String filename, //
      final Charset charset, //
      final UnicodeInputStream inputStream, //
      final int debugLinesBefore, //
      final int debugLinesAfter) {
    this.stream = new MappedCodePoints(filename, charset, inputStream, debugLinesBefore, debugLinesAfter);
  }

  /**
   * Instantiates a new <code>Reader</code> from an {@link MappedCodePoints} containing code points.
   * 
   * @param mappedStream
   *          is a {@link MappedCodePoints}
   */
  public Reader(final MappedCodePoints mappedStream) {
    this.stream = mappedStream;
  }

  /**
   * Reads the next code point and moves to the next one.
   * 
   * @return the next code point.
   * @throws ReaderException
   *           if the end of the stream has been reached.
   */
  public int read() {
    if (++this.position > this.stream.getEosIndex()) {
      this.position--;
      throw new ReaderException(this.debug("End of stream reached. No more character to read."));
    } else {
      return this.stream.codePointAt(this.position);
    }
  }

  /**
   * Unreads the previous code point read.
   * 
   * @throws ReaderException
   *           if the beginning of the stream has been reached.
   */
  public void unread() {
    if (this.position <= 0) {
      throw new ReaderException(this.debug("Nothing to unread."));
    } else {
      this.position--;
    }
  }

  /**
   * Unreads the previous code point read like {link #unread()} but without throwing an exception if the beginning of
   * the stream has been read.
   */
  public void unreadNotBeforeStart() {
    if (this.position > 1) {
      this.position--;
    }
  }

  /**
   * <p>
   * Returns the next code point without moving to the next one.
   * <p>
   * Calling this method multiple times in a row will always return the same code point.
   * 
   * @return the next code point.
   * @throws ReaderException
   *           if the end of the stream has been reached.
   */
  public int peek() {
    if (this.position + 1 > this.stream.getEosIndex()) {
      throw new ReaderException(this.debug("End of file reached. No more character to read."));
    } else {
      return this.stream.codePointAt(this.position + 1);
    }
  }

  /**
   * <p>
   * Saves the current position.
   * 
   * <p>
   * Calling {@link #reset()} will reset the current position to this one.
   */
  public void mark() {
    this.mark.push(this.position);
  }

  /**
   * Sets the current position to the one previously saved.
   */
  public void reset() {
    if (!this.mark.isEmpty()) {
      this.position = this.mark.pop();
    } else {
      throw new ReaderException(this.debug("No position has been saved. You must call mark() before."));
    }
  }

  /**
   * Sets the current position to the first one saved.
   */
  public void fullReset() {
    if (!this.mark.isEmpty()) {
      this.position = this.mark.getLast();
      this.mark.clear();
    }
  }

  /**
   * Removes all positions saved.
   */
  public void clearMark() {
    this.mark.clear();
  }

  /**
   * Is there any position saved ?
   * 
   * @return <code>true</code> is there is no position saved, otherwise <code>false</code>.
   */
  public boolean isMarkEmpty() {
    return this.mark.isEmpty();
  }

  /**
   * Returns the line of a code point at the current position.
   * 
   * @return the line of a code point at the current position.
   */
  public int getLine() {
    return this.stream.getLine(this.position);
  }

  /**
   * Returns the column of a code point at the current position.
   * 
   * @return the column of a code point at the current position.
   */
  public int getColumn() {
    return this.stream.getColumn(this.position);
  }

  /**
   * Returns the name of the file read.
   * 
   * @return the name of the file read.
   */
  public String getFilename() {
    return this.stream.getFilename();
  }

  /**
   * <p>
   * Returns a debug message, showing:
   * <ul>
   * <li>the name of the file mapped by this instance, if any
   * <li>a custom text, if any
   * <li>a number of lines before the line in error
   * <li>a visual position of the code point in error and its line
   * <li>a number of lines a the line in error
   * </ul>
   * <p>
   * Let's take the following text file named 'myTextFile.txt':
   * 
   * <pre>
   * line number 1
   * line number 2
   * lane number 3
   * line number 4
   * line number 5
   * </pre>
   * 
   * <p>
   * If we call this method with msg = "Expected: 'i'" and position = 16 the returned value would be (if we didn't
   * change the default values of the number of lines to be shown before and after the error):
   * 
   * <pre>
   * Expected: 'i'
   * line number 2
   * lane number 3
   *  ^__ Line 3
   * line number 4
   * </pre>
   * 
   * @param msg
   *          is a custom text displayed after the name of the file if any. Can be <code>null</code> or empty.
   * @return a debug message.
   */
  public String debug(final String msg) {
    return this.stream.debug(msg, this.position);
  }

  /**
   * <p>
   * Returns a debug message, showing:
   * <ul>
   * <li>the name of the file mapped by this instance, if any
   * <li>a custom text, if any
   * <li>a number of lines before the line in error
   * <li>a visual position of the code point in error and its line
   * <li>a number of lines a the line in error
   * </ul>
   * <p>
   * Let's take the following text file named 'myTextFile.txt':
   * 
   * <pre>
   * line number 1
   * line number 2
   * lane number 3
   * line number 4
   * line number 5
   * </pre>
   * 
   * <p>
   * If we call this method with msg = "Expected: 'i'" and position = 16 the returned value would be (if we didn't
   * change the default values of the number of lines to be shown before and after the error):
   * 
   * <pre>
   * Expected: 'i'
   * line number 2
   * lane number 3
   *  ^__ Line 3
   * line number 4
   * </pre>
   * 
   * @param msg
   *          is a custom text displayed after the name of the file if any. Can be <code>null</code> or empty.
   * @param position
   *          is the position of the error.
   * @return a debug message.
   * @throws MappedCodePointsException
   *           if the index is out of range (<tt>position &lt; 0 || position &gt; getEosIndex()</tt>).
   */
  public String debug(final String msg, final int position) {
    return this.stream.debug(msg, position);
  }
}