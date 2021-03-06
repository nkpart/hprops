import metascala.HLists._

package object hprops extends HPropsDSL with MAs with Instances with FunctionWs {
  import scalaz._
  import Scalaz._
  
  def missing(s: String): PropertyError = Missing(s)
  def invalid(s: String): PropertyError = Invalid(s)
  
  type Result[T] = Validation[NonEmptyList[PropertyError], T]
  
  def hlift[T, P[_]: InvariantFunctor](p: P[T]): P[HCons[T, HNil]] = p.xmap(v => HCons(v, HNil))((xs: HCons[T, HNil]) => xs.head)
}
