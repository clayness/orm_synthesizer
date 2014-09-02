Require Import Coq.Lists.List.
Require Import String.

Open Scope list_scope.

Class Tradespace := {
    SpecificationType: Set
  ; ImplementationType: Set
  ; MeasurementFunctionSetType: Set
  ; MeasurementResultSetType: Set
  ; synthesize : SpecificationType -> list (ImplementationType * MeasurementFunctionSetType)
  ; runBenchmark: ImplementationType * MeasurementFunctionSetType -> (ImplementationType * MeasurementResultSetType)
  ; analyze (input: list (ImplementationType * MeasurementFunctionSetType)) : list (ImplementationType * MeasurementResultSetType) :=
      map runBenchmark input
  ; tradespace (spec: SpecificationType): list (ImplementationType * MeasurementResultSetType) :=
     analyze (synthesize spec) 
}.

Inductive DBSpecification: Set := DBSpecification1.
Inductive DBImplementation: Set := DBImplementation1.
Inductive DBMeasurementFunctionSet: Set := DBMeasurementFunctionSet1.
Inductive DBMeasurementResultSet: Set :=  DBMeasurementResultSet1.

Definition dbSynthesize (spec: DBSpecification) : list (DBImplementation * DBMeasurementFunctionSet) :=
  (DBImplementation1, DBMeasurementFunctionSet1) :: nil.

Definition dbRunBenchmark (mfPair : DBImplementation * DBMeasurementFunctionSet) : (prod DBImplementation DBMeasurementResultSet) := 
  (DBImplementation1, DBMeasurementResultSet1).

Instance DBTradeSpace: Tradespace := {
   SpecificationType := DBSpecification
  ; ImplementationType := DBImplementation
  ; MeasurementFunctionSetType := DBMeasurementFunctionSet
  ; MeasurementResultSetType := DBMeasurementResultSet
  ; synthesize := dbSynthesize
  ; runBenchmark := dbRunBenchmark
}.


Class ParetoFront := {
    pf_tradespace :> Tradespace
  ; paretoFilter: list (ImplementationType * MeasurementResultSetType) -> list (ImplementationType * MeasurementResultSetType)
  ; paretoFront (spec: SpecificationType): list (ImplementationType * MeasurementResultSetType) := paretoFilter (tradespace spec)
}.

Definition DBParetoFilter (implList: list (DBImplementation * DBMeasurementResultSet)) := 
  (DBImplementation1, DBMeasurementResultSet1) :: nil.

Instance DBParetoFront: ParetoFront := {
    pf_tradespace := DBTradeSpace
  ; paretoFilter := DBParetoFilter
}.


Class Trademaker := {
    tm_Tradespace :> Tradespace
  ; tm_ParetoFront :> ParetoFront

  (* Internal, formal-specification-based types *)
  ; FormalSpecificationType: Set
  ; FormalImplementationType: Set
  ; FormalAbstractMeasurementFunctionSetType: Set
  ; FormalConcreteMeasurementFunctionSetType: Set

  (* Internal, formal-specification-based functions *)
  ; cFunction: FormalSpecificationType -> list FormalImplementationType
  ; aFunction: FormalImplementationType -> FormalSpecificationType
  ; lFunction: FormalSpecificationType -> FormalAbstractMeasurementFunctionSetType
  ; tFunction: FormalAbstractMeasurementFunctionSetType -> list ImplementationType -> list FormalConcreteMeasurementFunctionSetType

  (* mappings into and out of formal-specification-based representations *)
  ; sFunction: SpecificationType -> FormalSpecificationType
  ; iFunction: FormalImplementationType -> ImplementationType
  ; bFunction: FormalConcreteMeasurementFunctionSetType -> MeasurementFunctionSetType

  (* Laws *)
  ; aInvertsC: forall (spec: FormalSpecificationType) (fImpl: FormalImplementationType), In fImpl (cFunction spec) -> (spec = aFunction fImpl)

  ; implementationLine: forall (spec: SpecificationType) (fImpl: FormalImplementationType) (impl: ImplementationType) (fSpec: FormalSpecificationType), 
                            (fSpec = sFunction spec) -> (In fImpl (cFunction fSpec)) -> (impl = iFunction fImpl) 
                                           -> In impl (map (@fst ImplementationType  MeasurementFunctionSetType) (synthesize spec)) 

  ; testLoadsLine: forall (spec: SpecificationType) (mfs: MeasurementFunctionSetType) (fCMFs: FormalConcreteMeasurementFunctionSetType)
                          (fSpec: FormalSpecificationType) (fAMFs: FormalAbstractMeasurementFunctionSetType) (impl: ImplementationType)
                          (fImpl: FormalImplementationType),
                          (fSpec = sFunction spec) -> (In fImpl (cFunction fSpec)) -> (fAMFs = (lFunction fSpec)) ->
                          (In fCMFs (tFunction  fAMFs (map iFunction (cFunction fSpec)))) -> (mfs = bFunction fCMFs) -> 
                          In mfs (map (@snd ImplementationType  MeasurementFunctionSetType) (synthesize spec))
}.


