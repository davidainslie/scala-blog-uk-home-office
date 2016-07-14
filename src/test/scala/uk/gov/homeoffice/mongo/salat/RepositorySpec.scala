package uk.gov.homeoffice.mongo.salat

import org.specs2.matcher.Scope
import org.specs2.mutable.Specification
import uk.gov.homeoffice.mongo.casbah.EmbeddedMongoSpecification

class RepositorySpec extends Specification with EmbeddedMongoSpecification {
  trait Context extends Scope {
    val repository = new Repository[TestModel] with TestMongo {
      val collectionName = "tests"
    }
  }

  "Repository" should {
    "save and find 2 tests" in new Context {
      val testModel1 = TestModel("1")
      repository save testModel1

      val testModel2 = TestModel("2")
      repository save testModel2

      repository.findAll().toList must beLike {
        case List(TestModel(id1), TestModel(id2)) =>
          id1 mustEqual testModel1.id
          id2 mustEqual testModel2.id
      }
    }
  }
}

case class TestModel(id: String)