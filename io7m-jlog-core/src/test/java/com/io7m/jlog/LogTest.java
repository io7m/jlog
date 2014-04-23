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

package com.io7m.jlog;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jnull.NullCheckException;

@SuppressWarnings("static-method") public final class LogTest
{
  @SuppressWarnings("null") private void logExercise(
    final LogType log_main)
  {
    final AtomicReference<LogConfigReadableType> saved_log =
      new AtomicReference<LogConfigReadableType>();
    final AtomicReference<LogLevel> saved_level =
      new AtomicReference<LogLevel>();
    final AtomicReference<String> saved_message =
      new AtomicReference<String>();

    final LogCallbackType cb = new LogCallbackType() {
      @Override public void call(
        final LogConfigReadableType log,
        final LogLevel level,
        final String message)
      {
        saved_log.set(log);
        saved_level.set(level);
        saved_message.set(message);
      }
    };

    log_main.setCallback(cb);

    for (final LogLevel v : LogLevel.values()) {
      switch (v) {
        case LOG_CRITICAL:
        {
          log_main.critical(v.toString());
          break;
        }
        case LOG_DEBUG:
        {
          log_main.debug(v.toString());
          break;
        }
        case LOG_ERROR:
        {
          log_main.error(v.toString());
          break;
        }
        case LOG_INFO:
        {
          log_main.info(v.toString());
          break;
        }
        case LOG_WARN:
        {
          log_main.warn(v.toString());
          break;
        }
      }

      Assert.assertEquals(log_main, saved_log.get());
      Assert.assertEquals(v, saved_level.get());
      Assert.assertEquals(v.toString(), saved_message.get());
      saved_log.set(null);
      saved_level.set(null);
      saved_message.set(null);
    }

    for (final LogLevel v : LogLevel.values()) {
      log_main.write(v, v.toString());
      Assert.assertEquals(log_main, saved_log.get());
      Assert.assertEquals(v, saved_level.get());
      Assert.assertEquals(v.toString(), saved_message.get());
      saved_log.set(null);
      saved_level.set(null);
      saved_message.set(null);
    }
  }

  @Test(expected = IllegalArgumentException.class) public
    void
    testBadName_0()
  {
    Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main.x");
  }

  @Test(expected = NullCheckException.class) public void testBadName_1()
  {
    final LogType log =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");

    log.with((String) TestUtilities.actuallyNull());
  }

  @Test(expected = IllegalArgumentException.class) public
    void
    testBadName_2()
  {
    final LogType log =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");

    log.with("main.x");
  }

  @Test(expected = NullCheckException.class) public void testCallback_0()
  {
    final LogType log_main =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");
    Assert.assertEquals("main", log_main.getAbsoluteDestination());

    log_main.setCallback((LogCallbackType) TestUtilities.actuallyNull());
  }

  @Test public void testInit()
  {
    final LogType log_main =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");
    Assert.assertEquals("main", log_main.getAbsoluteDestination());

    final LogType log_a = log_main.with("a");
    final LogType log_b = log_main.with("b");
    final LogType log_c = log_main.with("c");
    Assert.assertEquals("main.a", log_a.getAbsoluteDestination());
    Assert.assertEquals("main.b", log_b.getAbsoluteDestination());
    Assert.assertEquals("main.c", log_c.getAbsoluteDestination());

    final LogType log_aa = log_a.with("a");
    final LogType log_ab = log_a.with("b");
    final LogType log_ac = log_a.with("c");
    Assert.assertEquals("main.a.a", log_aa.getAbsoluteDestination());
    Assert.assertEquals("main.a.b", log_ab.getAbsoluteDestination());
    Assert.assertEquals("main.a.c", log_ac.getAbsoluteDestination());

    final LogType log_aax = log_a.with("a");
    final LogType log_abx = log_a.with("b");
    final LogType log_acx = log_a.with("c");
    Assert.assertEquals("main.a.a", log_aax.getAbsoluteDestination());
    Assert.assertEquals("main.a.b", log_abx.getAbsoluteDestination());
    Assert.assertEquals("main.a.c", log_acx.getAbsoluteDestination());

    Assert.assertEquals(log_aa, log_aax);
    Assert.assertEquals(log_ab, log_abx);
    Assert.assertEquals(log_ac, log_acx);
  }

  @Test public void testLogging_0()
  {
    final LogType log_main =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");
    Assert.assertEquals("main", log_main.getAbsoluteDestination());

    this.logExercise(log_main);
  }

