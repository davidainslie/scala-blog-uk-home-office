package uk.gov.homeoffice.json

import org.json4s.jackson.JsonMethods._
import org.scalactic.Good
import org.specs2.mutable.Specification

class JsonSchemaSpec extends Specification {
  val schema = parse("""
  {
    "$schema": "http://json-schema.org/draft-04/schema#",
    "id": "http://www.bad.com/schema",
    "type": "object",
    "properties": {
      "address": {
        "type": "object",
        "properties": {
          "street": {
            "type": "string"
          },
          "city": {
            "type": "string"
          }
        },
        "required": [
          "street",
          "city"
        ]
      },
      "phoneNumbers": {
        "type": "array",
        "items": {
          "type": "object",
          "properties": {
            "location": {
              "type": "string",
              "minLength": 4
            },
            "code": {
              "type": "integer",
              "minimum": 131
            }
          }
        },
        "minItems": 2,
        "uniqueItems": true
      }
    },
    "required": [
      "address",
      "phoneNumbers"
    ]
  }""")

  "JSON schema" should {
    "be valid" in {
      JsonSchema(schema) must beAnInstanceOf[JsonSchema]
    }
  }

  "JSON" should {
    "validate against schema" in {
      val json = parse("""
      {
        "address": {
          "street": "The Street",
          "city": "Edinburgh"
        },
        "phoneNumbers": [
          {
            "location": "home",
            "code": 131
          },
          {
            "location": "work",
            "code": 131
          }
        ]
      }""")

      JsonSchema(schema) validate json mustEqual Good(json)
    }
  }
}