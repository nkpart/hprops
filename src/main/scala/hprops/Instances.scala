package hprops

trait Instances {
  import scalaz._
  import Scalaz._
  
  // Properties are invariant functors
  implicit def ReadWriteInvariantFunctor[Src]: InvariantFunctor[PartialApply1Of2[ReadWrite, Src]#Apply] = 
    new scalaz.InvariantFunctor[PartialApply1Of2[ReadWrite, Src]#Apply] {
      def xmap[T, U](prop: ReadWrite[Src, T], f: T => U, g: U => T): ReadWrite[Src, U] = new ReadWrite[Src, U] {
        def read(e: Src) = prop.read(e) map f
        def put(u: U, e: Src) = prop.put(g(u), e)
      }
    }
  
    // Properties are invariant functors
  implicit def ReadUpdateInvariantFunctor[Src]: InvariantFunctor[PartialApply1Of2[ReadUpdate, Src]#Apply] = 
    new scalaz.InvariantFunctor[PartialApply1Of2[ReadUpdate, Src]#Apply] {
      def xmap[T, U](prop: ReadUpdate[Src, T], f: T => U, g: U => T): ReadUpdate[Src, U] = new ReadUpdate[Src, U] {
        def read(s: Src) = prop.read(s) map f

        def update(s: Src, u: U): Result[U] = prop.update(s, g(u)) map f
      }
    }
  
  implicit def AttrReadFunctor[Src] = new scalaz.Functor[PartialApply1Of2[AttrRead, Src]#Apply] {
    def fmap[A,B](reader: AttrRead[Src, A], f: A => B) = new AttrRead[Src, B] {
      def read(s: Src): Validation[NonEmptyList[PropertyError], B] = reader.read(s) map f
    }
  }
  
  implicit def AttrPutCofunctor[Src] = new scalaz.Cofunctor[PartialApply1Of2[AttrPut, Src]#Apply] {
    def comap[A,B](writer: AttrPut[Src, A], f: B => A) = new AttrPut[Src, B] {
      def put(b: B, s: Src) = writer.put(f(b), s)
    }
  }
}