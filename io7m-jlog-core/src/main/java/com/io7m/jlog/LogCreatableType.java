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
 * The type of logging interfaces that can create new log interfaces.
 * </p>
 * <p>
 * All functions are thread-safe; any number of threads may call any of the
 * functions without explicit synchronization.
 * </p>
 */

public interface LogCreatableType
{
  /**
   * <p>
   * Construct a new child {@link LogType} interface, with parent
   * <code>parent</code>. The string <code>destination</code> will be appended
   * to that of the parent.
   * </p>
   * 
   * @return A new log interface
   * @param destination
   *          The destination to append to the parent
   */

  LogType with(
    final String destination);
}
