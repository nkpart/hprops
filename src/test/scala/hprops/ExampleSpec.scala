package hprops

import metascala.HLists._
import scalaz._
import Scalaz._

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class ExampleSpec extends BaseSuite {
  
  // A property that can store a string in a map with a certain key
  def prop(s: String) = new ReadWrite[Map[String,String], String] {
    def get(map: Map[String,String]) = map.get(s).toSuccess(missing(s).pure)
    def put(value: String, map: Map[String,String]) = {
      map + (s -> value)
    }.success
  } 
  
  test("storing values") {
    val m = Map[String,String]()
        
    prop("a").put("a value", m) should equal (Map("a" -> "a value").success)
    (prop("a") :: prop("b")).put("a value" :: "b value" :: HNil, m) should equal (Map("a" -> "a value", "b" -> "b value").success)
  }
  
  test("reading values") {
    val ab = prop("a") :: prop("b") :: prop("c")
    
    val complete = Map("a" -> "1", "b" -> "2", "c" -> "3")
    ab.get(complete) should equal {
      ("1" :: "2" :: "3" :: HNil).success
    }
    
    val incomplete = Map[String,String]()
    ab.get(incomplete).failure.map(_.list) should equal {
      Some(List(missing("a"), missing("b"), missing("c")))
    }
  }

  case class AB(a: String, b: String)    
  test("mapping to case classes") {
    val ab = prop("a") :: prop("b") >< (AB <-> AB.unapply _)
    
    ab.put(AB("1", "2"), Map[String,String]()) should equal {
      Map("a" -> "1", "b" -> "2").success
    }
    
    ab.get(Map("a"->"1", "b" -> "2")) should equal {
      AB("1", "2").success
    }
  }
}

