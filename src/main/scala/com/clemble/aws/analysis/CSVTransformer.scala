package com.clemble.aws.analysis

trait CSVTransformer {

  def transform(csv: CSV): CSV

}

object AWSScoutTransformer extends CSVTransformer {

  override def transform(csv: CSV): CSV = {
    csv
  }

}
