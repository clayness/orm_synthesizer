package edu.virginia.cs.Framework

// changed name from CoqMain to Framework
// changed object to trait to be inherited
trait TrademakerFramework {
  sealed abstract class Prod[A, B]
  case class Pair[A, B](x1: A, x2: B) extends Prod[A, B]
  def fst[A, B]: (Prod[A, B] => A) = {
    (p: Prod[A, B]) =>
      p match {
        case Pair(x, y) => x
      }
  }
  def snd[A, B]: (Prod[A, B] => B) = {
    (p: Prod[A, B]) =>
      p match {
        case Pair(x, y) => y
      }
  }
  sealed abstract class List[A]
  case class Nil[A]() extends List[A]
  case class Cons[A](x1: A, x2: List[A]) extends List[A]
  def hd[A]: (A => (List[A] => A)) = {
    (default: A) =>
      (l: List[A]) =>
        l match {
          case Nil() => default
          case Cons(x, l0) => x
        }
  }
  def tl[A]: (List[A] => List[A]) = {
    (l: List[A]) =>
      l match {
        case Nil() => Nil()
        case Cons(a0, m) => m
      }
  }
  def map[A, B]: ((A => B) => (List[A] => List[B])) = {
    (f: (A => B)) =>
      (l: List[A]) =>
        l match {
          case Nil() => Nil()
          case Cons(a0, t) => Cons(f(a0), map(f)(t))
        }
  }
  def combine[A, B]: (List[A] => (List[B] => List[Prod[A, B]])) = {
    (l: List[A]) =>
      (l$prime: List[B]) =>
        l match {
          case Nil() => Nil()
          case Cons(x, tl0) => l$prime match {
            case Nil() => Nil()
            case Cons(y, tl$prime) => Cons(Pair(x, y), combine(tl0)(tl$prime))
          }
        }
  }
  sealed abstract class Tradespace
  case class Build_Tradespace(x1: (Any => List[Prod[Any, Any]]), x2: (Prod[Any, Any] => Prod[Any, Any])) extends Tradespace
  type SpecificationType = Any
  type ImplementationType = Any
  type MeasurementFunctionSetType = Any
  type MeasurementResultSetType = Any
  def synthesize: (Tradespace => (SpecificationType => List[Prod[ImplementationType, MeasurementFunctionSetType]])) = {
    (tradespace0: Tradespace) =>
      tradespace0 match {
        case Build_Tradespace(synthesize0, runBenchmark0) => synthesize0
      }
  }
  def runBenchmark: (Tradespace => (Prod[ImplementationType, MeasurementFunctionSetType] => Prod[ImplementationType, MeasurementResultSetType])) = {
    (tradespace0: Tradespace) =>
      tradespace0 match {
        case Build_Tradespace(synthesize0, runBenchmark0) => runBenchmark0
      }
  }
  def analyze: (Tradespace => (List[Prod[ImplementationType, MeasurementFunctionSetType]] => List[Prod[ImplementationType, MeasurementResultSetType]])) = {
    (tradespace0: Tradespace) =>
      (input: List[Prod[Any, Any]]) =>
        map[Prod[Any, Any], Prod[Any, Any]](runBenchmark(tradespace0))(input)
  }
  def tradespace: (Tradespace => (SpecificationType => List[Prod[ImplementationType, MeasurementResultSetType]])) = {
    (tradespace0: Tradespace) =>
      (spec: Any) =>
        analyze(tradespace0)(synthesize(tradespace0)(spec))
  }
  sealed abstract class ParetoFront
  case class Build_ParetoFront(x1: Tradespace, x2: (List[Prod[ImplementationType, MeasurementResultSetType]] => List[Prod[ImplementationType, MeasurementResultSetType]])) extends ParetoFront
  def pf_tradespace: (ParetoFront => Tradespace) = {
    (paretoFront0: ParetoFront) =>
      paretoFront0 match {
        case Build_ParetoFront(pf_tradespace0, paretoFilter0) => pf_tradespace0
      }
  }
  def paretoFilter: (ParetoFront => (List[Prod[ImplementationType, MeasurementResultSetType]] => List[Prod[ImplementationType, MeasurementResultSetType]])) = {
    (paretoFront0: ParetoFront) =>
      paretoFront0 match {
        case Build_ParetoFront(pf_tradespace0, paretoFilter0) => paretoFilter0
      }
  }
  def paretoFront: (ParetoFront => (SpecificationType => List[Prod[ImplementationType, MeasurementResultSetType]])) = {
    (paretoFront0: ParetoFront) =>
      (spec: Any) =>
        paretoFilter(paretoFront0)(tradespace(pf_tradespace(paretoFront0))(spec))
  }
  sealed abstract class Trademaker
  case class Build_Trademaker(x1: Tradespace, x2: ParetoFront, x3: (Any => List[Any]), x4: (Any => Any), x5: (Any => Any), x6: (Any => (List[Any] => List[Any])), x7: (SpecificationType => Any), x8: (Any => ImplementationType), x9: (Any => Any)) extends Trademaker
  type FormalSpecificationType = Any
  type FormalImplementationType = Any
  type FormalAbstractMeasurementFunctionSet = Any
  type FormalConcreteMeasurementFunctionSet = Any
  def cFunction: (Trademaker => (FormalSpecificationType => List[FormalImplementationType])) = {
    (trademaker: Trademaker) =>
      trademaker match {
        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => cFunction0
      }
  }
  def aFunction: (Trademaker => (FormalImplementationType => FormalSpecificationType)) = {
    (trademaker: Trademaker) =>
      trademaker match {
        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => aFunction0
      }
  }
  def lFunction: (Trademaker => (FormalSpecificationType => FormalAbstractMeasurementFunctionSet)) = {
    (trademaker: Trademaker) =>
      trademaker match {
        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => lFunction0
      }
  }
  def tFunction: (Trademaker => (FormalAbstractMeasurementFunctionSet => (List[ImplementationType] => List[FormalConcreteMeasurementFunctionSet]))) = {
    (trademaker: Trademaker) =>
      trademaker match {
        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => tFunction0
      }
  }
  def sFunction: (Trademaker => (SpecificationType => FormalSpecificationType)) = {
    (trademaker: Trademaker) =>
      trademaker match {
        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => sFunction0
      }
  }
  def iFunction: (Trademaker => (FormalImplementationType => ImplementationType)) = {
    (trademaker: Trademaker) =>
      trademaker match {
        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => iFunction0
      }
  }
  def bFunction: (Trademaker => (FormalAbstractMeasurementFunctionSet => FormalConcreteMeasurementFunctionSet)) = {
    (trademaker: Trademaker) =>
      trademaker match {
        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => bFunction0
      }
  }
}



