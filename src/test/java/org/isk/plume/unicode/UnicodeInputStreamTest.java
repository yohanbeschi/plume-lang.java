package org.isk.plume.unicode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.isk.plume.exception.UnicodeException;
import org.junit.Assert;
import org.junit.Test;

public class UnicodeInputStreamTest {

  @Test
  public void read_constructorBytes() {
    final UnicodeInputStream stream = new UnicodeInputStream(UnicodeTestData.UTF16_BE_BOM_BYTEARRAY);
    this.assertUtf16beNoBom(stream);
  }

  @Test
  public void constructorBytes_null() {
    final byte[] bytes = null;
    try (final UnicodeInputStream inputStream = new UnicodeInputStream(bytes)) {
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert
          .assertEquals("Impossible to instantiate an UnicodeInputStream, there is no bytes to read.", e.getMessage());
    }
  }

  @Test
  public void constructorBytes_empty() {
    final byte[] bytes = new byte[0];
    try (final UnicodeInputStream inputStream = new UnicodeInputStream(bytes)) {
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert
          .assertEquals("Impossible to instantiate an UnicodeInputStream, there is no bytes to read.", e.getMessage());
    }
  }

  @Test
  public void read_constructorInputStream() {
    final ByteArrayInputStream inputStream = new ByteArrayInputStream(UnicodeTestData.UTF16_BE_BOM_BYTEARRAY);
    final UnicodeInputStream stream = new UnicodeInputStream(inputStream);
    this.assertUtf16beNoBom(stream);
  }

  @Test
  public void constructorInputStream_null() {
    final InputStream inputStream = null;
    try (final UnicodeInputStream otherInputStream = new UnicodeInputStream(inputStream)) {
      Assert.fail();
    } catch (final UnicodeException e) {
      Assert.assertEquals("Impossible to instantiate an UnicodeInputStream, the InputStream is null.", e.getMessage());
    }
  }

  @Test(expected = NullPointerException.class)
  public void constructorInputStream_NullByteArray() {
    try (final UnicodeInputStream inputStream = new UnicodeInputStream(new ByteArrayInputStream(null))) {

    }
  }

  @Test
  public void constructorInputStream_EmptyByteArray() {
    final byte[] bytes = new byte[0];
    final UnicodeInputStream stream = new UnicodeInputStream(new ByteArrayInputStream(bytes));
    Assert.assertNotNull(stream);
  }

  @Test
  public void hasNext() {
    final byte[] bytes = { 'a', 'b' };
    try (final UnicodeInputStream stream = new UnicodeInputStream(bytes)) {
      Assert.assertTrue(stream.hasNext());
      stream.read();
      Assert.assertTrue(stream.hasNext());
      stream.read();
      Assert.assertFalse(stream.hasNext());
      Assert.assertFalse(stream.hasNext());
    }
  }

  @Test
  public void read_TooFar() {
    final byte[] bytes = { 'a', 'b' };

    try (final UnicodeInputStream stream = new UnicodeInputStream(bytes);) {
      stream.read();
      stream.read();

      try {
        stream.read();
        Assert.fail();
      } catch (final UnicodeException e) {
        Assert.assertEquals("This UnicodeInputStream has been read completely!", e.getMessage());
      }
    }
  }

  @Test
  public void unread() {
    final byte[] bytes = { 'a', 'b' };

    try (final UnicodeInputStream stream = new UnicodeInputStream(bytes)) {
      int b = stream.read();
      stream.unread(b);
      b = stream.read();
      Assert.assertEquals('a', b);

      b = stream.read();
      stream.unread(b);
      b = stream.read();
      Assert.assertEquals('b', b);
    }
  }

  @Test
  public void unread_pushBackBufferFull() {
    final byte[] bytes = { 'a', 'b' };

    try (final UnicodeInputStream stream = new UnicodeInputStream(bytes)) {
      final int b1 = stream.read();
      final int b2 = stream.read();
      stream.unread(b1);

      try {
        stream.unread(b2);
        Assert.fail();
      } catch (final UnicodeException e) {
        Assert.assertEquals("Something went wrong while unreading this UnicodeInputStream!", e.getMessage());
      }
    }
  }

  private void assertUtf16beNoBom(final UnicodeInputStream stream) {
    Assert.assertEquals(0xFE, stream.read());
    Assert.assertEquals(0xFF, stream.read());
    Assert.assertEquals(0x00, stream.read());
    Assert.assertEquals(0x61, stream.read());
    Assert.assertEquals(0x09, stream.read());
    Assert.assertEquals(0x28, stream.read());
    Assert.assertEquals(0x09, stream.read());
    Assert.assertEquals(0x3F, stream.read());
    Assert.assertEquals(0x4E, stream.read());
    Assert.assertEquals(0x9C, stream.read());
    Assert.assertEquals(0xD8, stream.read());
    Assert.assertEquals(0x00, stream.read());
    Assert.assertEquals(0xDC, stream.read());
    Assert.assertEquals(0x83, stream.read());
  }
}
