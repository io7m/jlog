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

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jnull.NullCheckException;
import com.io7m.jproperties.JPropertyException;
import com.io7m.jproperties.JPropertyIncorrectType;

@SuppressWarnings("static-method") public final class LogPolicyPropertiesTest
{
  @Test(expected = NullCheckException.class) public
    void
    testInitialization_0()
      throws JPropertyException
  {
    LogPolicyProperties.newPolicy(
      (Properties) TestUtilities.actuallyNull(),
      "com.io7m.tests");
  }

  @Test(expected = NullCheckException.class) public
    void
    testInitialization_1()
      throws JPropertyException
  {
    final Properties properties = new Properties();
    LogPolicyProperties.newPolicy(
      properties,
      (String) TestUtilities.actuallyNull());
  }

  @Test(expected = JPropertyIncorrectType.class) public
    void
    testInitialization_2()
      throws JPropertyException
  {
    final Properties properties = new Properties();
    properties.setProperty("com.io7m.tests.level", "invalid");

    LogPolicyProperties.newPolicy(properties, "com.io7m.tests");
  }

  @Test public void testInitLevel_0()
    throws JPropertyException
  {
    for (final LogLevel l : LogLevel.values()) {
      final Properties properties = new Properties();
      properties.setProperty("com.io7m.tests.level", l.toString());

      final LogPolicyType p =
        LogPolicyProperties.newPolicy(properties, "com.io7m.tests");
      Assert.assertEquals(l, p.policyDefaultLevel());
    }
  }

  @Test public void testInitLevel_1()
    throws JPropertyException
  {
    final Properties properties = new Properties();

    final LogPolicyType p =
      LogPolicyProperties.newPolicy(properties, "com.io7m.tests");
    Assert.assertEquals(LogLevel.LOG_DEBUG, p.policyDefaultLevel());
  }

  @Test public void testInitProperties_0()
    throws JPropertyException
  {
    final Properties properties = new Properties();
    properties.setProperty("com.io7m.tests.logs.main", "true");
    properties.setProperty("com.io7m.tests.logs.main.a", "true");
    properties.setProperty("com.io7m.tests.logs.main.b", "false");
    properties.setProperty("com.io7m.tests.logs.main.b.c", "true");
    properties.setProperty("com.io7m.tests.logs.main.c", "true");
    properties.setProperty("com.io7m.tests.logs.main.c.a", "false");

    final LogPolicyType p =
      LogPolicyProperties.newPolicy(properties, "com.io7m.tests");
    Assert.assertEquals(LogLevel.LOG_DEBUG, p.policyDefaultLevel());

    Assert.assertTrue(p.policyDestinationEnabled("main"));
    Assert.assertTrue(p.policyDestinationEnabled("main.a"));

    Assert.assertFalse(p.policyDestinationEnabled("main.b"));
    Assert.assertFalse(p.policyDestinationEnabled("main.b.a"));
    Assert.assertFalse(p.policyDestinationEnabled("main.b.b"));
    Assert.assertTrue(p.policyDestinationEnabled("main.b.c"));

    Assert.assertTrue(p.policyDestinationEnabled("main.c"));
    Assert.assertFalse(p.policyDestinationEnabled("main.c.a"));
    Assert.assertTrue(p.policyDestinationEnabled("main.c.b"));
    Assert.assertTrue(p.policyDestinationEnabled("main.c.c"));

    Assert.assertTrue(p.policyDestinationEnabled("main.c"));

    Assert.assertTrue(p.policyDestinationEnabled("unknown"));
  }

  @Test(expected = NullCheckException.class) public
    void
    testInitProperties_1()
      throws JPropertyException
  {
    final Properties properties = new Properties();
    final LogPolicyType p =
      LogPolicyProperties.newPolicy(properties, "com.io7m.tests");

    p.policyDestinationEnabled((String) TestUtilities.actuallyNull());
  }
}
