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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.io7m.jaux.UnreachableCodeException;

@ThreadSafe public final class LevelName
{
  public static final int LEVEL_STRING_LONGEST = "critical".length();

  public static @Nonnull String get(
    final @Nonnull Level level)
  {
    switch (level) {
      case LOG_DEBUG:
        return "debug";
      case LOG_INFO:
        return "info";
      case LOG_WARN:
        return "warn";
      case LOG_ERROR:
        return "error";
      case LOG_CRITICAL:
        return "critical";
      default:
        throw new UnreachableCodeException();
    }
  }
}
