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

package com.io7m.jlog;

import java.io.OutputStream;

import javax.annotation.Nonnull;

public interface LogInterface
{
  /**
   * Log a message <code>message</code> of severity <code>LOG_CRITICAL</code>.
   * 
   * @see LogInterface#write(Level, String)
   */

  void critical(
    final @Nonnull String message);

  /**
   * Log a message <code>message</code> of severity <code>LOG_DEBUG</code>.
   * 
   * @see LogInterface#write(Level, String)
   */

  void debug(
    final @Nonnull String message);

  /**
   * Return <code>true</code> iff attempting to log a message of severity
   * <code>level</code> would actually result in a message being logged, given
   * the current level setting and log configuration.
   */

  boolean enabled(
    final @Nonnull Level level);

  /**
   * Return <code>true</code> iff attempting to log any message would actually
   * result in a message being logged, given the current log configuration.
   */

  boolean enabledByConfiguration();

  /**
   * Return <code>true</code> iff attempting to log a message of severity
   * <code>level</code> would actually result in a message being logged, given
   * the current level setting.
   */

  boolean enabledByLevel(
    final @Nonnull Level level);

  /**
   * Log a message <code>message</code> of severity <code>LOG_ERROR</code>.
   * 
   * @see LogInterface#write(Level, String)
   */

  void error(
    final @Nonnull String message);

  /**
   * Retrieve the name of the current absolute destination. This will be a
   * fully qualified name such as <code>com.io7m.jlog.example</code>.
   */

  @Nonnull String getAbsoluteDestination();

  /**
   * Retrieve the name of the current destination.
   */

  @Nonnull String getDestination();

  /**
   * Retrieve the current level setting.
   */

  @Nonnull Level getLevel();

  /**
   * Retrieve the root of the current tree of log handles.
   */

  @Nonnull LogInterface getRoot();

  /**
   * Log a message <code>message</code> of severity <code>LOG_INFO</code>.
   * 
   * @see LogInterface#write(Level, String)
   */

  void info(
    final @Nonnull String message);

  /**
   * Return <code>true</code> iff this log handle has no parent.
   */

  boolean isRoot();

  void setCallback(
    final @Nonnull Callbacks callback);

  /**
   * Set the current log level to <code>level</code>.
   */

  void setLevel(
    final @Nonnull Level level);

  /**
   * Set the output stream for this log handle.
   */

  void setOutputStream(
    final @Nonnull OutputStream stream);

  /**
   * Log a message <code>message</code> of severity <code>LOG_WARN</code>.
   * 
   * @see LogInterface#write(Level, String)
   */

  void warn(
    final @Nonnull String message);

  /**
   * Log a message <code>message</code> of severity <code>level</code>.
   */

  void write(
    final @Nonnull Level level,
    final @Nonnull String message);

  @Deprecated void write(
    final @Nonnull String message);
}
