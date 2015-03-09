package org.isk.plume.unicode;

import java.util.Arrays;

import org.isk.plume.exception.MappedCodePointsException;
import org.isk.plume.unicode.CodePoints.Charset;
import org.isk.plume.unicode.CodePoints.Converter;
import org.isk.plume.unicode.CodePoints.UtfToCodePoint;

/**
 * <p>
 * A MappedCodePoints offers the ability to map a array of code units (array of byte) or of code points (array of int)
 * as a text file with a line number and a column number for each code point.
 * <p>
 * After the instantiation a pre-processing is done to standardize the line break, represented by carriage return (CR),
 * a line feed (LF) or a CRLF, by replacing any CR and CRLF by a LF.
 * <p>
 * Even if a buffer of 4kb (by increment) is used internally to add only the right characters (without CR and CRLF) be
 * aware that at the end everything ends up in memory.
 * <p>
 * Moreover the MappedCodePoints class has a {@link MappedCodePoints#debug(String, int)} that display the line
 * containing a precise position, where this position is highlighted, and a given number of lines before and after.
 *
 */
public class MappedCodePoints {

  /**
   * Size of the buffer.
   */
  final private static int DEFAULT_BUFFER_SIZE = 1024 * 4;

  /**
   * Number of lines before the line in error to be displayed.
   */
  final public int debugLinesBefore;

  /**
   * Number of lines after the line in error to be displayed.
   */
  final public int debugLinesAfter;

  /**
   * End of Stream (-1)
   */
  final public static int EOS = -1;

  /**
   * Line Feed.
   */
  final public static int LF = 0x0A;

  /**
   * Carriage Return.
   */
  final public static int CR = 0x0D;

  /**
   * Name of the file to be mapped if any.
   */
  final public String filename;

  /**
   * The file as an array of ints.
   */
  private int[] stream = new int[MappedCodePoints.DEFAULT_BUFFER_SIZE];

  /**
   * The line of a character in the stream.
   */
  private int[] lines = new int[MappedCodePoints.DEFAULT_BUFFER_SIZE];

  /**
   * The column of a character in the stream.
   */
  private int[] columns = new int[MappedCodePoints.DEFAULT_BUFFER_SIZE];

  /**
   * Index of the End Of Stream.
   */
  private int eosIndex = 0;

  /**
   * Instantiates a new <code>MappedCodePoints</code> from an {@link UnicodeInputStream} containing code points of a
   * specified encoding.
   * 
   * @param charset
   *          is the encoding of the code units.
   * @param inputStream
   *          is an {@link UnicodeInputStream} containing code units.
   */
  public MappedCodePoints( //
      final Charset charset, //
      final UnicodeInputStream inputStream) {
    this(null, charset, inputStream, 1, 1);
  }

  /**
   * Instantiates a new <code>MappedCodePoints</code> from an {@link UnicodeInputStream} containing code points of a
   * specified encoding.
   * 
   * @param filename
   *          is the name of the file to be mapped.
   * @param charset
   *          is the encoding of the code units.
   * @param inputStream
   *          is an {@link UnicodeInputStream} containing code units.
   */
  public MappedCodePoints( //
      final String filename, //
      final Charset charset, //
      final UnicodeInputStream inputStream) {
    this(filename, charset, inputStream, 1, 1);
  }

  /**
   * Instantiates a new <code>MappedCodePoints</code> from an {@link UnicodeInputStream} containing code points of a
   * specified encoding.
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
  public MappedCodePoints( //
      final String filename, //
      final Charset charset, //
      final UnicodeInputStream inputStream, //
      final int debugLinesBefore, //
      final int debugLinesAfter) {
    this(filename, new UncodeInputStreamBuilder(filename, charset, inputStream), debugLinesBefore, debugLinesAfter);
  }

  /**
   * Instantiates a new <code>MappedCodePoints</code> from an array of code points.
   * 
   * @param codePoints
   *          is an array of code points.
   */
  public MappedCodePoints(final int[] codePoints) {
    this(null, codePoints, 1, 1);
  }

  /**
   * Instantiates a new <code>MappedCodePoints</code> from an array of code points.
   * 
   * @param codePoints
   *          is an array of code points.
   * @param debugLinesBefore
   *          is the number of lines before the line in error to be displayed
   * @param debugLinesAfter
   *          is the number of lines after the line in error to be displayed
   */
  public MappedCodePoints(final int[] codePoints, //
      final int debugLinesBefore, //
      final int debugLinesAfter) {
    this(null, codePoints, debugLinesBefore, debugLinesAfter);
  }

  /**
   * Instantiates a new <code>MappedCodePoints</code> from an array of code points.
   * 
   * @param filename
   *          is the name of the file to be mapped.
   * @param codePoints
   *          is an array of code points.
   * @param debugLinesBefore
   *          is the number of lines before the line in error to be displayed
   * @param debugLinesAfter
   *          is the number of lines after the line in error to be displayed
   */
  public MappedCodePoints(final String filename, //
      final int[] codePoints, //
      final int debugLinesBefore, //
      final int debugLinesAfter) {
    this(filename, new CodePointsArrayBuilder(codePoints), debugLinesBefore, debugLinesAfter);
  }

