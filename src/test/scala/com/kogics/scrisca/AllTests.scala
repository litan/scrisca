package com.kogics.scrisca

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(
  classOf[io.TestRichFile],
  classOf[proc.TestProcess]
))
class AllTests {}
