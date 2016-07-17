Welcome to the third of a series of blog posts about [Scala](http://www.scala-lang.org/).

As discussed in the first post [An Introduction](https://davidainslie.github.io/scala-blog-introduction/), Scala just might be your best programming investment.

Scala took a while to get going, but now it is being rapidly adopted. The UK Home Office, and indeed many other Government departments are using Scala now, and more and more projects are choosing to go with Scala. Even banks, and especially investment banks that have millions and millions invested in technologies (mainly C, C++ and Java) have got onto the Scala bandwagon - this is very significant, because they will only adopt technology that they see as the future.

How are we using Scala at the UK Home Office?

Within the Scala community, some of the open source libraries we use are:
- [Play](https://playframework.com/), based on a lightweight, stateless, web-friendly architecture and features predictable and minimal resource consumption (CPU, memory, threads) for highly-scalable applications thanks to its reactive model, based on Akka Streams.
- [Spray](http://spray.io/), being asynchronous, actor-based, fast, lightweight, modular and testable it's a great way to connect your Scala applications to the world e.g. as RESTful microservices (currently being merged into Akka and renamed as Akka HTTP).
- [Akka](http://akka.io/), to build highly concurrent, distributed, and resilient message driven applications. Akka is built around the [Actor methodology](https://en.wikipedia.org/wiki/Actor_model) and provides many libraries for the likes of clustering, persistence, reactive streams and RESTful services. You can even write automated tests for anything provided such as a cluster.
- [Kafka](http://kafka.apache.org/), designed to allow a single cluster to serve as the central data backbone for a large organization. It can be elastically and transparently expanded without downtime.

# Scala at The Home Office
Scala is already used on many projects in the Home Office, and we continue creating [open source libraries](https://github.com/UKHomeOffice) - we'll cover quite a few from the Home Office Registered Traveller Programme (RTP - where libraries are named rtp-library-name).

Scala is also used within many over UK government departments.

Within the Home Office we use: SBT; Play; Akka; Spray (merged into Akka-HTTP); Specs2 and Scalatest; Casbah, Salat, Reactivemongo to interact with Mongodb.

The [Registered Traveller Caseworker](https://www.gov.uk/registered-traveller) system accepts applications from a customer facing system for fast tracking through UK borders. Applications (Cases) are held in Mongodb and go through a workflow process, managed with a Play frontend.

The [Register to Apply for US Global Entry](https://www.gov.uk/apply-faster-entry-usa) system is similar, but for fast tracking UK citizens through USA borders. This shows a good example of using Akka Clustering - very powerful and yet easy. We have load-balanced boxes that talk to the USA system via SOAP services. Our system constantly asks the USA system questions about customers either registering or already registered. Because each box does the same thing, we don't want the same question asked from both boxes, as we may get race conditions. By using an Akka Singleton Cluster, actors attempt to start up on all boxes e.g. actor of type A wants to start up on Nodes 1, 2 and 3. However, only one box will have actor A running. The **actor systems** on the boxes, constantly communicate - if actor A goes down on Node 1, or if Node 1 itself goes down, Akka will start up the same actor on one of the available boxes. If we had a Play application for monitoring these boxes, it would be easy, as it would simply join the Akka Cluster.

The following is just a few of our Home Office Registered Traveller open source Scala libraries. Note that all the code can be found in [Github](https://github.com/davidainslie/scala-blog-uk-home-office).

## IO functionality
[rtp-io-lib](https://github.com/UKHomeOffice/rtp-io-lib) is a general purpose library focused around IO. Many of our projects in the Home Office use JSON to communicate with other systems. Even though JSON is schemaless, a [JSON Schema](http://json-schema.org/) can be used, as we do. We use JSON schemas as contracts between systems, and they are used to validate JSON so adding minimal security. By using this approach it is easier to develop and test systems in isolation, knowing that upon integration testing, things should just work! 

##### Validate JSON against a schema
e.g.
```scala
val jsonSchema = JsonSchema(<your schema from a JSON file>)
val json = JValue(<your JSON>)

jsonSchema validate json map processJson badMap processError
```
Interesting line (**jsonSchema validate json map...**), especially if you don't know Scala and/or functional programming.

Because Scala can easily produces DSLs (Domain Specific Languages) both internal and external, we can write and read our code somewhat fairly close to English. So how do we understand that line?

Well **map** (as mentioned) is "something" that given input, in this case the result of **jsonSchema validate json**, will produce output. It produces output by applying a function, in this case **processJson** to the given input.

And **badMap** does just the same as **map**, but as the name suggests, its input is something dodgy!

Well **jsonSchema validate json**, validates the **json** against the **jsonSchema**. Now either validation passes or fails. If it passes, that *good* JSON will be given to **map** to apply its function **processJson**. If the validation fails, the *bad* errors will be given to **badMap** to apply its function **processError**. What this one "DSL" line means, is that functionality is split into reusable, testable, easier to manage and reason about functions.

This library provides so much more:
- Transform JSON e.g. renaming properties; altering values including changing their types; restructuring the JSON.
- DSL for condition logic.
- Configuration "add-ons" for the powerful [Scala configuration library](https://github.com/typesafehub/config).
- Easily read resources into something you need, and easily add to this functionality.

## Akka functionality
[rtp-akka-lib](https://github.com/UKHomeOffice/rtp-akka-lib) provides some extras for working with Akka and Spray (though Spray is being merged into Akka). [Akka](http://akka.io/) is too big to describe what it really is, but it's all about distributed, concurrent, fault-tolerant, resilient, but amazingly easy to test systems. [Spray](http://spray.io/) is also big, but centres around easily creating RESTful services. At the Home Office we use Spray for microservices.

This library provides:
- Easy composition of routings i.e. RESTful endpoints.
- Extra marshalling for converting external requests/responses to/from domain code.
- Scheduling.
- Clustering, specifically singleton clustering i.e. some functionality only runs on one box.

## Interacting with Mongodb the NoSQL database
[rtp-mongo-lib](https://github.com/UKHomeOffice/rtp-mongo-lib) to seamlessly work with Mongo drivers - currently Casbah, with or without Salat, and ReactiveMongo.

Casbah is a Scala library that wraps the official Java driver for Mongodb (a library to interact with Mongodb). Salat sits on top of Casbah to provide, well, think of it as the equivalent of an ORM tool i.e. Object to Relational Mapping. Of course Mongodb is not a Relational database, but the idea is the same - the tool abstracts the underlying data structure of the database to a programmer's Object world. Note that Casbah synchronously interacts with the database. Mmmm! Not reactive. However, there is a newer asynchronous driver that has come out to probably replace Casbah.

As for Reactivemongo. It is an asynchronous Mongodb driver.

Various functionality is provided in this library to not only make source code easier to write against Mongodb but also very easy when writing your tests e.g. let's save two "test" objects to Mongodb, running as an embedded server in your test, and them read them back to make sure that these "tests" were indeed persisted:
```scala
case class Test(data: String)

class RepositorySpec extends Specification with EmbeddedMongoSpecification {
  "Repository" should {
    "save and find 2 tests" in {
      val repository = new Repository[Test] with TestMongo {
        val collectionName = "tests"
      }

      val test1 = Test("1")
      repository save test1

      val test2 = Test("2")
      repository save test2

      repository.findAll().toList must beLike {
        case List(Test(data1), Test(data2)) =>
          data1 mustEqual test1.data
          data2 mustEqual test2.data
      }
    }
  }
}
```

This library provides:
- Easily create a Repository (representation of a Mongodb Collection, much like a Relational Table), injecting the "configuration" such as location and credentials of your database.
- Embed a Mongodb server with a specification so that each test can interact with Mongo but still be completely independent i.e. no test interference.

## Publish/Subscribe to RabbitMQ messaging system
[rtp-rabbit-lib](https://github.com/UKHomeOffice/rtp-rabbit-lib) allows for easy interaction with [RabbitMQ](https://www.rabbitmq.com/), the messaging system written in [Erlang](https://www.erlang.org/). Originally, our RTP frontend and backend systems, were separated by RabbitMQ - frontend public facing systems would publish applications to RabbitMQ queues from which backend systems would subscribe. Note that we decided that the library should only work with JSON (functionality could be added for other data formats).

An example of writing a test:
```scala
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
}
```

Note that unlike most other **rtp** libraries, testing against RabbitMQ requires an actual instance of RabbitMQ running. However, all tests run independently where unique temporary queues are automatically created and torn down.

For this example, an instance of RabbitMQ publisher and consumer (subscriber) is created as the same instance - publisher will publish a message to a **queue** that the consumer listens to. JSON is published as the library was (for us) only required to handle JSON, and so it was easy to automatically validate the JSON via a schema, by using the **rtp-io-lib** already mentioned.

This library provides:
- Automatic subscription, where business functionality only has to implement a method that accepts JSON that was published onto a queue.
- Publication and subscription of JSON is automatically validated against a provided JSON schema.
- Messages are only removed from queues, when your business logic is done and happy.
- Error messages can be published to error queues - we coded an Error Policy.
- There is a default Retry Strategy, which can be extended.
- Easy testing. Even though a running instance of RabbitMQ is required (we do not have an embedded version as RabbitMQ is written in Erlang) every test creates its own unique, temporary queues - creation and teardown is done for you.

## Publish/Subscribe to Amazon SQS messaging system
[rtp-amazon-sqs-lib](https://github.com/UKHomeOffice/rtp-amazon-sqs-lib) allows for easy integration with Amazon's SQS messaging system, with the help of [ElasticMQ](https://github.com/adamw/elasticmq), an implementation of Amazon SQS. RTP now uses Amazon SQS in favour of RabbitMQ, having moved to AWS. The idea of this library, is much the same as the one discussed above (for RabbitMQ), except that it is a tad more generic, and testing is even easier with an actual embedded Amazon SQS, thanks to ElasticMQ and some custom wrappers.

The following example shows the use of a bespoke Akka actor to subscribe to a message that is published to a queue on an embedded Amazon SQS instance:

```scala
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
```

The test fixture i.e. the **trait** named **Context**, sets up an embedded Amazon SQS server for each test, along with an Actor System context which is required when dealing with Akka actors. Again we can see just how easy it is to write integration tests as unit tests. A **Publisher** publishes **blah** text to an Amazon SQS **queue**, which will eventually be **Processed** by the **SubscriberActor**.

This library provides:
- Automatic subscription of any kind of messages.
- Processing of messages is done via your custom messaging protocol built on top of Akka (unlike the RabbitMQ library we have, where you implement a contract based on JSON).
- Publication and subscription of JSON can be easily validated against a JSON schema.
- Messages can be filtered by chaining filter functions. A message can be altered, replaced, or even ignored.
- Message listeners can be registered.
- Messages are only removed from queues, when your business logic is done and happy.
- Error messages can be published to error queues.
- Easy testing. Everything is embedded and tests do not interfere with other.
- Integration tests are written as easily as unit tests and mocking is rarely required, making your tests more viable and code design better.