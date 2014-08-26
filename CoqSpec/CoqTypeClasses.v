Require Import Coq.Lists.List.
Require Import String.

Open Scope list_scope.


Class Tradespace := {
    SpecificationType: Set
  ; ImplementationType: Set
  ; MeasurementFunctionSetType: Set
  ; MeasurementResultSetType: Set
(*  ; synthesize: SpecificationType -> list (ImplementationType * MeasurementFunctionSetType)
  synthesize will call cFunction to synthesize formal implementation and iFunction to create implementation.
  Also, it will call lFunction to create abstract measurement function, and tFunction to create concrete measurement function, and 
  bFunction to create measurement function
*)
  ; synthesize : SpecificationType -> list (ImplementationType * MeasurementFunctionSetType)
      

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

  ; synthesize := 
      let impl := 
        let spec := SpecificationtType 
        in map iFunction (cFunction spec) 
        with
          mfs := _ 
      in list (impl * mfs)

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
  ; aInvertsC: forall (spec: FormalSpecificationType) (fImpl: FormalImplementationType), In fImpl (cFunction spec) -> (spec = aFunction fImpl)

  ; implementationLine: forall (spec: SpecificationType) (fImpl: FormalImplementationType) (impl: ImplementationType) (fSpec: FormalSpecificationType), 
                            (fSpec = sFunction spec) -> (In fImpl (cFunction fSpec)) -> (impl = iFunction fImpl) 
                                           -> In impl (map (@fst ImplementationType  MeasurementFunctionSetType) (synthesize spec)) 

  ; testLoadsLine: forall (spec: SpecificationType) (mfs: MeasurementFunctionSetType) (fCMFs: FormalConcreteMeasurementFunctionSet)
                          (fSpec: FormalSpecificationType) (fAMFs: FormalAbstractMeasurementFunctionSet), 
                           (fSpec = sFunction spec) -> (fAMFs = (lFunction fSpec)) -> (In fCMFs (tFunction fAMFs (cFunction fSpec)))
                            -> (mfs = bFunction fCMFs) -> In mfs (map (@snd ImplementationType  MeasurementFunctionSetType) (synthesize spec))
}.


(*
Class Trademaker := {
    tm_Tradespace :> Tradespace
  ; tm_ParetoFront :> ParetoFront

  ; synthesize := 
      let impl := iFunction (cFunction) with
          mfs := _ in
          list (impl * mfs)

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
  ; aInvertsC: forall (spec: FormalSpecificationType) (fImpl: FormalImplementationType), In fImpl (cFunction spec) -> (spec = aFunction fImpl)

  ; implementationLine: forall (spec: SpecificationType) (fImpl: FormalImplementationType) (impl: ImplementationType) (fSpec: FormalSpecificationType), 
                            (fSpec = sFunction spec) -> (In fImpl (cFunction fSpec)) -> (impl = iFunction fImpl) 
                                           -> In impl (map (@fst ImplementationType  MeasurementFunctionSetType) (synthesize spec)) 

  ; testLoadsLine: forall (spec: SpecificationType) (mfs: MeasurementFunctionSetType) (fCMFs: FormalConcreteMeasurementFunctionSet)
                          (fSpec: FormalSpecificationType) (fAMFs: FormalAbstractMeasurementFunctionSet), 
                           (fSpec = sFunction spec) -> (fAMFs = (lFunction fSpec)) -> (In fCMFs (tFunction fAMFs (cFunction fSpec)))
                            -> (mfs = bFunction fCMFs) -> In mfs (map (@snd ImplementationType  MeasurementFunctionSetType) (synthesize spec))
}.
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

