package org.isk.plume.unicode;

import org.isk.plume.unicode.CodePoints.Charset;
import org.isk.plume.unicode.exception.MappedCodePointsException;
import org.junit.Assert;
import org.junit.Test;

public class MappedCodePointsTest {

  // -------------------------------------------------------------------------------------------------------------------
  // Constants and Getters
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void lf() {
    Assert.assertEquals('\n', MappedCodePoints.LF);
  }

  @Test
  public void cr() {
    Assert.assertEquals('\r', MappedCodePoints.CR);
  }

  @Test
  public void codePointAt_oneLine_1() {
    final byte[] bytes = { 'a', 'b', 'c', 'd', 'e' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals('a', mcp.codePointAt(1));
    Assert.assertEquals('b', mcp.codePointAt(2));
    Assert.assertEquals('c', mcp.codePointAt(3));
    Assert.assertEquals('d', mcp.codePointAt(4));
    Assert.assertEquals('e', mcp.codePointAt(5));
    Assert.assertEquals(MappedCodePoints.EOS, mcp.codePointAt(6));
  }

  @Test
  public void codePointAt_oneLine_2() {
    final byte[] bytes = { 'a', 'b', 'c', 'd', 'e', '\n' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals('a', mcp.codePointAt(1));
    Assert.assertEquals('b', mcp.codePointAt(2));
    Assert.assertEquals('c', mcp.codePointAt(3));
    Assert.assertEquals('d', mcp.codePointAt(4));
    Assert.assertEquals('e', mcp.codePointAt(5));
    Assert.assertEquals(MappedCodePoints.EOS, mcp.codePointAt(6));
  }

  @Test
  public void getLine_oneLine() {
    final byte[] bytes = { 'a', 'b' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals(1, mcp.getLine(1));
    Assert.assertEquals(1, mcp.getLine(2));
  }

  @Test
  public void getColumn_oneLine() {
    final byte[] bytes = { 'a', 'b', 'c', 'd', 'e' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals(1, mcp.getColumn(1));
    Assert.assertEquals(2, mcp.getColumn(2));
    Assert.assertEquals(3, mcp.getColumn(3));
    Assert.assertEquals(4, mcp.getColumn(4));
    Assert.assertEquals(5, mcp.getColumn(5));
  }

  @Test
  public void codePointAt_multiLines() {
    final byte[] bytes = { 'a', 'b', '\n', 'c', '\n', 'd', 'e' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals('a', mcp.codePointAt(1));
    Assert.assertEquals('b', mcp.codePointAt(2));
    Assert.assertEquals('\n', mcp.codePointAt(3));
    Assert.assertEquals('c', mcp.codePointAt(4));
    Assert.assertEquals('\n', mcp.codePointAt(5));
    Assert.assertEquals('d', mcp.codePointAt(6));
    Assert.assertEquals('e', mcp.codePointAt(7));
  }

  @Test
  public void getLine_multiLines() {
    final byte[] bytes = { 'a', '\n', 'b' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals(1, mcp.getLine(1));
    Assert.assertEquals(1, mcp.getLine(2)); // LF is still on the previous line
    Assert.assertEquals(2, mcp.getLine(3));
  }

  @Test
  public void getColumn_multiLines() {
    final byte[] bytes = { 'a', 'b', '\n', 'c', '\n', 'd', 'e', '\n' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals(1, mcp.getColumn(1));
    Assert.assertEquals(2, mcp.getColumn(2));
    Assert.assertEquals(3, mcp.getColumn(3));
    Assert.assertEquals(1, mcp.getColumn(4));
    Assert.assertEquals(2, mcp.getColumn(5));
    Assert.assertEquals(1, mcp.getColumn(6));
    Assert.assertEquals(2, mcp.getColumn(7));
  }

  @Test
  public void init_cr() {
    final byte[] bytes = { 'a', 'b', '\r', 'c', '\r', 'd', 'e', '\r' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals('a', mcp.codePointAt(1));
    Assert.assertEquals('b', mcp.codePointAt(2));
    Assert.assertEquals('\n', mcp.codePointAt(3));
    Assert.assertEquals('c', mcp.codePointAt(4));
    Assert.assertEquals('\n', mcp.codePointAt(5));
    Assert.assertEquals('d', mcp.codePointAt(6));
    Assert.assertEquals('e', mcp.codePointAt(7));
    Assert.assertEquals(MappedCodePoints.EOS, mcp.codePointAt(8));
  }

  @Test
  public void init_crlf() {
    final byte[] bytes = { 'a', 'b', '\r', '\n', 'c', '\r', '\n', 'd', 'e', '\r', '\n' };
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF8, new UnicodeInputStream(bytes));
    Assert.assertEquals('a', mcp.codePointAt(1));
    Assert.assertEquals('b', mcp.codePointAt(2));
    Assert.assertEquals('\n', mcp.codePointAt(3));
    Assert.assertEquals('c', mcp.codePointAt(4));
    Assert.assertEquals('\n', mcp.codePointAt(5));
    Assert.assertEquals('d', mcp.codePointAt(6));
    Assert.assertEquals('e', mcp.codePointAt(7));
    Assert.assertEquals(MappedCodePoints.EOS, mcp.codePointAt(8));
  }

  @Test
  public void codePointAt() {
    final int[] codepoints = { 'a', 'b', '\r', '\n', 'c', '\r', '\n', 'd', 'e', '\r', '\n' };
    final MappedCodePoints mcp = new MappedCodePoints(codepoints);
    Assert.assertEquals('a', mcp.codePointAt(1));
    Assert.assertEquals('b', mcp.codePointAt(2));
    Assert.assertEquals('\n', mcp.codePointAt(3));
    Assert.assertEquals('c', mcp.codePointAt(4));
    Assert.assertEquals('\n', mcp.codePointAt(5));
    Assert.assertEquals('d', mcp.codePointAt(6));
    Assert.assertEquals('e', mcp.codePointAt(7));
    Assert.assertEquals(MappedCodePoints.EOS, mcp.codePointAt(8));
  }

  @Test
  public void getEosIndex() {
    final int[] codepoints = { 'a', 'b', '\r', '\n', 'c', '\r', '\n', 'd', 'e', '\r', '\n' };
    final MappedCodePoints mcp = new MappedCodePoints(codepoints);
    Assert.assertEquals(8, mcp.getEosIndex());
  }

  @Test
  public void getFilename_constructor_1() {
    final MappedCodePoints mcp = new MappedCodePoints("foo.txt", Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    Assert.assertEquals("foo.txt", mcp.getFilename());
  }

  @Test
  public void getFilename_constructor_2() {
    final MappedCodePoints mcp = new MappedCodePoints("foo.txt", Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream(), 10, 10);
    Assert.assertEquals("foo.txt", mcp.getFilename());
  }

  @Test
  public void getFilename_constructor_3() {
    final int[] codePoints = MappedCodePointsTest.getFiveLines_codepoints();
    final MappedCodePoints mcp = new MappedCodePoints("foo.txt", codePoints, 8, 3);
    Assert.assertEquals("foo.txt", mcp.getFilename());
  }

  @Test
  public void getLine_negativeIndex() {
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    try {
      mcp.getLine(-1);
      Assert.fail();
    } catch (final MappedCodePointsException e) {
      Assert
          .assertEquals(
              "Index outside of range (-1). It should be greater than or equal to 0 and less than or equal to the end of stream.",
              e.getMessage());
    }
  }

  @Test
  public void getLine_indexTooBig() {
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    try {
      mcp.getLine(mcp.getEosIndex() + 1);
      Assert.fail();
    } catch (final MappedCodePointsException e) {
      Assert
          .assertEquals(
              "Index outside of range (71). It should be greater than or equal to 0 and less than or equal to the end of stream.",
              e.getMessage());
    }
  }

  @Test
  public void getColumn_negativeIndex() {
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    try {
      mcp.getColumn(-1);
      Assert.fail();
    } catch (final MappedCodePointsException e) {
      Assert
          .assertEquals(
              "Index outside of range (-1). It should be greater than or equal to 0 and less than or equal to the end of stream.",
              e.getMessage());
    }
  }

  @Test
  public void getColumn_indexTooBig() {
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    try {
      mcp.getColumn(mcp.getEosIndex() + 1);
      Assert.fail();
    } catch (final MappedCodePointsException e) {
      Assert
          .assertEquals(
              "Index outside of range (71). It should be greater than or equal to 0 and less than or equal to the end of stream.",
              e.getMessage());
    }
  }

  @Test
  public void codePointAt_negativeIndex() {
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    try {
      mcp.codePointAt(-1);
      Assert.fail();
    } catch (final MappedCodePointsException e) {
      Assert
          .assertEquals(
              "Index outside of range (-1). It should be greater than or equal to 0 and less than or equal to the end of stream.",
              e.getMessage());
    }
  }

  @Test
  public void codePointAt_indexTooBig() {
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    try {
      mcp.codePointAt(mcp.getEosIndex() + 1);
      Assert.fail();
    } catch (final MappedCodePointsException e) {
      Assert
          .assertEquals(
              "Index outside of range (71). It should be greater than or equal to 0 and less than or equal to the end of stream.",
              e.getMessage());
    }
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void constructor_unicodeInputStream() {
    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    this.testFiveLines(mcp);
  }

  @Test
  public void constructor_unicodeInputStream_filename() {
    final MappedCodePoints mcp = new MappedCodePoints("myFile.txt", Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream());
    this.testFiveLines(mcp);
  }

  @Test
  public void constructor_unicodeInputStream_filename_debug() {
    final MappedCodePoints mcp = new MappedCodePoints("myFile.txt", Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream(), 2, 2);
    this.testFiveLines(mcp);
  }

  @Test
  public void constructor_codepoints() {
    final MappedCodePoints mcp = new MappedCodePoints(MappedCodePointsTest.getFiveLines_codepoints());
    this.testFiveLines(mcp);
  }

  @Test
  public void constructor_codepoints_filename() {
    final MappedCodePoints mcp = new MappedCodePoints(MappedCodePointsTest.getFiveLines_codepoints(), 2, 2);
    this.testFiveLines(mcp);
  }

  @Test
  public void constructor_codepoints_filename_debug() {
    final MappedCodePoints mcp = new MappedCodePoints("myFile.txt", MappedCodePointsTest.getFiveLines_codepoints(), 2,
        2);
    this.testFiveLines(mcp);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Constructors - Unusual values
  // -------------------------------------------------------------------------------------------------------------------

  @Test(expected = NullPointerException.class)
  public void constructor_null_charset() {
    new MappedCodePoints(null, MappedCodePointsTest.getFiveLines_UnicodeInputStream());
  }

  @Test
  public void constructor_null_unicodeInputStream_withoutFile() {
    try {
      new MappedCodePoints(Charset.UTF32BE, null);
      Assert.fail();
    } catch (final MappedCodePointsException e) {
      Assert.assertEquals("This stream is null.", e.getMessage());
    }
  }

  @Test
  public void constructor_null_unicodeInputStream_withFile() {
    try {
      new MappedCodePoints("myFile.txt", Charset.UTF32BE, null);
      Assert.fail();
    } catch (final MappedCodePointsException e) {
      Assert.assertEquals("This stream is null (myFile.txt).", e.getMessage());
    }
  }

  @Test(expected = NullPointerException.class)
  public void constructor_null_codePoints() {
    new MappedCodePoints(null);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Debug
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void debug_1before_1after() {
    final String expected = "This is an exception.\n" //
        + "line number 1\n" //
        + "line number 2\n" //
        + "          ^__ Line 2\n" //
        + "line number 3";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 25);
    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_negativeValues_1before_1after() {
    final String expected = "This is an exception.\n" //
        + "line number 1\n" //
        + "line number 2\n" //
        + "          ^__ Line 2\n" //
        + "line number 3";

    final MappedCodePoints mcp = new MappedCodePoints(null, Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream(), -5, -1);
    final String debug = mcp.debug("This is an exception.", 25);
    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_2before_2after() {
    final String expected = "This is an exception.\n" //
        + "line number 1\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + " ^__ Line 3\n" //
        + "line number 4\n" //
        + "line number 5";

    final MappedCodePoints mcp = new MappedCodePoints(null, Charset.UTF32BE,
        MappedCodePointsTest.getFiveLines_UnicodeInputStream(), 2, 2);
    final String debug = mcp.debug("This is an exception.", 30);
    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_3before_2after() {
    final String expected = "In file myFile.txt\n" //
        + "This is an exception.\n" //
        + "line number 1\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + "line number 4\n" //
        + "  ^__ Line 4\n" //
        + "line number 5\n" //
        + "line number 6";

    final MappedCodePoints mcp = new MappedCodePoints("myFile.txt", Charset.UTF32BE,
        MappedCodePointsTest.getSevenLines_UnicodeInputStream(), 3, 2);
    final String debug = mcp.debug("This is an exception.", 45);
    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_firstline_beforestart() {
    final String expected = "This is an exception.\n" //
        + "^__ Line 0\n" //
        + "line number 1";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 0);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_firstline_firstchar() {
    final String expected = "This is an exception.\n" //
        + "line number 1\n" //
        + "^__ Line 1\n" //
        + "line number 2";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 1);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_firstline_secondchar() {
    final String expected = "This is an exception.\n" //
        + "line number 1\n" //
        + " ^__ Line 1\n" //
        + "line number 2";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 2);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_firstline_lastchar() {
    final String expected = "This is an exception.\n" //
        + "line number 1\n" //
        + "             ^__ Line 1\n" //
        + "line number 2";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 14);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_secondline_firstchar() {
    final String expected = "This is an exception.\n" + "line number 1\n" //
        + "line number 2\n" //
        + "^__ Line 2\n" + "line number 3";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 15);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_secondline_secondchar() {
    final String expected = "This is an exception.\n" //
        + "line number 1\n" //
        + "line number 2\n" //
        + " ^__ Line 2\n" //
        + "line number 3";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 16);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_secondline_lastchar() {
    final String expected = "This is an exception.\n" //
        + "line number 1\n" //
        + "line number 2\n" //
        + "             ^__ Line 2\n" //
        + "line number 3";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 28);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_thirdline_firstchar() {
    final String expected = "This is an exception.\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + "^__ Line 3";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 29);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_thirdline_secondchar() {
    final String expected = "This is an exception.\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + " ^__ Line 3";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 30);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_thirdline_lastchar() {
    final String expected = "This is an exception.\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + "            ^__ Line 3";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 41);

    Assert.assertEquals(expected, debug);
  }

  @Test
  public void debug_eos() {
    final String expected = "This is an exception.\n" //
        + "line number 2\n" //
        + "line number 3\n" //
        + "             ^__ Line 3";

    final MappedCodePoints mcp = new MappedCodePoints(Charset.UTF32BE,
        MappedCodePointsTest.getThreeLines_UnicodeInputStream());
    final String debug = mcp.debug("This is an exception.", 42);

    Assert.assertEquals(expected, debug);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------------------------------------------------

  private void testFiveLines(final MappedCodePoints mcp) {
    // ---- Line 1
    Assert.assertEquals('l', mcp.codePointAt(1));
    Assert.assertEquals(1, mcp.getLine(1));
    Assert.assertEquals(1, mcp.getColumn(1));

    Assert.assertEquals('1', mcp.codePointAt(13));
    Assert.assertEquals(1, mcp.getLine(1));
    Assert.assertEquals(13, mcp.getColumn(13));

    Assert.assertEquals('\n', mcp.codePointAt(14));
    Assert.assertEquals(1, mcp.getLine(14));
    Assert.assertEquals(14, mcp.getColumn(14));

    // ---- Line 2
    Assert.assertEquals('l', mcp.codePointAt(15));
    Assert.assertEquals(2, mcp.getLine(15));
    Assert.assertEquals(1, mcp.getColumn(15));

    Assert.assertEquals('2', mcp.codePointAt(27));
    Assert.assertEquals(2, mcp.getLine(27));
    Assert.assertEquals(13, mcp.getColumn(27));

    Assert.assertEquals('\n', mcp.codePointAt(28));
    Assert.assertEquals(2, mcp.getLine(28));
    Assert.assertEquals(14, mcp.getColumn(28));

    // ---- Line 3
    Assert.assertEquals('l', mcp.codePointAt(29));
    Assert.assertEquals(3, mcp.getLine(29));
    Assert.assertEquals(1, mcp.getColumn(29));

    Assert.assertEquals('3', mcp.codePointAt(41));
    Assert.assertEquals(3, mcp.getLine(41));
    Assert.assertEquals(13, mcp.getColumn(41));

    Assert.assertEquals('\n', mcp.codePointAt(42));
    Assert.assertEquals(3, mcp.getLine(42));
    Assert.assertEquals(14, mcp.getColumn(42));

    // ---- Line 4
    Assert.assertEquals('l', mcp.codePointAt(43));
    Assert.assertEquals(4, mcp.getLine(43));
    Assert.assertEquals(1, mcp.getColumn(43));

    Assert.assertEquals('4', mcp.codePointAt(55));
    Assert.assertEquals(4, mcp.getLine(55));
    Assert.assertEquals(13, mcp.getColumn(55));

    Assert.assertEquals('\n', mcp.codePointAt(56));
    Assert.assertEquals(4, mcp.getLine(56));
    Assert.assertEquals(14, mcp.getColumn(56));

    // ---- Line 5
    Assert.assertEquals('l', mcp.codePointAt(57));
    Assert.assertEquals(5, mcp.getLine(57));
    Assert.assertEquals(1, mcp.getColumn(57));

    Assert.assertEquals('5', mcp.codePointAt(69));
    Assert.assertEquals(5, mcp.getLine(69));
    Assert.assertEquals(13, mcp.getColumn(69));

    Assert.assertEquals(MappedCodePoints.EOS, mcp.codePointAt(70));
  }

  public static UnicodeInputStream getThreeLines_UnicodeInputStream() {
    return new UnicodeInputStream(UnicodeTestData.THREE_LINES.getBytes(UnicodeTestData.NIO_CHARSET_UTF32BE));
  }

  public static UnicodeInputStream getFiveLines_UnicodeInputStream() {
    return new UnicodeInputStream(UnicodeTestData.FIVE_LINES.getBytes(UnicodeTestData.NIO_CHARSET_UTF32BE));
  }

  public static UnicodeInputStream getSevenLines_UnicodeInputStream() {
    return new UnicodeInputStream(UnicodeTestData.SEVEN_LINES.getBytes(UnicodeTestData.NIO_CHARSET_UTF32BE));
  }

  public static int[] getThreeLines_codepoints() {
    return UnicodeTestData.THREE_LINES.codePoints().toArray();
  }

  public static int[] getFiveLines_codepoints() {
    return UnicodeTestData.FIVE_LINES.codePoints().toArray();
  }

  public static int[] getSevenLines_codepoints() {
    return UnicodeTestData.SEVEN_LINES.codePoints().toArray();
  }
}
