package org.isk.plume.unicode;

import java.io.ByteArrayOutputStream;

import org.isk.plume.unicode.CodePoints.Charset;
import org.isk.plume.unicode.CodePoints.Converter;
import org.isk.plume.unicode.exception.UnicodeException;
import org.junit.Assert;
import org.junit.Test;

public class CodePointsTest {
  // -------------------------------------------------------------------------------------------------------------------
  // Code Points manipulations
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void constructor_empty() {
    final CodePoints cp = new CodePoints();
    cp.add('a');
    Assert.assertEquals("a", cp.toString());
  }

  @Test
  public void constructor_initialSize() {
    final CodePoints cp = new CodePoints(5);
    cp.add('a');
    cp.add('a');
    cp.add('a');
    cp.add('a');
    cp.add('a');
    cp.add("bbbbb");
    cp.add('c');
    cp.add('c');
    cp.add('c');
    cp.add('c');
    cp.add('c');
    Assert.assertEquals("aaaaabbbbbccccc", cp.toString());
  }

  @Test
  public void constructor_initialCapacity_negative() {
    try {
      new CodePoints(-1);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals("The size of the buffer can't be 0 or less.", e.getMessage());
    }
  }

  @Test
  public void constructor_initialCapacity_zero() {
    try {
      new CodePoints(0);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals("The size of the buffer can't be 0 or less.", e.getMessage());
    }
  }