//
//// changed name from CoqMain to Framework
//// changed object to trait to be inherited
//trait TrademakerFramework {
//  sealed abstract class Prod[A, B]
//  case class Pair[A, B](x1: A, x2: B) extends Prod[A, B]
//
//  def fst[A, B]: (Prod[A, B] => A) = {
//    (p: Prod[A, B]) =>
//      p match {
//        case Pair(x, y) => x
//      }
//  }
//  def snd[A, B]: (Prod[A, B] => B) = {
//    (p: Prod[A, B]) =>
//      p match {
//        case Pair(x, y) => y
//      }
//  }
//
//  def combine[A, B]: (List[A] => (List[B] => List[Prod[A, B]])) = {
//    (l: List[A]) =>
//      (l$prime: List[B]) =>
//        l match {
//          case Nil() => Nil()
//          case Cons(x, tl) => l$prime match {
//            case Nil() => Nil()
//            case Cons(y, tl$prime) => Cons(Pair(x, y), combine(tl)(tl$prime))
//          }
//        }
//  }
//
//  sealed abstract class List[A]
//  case class Nil[A]() extends List[A]
//  case class Cons[A](x1: A, x2: List[A]) extends List[A]
//
//  def hd[A]: (A => (List[A] => A)) = {
//    (default: A) =>
//      (l: List[A]) =>
//        l match {
//          case Nil() => default
//          case Cons(x, l0) => x
//        }
//  }
//  
//  def tl[A]: (List[A] => List[A]) = {
//    (l: List[A]) =>
//      l match {
//        case Nil() => Nil()
//        case Cons(a0, m) => m
//      }
//  }
//
//  def map[A, B]: ((A => B) => (List[A] => List[B])) = {
//    (f: (A => B)) =>
//      (l: List[A]) =>
//        l match {
//          case Nil() => Nil()
//          case Cons(a0, t) => Cons(f(a0), map[A, B](f)(t))
//        }
//  }
//
//  sealed abstract class Tradespace
//  case class Build_Tradespace(x1: (Any => List[Prod[Any, Any]]), x2: (Prod[Any, Any] => Prod[Any, Any])) extends Tradespace
//
//  type SpecificationType = Any
//  type ImplementationType = Any
//  type BenchmarkType = Any
//  type BenchmarkResultType = Any
//
//  def synthesize: (Tradespace => (SpecificationType => List[Prod[ImplementationType, BenchmarkType]])) = {
//    (tradespace0: Tradespace) =>
//      tradespace0 match {
//        case Build_Tradespace(synthesize0, runBenchmark0) => synthesize0
//      }
//  }
//
//  def runBenchmark: (Tradespace => (Prod[ImplementationType, BenchmarkType] => Prod[ImplementationType, BenchmarkResultType])) = {
//    (tradespace0: Tradespace) =>
//      tradespace0 match {
//        case Build_Tradespace(synthesize0, runBenchmark0) => runBenchmark0
//      }
//  }
//
//  def analyze: (Tradespace => (List[Prod[ImplementationType, BenchmarkType]] => List[Prod[ImplementationType, BenchmarkResultType]])) = {
//    (tradespace0: Tradespace) =>
//      (input: List[Prod[Any, Any]]) =>
//        map[Prod[Any, Any], Prod[Any, Any]](runBenchmark(tradespace0))(input)
//  }
//
//  def tradespace: (Tradespace => (SpecificationType => List[Prod[ImplementationType, BenchmarkResultType]])) = {
//    (tradespace0: Tradespace) =>
//      (spec: Any) =>
//        analyze(tradespace0)(synthesize(tradespace0)(spec))
//  }
//
//  sealed abstract class ParetoFront
//  case class Build_ParetoFront(x1: Tradespace, x2: (List[Prod[ImplementationType, BenchmarkResultType]] => List[Prod[ImplementationType, BenchmarkResultType]])) extends ParetoFront
//
//  def pf_tradespace: (ParetoFront => Tradespace) = {
//    (paretoFront0: ParetoFront) =>
//      paretoFront0 match {
//        case Build_ParetoFront(pf_tradespace0, paretoFilter0) => pf_tradespace0
//      }
//  }
//
//  def paretoFilter: (ParetoFront => (List[Prod[ImplementationType, BenchmarkResultType]] => List[Prod[ImplementationType, BenchmarkResultType]])) = {
//    (paretoFront0: ParetoFront) =>
//      paretoFront0 match {
//        case Build_ParetoFront(pf_tradespace0, paretoFilter0) => paretoFilter0
//      }
//  }
//
//  def paretoFront: (ParetoFront => (SpecificationType => List[Prod[ImplementationType, BenchmarkResultType]])) = {
//    (paretoFront0: ParetoFront) =>
//      (spec: Any) =>
//        paretoFilter(paretoFront0)(tradespace(pf_tradespace(paretoFront0))(spec))
//  }
//
//  sealed abstract class Trademaker
//  case class Build_Trademaker(x1: Tradespace, x2: ParetoFront, x3: (Any => List[Any]), x4: (Any => Any), x5: (Any => Any), x6: (Any => (List[Any] => List[Any])), x7: (SpecificationType => Any), x8: (Any => ImplementationType), x9: (Any => BenchmarkType)) extends Trademaker
//  type FormalSpecificationType = Any
//  type FormalImplementationType = Any
//  type FormalAbstractBenchmarkType = Any
//  type FormalConcreteBenchmarkType = Any
//  def cFunction: (Trademaker => (FormalSpecificationType => List[FormalImplementationType])) = {
//    (trademaker: Trademaker) =>
//      trademaker match {
//        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => cFunction0
//      }
//  }
//  def aFunction: (Trademaker => (FormalImplementationType => FormalSpecificationType)) = {
//    (trademaker: Trademaker) =>
//      trademaker match {
//        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => aFunction0
//      }
//  }
//  def lFunction: (Trademaker => (FormalSpecificationType => FormalAbstractBenchmarkType)) = {
//    (trademaker: Trademaker) =>
//      trademaker match {
//        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => lFunction0
//      }
//  }
//  def tFunction: (Trademaker => (FormalAbstractBenchmarkType => (List[FormalImplementationType] => List[FormalConcreteBenchmarkType]))) = {
//    (trademaker: Trademaker) =>
//      trademaker match {
//        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => tFunction0
//      }
//  }
//  def sFunction: (Trademaker => (SpecificationType => FormalSpecificationType)) = {
//    (trademaker: Trademaker) =>
//      trademaker match {
//        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => sFunction0
//      }
//  }
//  def iFunction: (Trademaker => (FormalImplementationType => ImplementationType)) = {
//    (trademaker: Trademaker) =>
//      trademaker match {
//        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => iFunction0
//      }
//  }
//  def bFunction: (Trademaker => (FormalConcreteBenchmarkType => BenchmarkType)) = {
//    (trademaker: Trademaker) =>
//      trademaker match {
//        case Build_Trademaker(tm_Tradespace, tm_ParetoFront, cFunction0, aFunction0, lFunction0, tFunction0, sFunction0, iFunction0, bFunction0) => bFunction0
//      }
//  }
//}
