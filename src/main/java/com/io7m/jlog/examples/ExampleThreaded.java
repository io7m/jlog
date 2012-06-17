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

package com.io7m.jlog.examples;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.io7m.jlog.Level;
import com.io7m.jlog.Log;

public final class ExampleThreaded implements Runnable
{
  public static void main(
    final String args[])
    throws IOException
  {
    if (args.length < 1) {
      ExampleThreaded.usage();
    }

    final FileInputStream file = new FileInputStream(args[0]);
    final Properties p = new Properties();
    p.load(file);

    final ExecutorService pool = Executors.newCachedThreadPool();
    for (int index = 0; index < 100; ++index) {
      pool.execute(new ExampleThreaded(p));
    }
  }
  private static void usage()
  {
    System.err.println("usage: properties.cfg");
    System.exit(1);
  }
  private final Log main;
  private final Log main_a;

  private final Log main_b;

  private final Log blackhole;

  public ExampleThreaded(
    final Properties properties)
  {
    this.main = new Log(properties, "com.io7m.jlog", "main");
    this.main_a = new Log(this.main, "a");
    this.main_b = new Log(this.main, "b");
    this.blackhole = new Log(this.main, "blackhole");
  }

  @Override public void run()
  {
    for (int index = 0; index < 32; ++index) {
      for (final Level level : Level.values()) {
        this.main.write(level, "main");
        this.main_a.write(level, "main_a");
        this.main_b.write(level, "main_b");
        this.blackhole.write(level, "blackhole");
      }
    }
  }
}