  @SuppressWarnings("null") @Test public void testLogging_1()
  {
    for (final LogLevel v : LogLevel.values()) {
      final LogType log = Log.newLog(LogPolicyAllOn.newPolicy(v), "main");

      final AtomicReference<LogConfigReadableType> saved_log =
        new AtomicReference<LogConfigReadableType>();
      final AtomicReference<LogLevel> saved_level =
        new AtomicReference<LogLevel>();
      final AtomicReference<String> saved_message =
        new AtomicReference<String>();

      final LogCallbackType cb = new LogCallbackType() {
        @Override public void call(
          final LogConfigReadableType rlog,
          final LogLevel level,
          final String message)
        {
          saved_log.set(rlog);
          saved_level.set(level);
          saved_message.set(message);
        }
      };

      log.setCallback(cb);

      for (final LogLevel q : LogLevel.values()) {
        log.write(q, q.toString());

        if (q.getImportance() >= v.getImportance()) {
          Assert.assertEquals(log, saved_log.get());
          Assert.assertEquals(q, saved_level.get());
          Assert.assertEquals(q.toString(), saved_message.get());
          saved_log.set(null);
          saved_level.set(null);
          saved_message.set(null);
        } else {
          Assert.assertNull(saved_log.get());
          Assert.assertNull(saved_level.get());
          Assert.assertNull(saved_message.get());
        }
      }
    }
  }

  @Test public void testLogging_2()
  {
    final LogType log_main =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");
    Assert.assertEquals("main", log_main.getAbsoluteDestination());
    final LogType log_alt = log_main.with("alt");
    this.logExercise(log_alt);
  }

  @SuppressWarnings("null") @Test public void testLogging_3()
  {
    final LogType log =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");

    final AtomicReference<LogConfigReadableType> saved_log =
      new AtomicReference<LogConfigReadableType>();
    final AtomicReference<LogLevel> saved_level =
      new AtomicReference<LogLevel>();
    final AtomicReference<String> saved_message =
      new AtomicReference<String>();

    final LogCallbackType cb = new LogCallbackType() {
      @Override public void call(
        final LogConfigReadableType rlog,
        final LogLevel level,
        final String message)
      {
        saved_log.set(rlog);
        saved_level.set(level);
        saved_message.set(message);
      }
    };

    log.setCallback(cb);

    for (final LogLevel v : LogLevel.values()) {
      log.setLevel(v);

      for (final LogLevel q : LogLevel.values()) {
        Assert.assertTrue(log.isEnabled());

        log.write(q, q.toString());

        if (q.getImportance() >= v.getImportance()) {
          Assert.assertTrue(log.wouldLog(q));
          Assert.assertTrue(log.wouldLogAtLevel(q));
          Assert.assertEquals(log, saved_log.get());
          Assert.assertEquals(q, saved_level.get());
          Assert.assertEquals(q.toString(), saved_message.get());
          saved_log.set(null);
          saved_level.set(null);
          saved_message.set(null);
        } else {
          Assert.assertFalse(log.wouldLogAtLevel(q));
          Assert.assertFalse(log.wouldLog(q));
          Assert.assertNull(saved_log.get());
          Assert.assertNull(saved_level.get());
          Assert.assertNull(saved_message.get());
        }
      }
    }
  }

  @SuppressWarnings("null") @Test public void testLogging_4()
  {
    final LogType log =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "main");

    final AtomicReference<LogConfigReadableType> saved_log =
      new AtomicReference<LogConfigReadableType>();
    final AtomicReference<LogLevel> saved_level =
      new AtomicReference<LogLevel>();
    final AtomicReference<String> saved_message =
      new AtomicReference<String>();

    final LogCallbackType cb = new LogCallbackType() {
      @Override public void call(
        final LogConfigReadableType rlog,
        final LogLevel level,
        final String message)
      {
        saved_log.set(rlog);
        saved_level.set(level);
        saved_message.set(message);
      }
    };

    log.setCallback(cb);

    for (final LogLevel v : LogLevel.values()) {
      final LogType alt = log.with("alt");

      alt.setLevel(v);

      for (final LogLevel q : LogLevel.values()) {
        Assert.assertTrue(alt.isEnabled());

        alt.write(q, q.toString());

        if (q.getImportance() >= v.getImportance()) {
          Assert.assertTrue(alt.wouldLog(q));
          Assert.assertTrue(alt.wouldLogAtLevel(q));
          Assert.assertEquals(alt, saved_log.get());
          Assert.assertEquals(q, saved_level.get());
          Assert.assertEquals(q.toString(), saved_message.get());
          saved_log.set(null);
          saved_level.set(null);
          saved_message.set(null);
        } else {
          Assert.assertFalse(alt.wouldLogAtLevel(q));
          Assert.assertFalse(alt.wouldLog(q));
          Assert.assertNull(saved_log.get());
          Assert.assertNull(saved_level.get());
          Assert.assertNull(saved_message.get());
        }
      }
    }
  }
}
