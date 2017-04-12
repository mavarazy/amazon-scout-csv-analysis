package com.clemble.aws.analysis

import java.io.File

import org.specs2.mutable.Specification

class AntiDumpingRegulationsTests extends Specification {

  val USA_REG = new File(this.getClass.getClassLoader.getResource("GAD-USA.xls").getFile)

  "AntiDumping DB from Excel" should {
    val regs = AntiDumpingDatabase.fromExcel(USA_REG)
    regs.isRegulated("Melamine in Crystal") shouldEqual true
    regs.isRegulated("Kawabanga") shouldEqual false
  }

}
