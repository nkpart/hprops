package scapps

import scalaz._
import Scalaz._

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class DummySpec extends BaseSuite {
  
  test("dummy") {
    true should equal (false)
  }
}

