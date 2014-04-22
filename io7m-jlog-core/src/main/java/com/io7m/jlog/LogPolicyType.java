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
 * The type of log policies.
 * </p>
 * <p>
 * A log policy controls whether or not a given log destination will be
 * enabled or disabled by default. It also controls the default log level.
 * </p>
 * <p>
 * All functions are thread-safe; any number of threads may call any of the
 * functions without explicit synchronization.
 * </p>
 */

public interface LogPolicyType
{
  /**
   * @return The default log level for the policy.
   */

  LogLevel policyDefaultLevel();

  /**
   * @param destination
   *          The destination.
   * @return <code>true</code> if logging to the given absolute destination is
   *         enabled by the policy.
   */

  boolean policyDestinationEnabled(
    final String destination);
}
