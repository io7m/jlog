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
 * Log interfaces that allow reading of their configuration.
 * </p>
 * <p>
 * All functions are thread-safe; any number of threads may call any of the
 * functions without explicit synchronization.
 * </p>
 */

public interface LogConfigReadableType
{
  /**
   * <p>
   * Retrieve the name of the current absolute destination. This will be a
   * fully qualified name such as <code>com.io7m.jlog.example</code>.
   * </p>
   * 
   * @return The fully-qualified destination.
   */

  String getAbsoluteDestination();

  /**
   * @return <code>true</code> if the log interface is enabled.
   */

  boolean isEnabled();

  /**
   * @param level
   *          The intended level
   * @return <code>true</code> if writing to the current log at the given
   *         level would actually result in a message.
   */

  boolean wouldLog(
    final LogLevel level);

  /**
   * @param level
   *          The intended level
   * @return <code>true</code> if writing to the current log at the given
   *         level would actually result in a message, if the log was enabled.
   * @see #wouldLog(LogLevel)
   */

  boolean wouldLogAtLevel(
    final LogLevel level);
}
