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
  ; tFunction: FormalAbstractMeasurementFunctionSet -> list FormalImplementationType -> list FormalConcreteMeasurementFunctionSet

  (* mappings into and out of formal-specification-based representations *)
  ; sFunction: SpecificationType -> FormalSpecificationType
  ; iFunction: FormalImplementationType -> ImplementationType
  ; bFunction: FormalConcreteMeasurementFunctionSet -> MeasurementFunctionSetType

  (* Laws *)
  ; aInvertsC: forall (spec: FormalSpecificationType) (impl: FormalImplementationType), In impl (cFunction spec) -> (spec = aFunction impl)

  ; implementationLine: forall (spec: SpecificationType) (cdt: FormalImplementationType) (impl: ImplementationType) (bmt: MeasurementFunctionSetType) 
                          (clt: FormalConcreteMeasurementFunctionSet) (adt: FormalSpecificationType) (alt: FormalAbstractMeasurementFunctionSet), 
                            (adt = sFunction spec) 
                              -> (In cdt (cFunction adt)) 
                                -> (impl = iFunction cdt) -> In impl (map (@fst ImplementationType  MeasurementFunctionSetType) (synthesize spec)) 

  ; testLoadsLine: forall (st: SpecificationType) (cdt: FormalImplementationType) (imt: ImplementationType) (bmt: MeasurementFunctionSetType) 
                          (clt: FormalConcreteMeasurementFunctionSet) (adt: FormalSpecificationType) (alt: FormalAbstractMeasurementFunctionSet), 
                           (adt = sFunction st) -> (alt = (lFunction adt)) -> (In clt (tFunction alt (cFunction adt)))
                            -> (bmt = bFunction clt) -> In bmt (map (@snd ImplementationType  MeasurementFunctionSetType) (synthesize st))
}.



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

