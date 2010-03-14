package hprops

import metascala.HLists._

import scalaz._
import Scalaz._

trait StdReadWriteW[Src, L] {
  val p: ReadWrite[Src, L]
  def ::[V](property: ReadWrite[Src, V]): ReadWrite[Src, HCons[V, HCons[L, HNil]]] = {
    val lifted: ReadWrite[Src, HCons[L, HNil]] = hprops.hlift[L, PartialApply1Of2[ReadWrite, Src]#Apply](p)
    pimpPropListThing[Src, HCons[L, HNil]](lifted).::[V](property) 
  }

  def hlift = hprops.hlift[L, PartialApply1Of2[ReadWrite, Src]#Apply](p)
}

trait ListReadWriteW[Src, L <: HList] {
  val p: ReadWrite[Src, L]
  
  def ::[V](property: ReadWrite[Src, V]): ReadWrite[Src, HCons[V, L]] = new ReadWrite[Src, HCons[V, L]] {
    def read(e: Src) = (property.read(e) <|*|> p.read(e)) map { case (v, xs) => HCons(v, xs) }
    def put(vls: HCons[V, L], e: Src) = { 
      p.put(vls.tail, e) >>= (x => property.put(vls.head, x))
    }
  }
}

trait StdReadUpdateW[Src, L] {
  val p: ReadUpdate[Src, L]
  def ::[V](property: ReadUpdate[Src, V]): ReadUpdate[Src, HCons[V, HCons[L, HNil]]] = 
    property :: hprops.hlift[L, PartialApply1Of2[ReadUpdate, Src]#Apply](p)
    
  def hlift = hprops.hlift[L, PartialApply1Of2[ReadUpdate, Src]#Apply](p)
}

trait ListReadUpdateW[Src, L <: HList] {
  val p: ReadUpdate[Src, L]
  def ::[V](property: ReadUpdate[Src, V]): ReadUpdate[Src, HCons[V, L]] = new ReadUpdate[Src, HCons[V, L]] {
    def read(s: Src): Result[HCons[V, L]] = (property.read(s) <|*|> p.read(s)) map { case (v, xs) => HCons(v, xs) }
    def update(s: Src, vls: HCons[V, L]): Result[HCons[V, L]] = {
      val ell = p.update(s, vls.tail)
      val v = property.update(s, vls.head)
      (v <|*|> ell) map { case (v, ell) => HCons(v, ell) }
    }
  }
}
// Provides the DSL for composing properties
trait HPropsDSL {
  
  implicit def pimpPropListThing[Src, L <: HList](in: ReadWrite[Src, L]) = new ListReadWriteW[Src, L] { val p = in }
  
  implicit def pimpPropThing[Src, L](in: ReadWrite[Src, L]) = new StdReadWriteW[Src, L] { val p = in }
  
  implicit def pimpReadUpdateList[Src, L <: HList](in: ReadUpdate[Src, L]) = new ListReadUpdateW[Src, L] { val p = in }

  implicit def pimpReadUpdateProp[Src, L](in: ReadUpdate[Src, L]) = new StdReadUpdateW[Src, L] { val p = in }
}
