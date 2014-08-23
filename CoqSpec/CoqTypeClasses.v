Require Import Coq.Lists.List.
Require Import String.

Open Scope list_scope.


Class Tradespace := {
    SpecificationType: Set
  ; ImplementationType: Set
  ; MeasurementFunctionSetType: Set
  ; MeasurementResultSetType: Set
  ; synthesize: SpecificationType -> list (ImplementationType * MeasurementFunctionSetType)
  ; runBenchmark: ImplementationType * MeasurementFunctionSetType -> (ImplementationType * MeasurementResultSetType)
  ; analyze (input: list (ImplementationType * MeasurementFunctionSetType)) : list (ImplementationType * MeasurementResultSetType) :=
      map runBenchmark input
  ; tradespace (spec: SpecificationType): list (ImplementationType * MeasurementResultSetType) :=
     analyze (synthesize spec) 
}.


Class ParetoFront := {
    pf_tradespace :> Tradespace
  ; paretoFilter: list (ImplementationType * MeasurementResultSetType) -> list (ImplementationType * MeasurementResultSetType)
  ; paretoFront (spec: SpecificationType): list (ImplementationType * MeasurementResultSetType) := paretoFilter (tradespace spec)
}.

Class Trademaker := {
    tm_Tradespace :> Tradespace
  ; tm_ParetoFront :> ParetoFront

  (* Internal, formal-specification-based types *)
  ; FormalSpecificationType: Set
  ; FormalImplementationType: Set
  ; FormalAbstractMeasurementFunctionSet: Set
  ; FormalConcreteMeasurementFunctionSet: Set

  (* Internal, formal-specification-based functions *)
  ; cFunction: FormalSpecificationType -> list FormalImplementationType
  ; aFunction: FormalImplementationType -> FormalSpecificationType
  ; lFunction: FormalSpecificationType -> FormalAbstractMeasurementFunctionSet
  ; tFunction: FormalAbstractMeasurementFunctionSet -> list ImplementationType -> list FormalConcreteMeasurementFunctionSet


  (* mappings into and out of formal-specification-based representations *)
  ; sFunction: SpecificationType -> FormalSpecificationType
  ; iFunction: FormalImplementationType -> ImplementationType
  ; bFunction: FormalAbstractMeasurementFunctionSet -> FormalConcreteMeasurementFunctionSet
}.

(** 
Note: I have removed the "laws" from this specification.
*)

Extraction Language Scala.
Recursive Extraction 
SpecificationType
ImplementationType
MeasurementFunctionSetType
MeasurementResultSetType
FormalSpecificationType
FormalImplementationType
FormalAbstractMeasurementFunctionSet
FormalConcreteMeasurementFunctionSet
synthesize
runBenchmark
analyze
tradespace
Tradespace 
ParetoFront
Trademaker
paretoFilter
paretoFront
cFunction
aFunction
lFunction
tFunction
sFunction
iFunction
bFunction
prod
fst
snd
combine
hd
tl.

