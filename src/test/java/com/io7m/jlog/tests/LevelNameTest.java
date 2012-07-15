package com.io7m.jlog.tests;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jlog.Level;
import com.io7m.jlog.LevelName;

public class LevelNameTest
{
  @SuppressWarnings("static-method") @Test public void testLevels()
  {
    Assert.assertEquals("debug", LevelName.get(Level.LOG_DEBUG));
    Assert.assertEquals("info", LevelName.get(Level.LOG_INFO));
    Assert.assertEquals("warn", LevelName.get(Level.LOG_WARN));
    Assert.assertEquals("error", LevelName.get(Level.LOG_ERROR));
    Assert.assertEquals("critical", LevelName.get(Level.LOG_CRITICAL));
  }
}