Inductive DBFormalSpecification: Set := DBFormalSpecification1.
Inductive DBFormalImplementation: Set := DBFormalImplementation1.
Inductive DBFormalAbstractMeasurementFunctionSet: Set := DBFormalAbstractMeasurementFunctionSet1.
Inductive DBFormalConcreteMeasurementFunctionSet: Set := DBFormalConcreteMeasurementFunctionSet1.

Definition dbCFunction (spec: DBFormalSpecification) : list DBFormalImplementation := 
  DBFormalImplementation1::nil.

Definition dbAFunction (impl: DBFormalImplementation) : DBFormalSpecification := DBFormalSpecification1.

Definition dbLFunction (spec: DBFormalSpecification) : DBFormalAbstractMeasurementFunctionSet := DBFormalAbstractMeasurementFunctionSet1.

Definition dbTFunction (dbFAMFS: DBFormalAbstractMeasurementFunctionSet) (implList: list ImplementationType) : list DBFormalConcreteMeasurementFunctionSet := 
  DBFormalConcreteMeasurementFunctionSet1::nil.

Definition dbSFunction (spec: DBSpecification) : DBFormalSpecification := DBFormalSpecification1.
Definition dbIFunction (fImpl: DBFormalImplementation) : DBImplementation := DBImplementation1.
Definition dbBFunction (dbFCMFS: DBFormalConcreteMeasurementFunctionSet) : DBMeasurementFunctionSet := DBMeasurementFunctionSet1.

Instance DBTrademaker : Trademaker := {
    tm_Tradespace := DBTradeSpace
  ; tm_ParetoFront := DBParetoFront
  ; FormalSpecificationType := DBFormalSpecification
  ; FormalImplementationType := DBFormalImplementation
  ; FormalAbstractMeasurementFunctionSetType := DBFormalAbstractMeasurementFunctionSet
  ; FormalConcreteMeasurementFunctionSetType := DBFormalConcreteMeasurementFunctionSet
  ; cFunction := dbCFunction
  ; aFunction := dbAFunction
  ; lFunction := dbLFunction
  ; tFunction := dbTFunction
  ; sFunction := dbSFunction
  ; iFunction := dbIFunction
  ; bFunction := dbBFunction
}.
Proof.
intros.
induction fImpl.
induction spec.
compute. reflexivity.
intros.
induction spec.
compute.
induction impl.
apply or_introl. reflexivity.
intros.
induction spec.
compute.
apply or_introl.
induction mfs.
reflexivity.
Qed.

Extraction Language Scala.
Recursive Extraction 
SpecificationType
ImplementationType
MeasurementFunctionSetType
MeasurementResultSetType
FormalSpecificationType
FormalImplementationType
FormalAbstractMeasurementFunctionSetType
FormalConcreteMeasurementFunctionSetType
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

