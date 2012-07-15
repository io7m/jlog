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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public final class Log implements LogInterface
{
  private final static class DefaultCallback implements Callbacks
  {
    public DefaultCallback()
    {

    }

    @Override public void call(
      final @Nonnull OutputStream out,
      final @Nonnull String destination,
      final @Nonnull Level level,
      final @Nonnull String message)
    {
      try {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(destination);

        buffer.append(" : ");
        buffer.append(LevelName.get(level));

        buffer.append(" : ");
        buffer.append(message);
        buffer.append("\n");

        out.write(buffer.toString().getBytes());
      } catch (final IOException e) {
        System.err.println("jlog: failed to write log message: "
          + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public static final @Nonnull Callbacks defaultCallback =
                                                           new DefaultCallback();

  private static int levelInteger(
    final @Nonnull Level level)
  {
    switch (level) {
      case LOG_DEBUG:
        return 0;
      case LOG_INFO:
        return 1;
      case LOG_WARN:
        return 2;
      case LOG_ERROR:
        return 3;
      case LOG_CRITICAL:
        return 4;
      default:
        /* UNREACHABLE */
        throw new IllegalArgumentException(
          "unreachable code reached - report this bug!");
    }
  }

  private final @CheckForNull Log                                parent;
  private final @Nonnull CopyOnWriteArrayList<Log>               children;
  private final @CheckForNull ConcurrentHashMap<String, Boolean> configuration;
  private final @Nonnull AtomicReference<Callbacks>              callback =
                                                                            new AtomicReference<Callbacks>();

  private final @Nonnull AtomicReference<OutputStream>           stream   =
                                                                            new AtomicReference<OutputStream>();
  private final @Nonnull AtomicReference<Level>                  level    =
                                                                            new AtomicReference<Level>();
  private final @Nonnull String                                  destination_abs;

  /*
   * Default log callback that just writes the destination, followed by a
   * colon, followed by the given message, followed by a newline, to the
   * current output stream.
   */

  private final @Nonnull String                                  destination_local;
  private final @CheckForNull AtomicInteger                      destination_longest;

  public Log(
    final @Nonnull Log parent,
    final @Nonnull String destination)
  {
    if (parent == null) {
      throw new IllegalArgumentException("parent must not be null");
    }
    if (destination == null) {
      throw new IllegalArgumentException("destination must not be null");
    }

    this.children = new CopyOnWriteArrayList<Log>();
    this.parent = parent;
    this.parent.children.add(this);

    this.callback.set(null);
    this.stream.set(null);
    this.level.set(Level.LOG_DEBUG);
    this.configuration = null;

    this.destination_local = destination;
    this.destination_longest = null;

    final @Nonnull StringBuilder buffer = new StringBuilder();
    this.getAbsoluteDestinationInner(buffer);
    this.destination_abs = buffer.toString();
    this.setDestinationLongest(this.destination_abs.length());
  }

  public Log(
    final @Nonnull Properties properties,
    final @Nonnull String property_prefix,
    final @Nonnull String destination)
  {
    if (destination == null) {
      throw new IllegalArgumentException("destination must not be null");
    }
    if (properties == null) {
      throw new IllegalArgumentException("properties must not be null");
    }
    if (property_prefix == null) {
      throw new IllegalArgumentException("property prefix must not be null");
    }

    this.children = new CopyOnWriteArrayList<Log>();
    this.parent = null;

    this.callback.set(Log.defaultCallback);
    this.stream.set(System.err);
    this.level.set(Level.LOG_DEBUG);
    this.configuration = new ConcurrentHashMap<String, Boolean>();

    this.destination_local = destination;
    this.destination_abs = this.destination_local;
    this.destination_longest = new AtomicInteger(0);

    this.loadConfigurationProperties(properties, property_prefix);
  }

  @Override public void critical(
    final @Nonnull String message)
  {
    this.write(Level.LOG_CRITICAL, message);
  }

  @Override public void debug(
    final @Nonnull String message)
  {
    this.write(Level.LOG_DEBUG, message);
  }

  @Override public boolean enabled(
    final @Nonnull Level in_level)
  {
    return this.enabledByLevel(in_level) && this.enabledByConfiguration();
  }

  @Override public boolean enabledByConfiguration()
  {
    return this.enabledByConfigurationInner((Log) this.getRoot());
  }

  private boolean enabledByConfigurationInner(
    final @Nonnull Log root)
  {
    assert (root != null);
    assert (root.configuration != null);

    /* Configuration exists for this log? */
    if (root.configuration.containsKey(this.destination_abs)) {
      final Boolean enabled = root.configuration.get(this.destination_abs);
      assert enabled != null;
      return enabled.booleanValue();
    }

    /* Try the parent instead. */
    if (this.parent != null) {
      return this.parent.enabledByConfigurationInner(root);
    }

    /* Otherwise, assume the log is enabled. */
    return true;
  }

  @Override public boolean enabledByLevel(
    final @Nonnull Level in_level)
  {
    final int this_level = Log.levelInteger(this.getLevel());
    final int want_level = Log.levelInteger(in_level);

    return want_level >= this_level;
  }

  @Override public void error(
    final @Nonnull String message)
  {
    this.write(Level.LOG_ERROR, message);
  }

  /**
   * Get the absolute (fully qualified) destination of the current log.
   */

  @Override public String getAbsoluteDestination()
  {
    return this.destination_abs;
  }

  /**
   * Get the absolute (fully qualified) destination of the current log. The
   * function recursively prepends the destinations of the ancestor logs and
   * periods to result in a fully qualified destination of the form:
   * 
   * "ancestor3.ancestor2.parent.current"
   */

  private void getAbsoluteDestinationInner(
    final @Nonnull StringBuilder buffer)
  {
    buffer.insert(0, this.destination_local);
    if (this.parent != null) {
      buffer.insert(0, ".");
      this.parent.getAbsoluteDestinationInner(buffer);
    }
  }

  /**
   * Return the callback that will be used for the current log. The function
   * recursively takes the callback of the parent log until it finds a
   * non-null value.
   */

  private @Nonnull Callbacks getCallback()
  {
    if (this.callback.get() != null) {
      return this.callback.get();
    }
    if (this.parent != null) {
      return this.parent.getCallback();
    }

    /* UNREACHABLE */
    throw new IllegalArgumentException(
      "unreachable code reached - report this bug!");
  }

  /**
   * Get the unqualified destination of the current log.
   */

  @Override public @Nonnull String getDestination()
  {
    return this.destination_local;
  }

  @Override public Level getLevel()
  {
    final Log log = (Log) this.getRoot();
    return log.level.get();
  }

  /**
   * Return the output stream that will be used for the current log. The
   * function recursively takes the output stream of the parent log until it
   * finds a non-null stream.
   */

  private @Nonnull OutputStream getOutputStream()
  {
    if (this.stream.get() != null) {
      return this.stream.get();
    } else if (this.parent != null) {
      return this.parent.getOutputStream();
    }

    /* UNREACHABLE */
    throw new IllegalArgumentException(
      "unreachable code reached - report this bug!");
  }

  /**
   * Get the root ancestor log of the current log.
   */

  @Override public LogInterface getRoot()
  {
    if (this.isRoot()) {
      return this;
    }

    assert this.parent != null;
    return this.parent.getRoot();
  }

  /*
   * Levels.
   */

  @Override public void info(
    final @Nonnull String message)
  {
    this.write(Level.LOG_INFO, message);
  }

  /**
   * Current log is the root log.
   */

  @Override public boolean isRoot()
  {
    return this.parent == null;
  }

  private void loadConfigurationProperties(
    final @Nonnull Properties properties,
    final @Nonnull String property_prefix)
  {
    final String logs_prefix = property_prefix + ".logs.";
    for (final Entry<Object, Object> entry : properties.entrySet()) {
      final String key = (String) entry.getKey();
      if (key.startsWith(logs_prefix)) {
        final String destination = key.substring(logs_prefix.length());
        final String value = (String) entry.getValue();
        if (value.equals("true")) {
          this.configuration.put(destination, Boolean.TRUE);
        } else if (value.equals("false")) {
          this.configuration.put(destination, Boolean.FALSE);
        } else {
          throw new IllegalArgumentException("could not parse '"
            + value
            + "' as boolean (true|false) value for key '"
            + key
            + "'");
        }
      }
    }

    final String level_key = property_prefix + ".level";
    if (properties.containsKey(level_key)) {
      final String value = properties.getProperty(level_key);
      try {
        final Level config_level = Level.valueOf(value);
        this.level.set(config_level);
      } catch (final IllegalArgumentException e) {
        throw new IllegalArgumentException("could not parse '"
          + value
          + "' as log level value for key '"
          + level_key
          + "'", e);
      }
    }
  }

  /**
   * Set the callback that will be used for the current log.
   */

  @Override public void setCallback(
    final @Nonnull Callbacks callback)
  {
    if (callback == null) {
      throw new IllegalArgumentException("callback must not be null");
    }
    this.callback.set(callback);
  }

  private void setDestinationLongest(
    final int length)
  {
    final Log log = (Log) this.getRoot();
    assert (log.destination_longest != null);

    if (log.destination_longest.get() < length) {
      log.destination_longest.set(length);
    }
  }

  /*
   * Configuration.
   */

  @Override public void setLevel(
    final Level in_level)
  {
    final Log log = (Log) this.getRoot();
    log.level.set(in_level);
  }

  /**
   * Set the output stream for the current log.
   */

  @Override public void setOutputStream(
    final @Nonnull OutputStream stream)
  {
    if (stream == null) {
      throw new IllegalArgumentException("stream must not be null");
    }
    this.stream.set(stream);
  }

  @Override public void warn(
    final @Nonnull String message)
  {
    this.write(Level.LOG_WARN, message);
  }

  /*
   * Constructors.
   */

  @Override public void write(
    final @Nonnull Level in_level,
    final @Nonnull String message)
  {
    if (this.enabled(in_level)) {
      final String destination = this.destination_abs;
      @SuppressWarnings("resource") final OutputStream out =
        this.getOutputStream();
      final Callbacks c = this.getCallback();
      c.call(out, destination, in_level, message);
    }
  }

  @Override public void write(
    final @Nonnull String message)
  {
    this.write(Level.LOG_DEBUG, message);
  }
}
