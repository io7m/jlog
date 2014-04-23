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

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.io7m.jnull.NullCheck;
import com.io7m.jproperties.JProperties;
import com.io7m.jproperties.JPropertyException;
import com.io7m.jproperties.JPropertyIncorrectType;

/**
 * <p>
 * Log policy based on {@link java.util.Properties}.
 * </p>
 * <p>
 * The default enabled/disabled state for log interfaces are parsed from the
 * given properties using a <i>prefix</i>. Specific interfaces effectively
 * inherit their default enabled/disabled state from their closest ancestor.
 * So, if an interface named <code>main</code> is set to be disabled by
 * default, then an interface named <code>main.a</code> will also be disabled
 * by default.
 * </p>
 * <p>
 * As an example, given the prefix <code>com.io7m.tests</code> and the
 * following properties:
 * </p>
 * 
 * <pre>
 * com.io7m.tests.level         = LOG_DEBUG
 * com.io7m.tests.logs          = true
 * com.io7m.tests.logs.main     = true
 * com.io7m.tests.logs.main.a   = false
 * com.io7m.tests.logs.main.b.a = false
 * </pre>
 * <p>
 * The initial log level will be {@link LogLevel#LOG_DEBUG}, an interface
 * named <code>main</code> will be disabled by default, as will
 * <code>main.a</code> and <code>main.b.a</code>. However, there is no
 * specific entry for an interface <code>main.b</code>, and the closest
 * ancestor of <code>main.b</code> is <code>main</code>, so
 * <code>main.b</code> will inherit <code>main</code>'s default state and will
 * be enabled by default. If there is no ancestor, the default value is used,
 * specified by <code>com.io7m.tests.logs</code>.
 * </p>
 */

public final class LogPolicyProperties implements LogPolicyType
{
  /**
   * <p>
   * Construct a new log policy, reading properties prefixed with
   * <code>property_prefix</code> from <code>props</code>.
   * </p>
   * 
   * @param props
   *          The properties.
   * @param property_prefix
   *          The property prefix.
   * @return A new log policy.
   * @throws JPropertyException
   *           If the given properties are invalid.
   */

  public static LogPolicyType newPolicy(
    final Properties props,
    final String property_prefix)
    throws JPropertyException
  {
    return new LogPolicyProperties(props, property_prefix);
  }

  private final ConcurrentHashMap<String, Boolean> enabled;
  private boolean                                  enabled_default;
  private LogLevel                                 level;
  private final Properties                         properties;

  private LogPolicyProperties(
    final Properties props,
    final String prefix)
    throws JPropertyException
  {
    this.properties = NullCheck.notNull(props, "Properties");
    NullCheck.notNull(prefix, "Property prefix");

    {
      final String level_key = prefix + ".level";
      final String debug = LogLevel.LOG_DEBUG.toString();
      assert debug != null;

      final String level_text =
        JProperties.getStringOptional(this.properties, level_key, debug);

      try {
        this.level = LogLevel.valueOf(level_text);
      } catch (final IllegalArgumentException x) {
        final StringBuilder message = new StringBuilder();
        message.append("Could not parse key '");
        message.append(level_key);
        message.append("' as a value of type ");
        message.append(LogLevel.class.getCanonicalName());
        final String r = message.toString();
        assert r != null;
        throw new JPropertyIncorrectType(r);
      }
    }

    {
      final String default_key = prefix + ".logs";
      this.enabled_default =
        JProperties.getBooleanOptional(this.properties, default_key, true);
    }

    {
      this.enabled = new ConcurrentHashMap<String, Boolean>();
      final String logs_prefix = prefix + ".logs.";
      final Enumeration<Object> e = this.properties.keys();
      while (e.hasMoreElements()) {
        final String key = (String) e.nextElement();
        if (key.startsWith(logs_prefix)) {
          final String rest = key.substring(logs_prefix.length());
          final boolean value = JProperties.getBoolean(this.properties, key);
          this.enabled.put(rest, Boolean.valueOf(value));
        }
      }
    }
  }

  @Override public LogLevel policyDefaultLevel()
  {
    return this.level;
  }

  @Override public boolean policyDestinationEnabled(
    final String destination)
  {
    String key = NullCheck.notNull(destination, "Destination");

    for (;;) {
      if (this.enabled.containsKey(key)) {
        return this.enabled.get(key).booleanValue();
      }

      final int last_dot = key.lastIndexOf('.');
      if (last_dot == -1) {
        break;
      }

      key = key.substring(0, last_dot);
    }

    return this.enabled_default;
  }
}
