package uk.gov.homeoffice.mongo.salat

import org.specs2.mutable.Specification
import uk.gov.homeoffice.mongo.casbah.EmbeddedMongoSpecification

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

case class Test(data: String)