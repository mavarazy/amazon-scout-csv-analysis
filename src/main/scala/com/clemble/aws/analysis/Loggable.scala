package com.clemble.aws.analysis

import org.slf4j.LoggerFactory

trait Loggable {

  val LOG = LoggerFactory.getLogger(this.getClass)

}
