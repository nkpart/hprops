package hprops

import scalaz._
import Scalaz._

import metascala.HLists._
import metascala.Nats._
  
sealed trait PropertyError {
  val property: String
}

case class Missing(property: String) extends PropertyError
case class Invalid(property: String) extends PropertyError

trait AttrRead[Src, T] {
  // Failure cases must report a missing property/field name
  def read(e: Src): Result[T]
}

trait AttrPut[Src, T] {
  def put(t: T, e: Src): Result[Src]
}

trait AttrUpdate[Src, T] {
  def update(s: Src, t: T): Result[T]
}

// Represents getting a value that can be read from, and written to, a source type.
// This is a type that can be encoded in (and decoded from) Src
// eg. ReadWrite[JSONObject, Person]
trait ReadWrite[Src, T] extends AttrRead[Src,T] with AttrPut[Src, T] {
  // Helper for working with case classes, should be able to call like this (note the <-> helper from FunctionWs):
  //   case class Foo(...)
  //   someProp >< (Foo <-> Foo.unapply _)
  def ><[U](t2: (T => U, U => T)): ReadWrite[Src, U] = this.xmap(t2._1, t2._2)
}

// Represents a value that can be read from, and updated from, a source type.
// eg. ReadUpdate[HttpRequest, Person]
// an existing person value can be updated
// with values from the request, or a new person
// could be created.
trait ReadUpdate[Src, T] extends AttrRead[Src, T] with AttrUpdate[Src, T] {
  def ><[U](t2: (T => U, U => T)): ReadUpdate[Src, U] = this.xmap(t2._1, t2._2)
}

