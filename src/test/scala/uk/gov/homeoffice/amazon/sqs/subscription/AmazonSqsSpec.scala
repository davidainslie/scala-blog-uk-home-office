package uk.gov.homeoffice.amazon.sqs.subscription

import akka.actor.Props
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import uk.gov.homeoffice.akka.{ActorExpectations, ActorSystemContext}
import uk.gov.homeoffice.amazon.sqs.subscription.protocol.Processed
import uk.gov.homeoffice.amazon.sqs.{EmbeddedSQSServer, Message, Publisher, Queue}
import uk.gov.homeoffice.json.JsonFormats

class AmazonSqsSpec(implicit ev: ExecutionEnv) extends Specification with JsonFormats {
  trait Context extends ActorSystemContext with ActorExpectations with EmbeddedSQSServer {
    implicit val listeners = Seq(testActor)

    val queue = create(new Queue("test-queue"))
  }

  "Subscriber actor" should {
    "receive published text, and then fire a message indicating that the text message has been processed" in new Context {
      system actorOf Props {
        new SubscriberActor(new Subscriber(queue)) {
          def receive: Receive = {
            case m: Message => sender() ! Processed(m)
          }
        }
      }

      val message = "blah"

      new Publisher(queue) publish message

      eventuallyExpectMsg[Processed] {
        case Processed(m) => m.content == message
      }
    }
  }
}