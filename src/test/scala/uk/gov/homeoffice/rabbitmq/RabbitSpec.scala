package uk.gov.homeoffice.rabbitmq

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

/*
class RabbitSpec(implicit ev: ExecutionEnv) extends Specification with RabbitSpecification {
  "Consumer" should {
    "consume valid JSON message (for a simple test we do not have a JSON schema to validate against)" in {
      // Result will be set at some point but don't know when as publish/subscribe is asynchronous.
      val result = Promise[JValue]()

      // Create a Publisher that is also a Consumer where WithRabbit will connect to a RabbitMQ instance and create/teardown queues.
      val rabbit = new Publisher with NoJsonValidator with WithQueue.Consumer with WithRabbit {
        // This method is run, when JSON is consumed from a queue (JSON is automatically validated).
        def json(json: JValue) = result success json
      }

      // Publish JSON to RabbitMQ
      rabbit.publish("json" -> "ok")

      // At some point in the future, the published JSON will be consumed and here we assert that result.
      result.future must beLike[JValue] {
        case j => j \ "json" mustEqual JString("ok")
      }.await
    }
  }
}*/