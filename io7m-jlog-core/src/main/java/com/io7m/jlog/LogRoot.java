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

import java.io.PrintWriter;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.io7m.jequality.annotations.EqualityReference;
import com.io7m.jnull.NullCheck;

@EqualityReference final class LogRoot extends LogBase
{
  private final AtomicReference<LogCallbackType> callback;
  private final SortedMap<String, LogChild>      children;
  private final String                           destination;
  private final AtomicBoolean                    enabled;
  private final AtomicReference<LogLevel>        level;
  private final LogPolicyType                    policy;
  private final PrintWriter                      writer;

  LogRoot(
    final LogPolicyType in_policy,
    final String in_destination)
  {
    this.policy = NullCheck.notNull(in_policy, "Policy");
    this.destination =
      LogDestinationName.checkDestinationName(in_destination);

    this.level =
      new AtomicReference<LogLevel>(NullCheck.notNull(
        this.policy.policyDefaultLevel(),
        "Log level"));

    this.enabled = new AtomicBoolean(true);
    this.callback = new AtomicReference<LogCallbackType>();
    this.children = new TreeMap<String, LogChild>();
    this.writer = new PrintWriter(System.err);
  }

  @Override public void critical(
    final String message)
  {
    this.writeActual(this, LogLevel.LOG_CRITICAL, this.destination, message);
  }

  @Override public void debug(
    final String message)
  {
    this.writeActual(this, LogLevel.LOG_DEBUG, this.destination, message);
  }

  @Override public void error(
    final String message)
  {
    this.writeActual(this, LogLevel.LOG_ERROR, this.destination, message);
  }

  @Override public String getAbsoluteDestination()
  {
    return this.destination;
  }

  protected LogPolicyType getPolicy()
  {
    return this.policy;
  }

  @Override public void info(
    final String message)
  {
    this.writeActual(this, LogLevel.LOG_INFO, this.destination, message);
  }

  @Override public boolean isEnabled()
  {
    return this.enabled.get();
  }

  @Override public void setCallback(
    final LogCallbackType f)
  {
    this.callback.set(NullCheck.notNull(f, "Callback"));
  }

  @Override public void setLevel(
    final LogLevel r)
  {
    this.level.set(NullCheck.notNull(r, "Level"));
  }

  @Override public void warn(
    final String message)
  {
    this.writeActual(this, LogLevel.LOG_WARN, this.destination, message);
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

      final LogChild c = new LogChild(this, this, actual);
      this.children.put(actual, c);
      return c;
    }
  }

  @Override public boolean wouldLog(
    final LogLevel in_level)
  {
    return this.isEnabled() && this.wouldLogAtLevel(in_level);
  }

  @Override public boolean wouldLogAtLevel(
    final LogLevel target_level)
  {
    NullCheck.notNull(target_level, "Level");
    final LogLevel current_level = this.level.get();
    final int current_importance = current_level.getImportance();
    final int target_importance = target_level.getImportance();
    return target_importance >= current_importance;
  }

  @Override public void write(
    final LogLevel in_level,
    final String in_message)
  {
    this.writeActual(this, in_level, this.destination, in_message);
  }

  protected void writeActual(
    final LogConfigReadableType caller,
    final LogLevel in_level,
    final String in_destination,
    final String message)
  {
    NullCheck.notNull(caller, "Caller");
    NullCheck.notNull(in_level, "Level");
    NullCheck.notNull(in_destination, "Destination");
    NullCheck.notNull(message, "Message");

    final int current_importance = this.level.get().getImportance();
    if (in_level.getImportance() >= current_importance) {
      final LogCallbackType cb = this.callback.get();
      if (cb != null) {
        cb.call(caller, in_level, message);
      }

      synchronized (this.writer) {
        this.writer.write(String.format(
          "%s: %s: %s\n",
          in_destination,
          in_level.getName(),
          message));
        this.writer.flush();
      }
    }
  }
}
