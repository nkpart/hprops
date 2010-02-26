package hprops

import metascala.HLists._

import scalaz._
import Scalaz._

// Provides the DSL for composing properties
trait HPropsDSL {
  def hlift[T, P[_]: InvariantFunctor](p: P[T]): P[HCons[T, HNil]] = p.xmap(v => HCons(v, HNil), (xs: HCons[T, HNil]) => xs.head)
  
  implicit def pimpPropListThing[Src, L <: HList](p: ReadWrite[Src, L]) = new {
    def ::[V](property: ReadWrite[Src, V]): ReadWrite[Src, HCons[V, L]] = new ReadWrite[Src, HCons[V, L]] {
      def read(e: Src) = (property.read(e) <|*|> p.read(e)) map { case (v, xs) => HCons(v, xs) }
      def put(vls: HCons[V, L], e: Src) = { 
        p.put(vls.tail, e) >>= (x => property.put(vls.head, x))
      }
    }
  }
  
  implicit def pimpPropThing[Src, L](p: ReadWrite[Src, L]) = new {
    def ::[V](property: ReadWrite[Src, V]): ReadWrite[Src, HCons[V, HCons[L, HNil]]] = 
      property :: HPropsDSL.this.hlift[L, PartialApply1Of2[ReadWrite, Src]#Apply](p)

    def hlift = HPropsDSL.this.hlift[L, PartialApply1Of2[ReadWrite, Src]#Apply](p)
  }
  
  implicit def pimpReadUpdateList[Src, L <: HList](p: ReadUpdate[Src, L]) = new {
    def ::[V](property: ReadUpdate[Src, V]): ReadUpdate[Src, HCons[V, L]] = new ReadUpdate[Src, HCons[V, L]] {
      def read(s: Src) = (property.read(s) <|*|> p.read(s)) map { case (v, xs) => HCons(v, xs) }
      def update(s: Src, vls: HCons[V, L]) = {
        val ell = p.update(s, vls.tail)
        val v = property.update(s, vls.head)
        (v <|*|> ell) map { case (v, ell) => HCons(v, ell) }
      }
    }
  }

  implicit def pimpReadUpdateProp[Src, L](p: ReadUpdate[Src, L]) = new {
    def ::[V](property: ReadUpdate[Src, V]): ReadUpdate[Src, HCons[V, HCons[L, HNil]]] = 
      property :: HPropsDSL.this.hlift[L, PartialApply1Of2[ReadUpdate, Src]#Apply](p)
      
    def hlift = HPropsDSL.this.hlift[L, PartialApply1Of2[ReadUpdate, Src]#Apply](p)
  }
}
