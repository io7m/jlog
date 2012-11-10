/*
 * Copyright © 2012 http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jlog.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.io7m.jlog.Callbacks;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;

public class LogTest
{
  private static final class CountCallback implements Callbacks
  {
    public int count = 0;

    public CountCallback()
    {

    }

    @SuppressWarnings("unused") @Override public void call(
      final OutputStream out,
      final String destination,
      final Level level,
      final String message)
    {
      this.count = this.count + 1;
    }
  }

  /*
   * Initialization.
   */

  @SuppressWarnings("static-method") @Test public void test()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");
    final Log log_main_b = new Log(log_main, "b");
    final Log log_main_c = new Log(log_main, "c");

    Assert.assertEquals(log_main.getDestination(), "main");

    Assert.assertEquals(log_main_a.getDestination(), "a");
    Assert.assertEquals(log_main_a.getAbsoluteDestination(), "main.a");

    Assert.assertEquals(log_main_b.getDestination(), "b");
    Assert.assertEquals(log_main_b.getAbsoluteDestination(), "main.b");

    Assert.assertEquals(log_main_c.getDestination(), "c");
    Assert.assertEquals(log_main_c.getAbsoluteDestination(), "main.c");
  }

  @SuppressWarnings("static-method") @Test public void testLogConfig0()
  {
    final Properties properties = new Properties();
    properties.put("io7m.jlog.logs.main", "true");
    properties.put("io7m.jlog.logs.main.a", "true");
    properties.put("io7m.jlog.logs.main.b", "false");

    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");
    final Log log_main_b = new Log(log_main, "b");

    Assert.assertTrue(log_main.enabledByConfiguration());
    Assert.assertTrue(log_main_a.enabledByConfiguration());
    Assert.assertFalse(log_main_b.enabledByConfiguration());

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.write("main");
    Assert.assertEquals(1, callback.count);
    log_main_a.write("a");
    Assert.assertEquals(2, callback.count);
    log_main_b.write("b");
    Assert.assertEquals(2, callback.count);
  }

  @SuppressWarnings("static-method") @Test public void testLogConfig1()
  {
    final Properties properties = new Properties();
    properties.put("io7m.jlog.logs.main", "false");

    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");
    final Log log_main_b = new Log(log_main, "b");

    Assert.assertFalse(log_main.enabledByConfiguration());
    Assert.assertFalse(log_main_a.enabledByConfiguration());
    Assert.assertFalse(log_main_b.enabledByConfiguration());

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.write("main");
    Assert.assertEquals(0, callback.count);
    log_main_a.write("a");
    Assert.assertEquals(0, callback.count);
    log_main_b.write("b");
    Assert.assertEquals(0, callback.count);
  }

  @SuppressWarnings("static-method") @Test public void testLogConfig2()
  {
    final Properties properties = new Properties();
    properties.put("io7m.jlog.logs.main", "true");

    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");
    final Log log_main_b = new Log(log_main, "b");

    Assert.assertTrue(log_main.enabledByConfiguration());
    Assert.assertTrue(log_main_a.enabledByConfiguration());
    Assert.assertTrue(log_main_b.enabledByConfiguration());

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.write("main");
    Assert.assertEquals(1, callback.count);
    log_main_a.write("a");
    Assert.assertEquals(2, callback.count);
    log_main_b.write("b");
    Assert.assertEquals(3, callback.count);
  }

  @SuppressWarnings("static-method") @Test public void testLogConfig3()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");
    final Log log_main_b = new Log(log_main, "b");

    Assert.assertTrue(log_main.enabledByConfiguration());
    Assert.assertTrue(log_main_a.enabledByConfiguration());
    Assert.assertTrue(log_main_b.enabledByConfiguration());

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.write("main");
    Assert.assertEquals(1, callback.count);
    log_main_a.write("a");
    Assert.assertEquals(2, callback.count);
    log_main_b.write("b");
    Assert.assertEquals(3, callback.count);
  }

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = IllegalArgumentException.class) public
    void
    testLogConfigFailure()
  {
    final Properties properties = new Properties();
    properties.put("io7m.jlog.logs.main", "x");
    new Log(properties, "io7m.jlog", "main");
  }

  @SuppressWarnings("static-method") @Test public void testLogLevels0()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");
    final Log log_main_b = new Log(log_main, "b");

    Assert.assertTrue(log_main.enabledByLevel(Level.LOG_DEBUG));
    Assert.assertTrue(log_main_a.enabledByLevel(Level.LOG_DEBUG));
    Assert.assertTrue(log_main_b.enabledByLevel(Level.LOG_DEBUG));

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.write("main");
    Assert.assertEquals(1, callback.count);
    log_main_a.write("a");
    Assert.assertEquals(2, callback.count);
    log_main_b.write("b");
    Assert.assertEquals(3, callback.count);
  }

  /*
   * Stream selection.
   */

  @SuppressWarnings("static-method") @Test public void testLogLevels1()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");
    final Log log_main_b = new Log(log_main, "b");

    log_main.setLevel(Level.LOG_CRITICAL);

    Assert.assertFalse(log_main.enabledByLevel(Level.LOG_DEBUG));
    Assert.assertFalse(log_main_a.enabledByLevel(Level.LOG_DEBUG));
    Assert.assertFalse(log_main_b.enabledByLevel(Level.LOG_DEBUG));

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.write("main");
    Assert.assertEquals(0, callback.count);
    log_main_a.write("a");
    Assert.assertEquals(0, callback.count);
    log_main_b.write("b");
    Assert.assertEquals(0, callback.count);
  }

  @SuppressWarnings("static-method") @Test public void testLogLevels2()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");

    Assert.assertTrue(log_main.enabledByLevel(Level.LOG_DEBUG));

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.write(Level.LOG_DEBUG, "main");
    Assert.assertEquals(1, callback.count);
    log_main.write(Level.LOG_INFO, "main");
    Assert.assertEquals(2, callback.count);
    log_main.write(Level.LOG_WARN, "main");
    Assert.assertEquals(3, callback.count);
    log_main.write(Level.LOG_ERROR, "main");
    Assert.assertEquals(4, callback.count);
    log_main.write(Level.LOG_CRITICAL, "main");
  }

  @SuppressWarnings("static-method") @Test public void testLogLevels3()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");

    Assert.assertTrue(log_main.enabledByLevel(Level.LOG_DEBUG));

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.debug("main");
    Assert.assertEquals(1, callback.count);
    log_main.info("main");
    Assert.assertEquals(2, callback.count);
    log_main.warn("main");
    Assert.assertEquals(3, callback.count);
    log_main.error("main");
    Assert.assertEquals(4, callback.count);
    log_main.critical("main");
  }

  /*
   * Disabling logs by configuration disables logging!
   */

  @SuppressWarnings("static-method") @Test public void testLogLevelsConfig0()
  {
    final Properties properties = new Properties();
    properties.put("io7m.jlog.level", "LOG_DEBUG");

    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");
    final Log log_main_b = new Log(log_main, "b");

    Assert.assertTrue(log_main.enabledByLevel(Level.LOG_DEBUG));
    Assert.assertTrue(log_main_a.enabledByLevel(Level.LOG_DEBUG));
    Assert.assertTrue(log_main_b.enabledByLevel(Level.LOG_DEBUG));

    final CountCallback callback = new CountCallback();
    log_main.setCallback(callback);

    Assert.assertEquals(0, callback.count);
    log_main.write("main");
    Assert.assertEquals(1, callback.count);
    log_main_a.write("a");
    Assert.assertEquals(2, callback.count);
    log_main_b.write("b");
    Assert.assertEquals(3, callback.count);
  }

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = IllegalArgumentException.class) public
    void
    testLogLevelsConfigFail0()
  {
    final Properties properties = new Properties();
    properties.put("io7m.jlog.level", "x");
    new Log(properties, "io7m.jlog", "main");
  }

  @SuppressWarnings("static-method") @Test public void testLogStreamFailure()
  {
    final Properties properties = new Properties();
    properties.put("io7m.jlog.logs.main", "true");
    final Log log = new Log(properties, "io7m.jlog", "main");
    final OutputStream stream = new OutputStream() {
      @Override public void write(
        final int x)
        throws IOException
      {
        throw new IOException("failure");
      }
    };
    log.setOutputStream(stream);
    log.write("failed!");
  }

  @SuppressWarnings("static-method") @Test(
    expected = IllegalArgumentException.class) public void testNullCallback()
  {
    final Log log = new Log(new Properties(), "io7m.jlog", "main");
    log.setCallback(null);
  }

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = IllegalArgumentException.class) public
    void
    testNullDestination0()
  {
    final Properties properties = new Properties();
    new Log(properties, "io7m.jlog", null);
  }

  /*
   * Levels.
   */

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = IllegalArgumentException.class) public
    void
    testNullDestination1()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");
    new Log(log_main, null);
  }

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = IllegalArgumentException.class) public void testNullParent()
  {
    new Log(null, "a");
  }

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = IllegalArgumentException.class) public void testNullPrefix()
  {
    final Properties properties = new Properties();
    new Log(properties, null, "main");
  }

  @SuppressWarnings({ "unused", "static-method" }) @Test(
    expected = IllegalArgumentException.class) public
    void
    testNullProperties()
  {
    new Log(null, "io7m.jlog", "main");
  }

  @SuppressWarnings("static-method") @Test(
    expected = IllegalArgumentException.class) public void testStreamNull()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");
    log_main.setOutputStream(null);
  }

  /**
   * Output goes to the stream of the parent when a new stream is not
   * specified.
   */

  @SuppressWarnings("static-method") @Test public void testStreamSelection0()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");

    final ByteArrayOutputStream bos_main = new ByteArrayOutputStream();

    log_main.setCallback(Log.defaultCallback);
    log_main.setOutputStream(bos_main);

    log_main.write("main");
    Assert.assertEquals("main : debug : main\n", bos_main.toString());
    bos_main.reset();

    log_main_a.write("a");
    Assert.assertEquals("main.a : debug : a\n", bos_main.toString());
  }

  /*
   * Stream failure swallows exception.
   */

  /**
   * Output goes to the correct streams when they are explicitly specified.
   */

  @SuppressWarnings("static-method") @Test public void testStreamSelection1()
  {
    final Properties properties = new Properties();
    final Log log_main = new Log(properties, "io7m.jlog", "main");
    final Log log_main_a = new Log(log_main, "a");

    final ByteArrayOutputStream bos_main = new ByteArrayOutputStream();
    final ByteArrayOutputStream bos_a = new ByteArrayOutputStream();

    log_main.setCallback(Log.defaultCallback);
    log_main.setOutputStream(bos_main);
    log_main_a.setOutputStream(bos_a);

    log_main.write("main");
    log_main_a.write("a");

    System.out.println(bos_main.toString());
    System.out.println(bos_a.toString());

    Assert.assertEquals("main : debug : main\n", bos_main.toString());
    Assert.assertEquals("main.a : debug : a\n", bos_a.toString());
  }
}
