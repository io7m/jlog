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

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

@EqualityReference final class LogChild extends LogBase
{
  private final SortedMap<String, LogChild> children;
  private final String                      destination;
  private final AtomicBoolean               enabled;
  private final LogRoot                     root;

  LogChild(
    final LogRoot in_root,
    final LogBase in_parent,
    final String in_destination)
  {
    this.root = NullCheck.notNull(in_root, "Root");

    {
      final StringBuilder b = new StringBuilder();
      b.append(in_parent.getAbsoluteDestination());
      b.append(".");
      b.append(NullCheck.notNull(in_destination, "Destination"));
      final String r = b.toString();
      assert r != null;
      this.destination = r;
    }

    this.children = new TreeMap<String, LogChild>();
    this.enabled =
      new AtomicBoolean(in_root.getPolicy().policyDestinationEnabled(
        this.destination));
  }

  @Override public void critical(
    final String message)
  {
    this.root.writeActual(
      this,
      LogLevel.LOG_CRITICAL,
      this.destination,
      message);
  }

  @Override public void debug(
    final String message)
  {
    this.root
      .writeActual(this, LogLevel.LOG_DEBUG, this.destination, message);
  }

  @Override public void error(
    final String message)
  {
    this.root
      .writeActual(this, LogLevel.LOG_ERROR, this.destination, message);
  }

  @Override public String getAbsoluteDestination()
  {
    return this.destination;
  }

  @Override public void info(
    final String message)
  {
    this.root.writeActual(this, LogLevel.LOG_INFO, this.destination, message);
  }

  @Override public boolean isEnabled()
  {
    return this.enabled.get();
  }

  @Override public void setCallback(
    final LogCallbackType callback)
  {
    NullCheck.notNull(callback, "Callback");
    this.root.setCallback(callback);
  }

  @Override public void setLevel(
    final LogLevel level)
  {
    this.root.setLevel(level);
  }

  @Override public void warn(
    final String message)
  {
    this.root.writeActual(this, LogLevel.LOG_WARN, this.destination, message);
  }

  @Override public LogType with(
    final String new_destination)
  {
    final String actual =
      LogDestinationName.checkDestinationName(new_destination);

    synchronized (this.children) {
      if (this.children.containsKey(actual)) {
        final LogChild c = this.children.get(actual);
        assert c != null;
        return c;
      }

      final LogChild c = new LogChild(this.root, this, actual);
      this.children.put(actual, c);
      return c;
    }
  }

  @Override public boolean wouldLog(
    final LogLevel level)
  {
    return this.isEnabled() && this.root.wouldLogAtLevel(level);
  }

  @Override public boolean wouldLogAtLevel(
    final LogLevel level)
  {
    return this.root.wouldLogAtLevel(level);
  }

  @Override public void write(
    final LogLevel level,
    final String message)
  {
    this.root.writeActual(this, level, this.destination, message);
  }
}
