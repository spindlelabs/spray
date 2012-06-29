package cc.spray
package authentication

import org.specs2.mutable.Specification

class SecureEqualsSpec extends Specification {
  "SecureEquals" should {
    "compare strings for equality" in {
      SecureEquals.secureEquals("foo", "foo") must beTrue
      SecureEquals.secureEquals("FoO", "FoO") must beTrue
      SecureEquals.secureEquals("", "") must beTrue

      SecureEquals.secureEquals("FoO", "Foo") must beFalse
      SecureEquals.secureEquals("FoO", "") must beFalse
      SecureEquals.secureEquals("FoO", "FoObar") must beFalse
    }

    "compare byte arrays for equality" in {
      SecureEquals.secureEquals(Array[Byte](25, 42, -12), Array[Byte](25, 42, -12)) must beTrue
      SecureEquals.secureEquals(Array[Byte](0, 96, 123), Array[Byte](0, 96, 123)) must beTrue
      SecureEquals.secureEquals(Array[Byte](), Array[Byte]()) must beTrue

      SecureEquals.secureEquals(Array[Byte](0, 96, 123), Array[Byte](1, 96, 123)) must beFalse
      SecureEquals.secureEquals(Array[Byte](0, 96, 123), Array[Byte]()) must beFalse
      SecureEquals.secureEquals(Array[Byte](0, 96, 123), Array[Byte](0, 96, 123, 127, 8, 2)) must beFalse
    }
  }
}