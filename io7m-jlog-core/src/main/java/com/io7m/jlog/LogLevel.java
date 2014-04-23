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
 * A description of a log level.
 */

public enum LogLevel
{
  /**
   * Critical messages. The most important message level.
   */

  LOG_CRITICAL(4, "critical"),

  /**
   * Debug messages. The least important message level.
   */

  LOG_DEBUG(0, "debug"),

  /**
   * Error messages. More important than {@link #LOG_WARN} but less important
   * than {@link #LOG_CRITICAL}.
   */

  LOG_ERROR(3, "error"),

  /**
   * Info messages. More important than {@link #LOG_DEBUG} but less important
   * than {@link #LOG_WARN}.
   */

  LOG_INFO(1, "info"),

  /**
   * Warning messages. More important than {@link #LOG_INFO} but less
   * important than {@link #LOG_ERROR}.
   */

  LOG_WARN(2, "warn");

  private final int    importance;
  private final String name;

  private LogLevel(
    final int in_importance,
    final String in_name)
  {
    this.importance = in_importance;
    this.name = in_name;
  }

  /**
   * @return The importance of this level, relative to {@link #LOG_DEBUG}.
   */

  public int getImportance()
  {
    return this.importance;
  }

  /**
   * @return The name of the log level
   */

  public String getName()
  {
    return this.name;
  }
}