  /**
   * Instantiates a new <code>MappedCodePoints</code> from a {@link MappedStreamBuilder}, an object allowing us to
   * transparently iterate over an array of int or an {@link UnicodeInputStream}.
   * 
   * @param filename
   *          is the name of the file to be mapped.
   * @param builder
   *          is a {@link MappedStreamBuilder}
   * @param debugLinesBefore
   *          is the number of lines before the line in error to be displayed.
   * @param debugLinesAfter
   *          is the number of lines after the line in error to be displayed.
   */
  private MappedCodePoints(final String filename, //
      final MappedStreamBuilder builder, //
      final int debugLinesBefore, //
      final int debugLinesAfter) {
    this.filename = filename;
    this.debugLinesBefore = debugLinesBefore > 0 ? debugLinesBefore : 1;
    this.debugLinesAfter = debugLinesAfter > 0 ? debugLinesAfter : 1;
    this.init(builder);
  }

  /**
   * <p>
   * Decodes the code units contained by an {@link UnicodeInputStream} from a specified encoding to code points (or
   * processes the code points directly) and sets the line and the column of this code point in a virtual file.
   * <p>
   * Moreover, CR and CRLF are both replaced by LF code point (U+000A).
   *
   * @param builder
   *          is a {@link MappedStreamBuilder} pointing to an array of code units or an array of code points.
   * @throws MappedCodePointsException
   *           if the stream is empty.
   */
  private void init(final MappedStreamBuilder builder) {
    // Reads the first character to check if the stream is empty or not
    if (!builder.hasNext()) {
      if (this.filename == null) {
        throw new MappedCodePointsException("This stream is empty.");
      } else {
        throw new MappedCodePointsException("This stream is empty (" + this.filename + ").");
      }
    }

    // Before stream
    this.stream[0] = MappedCodePoints.LF;
    this.columns[0] = 0;
    this.lines[0] = 0;

    int currentChar = 0;
    int previousChar = 0;

    // Reads characters
    int index = 1; // 0 is for [before stream]
    while (builder.hasNext()) {
      currentChar = builder.nextCodePoint();

      if (previousChar == MappedCodePoints.CR && currentChar == MappedCodePoints.LF) {
        continue;
      }

      final int streamSize = this.stream.length;
      if (index >= streamSize) {
        this.stream = Arrays.copyOf(this.stream, MappedCodePoints.DEFAULT_BUFFER_SIZE + streamSize);
        this.columns = Arrays.copyOf(this.columns, MappedCodePoints.DEFAULT_BUFFER_SIZE + streamSize);
        this.lines = Arrays.copyOf(this.lines, MappedCodePoints.DEFAULT_BUFFER_SIZE + streamSize);
      }

      // CR replaced by LF
      if (currentChar == MappedCodePoints.CR) {
        this.stream[index] = MappedCodePoints.LF;
      }
      // Other characters (Skips CRLF)
      else {
        this.stream[index] = currentChar;
      }

      // Compute lines and columns
      this.computeLinesAndColumns(index);

      previousChar = currentChar;
      index++;
    }

    this.setEosIndex(index);
    this.stream[this.eosIndex] = MappedCodePoints.EOS;
    this.computeLinesAndColumns(this.eosIndex);
  }

  /**
   * Removes all new lines at the end of the stream.
   * 
   * @param index
   *          is the last index used to map the stream.
   */
  private void setEosIndex(final int index) {
    int i = 0;
    for (i = index - 1; i >= 0; i--) {
      if (this.stream[i] != MappedCodePoints.LF) {
        break;
      }

      this.stream[i] = 0;
      this.lines[i] = 0;
      this.columns[i] = 0;
    }

    this.eosIndex = i + 1;
  }

  /**
   * Sets the line and column for a given position.
   * 
   * @param position
   *          is the position from which we want to set the line and the column.
   */
  private void computeLinesAndColumns(final int position) {
    final int previousIndex = position - 1;
    if (this.stream[previousIndex] == MappedCodePoints.LF) {
      this.lines[position] = this.lines[previousIndex] + 1;
      this.columns[position] = 1;
    } else {
      this.lines[position] = this.lines[previousIndex];
      this.columns[position] = this.columns[previousIndex] + 1;
    }
  }

  /**
   * Returns the name of the file mapped.
   * 
   * @return the name of the file mapped.
   */
  public String getFilename() {
    return this.filename;
  }

  /**
   * Returns the index of the End of the Stream.
   * 
   * @return the index of the End of the Stream.
   */
  public int getEosIndex() {
    return this.eosIndex;
  }

  /**
   * Returns the line of a code point in the buffer at a given position.
   * 
   * @param position
   *          is the position of a code point in the buffer.
   * @return the line of a code point in the buffer at a given position.
   * @throws MappedCodePointsException
   *           if the index is out of range (<tt>position &lt; 0 || position &gt; getEosIndex()</tt>).
   */
  public int getLine(final int position) {
    if (position < 0 || position > this.eosIndex) {
      throw new MappedCodePointsException("Index outside of range (" + position
          + "). It should be greater than or equal to 0 and less than or equal to the end of stream.");
    }
    return this.lines[position];
  }

