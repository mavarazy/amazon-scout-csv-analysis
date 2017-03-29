package com.clemble.aws.analysis

trait AWSAnalysisService {

  def analyze(csv: Stream[CSV]): Stream[CSV]

}

case class SimpleAWSAnalysisService(transformer: CSVTransformer) extends AWSAnalysisService {

  override def analyze(csv: Stream[CSV]): Stream[CSV] = csv.map(transformer.transform)

}
