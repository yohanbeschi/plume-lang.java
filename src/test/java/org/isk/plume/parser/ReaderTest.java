package org.isk.plume.parser;

import java.io.UnsupportedEncodingException;

import org.isk.plume.parser.exception.ReaderException;
import org.isk.plume.unicode.CodePoints.Charset;
import org.isk.plume.unicode.MappedCodePoints;
import org.isk.plume.unicode.UnicodeInputStream;
import org.isk.plume.unicode.UnicodeTestData;
import org.isk.plume.unicode.exception.MappedCodePointsException;
import org.isk.plume.unicode.exception.UnicodeException;
import org.junit.Assert;
import org.junit.Test;

public class ReaderTest {
  @Test(expected = MappedCodePointsException.class)
  public void read_nullstream() {
    new Reader(null, Charset.UTF8, null);
  }

  @Test(expected = MappedCodePointsException.class)
  public void read_nullstream_withfilename() {
    new Reader("file.txt", Charset.UTF8, null);
  }

  @Test(expected = UnicodeException.class)
  public void read_blankstream() throws UnsupportedEncodingException {
    try (final UnicodeInputStream inputStream = new UnicodeInputStream("".getBytes("utf-8"))) {
      new Reader(null, Charset.UTF8, inputStream);
    }
  }

  @Test(expected = UnicodeException.class)
  public void read_blankstream_withfilename() throws UnsupportedEncodingException {
    try (final UnicodeInputStream inputStream = new UnicodeInputStream("".getBytes("utf-8"))) {
      new Reader("file.txt", Charset.UTF8, inputStream);
    }
  }

