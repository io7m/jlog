/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
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

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jlog.Log;
import com.io7m.jlog.LogCallbackType;
import com.io7m.jlog.LogConfigReadableType;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyProperties;
import com.io7m.jlog.LogPolicyType;
import com.io7m.jlog.LogType;
import com.io7m.jproperties.JPropertyException;

@SuppressWarnings("static-method") public final class LogPolicyTest
{
  private static final class Counter implements LogCallbackType
  {
    AtomicInteger count;

    public Counter()
    {
      this.count = new AtomicInteger(0);
    }

    @Override public void call(
      final LogConfigReadableType log,
      final LogLevel level,
      final String message)
    {
      this.count.incrementAndGet();
    }
  }

  @Test public void testWouldLog()
    throws JPropertyException
  {
    final Properties p = new Properties();
    p.setProperty("test.level", "LOG_DEBUG");
    p.setProperty("test.logs.x", "true");
    p.setProperty("test.logs.x.y", "false");
    p.setProperty("test.logs.x.y.z", "true");

    final Counter counter = new Counter();

    final LogPolicyType lp = LogPolicyProperties.newPolicy(p, "test");
    final LogType log_x = Log.newLog(lp, "x");
    final LogType log_xy = log_x.with("y");
    final LogType log_xyz = log_xy.with("z");

    Assert.assertTrue(log_x.wouldLog(LogLevel.LOG_DEBUG));
    Assert.assertFalse(log_xy.wouldLog(LogLevel.LOG_DEBUG));
    Assert.assertTrue(log_xyz.wouldLog(LogLevel.LOG_DEBUG));

    log_x.setCallback(counter);
    int expected = 0;

    for (final LogLevel l : LogLevel.values()) {
      assert l != null;
      log_x.write(l, "x");
      expected = expected + 1;
    }

    Assert.assertEquals(expected, counter.count.intValue());

    for (final LogLevel l : LogLevel.values()) {
      assert l != null;
      log_xy.write(l, "xy");
    }

    Assert.assertEquals(expected, counter.count.intValue());

    for (final LogLevel l : LogLevel.values()) {
      assert l != null;
      log_xyz.write(l, "xyz");
      expected = expected + 1;
    }

    Assert.assertEquals(expected, counter.count.intValue());
  }
}