  @Test
  public void constructor_codepoints() {
    final CodePoints cp = new CodePoints(UnicodeTestData.CODEPOINTS);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, cp.codepoints());
  }

  @Test
  public void constructor_codepoints_growthSize() {
    final CodePoints cp = new CodePoints(UnicodeTestData.CODEPOINTS, 1);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, cp.codepoints());
  }

  @Test
  public void constructor_codepoints_growthSize_negative() {
    try {
      new CodePoints(UnicodeTestData.CODEPOINTS, -1);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals("The growth size of the buffer can't be 0 or less.", e.getMessage());
    }
  }

  @Test
  public void constructor_codepoints_growthSize_zero() {
    try {
      new CodePoints(UnicodeTestData.CODEPOINTS, 0);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals("The growth size of the buffer can't be 0 or less.", e.getMessage());
    }
  }

  @Test
  public void constructor_utfAsByteArray() {
    final CodePoints cp = new CodePoints(Charset.UTF8BOM, UnicodeTestData.UTF8_BOM_BYTEARRAY);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, cp.codepoints());
  }

  @Test
  public void constructor_utfAsUnicodeInputStream() {
    final CodePoints cp = new CodePoints(Charset.UTF8BOM, new UnicodeInputStream(UnicodeTestData.UTF8_BOM_BYTEARRAY));
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, cp.codepoints());
  }

  @Test
  public void constructor_utfAsUnicodeInputStream_initialSize() {
    final CodePoints cp = new CodePoints(Charset.UTF8BOM, new UnicodeInputStream(UnicodeTestData.UTF8_BOM_BYTEARRAY), 1);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, cp.codepoints());
  }

  @Test
  public void isEmpty() {
    final CodePoints cp = new CodePoints(2);
    Assert.assertTrue(cp.isEmpty());
    cp.add('a');
    Assert.assertFalse(cp.isEmpty());
  }

  @Test
  public void length() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals(5, cp.length());
    Assert.assertEquals("abcde", cp.toString());
    Assert.assertEquals("abcde", cp.toString());
    Assert.assertEquals(5, cp.length());
  }

  @Test
  public void length_empty() {
    final CodePoints cp = new CodePoints(10);
    Assert.assertEquals(0, cp.length());
  }

  @Test
  public void setLength() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals(5, cp.length());
    cp.setLength(4);
    Assert.assertEquals(4, cp.length());
    Assert.assertEquals("abcd", cp.toString());
  }

  @Test
  public void setLength_sameSize() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals(5, cp.length());
    cp.setLength(5);
    Assert.assertEquals(5, cp.length());
    Assert.assertEquals("abcde", cp.toString());
  }

  @Test
  public void setLength_tooSmall() {
    final CodePoints cp = this.simpleAsciiString();
    try {
      cp.setLength(-1);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert
          .assertEquals(
              "The new length is outside of range (-1). It should be greater than or equal to 0 and less than or equal to the current length.",
              e.getMessage());
    }
  }

  @Test
  public void setLength_tooBig() {
    final CodePoints cp = this.simpleAsciiString();
    try {
      cp.setLength(6);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert
          .assertEquals(
              "The new length is outside of range (6). It should be greater than or equal to 0 and less than or equal to the current length.",
              e.getMessage());
    }
  }

  @Test
  public void clear() {
    final CodePoints cp = this.simpleAsciiString();
    cp.clear();
    Assert.assertEquals(0, cp.length());
    Assert.assertEquals("", cp.toString());
  }

  @Test
  public void setLengthAndAdd() {
    final CodePoints cp = this.simpleAsciiString();
    cp.setLength(4);
    cp.add('z');
    Assert.assertEquals(5, cp.length());
    Assert.assertEquals("abcdz", cp.toString());
  }

  @Test
  public void addString() {
    final CodePoints cp = new CodePoints();
    cp.add("Привет мир по-русски");
    Assert.assertEquals("Привет мир по-русски", cp.toString());
  }

  @Test
  public void addString_multiplanes_unicodeChars() {
    final CodePoints cp = new CodePoints();
    cp.add(UnicodeTestData.CODEPOINTS_AS_STRING);
    Assert.assertEquals(UnicodeTestData.CODEPOINTS_AS_STRING, cp.toString());
  }

  @Test
  public void addString_multiplanes_realChars_1() {
    final CodePoints cp = new CodePoints();
    cp.add("aनि亜𐂃");
    Assert.assertEquals("aनि亜𐂃", cp.toString());
  }

  @Test
  public void addString_multiplanes_realChars_2() {
    final CodePoints cp = new CodePoints();
    cp.add("aनि亜𐂃");
    Assert.assertEquals(UnicodeTestData.CODEPOINTS_AS_STRING, cp.toString());
  }

  @Test
  public void addString_multiplanes_realChars_3() {
    final CodePoints cp = new CodePoints();
    cp.add(UnicodeTestData.CODEPOINTS_AS_STRING);
    Assert.assertEquals("aनि亜𐂃", cp.toString());
  }

  @Test
  public void addCodePoint_outsideBMP() {
    final CodePoints cp = new CodePoints();
    for (final int codePoint : UnicodeTestData.CODEPOINTS) {
      cp.add(codePoint);
    }

    final byte[] stringAsByteArray = cp.toString().getBytes(java.nio.charset.Charset.forName("UTF-16BE"));
    Assert.assertArrayEquals(UnicodeTestData.UTF16_BE_NOBOM_BYTEARRAY, stringAsByteArray);
  }

  @Test
  public void addCodePoints_outsideBMP() {
    final CodePoints cp = new CodePoints();
    cp.add(UnicodeTestData.CODEPOINTS);
    Assert.assertEquals(UnicodeTestData.CODEPOINTS_AS_STRING, cp.toString());
  }

  @Test
  public void at() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals('c', cp.at(2));
  }

  @Test
  public void at_begining() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals('a', cp.at(0));
  }

  @Test
  public void at_end() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals('e', cp.at(4));
  }

  @Test
  public void at_tooSmall() {
    final CodePoints cp = this.simpleAsciiString();
    try {
      cp.at(-1);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals(
          "Index outside of range (-1). It should be greater than or equal to 0 and less than the current length.",
          e.getMessage());
    }
  }

  @Test
  public void at_tooBig() {
    final CodePoints cp = this.simpleAsciiString();
    try {
      cp.at(5);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals(
          "Index outside of range (5). It should be greater than or equal to 0 and less than the current length.",
          e.getMessage());
    }
  }

  @Test
  public void codepoints() {
    final CodePoints cp = new CodePoints(5);
    cp.add(UnicodeTestData.CODEPOINTS_AS_STRING);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, cp.codepoints());
  }

  @Test
  public void toString_nobom() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals("abcde", cp.toString());
  }

  @Test
  public void toString_bom() {
    final CodePoints cp = new CodePoints();
    cp.add(CodePoints.BOM_CODEPOINT);
    cp.add('a');
    cp.add('z');
    Assert.assertEquals("az", cp.toString());
  }

  @Test
  public void reset() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals("abcde", cp.toStringAndReset());
    Assert.assertEquals(0, cp.length());
    Assert.assertEquals("", cp.toString());
  }

  @Test
  public void resetAndAdd() {
    final CodePoints cp = this.simpleAsciiString();
    Assert.assertEquals("abcde", cp.toStringAndReset());
    cp.add('z');
    Assert.assertEquals(1, cp.length());
    Assert.assertEquals("z", cp.toString());
  }

  private CodePoints simpleAsciiString() {
    final CodePoints cp = new CodePoints();
    cp.add('a');
    cp.add('b');
    cp.add('c');
    cp.add('d');
    cp.add('e');
    return cp;
  }

  // -------------------------------------------------------------------------------------------------------------------
  // this.toUtf()
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void toUtf_nobom() {
    final CodePoints cp = new CodePoints();
    cp.add('a');
    cp.add('z');
    Assert.assertArrayEquals(new byte[] { 0x61, 0x7A }, cp.toUtf(Charset.UTF8));
  }

  @Test
  public void toUtf_bom() {
    final CodePoints cp = new CodePoints();
    cp.add(CodePoints.BOM_CODEPOINT);
    cp.add('a');
    cp.add('z');
    Assert.assertArrayEquals(new byte[] { 0x61, 0x7A }, cp.toUtf(Charset.UTF8));
  }

  // -------------------------------------------------------------------------------------------------------------------
  // CodePoints.toCodePoints
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void utf8ToCodePoints_noBOM() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF8, UnicodeTestData.UTF8_NOBOM_BYTEARRAY);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, codePoints);
  }

  @Test
  public void utf8ToCodePoints_wBOM() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF8BOM, UnicodeTestData.UTF8_BOM_BYTEARRAY);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, codePoints);
  }

  @Test
  public void utf16BEToCodePoints_noBOM() {
    try {
      CodePoints.toCodePoints(Charset.UTF16BE, UnicodeTestData.UTF16_BE_NOBOM_BYTEARRAY);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals("Wrong UTF-16-BE BOM. Expected 0xFE (byte 0).", e.getMessage());
    }
  }

  @Test
  public void utf16BEToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF16BE, UnicodeTestData.UTF16_BE_BOM_BYTEARRAY);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, codePoints);
  }

  @Test
  public void utf16LEToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF16LE, UnicodeTestData.UTF16_LE_BOM_BYTEARRAY);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, codePoints);
  }

  @Test
  public void utf32BEToCodePoints_noBOM() {
    try {
      CodePoints.toCodePoints(Charset.UTF32BE, UnicodeTestData.UTF32_BE_NOBOM_BYTEARRAY);
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals("Wrong UTF-32-BE BOM. Expected 0xFE (byte 2).", e.getMessage());
    }
  }

  @Test
  public void utf32BEToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF32BE, UnicodeTestData.UTF32_BE_BOM_BYTEARRAY);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, codePoints);
  }

  @Test
  public void utf32LEToCodePoints() {
    final int[] codePoints = CodePoints.toCodePoints(Charset.UTF32LE, UnicodeTestData.UTF32_LE_BOM_BYTEARRAY);
    Assert.assertArrayEquals(UnicodeTestData.CODEPOINTS, codePoints);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // CodePoints.toUtf
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void codePointsToUtf8_noBOM() {
    final byte[] utf8 = CodePoints.toUtf(UnicodeTestData.CODEPOINTS, Charset.UTF8);
    Assert.assertArrayEquals(UnicodeTestData.UTF8_NOBOM_BYTEARRAY, utf8);
  }

  @Test
  public void codePointsToUtf8_wBOM() {
    final byte[] utf8 = CodePoints.toUtf(UnicodeTestData.CODEPOINTS, Charset.UTF8BOM);
    Assert.assertArrayEquals(UnicodeTestData.UTF8_BOM_BYTEARRAY, utf8);
  }

  @Test
  public void codePointsToUtf16BE() {
    final byte[] utf16 = CodePoints.toUtf(UnicodeTestData.CODEPOINTS, Charset.UTF16BE);
    Assert.assertArrayEquals(UnicodeTestData.UTF16_BE_BOM_BYTEARRAY, utf16);
  }

  @Test
  public void codePointsToUtf16LE() {
    final byte[] utf16 = CodePoints.toUtf(UnicodeTestData.CODEPOINTS, Charset.UTF16LE);
    Assert.assertArrayEquals(UnicodeTestData.UTF16_LE_BOM_BYTEARRAY, utf16);
  }

  @Test
  public void codePointsToUtf32BE() {
    final byte[] utf32 = CodePoints.toUtf(UnicodeTestData.CODEPOINTS, Charset.UTF32BE);
    Assert.assertArrayEquals(UnicodeTestData.UTF32_BE_BOM_BYTEARRAY, utf32);
  }

  @Test
  public void codePointsToUtf32LE() {
    final byte[] utf32 = CodePoints.toUtf(UnicodeTestData.CODEPOINTS, Charset.UTF32LE);
    Assert.assertArrayEquals(UnicodeTestData.UTF32_LE_BOM_BYTEARRAY, utf32);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Converter.readBom
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void readBom_utf8() {
    Converter.readBom(Charset.UTF8, new UnicodeInputStream(new byte[] { 0x61 }));
  }

  @Test
  public void readBom_utf8bom() {
    Converter.readBom(Charset.UTF8BOM, new UnicodeInputStream(CodePoints.UTF8_BOM));
  }

  @Test
  public void readBom_utf16be() {
    Converter.readBom(Charset.UTF16BE, new UnicodeInputStream(CodePoints.UTF16BE_BOM));
  }

  @Test
  public void readBom_utf16le() {
    Converter.readBom(Charset.UTF16LE, new UnicodeInputStream(CodePoints.UTF16LE_BOM));
  }

  @Test
  public void readBom_utf32be() {
    Converter.readBom(Charset.UTF32BE, new UnicodeInputStream(CodePoints.UTF32BE_BOM));
  }

  @Test
  public void readBom_utf32le() {
    Converter.readBom(Charset.UTF32LE, new UnicodeInputStream(CodePoints.UTF32LE_BOM));
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Converter.initByteArray
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void initByteArray_utf8() {

  }

  @Test
  public void initByteArray_utf8bom() {
    final ByteArrayOutputStream out = Converter.initByteArray(Charset.UTF8BOM);
    Assert.assertArrayEquals(CodePoints.UTF8_BOM, out.toByteArray());
  }

  @Test
  public void initByteArray_utf16be() {
    final ByteArrayOutputStream out = Converter.initByteArray(Charset.UTF16BE);
    Assert.assertArrayEquals(CodePoints.UTF16BE_BOM, out.toByteArray());
  }

  @Test
  public void initByteArray_utf16le() {
    final ByteArrayOutputStream out = Converter.initByteArray(Charset.UTF16LE);
    Assert.assertArrayEquals(CodePoints.UTF16LE_BOM, out.toByteArray());
  }

  @Test
  public void initByteArray_utf32be() {
    final ByteArrayOutputStream out = Converter.initByteArray(Charset.UTF32BE);
    Assert.assertArrayEquals(CodePoints.UTF32BE_BOM, out.toByteArray());
  }

  @Test
  public void initByteArray_utf32le() {
    final ByteArrayOutputStream out = Converter.initByteArray(Charset.UTF32LE);
    Assert.assertArrayEquals(CodePoints.UTF32LE_BOM, out.toByteArray());
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Surrogates
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void surrogate_beforeFirstHigh() {
    Assert.assertFalse(Converter.isSurrogate(0xD7FF));
  }

  @Test
  public void surrogate_firstHigh() {
    Assert.assertTrue(Converter.isSurrogate(0xD800));
  }

  @Test
  public void highSurrogate_firstHigh() {
    Assert.assertTrue(Converter.isHighSurrogate(0xD800));
  }

  @Test
  public void surrogate_lastHigh() {
    Assert.assertTrue(Converter.isSurrogate(0xDBFF));
  }

  @Test
  public void highSurrogate_lastHigh() {
    Assert.assertTrue(Converter.isHighSurrogate(0xDBFF));
  }

  @Test
  public void surrogate_firstLow() {
    Assert.assertTrue(Converter.isSurrogate(0xDC00));
  }

  @Test
  public void lowSurrogate_firstLow() {
    Assert.assertTrue(Converter.isLowSurrogate(0xDC00));
  }

  @Test
  public void surrogate_lastLow() {
    Assert.assertTrue(Converter.isSurrogate(0xDFFF));
  }

  @Test
  public void lowSurrogate_lastLow() {
    Assert.assertTrue(Converter.isLowSurrogate(0xDFFF));
  }

  @Test
  public void surrogate_afterLastLow() {
    Assert.assertFalse(Converter.isSurrogate(0xE000));
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Converter.isNoncharacter
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void isNoncharacter() {
    int i = 0;
    int count = 0;
    for (i = 0; i < CodePoints.UNICODE_CODESPACE_SIZE; i++) {
      if (Converter.isNoncharacter(i)) {
        count++;
      }
    }

    Assert.assertEquals(CodePoints.NUMBER_OF_NONCHARACTERS, count);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Converter.isInPrivateUseArea
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void isInPrivateUseArea() {
    int i = 0;
    int count = 0;
    for (i = 0; i < 0xFFFFFF; i++) {
      if (Converter.isInPrivateUseArea(i)) {
        count++;
      }
    }

    Assert.assertEquals(CodePoints.NUMBER_OF_CODEPOINTS_IN_PRIVATE_USE_AREA, count);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Converter.isCodePoint
  // -------------------------------------------------------------------------------------------------------------------

  @Test
  public void isCodePoint() {
    int i = 0;
    int count = 0;
    for (i = 0; i < 0xFFFFFF; i++) {
      if (Converter.isCodePoint(i)) {
        count++;
      }
    }

    Assert.assertEquals(CodePoints.NUMBER_OF_VALID_CODEPOINTS, count);
  }
}