  @Test
  public void read_beforestart() throws UnsupportedEncodingException {
    final String string = "a";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      try {
        reader.unread();
        Assert.fail();
      } catch (final ReaderException e) {
        Assert.assertEquals("Nothing to unread.\n" //
            + "^__ Line 0\n" //
            + "a", e.getMessage());
      }
    }
  }

  @Test
  public void read_afterEOS() throws UnsupportedEncodingException {
    final String string = "a";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      reader.read(); // a
      reader.read(); // eof

      try {
        reader.read(); // too far
        Assert.fail();
      } catch (final ReaderException e) {
        Assert.assertEquals("End of stream reached. No more character to read.\n" //
            + "a\n" //
            + " ^__ Line 1", e.getMessage());
      }
    }
  }

  @Test
  public void getLine_beforestart() throws UnsupportedEncodingException {
    final String string = "a";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      Assert.assertEquals(0, reader.getLine());
    }
  }

  @Test
  public void getColumn_beforestart() throws UnsupportedEncodingException {
    final String string = "a";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      Assert.assertEquals(0, reader.getColumn());
    }
  }

  @Test
  public void read_onechar_letter() throws UnsupportedEncodingException {
    final String string = "a";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      final int character = reader.read();

      Assert.assertEquals('a', character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
    }
  }

  @Test
  public void read_onechar_cr() throws UnsupportedEncodingException {
    final String string = "a\rb";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      reader.read();
      final int character = reader.read();

      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());
    }
  }

  @Test
  public void read_onechar_crlf() throws UnsupportedEncodingException {
    final String string = "a\r\nb";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      reader.read();
      final int character = reader.read();

      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());
    }
  }

  @Test
  public void read_onechar_insideBMP() throws UnsupportedEncodingException {
    final String string = "\u2695";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      final int character = reader.read();

      Assert.assertEquals(0x2695, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
    }
  }

  @Test
  public void read_onechar_outsideBMP() throws UnsupportedEncodingException {
    final String string = new String(new int[] { 0x10083 }, 0, 1); // "\u10083"; This does not work as expected

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      final int character = reader.read();

      Assert.assertEquals(0x10083, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
    }
  }

  @Test
  public void read_eof() throws UnsupportedEncodingException {
    final String string = "a";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      reader.read();
      final int character = reader.read();

      Assert.assertEquals(MappedCodePoints.EOS, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());
    }
  }

  @Test
  public void read_eof_afternewline() throws UnsupportedEncodingException {
    final String string = "a\na";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      reader.read();
      reader.read();
      reader.read();
      final int character = reader.read();

      Assert.assertEquals(MappedCodePoints.EOS, character);
      Assert.assertEquals(2, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());
    }
  }

  @Test
  public void read_oneline() throws UnsupportedEncodingException {
    final String string = "abc";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      Assert.assertEquals('a', reader.read());
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
      Assert.assertEquals('b', reader.read());
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());
      Assert.assertEquals('c', reader.read());
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(3, reader.getColumn());
    }
  }

  @Test
  public void read_multilines() throws UnsupportedEncodingException {
    final String string = "abc\r\rxyz";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      // Line 1
      Assert.assertEquals('a', reader.read());
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
      Assert.assertEquals('b', reader.read());
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());
      Assert.assertEquals('c', reader.read());
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(3, reader.getColumn());
      Assert.assertEquals(MappedCodePoints.LF, reader.read());
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(4, reader.getColumn());

      // Line 2
      Assert.assertEquals(MappedCodePoints.LF, reader.read());
      Assert.assertEquals(2, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());

      // Line 3
      Assert.assertEquals('x', reader.read());
      Assert.assertEquals(3, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
      Assert.assertEquals('y', reader.read());
      Assert.assertEquals(3, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());
      Assert.assertEquals('z', reader.read());
      Assert.assertEquals(3, reader.getLine());
      Assert.assertEquals(3, reader.getColumn());
    }
  }

  @Test
  public void unread_onechar() throws UnsupportedEncodingException {
    final String string = "a";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      // a
      int character = reader.read();
      Assert.assertEquals('a', character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());

      // START
      reader.unread();

      // a
      character = reader.read();
      Assert.assertEquals('a', character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
    }
  }

  @Test
  public void unread_twochars() throws UnsupportedEncodingException {
    final String string = "abc";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      // a
      reader.read();
      // b
      int character = reader.read();
      Assert.assertEquals('b', character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());

      // b -> a
      reader.unread();
      // a -> START
      reader.unread();

      // a
      character = reader.read();
      Assert.assertEquals('a', character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
    }
  }

  @Test
  public void unread_newline() throws UnsupportedEncodingException {
    final String string = "abc\nxyz";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      // a
      reader.read();
      // b
      reader.read();
      // c
      reader.read();
      // LF
      int character = reader.read();
      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(4, reader.getColumn());

      // LF -> c
      reader.unread();

      // \n
      character = reader.read();
      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(4, reader.getColumn());
    }
  }

  @Test
  public void unread_newline_pastit() throws UnsupportedEncodingException {
    final String string = "abc\nxyz";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      // a
      reader.read();
      // b
      reader.read();
      // c
      reader.read();
      // LF
      reader.read();
      // x
      int character = reader.read();
      Assert.assertEquals('x', character);
      Assert.assertEquals(2, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());

      // x -> LF
      reader.unread();
      // LF -> c
      reader.unread();

      // \n
      character = reader.read();
      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(4, reader.getColumn());
    }
  }

  @Test
  public void peek() throws UnsupportedEncodingException {
    final String string = "abc\nxyz";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      int character = reader.peek();
      Assert.assertEquals('a', character);

      character = reader.read();
      Assert.assertEquals('a', character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());
    }
  }

  @Test
  public void peek_aftereof() throws UnsupportedEncodingException {
    final String string = "a";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      reader.read(); // a
      reader.read(); // eof

      try {
        reader.peek(); // too far
        Assert.fail();
      } catch (final ReaderException e) {
        Assert.assertEquals("End of file reached. No more character to read.\n" //
            + "a\n" //
            + " ^__ Line 1", //
            e.getMessage());
      }
    }
  }

  @Test
  public void markAndReset() throws UnsupportedEncodingException {
    final String string = "abc\nxyz";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      // ---- a
      reader.read();
      // ---- b
      reader.read();
      // ---- c
      reader.read();
      // ---- LF
      reader.read();
      // ---- x
      reader.read();

      // ==== MARK
      reader.mark();

      // ---- y
      reader.read();
      // ---- z
      reader.read();
      // ---- EOF
      reader.read();

      // ==== RESET
      reader.reset();

      final int character = reader.read();
      Assert.assertEquals('y', character);
      Assert.assertEquals(2, reader.getLine());
      Assert.assertEquals(2, reader.getColumn());
    }
  }

  @Test
  public void unreadMarkAndReset_multiple() throws UnsupportedEncodingException {
    final String string = "abc\nxyz";

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(string.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      // ---- a
      reader.read();
      // ---- b
      reader.read();
      // ---- c
      int character = reader.read();

      Assert.assertEquals('c', character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(3, reader.getColumn());

      // ==== Mark
      reader.mark();

      // ---- LF
      character = reader.read();
      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(4, reader.getColumn());

      // ==== Reset 1 [After C]
      reader.reset();

      // ---- LF
      character = reader.read();
      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(4, reader.getColumn());

      // ---- LF -> c
      reader.unread();

      // ==== Mark [After C]
      reader.mark();

      // ---- LF
      reader.read();

      // ==== Mark [After LF] (Override previous mark)
      reader.mark();

      // ---- x
      character = reader.read();
      Assert.assertEquals('x', character);
      Assert.assertEquals(2, reader.getLine());
      Assert.assertEquals(1, reader.getColumn());

      // ==== Reset 2 [After LF]
      reader.reset();

      // ---- LF
      reader.unread();

      // ---- LF
      character = reader.read();
      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(4, reader.getColumn());

      // ==== Reset 3 [After C]
      reader.reset();

      // ---- LF
      character = reader.read();
      Assert.assertEquals(MappedCodePoints.LF, character);
      Assert.assertEquals(1, reader.getLine());
      Assert.assertEquals(4, reader.getColumn());
    }
  }

  @Test
  public void exceptionWhileReading_firstline_beforestart() throws UnsupportedEncodingException {
    final String expected = "This is an exception.\n" //
        + "^__ Line 0" //
        + "\nline number 1";
    final int charsToRead = 0;

    this.testException(UnicodeTestData.THREE_LINES, expected, charsToRead);
  }

  @Test
  public void exceptionAfterReading_firstline_beforestart() throws UnsupportedEncodingException {
    final String expected = "This is an exception.\n" //
        + "^__ Line 0" //
        + "\nline number 1";
    final int position = 0;

    this.testException(UnicodeTestData.THREE_LINES, expected, position);
  }

  @Test
  public void exceptionWhileReading_firstline_firstchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 1\n" //
        + "^__ Line 1\n" //
        + "line number 2";
    final int charsToRead = 1;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_firstline_secondchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 1\n" //
        + " ^__ Line 1\n" //
        + "line number 2";
    final int charsToRead = 2;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_firstline_secondchar_withfileandunread() throws UnsupportedEncodingException {
    final String output = "In file file.txt\n" //
        + "This is an exception.\n" //
        + "line number 1\n" //
        + " ^__ Line 1\n" //
        + "line number 2";
    final int charsToRead = 2;

    try (final UnicodeInputStream inputStream = new UnicodeInputStream(UnicodeTestData.THREE_LINES.getBytes("utf-8"))) {
      final Reader reader = new Reader("file.txt", Charset.UTF8, inputStream);

      for (int i = 0; i < charsToRead; i++) {
        reader.read();
      }

      reader.read();
      reader.read();
      reader.unread();
      reader.unread();

      final String debug = reader.debug("This is an exception.");
      Assert.assertEquals(output, debug);
    }
  }

  @Test
  public void exceptionWhileReading_firstline_lastchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 1\n" //
        + "             ^__ Line 1\n" //
        + "line number 2";
    final int charsToRead = 14;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_secondline_firstchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" + "line number 1\n" //
        + "line number 2\n" //
        + "^__ Line 2\n" + "line number 3";
    final int charsToRead = 15;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_secondline_secondchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 1\n" //
        + "line number 2\n" //
        + " ^__ Line 2\n" //
        + "line number 3";
    final int charsToRead = 16;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_secondline_lastchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 1\n" //
        + "line number 2\n" //
        + "             ^__ Line 2\n" //
        + "line number 3";
    final int charsToRead = 28;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_thirdline_firstchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + "^__ Line 3";
    final int charsToRead = 29;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_thirdline_secondchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + " ^__ Line 3";
    final int charsToRead = 30;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_thirdline_lastchar() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + "            ^__ Line 3";
    final int charsToRead = 41;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  @Test
  public void exceptionWhileReading_eof() throws UnsupportedEncodingException {
    final String output = "This is an exception.\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + "             ^__ Line 3";
    final int charsToRead = 42;

    this.testException(UnicodeTestData.THREE_LINES, output, charsToRead);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------------------------------------------------

  private void testException(final String input, final String output, final int charsToRead)
      throws UnsupportedEncodingException {
    this.testExceptionWhileReading(input, output, charsToRead);
    this.testExceptionAfterReading(input, output, charsToRead);
  }

  private void testExceptionWhileReading(final String input, final String output, final int charsToRead)
      throws UnsupportedEncodingException {
    try (final UnicodeInputStream inputStream = new UnicodeInputStream(input.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      for (int i = 0; i < charsToRead; i++) {
        reader.read();
      }

      final String debug = reader.debug("This is an exception.");
      Assert.assertEquals(output, debug);
    }
  }

  private void testExceptionAfterReading(final String input, final String output, final int position)
      throws UnsupportedEncodingException {
    try (final UnicodeInputStream inputStream = new UnicodeInputStream(input.getBytes("utf-8"))) {
      final Reader reader = new Reader(null, Charset.UTF8, inputStream);

      final String debug = reader.debug("This is an exception.", position);
      Assert.assertEquals(output, debug);
    }
  }
}
