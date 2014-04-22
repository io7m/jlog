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

/**
 * <p>
 * The type of writable logs.
 * </p>
 * <p>
 * Logging is thread-safe; any number of threads may log to the same log
 * interface.
 * </p>
 */

public interface LogWritableType
{
  /**
   * <p>
   * Log a message <code>message</code> of level {@link LogLevel#LOG_CRITICAL}
   * .
   * </p>
   * 
   * @param message
   *          The message to be written
   * @see LogWritableType#write(LogLevel, String)
   */

  void critical(
    final String message);

  /**
   * <p>
   * Log a message <code>message</code> of level {@link LogLevel#LOG_DEBUG} .
   * </p>
   * 
   * @param message
   *          The message to be written
   * @see LogWritableType#write(LogLevel, String)
   */

  void debug(
    final String message);

  /**
   * <p>
   * Log a message <code>message</code> of level {@link LogLevel#LOG_ERROR} .
   * </p>
   * 
   * @param message
   *          The message to be written
   * @see LogWritableType#write(LogLevel, String)
   */

  void error(
    final String message);

  /**
   * <p>
   * Log a message <code>message</code> of level {@link LogLevel#LOG_INFO}.
   * </p>
   * 
   * @param message
   *          The message to be written
   * @see LogWritableType#write(LogLevel, String)
   */

  void info(
    final String message);

  /**
   * <p>
   * Log a message <code>message</code> of level {@link LogLevel#LOG_WARN} .
   * </p>
   * 
   * @param message
   *          The message to be written
   * @see LogWritableType#write(LogLevel, String)
   */

  void warn(
    final String message);

  /**
   * <p>
   * Log a message <code>message</code> of at the given log level.
   * </p>
   * 
   * @param level
   *          The log level
   * @param message
   *          The message to be written
   */

  void write(
    final LogLevel level,
    final String message);

}
