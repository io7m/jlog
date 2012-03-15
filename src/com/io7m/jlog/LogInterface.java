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
  void critical(
    final @Nonnull String message);

  void debug(
    final @Nonnull String message);

  /*
   * Roots.
   */

  boolean enabled(
    final @Nonnull Level level);

  boolean enabledByConfiguration();

  /*
   * Destinations.
   */

  boolean enabledByLevel(
    final @Nonnull Level level);

  void error(
    final @Nonnull String message);

  /*
   * Levels.
   */

  @Nonnull String getAbsoluteDestination();

  @Nonnull String getDestination();

  /*
   * Message writing.
   */

  @Nonnull Level getLevel();

  @Nonnull LogInterface getRoot();

  void info(
    final @Nonnull String message);

  boolean isRoot();

  void setCallback(
    final @Nonnull Callbacks callback);

  void setLevel(
    final @Nonnull Level level);

  void setOutputStream(
    final @Nonnull OutputStream stream);

  /*
   * Enabled by level/configuration.
   */

  void warn(
    final @Nonnull String message);

  void write(
    final @Nonnull Level level,
    final @Nonnull String message);

  void write(
    final @Nonnull String message);
}