  /**
   * Returns the column of a code point in the buffer at a given position.
   * 
   * @param position
   *          is the position of a code point in the buffer.
   * @return the column of a code point in the buffer at a given position.
   * @throws MappedCodePointsException
   *           if the index is out of range (<tt>position &lt; 0 || position &gt; getEosIndex()</tt>).
   */
  public int getColumn(final int position) {
    if (position < 0 || position > this.eosIndex) {
      throw new MappedCodePointsException("Index outside of range (" + position
          + "). It should be greater than or equal to 0 and less than or equal to the end of stream.");
    }
    return this.columns[position];
  }

  /**
   * <p>
   * Returns a code point at a given position in the buffer.
   * <p>
   * <u>Important note</u>: the first code point will be at position 1, 0 being reserved for debugging purposes.
   * 
   * @param position
   *          is the position of a code point in the buffer.
   * @return the code point at a given position in the buffer.
   * @throws MappedCodePointsException
   *           if the index is out of range (<tt>position &lt; 0 || position &gt; getEosIndex()</tt>).
   */
  public int codePointAt(final int position) {
    if (position < 0 || position > this.eosIndex) {
      throw new MappedCodePointsException("Index outside of range (" + position
          + "). It should be greater than or equal to 0 and less than or equal to the end of stream.");
    }
    return this.stream[position];
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
    if (position < 0 || position > this.eosIndex) {
      throw new MappedCodePointsException("Index outside of range (" + position
          + "). It should be greater than or equal to 0 and less than or equal to the end of stream.");
    }

    final int line = this.getLine(position);
    final int column = this.getColumn(position);
    final int startIndex = this.getDebugStartPosition(line, position);
    final int lastLineToLog = line + this.debugLinesAfter;

    final CodePoints debugMessage = new CodePoints();

    if (this.filename != null) {
      debugMessage.add("In file " + this.filename + "\n");
    }

    // Message
    if (msg != null && !msg.isEmpty()) {
      debugMessage.add(msg + "\n");
    }

    // Lines before error and line with error
    int i = startIndex;
    while (i < this.eosIndex && this.lines[i] <= line) {
      debugMessage.add(this.stream[i++]);
    }

    // Dummy line with caret at error position
    // Add newline in case of last line of the stream
    if (i > 0 && this.stream[i - 1] != MappedCodePoints.LF) {
      debugMessage.add(MappedCodePoints.LF);
    }

    int j = 1;
    while (j++ < column) {
      debugMessage.add(' ');
    }

    debugMessage.add("^__ Line ");
    debugMessage.add(String.valueOf(line));
    debugMessage.add('\n');

    // Lines after error
    while (i < this.eosIndex && this.lines[i] <= lastLineToLog) {
      debugMessage.add(this.stream[i++]);
    }

    String s = debugMessage.toString();
    if (s.endsWith("\n")) {
      s = s.substring(0, s.length() - 1);
    }

    return s;
  }

  /**
   * Returns the position of the first line to be displayed for debugging purpose.
   * 
   * @param currentLine
   *          is the line of the error.
   * @param position
   *          if the position of the code point in the buffer in error.
   * @return the position of the first line to be displayed.
   */
  private int getDebugStartPosition(final int currentLine, final int position) {
    if (currentLine <= this.debugLinesBefore + 1) {
      return 1;
    } else {
      final int expectedLine = currentLine - this.debugLinesBefore;
      int i = position - 1;

      while (i >= 0 && this.lines[i] >= expectedLine) {
        i--;
      }

      return i + 1;
    }
  }

  private static interface MappedStreamBuilder {
    boolean hasNext();

    int nextCodePoint();
  }

  private static class UncodeInputStreamBuilder implements MappedStreamBuilder {
    final private UtfToCodePoint utfToCodePoint;
    final private UnicodeInputStream inputStream;

    public UncodeInputStreamBuilder(final String filename, final Charset charset, final UnicodeInputStream inputStream) {

      if (inputStream == null) {
        if (filename == null) {
          throw new MappedCodePointsException("This stream is null.");
        } else {
          throw new MappedCodePointsException("This stream is null (" + filename + ").");
        }
      }

      this.utfToCodePoint = Converter.findUtfToCodePoint(charset);
      this.inputStream = inputStream;
    }

    @Override
    public boolean hasNext() {
      return this.inputStream.hasNext();
    }

    @Override
    public int nextCodePoint() {
      return this.utfToCodePoint.toCodePoint(this.inputStream);
    }
  }

  private static class CodePointsArrayBuilder implements MappedStreamBuilder {
    final private int[] codePoints;
    private int index = 0;

    public CodePointsArrayBuilder(final int[] codePoints) {
      super();
      this.codePoints = codePoints;
    }

    @Override
    public boolean hasNext() {
      return this.index < this.codePoints.length;
    }

    @Override
    public int nextCodePoint() {
      return this.codePoints[this.index++];
    }
  }
}
