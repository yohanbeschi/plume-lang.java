package org.isk.plume.unicode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.isk.plume.unicode.CodePoints.Charset;
import org.isk.plume.unicode.CodePoints.Converter;
import org.isk.plume.unicode.UnicodeTestData.Line;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CodePointsSlowTest {
  private static List<Line> ALL_CODEPOINTS_AND_UTF = new ArrayList<>(CodePoints.NUMBER_OF_VALID_CODEPOINTS);
  private static int[] ALL_CODEPOINTS = new int[CodePoints.NUMBER_OF_VALID_CODEPOINTS];
  private static byte[] ALL_UTF8;
  private static byte[] ALL_UTF8BOM;
  private static byte[] ALL_UTF16BE;
  private static byte[] ALL_UTF16LE;
  private static byte[] ALL_UTF32BE;
  private static byte[] ALL_UTF32LE;

  /**
   * Create all valid Code Points - Remember that surrogates are not valid Code Points as a high or low surrogate can't
   * stand on its own without it's counterpart (respectively low and high), it's the reason we call them
   * "a surrogate pair". Moreover, surrogates have only been created (for the UTF-16 encoding to support Code Points
   * outside of the BMP) to avoid any collision between a real code point and a 16-bit code unit (where two 16-bit code
   * unit will constitute a real Code Point).
   * 
   * @throws Exception
   *           we don't care
   */
  @BeforeClass
  public static void setUpClass() throws Exception {
    // final long start = System.nanoTime();

    final ByteArrayOutputStream _utf8 = Converter.initByteArray(Charset.UTF8);
    final ByteArrayOutputStream _utf8bom = Converter.initByteArray(Charset.UTF8BOM);
    final ByteArrayOutputStream _utf16be = Converter.initByteArray(Charset.UTF16BE);
    final ByteArrayOutputStream _utf16le = Converter.initByteArray(Charset.UTF16LE);
    final ByteArrayOutputStream _utf32be = Converter.initByteArray(Charset.UTF32BE);
    final ByteArrayOutputStream _utf32le = Converter.initByteArray(Charset.UTF32LE);

    final java.nio.charset.Charset charsetUTF8 = java.nio.charset.Charset.forName("UTF-8");
    final java.nio.charset.Charset charsetUTF16BE = java.nio.charset.Charset.forName("UTF-16BE");
    final java.nio.charset.Charset charsetUTF16LE = java.nio.charset.Charset.forName("UTF-16LE");
    final java.nio.charset.Charset charsetUTF32BE = java.nio.charset.Charset.forName("UTF-32BE");
    final java.nio.charset.Charset charsetUTF32LE = java.nio.charset.Charset.forName("UTF-32LE");

    int count = 0;
    int cpCounter = 0;
    for (int i = 0; i <= 0x10FFFF; i++) {
      if (i <= 0xD7FF || i >= 0xE000) {
        final String string = new String(new int[] { count }, 0, 1);
        final byte[] utf8 = string.getBytes(charsetUTF8);
        final byte[] utf16be = string.getBytes(charsetUTF16BE);
        final byte[] utf16le = string.getBytes(charsetUTF16LE);
        final byte[] utf32be = string.getBytes(charsetUTF32BE);
        final byte[] utf32le = string.getBytes(charsetUTF32LE);
        final Line l = new Line(count, utf8, utf16be, utf16le, utf32be, utf32le);

        CodePointsSlowTest.ALL_CODEPOINTS_AND_UTF.add(l);
        CodePointsSlowTest.ALL_CODEPOINTS[cpCounter++] = l.codePoint;

        _utf8.write(l.utf8);
        _utf8bom.write(l.utf8);
        _utf16be.write(l.utf16be);
        _utf16le.write(l.utf16le);
        _utf32be.write(l.utf32be);
        _utf32le.write(l.utf32le);
      }

      count++;
    }

    CodePointsSlowTest.ALL_UTF8 = _utf8.toByteArray();
    CodePointsSlowTest.ALL_UTF8BOM = _utf8bom.toByteArray();
    CodePointsSlowTest.ALL_UTF16BE = _utf16be.toByteArray();
    CodePointsSlowTest.ALL_UTF16LE = _utf16le.toByteArray();
    CodePointsSlowTest.ALL_UTF32BE = _utf32be.toByteArray();
    CodePointsSlowTest.ALL_UTF32LE = _utf32le.toByteArray();

    // System.out.println("Init time: " + (System.nanoTime() - start) / 1_000_000_000 + "s");
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Converter.utf8ToCodePoint
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void utf8ToCodePoint() {
    this.testUtfToCodePoint(e -> e.codePoint == Converter.utf8ToCodePoint(new UnicodeInputStream(e.utf8)));
  }

  @Test
  public void utf16beToCodePoint() {
    this.testUtfToCodePoint(e -> e.codePoint == Converter.utf16beToCodePoint(new UnicodeInputStream(e.utf16be)));
  }

  @Test
  public void utf16leToCodePoint() {
    this.testUtfToCodePoint(e -> e.codePoint == Converter.utf16leToCodePoint(new UnicodeInputStream(e.utf16le)));
  }

  @Test
  public void utf32beToCodePoint() {
    this.testUtfToCodePoint(e -> e.codePoint == Converter.utf32beToCodePoint(new UnicodeInputStream(e.utf32be)));
  }

  @Test
  public void utf32leToCodePoint() {
    this.testUtfToCodePoint(e -> e.codePoint == Converter.utf32leToCodePoint(new UnicodeInputStream(e.utf32le)));
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Converter.codePointToUtfX
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void codePointToUtf8() {
    this.testUtfToCodePoint(e -> {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4);
      Converter.codePointToUtf8(e.codePoint, outputStream);
      return Arrays.equals(e.utf8, outputStream.toByteArray());
    });
  }

  @Test
  public void codePointToUtf16be() {
    this.testUtfToCodePoint(e -> {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4);
      Converter.codePointToUtf16be(e.codePoint, outputStream);
      return Arrays.equals(e.utf16be, outputStream.toByteArray());
    });
  }

  @Test
  public void codePointToUtf16le() {
    this.testUtfToCodePoint(e -> {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4);
      Converter.codePointToUtf16le(e.codePoint, outputStream);
      return Arrays.equals(e.utf16le, outputStream.toByteArray());
    });
  }

  @Test
  public void codePointToUtf32be() {
    this.testUtfToCodePoint(e -> {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4);
      Converter.codePointToUtf32be(e.codePoint, outputStream);
      return Arrays.equals(e.utf32be, outputStream.toByteArray());
    });
  }

  @Test
  public void codePointToUtf32le() {
    this.testUtfToCodePoint(e -> {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4);
      Converter.codePointToUtf32le(e.codePoint, outputStream);
      return Arrays.equals(e.utf32le, outputStream.toByteArray());
    });
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Converter.utfToUtf
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void utf8ToUtf16be() {
    this.testUtfToCodePoint(e -> {
      final byte[] utf16be = Converter.utfToUtf(e.utf8, Charset.UTF8, Charset.UTF16BE);
      return Arrays.equals(e.utf16be, utf16be);
    });
  }

  @Test
  public void utf8ToUtf16le() {
    this.testUtfToCodePoint(e -> {
      final byte[] utf16le = Converter.utfToUtf(e.utf8, Charset.UTF8, Charset.UTF16LE);
      return Arrays.equals(e.utf16le, utf16le);
    });
  }

  @Test
  public void utf8ToUtf32be() {
    this.testUtfToCodePoint(e -> {
      final byte[] utf32be = Converter.utfToUtf(e.utf8, Charset.UTF8, Charset.UTF32BE);
      return Arrays.equals(e.utf32be, utf32be);
    });
  }

  @Test
  public void utf8ToUtf32le() {
    this.testUtfToCodePoint(e -> {
      final byte[] utf32le = Converter.utfToUtf(e.utf8, Charset.UTF8, Charset.UTF32LE);
      return Arrays.equals(e.utf32le, utf32le);
    });
  }

  // -------------------------------------------------------------------------------------------------------------------
  // CodePoints.this.toUtf
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void codePointsToUtf8() {
    final CodePoints cp = new CodePoints(CodePointsSlowTest.ALL_CODEPOINTS);
    final byte[] utf8 = cp.toUtf(Charset.UTF8);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8, utf8);
  }

  @Test
  public void codePointsToUtf16be() {
    final CodePoints cp = new CodePoints(CodePointsSlowTest.ALL_CODEPOINTS);
    final byte[] utf16be = cp.toUtf(Charset.UTF16BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16BE, utf16be);
  }

  @Test
  public void codePointsToUtf16le() {
    final CodePoints cp = new CodePoints(CodePointsSlowTest.ALL_CODEPOINTS);
    final byte[] utf16le = cp.toUtf(Charset.UTF16LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16LE, utf16le);
  }

  @Test
  public void codePointsToUtf32be() {
    final CodePoints cp = new CodePoints(CodePointsSlowTest.ALL_CODEPOINTS);
    final byte[] utf32be = cp.toUtf(Charset.UTF32BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32BE, utf32be);
  }

  @Test
  public void codePointsToUtf32le() {
    final CodePoints cp = new CodePoints(CodePointsSlowTest.ALL_CODEPOINTS);
    final byte[] utf32le = cp.toUtf(Charset.UTF32LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32LE, utf32le);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // CodePoints.toCodePoints
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void utf8ToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF8, CodePointsSlowTest.ALL_UTF8);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_CODEPOINTS, codePoints);
  }

  @Test
  public void utf8BomToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF8BOM, CodePointsSlowTest.ALL_UTF8BOM);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_CODEPOINTS, codePoints);
  }

  @Test
  public void utf16beToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF16BE, CodePointsSlowTest.ALL_UTF16BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_CODEPOINTS, codePoints);
  }

  @Test
  public void utf16leToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF16LE, CodePointsSlowTest.ALL_UTF16LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_CODEPOINTS, codePoints);
  }

  @Test
  public void utf32beToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF32BE, CodePointsSlowTest.ALL_UTF32BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_CODEPOINTS, codePoints);
  }

  @Test
  public void utf32leToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF32LE, CodePointsSlowTest.ALL_UTF32LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_CODEPOINTS, codePoints);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // CodePoints.utfToUtf
  // -------------------------------------------------------------------------------------------------------------------
  @Test
  public void utf8ToUtf8_array() {
    final byte[] utf8 = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8, Charset.UTF8, Charset.UTF8);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8, utf8);
  }

  @Test
  public void utf8ToUtf8bom_array() {
    final byte[] utf8bom = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8, Charset.UTF8, Charset.UTF8BOM);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8BOM, utf8bom);
  }

  @Test
  public void utf8ToUtf16be_array() {
    final byte[] utf16be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8, Charset.UTF8, Charset.UTF16BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16BE, utf16be);
  }

  @Test
  public void utf8ToUtf16le_array() {
    final byte[] utf16le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8, Charset.UTF8, Charset.UTF16LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16LE, utf16le);
  }

  @Test
  public void utf8ToUtf32be_array() {
    final byte[] utf32be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8, Charset.UTF8, Charset.UTF32BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32BE, utf32be);
  }

  @Test
  public void utf8ToUtf32le_array() {
    final byte[] utf32le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8, Charset.UTF8, Charset.UTF32LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32LE, utf32le);
  }

  @Test
  public void utf8bomToUtf8_array() {
    final byte[] utf8 = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8BOM, Charset.UTF8BOM, Charset.UTF8);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8, utf8);
  }

  @Test
  public void utf8bomToUtf8bom_array() {
    final byte[] utf8bom = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8BOM, Charset.UTF8BOM, Charset.UTF8BOM);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8BOM, utf8bom);
  }

  @Test
  public void utf8bomToUtf16be_array() {
    final byte[] utf16be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8BOM, Charset.UTF8BOM, Charset.UTF16BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16BE, utf16be);
  }

  @Test
  public void utf8bomToUtf16le_array() {
    final byte[] utf16le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8BOM, Charset.UTF8BOM, Charset.UTF16LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16LE, utf16le);
  }

  @Test
  public void utf8bomToUtf32be_array() {
    final byte[] utf32be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8BOM, Charset.UTF8BOM, Charset.UTF32BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32BE, utf32be);
  }

  @Test
  public void utf8bomToUtf32le_array() {
    final byte[] utf32le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF8BOM, Charset.UTF8BOM, Charset.UTF32LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32LE, utf32le);
  }

  @Test
  public void utf16beToUtf16be_array() {
    final byte[] utf16be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16BE, Charset.UTF16BE, Charset.UTF16BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16BE, utf16be);
  }

  @Test
  public void utf16beToUtf16le_array() {
    final byte[] utf16le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16BE, Charset.UTF16BE, Charset.UTF16LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16LE, utf16le);
  }

  @Test
  public void utf16beToUtf8_array() {
    final byte[] utf8 = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16BE, Charset.UTF16BE, Charset.UTF8);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8, utf8);
  }

  @Test
  public void utf16beToUtf8bom_array() {
    final byte[] utf8bom = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16BE, Charset.UTF16BE, Charset.UTF8BOM);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8BOM, utf8bom);
  }

  @Test
  public void utf16beToUtf32be_array() {
    final byte[] utf32be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16BE, Charset.UTF16BE, Charset.UTF32BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32BE, utf32be);
  }

  @Test
  public void utf16beToUtf32le_array() {
    final byte[] utf32le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16BE, Charset.UTF16BE, Charset.UTF32LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32LE, utf32le);
  }

  @Test
  public void utf16leToUtf16le_array() {
    final byte[] utf16le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16LE, Charset.UTF16LE, Charset.UTF16LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16LE, utf16le);
  }

  @Test
  public void utf16leToUtf16be_array() {
    final byte[] utf16be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16LE, Charset.UTF16LE, Charset.UTF16BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16BE, utf16be);
  }

  @Test
  public void utf16leToUtf8_array() {
    final byte[] utf8 = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16LE, Charset.UTF16LE, Charset.UTF8);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8, utf8);
  }

  @Test
  public void utf16leToUtf8bom_array() {
    final byte[] utf8bom = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16LE, Charset.UTF16LE, Charset.UTF8BOM);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8BOM, utf8bom);
  }

  @Test
  public void utf16leToUtf32be_array() {
    final byte[] utf32be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16LE, Charset.UTF16LE, Charset.UTF32BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32BE, utf32be);
  }

  @Test
  public void utf16leToUtf32le_array() {
    final byte[] utf32le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF16LE, Charset.UTF16LE, Charset.UTF32LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32LE, utf32le);
  }

  @Test
  public void utf32beToUtf32be_array() {
    final byte[] utf32be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32BE, Charset.UTF32BE, Charset.UTF32BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32BE, utf32be);
  }

  @Test
  public void utf32beToUtf32le_array() {
    final byte[] utf32le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32BE, Charset.UTF32BE, Charset.UTF32LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32LE, utf32le);
  }

  @Test
  public void utf32beToUtf8_array() {
    final byte[] utf8 = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32BE, Charset.UTF32BE, Charset.UTF8);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8, utf8);
  }

  @Test
  public void utf32beToUtf8bom_array() {
    final byte[] utf8bom = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32BE, Charset.UTF32BE, Charset.UTF8BOM);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8BOM, utf8bom);
  }

  @Test
  public void utf32beToUtf16be_array() {
    final byte[] utf16be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32BE, Charset.UTF32BE, Charset.UTF16BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16BE, utf16be);
  }

  @Test
  public void utf32beToUtf16le_array() {
    final byte[] utf16le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32BE, Charset.UTF32BE, Charset.UTF16LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16LE, utf16le);
  }

  @Test
  public void utf32leToUtf32le_array() {
    final byte[] utf32le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32LE, Charset.UTF32LE, Charset.UTF32LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32LE, utf32le);
  }

  @Test
  public void utf32leToUtf32be_array() {
    final byte[] utf32be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32LE, Charset.UTF32LE, Charset.UTF32BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF32BE, utf32be);
  }

  @Test
  public void utf32leToUtf8_array() {
    final byte[] utf8 = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32LE, Charset.UTF32LE, Charset.UTF8);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8, utf8);
  }

  @Test
  public void utf32leToUtf8bom_array() {
    final byte[] utf8bom = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32LE, Charset.UTF32LE, Charset.UTF8BOM);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF8BOM, utf8bom);
  }

  @Test
  public void utf32leToUtf16be_array() {
    final byte[] utf16be = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32LE, Charset.UTF32LE, Charset.UTF16BE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16BE, utf16be);
  }

  @Test
  public void utf32leToUtf16le_array() {
    final byte[] utf16le = CodePoints.utfToUtf(CodePointsSlowTest.ALL_UTF32LE, Charset.UTF32LE, Charset.UTF16LE);
    Assert.assertArrayEquals(CodePointsSlowTest.ALL_UTF16LE, utf16le);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------------------------------------------------

  // Test all code points
  public void testUtfToCodePoint(final Predicate<Line> predicate) {
    final long count = CodePointsSlowTest.ALL_CODEPOINTS_AND_UTF.stream().parallel().filter(predicate).count();
    Assert.assertEquals(CodePoints.NUMBER_OF_VALID_CODEPOINTS, count);
  }
}
