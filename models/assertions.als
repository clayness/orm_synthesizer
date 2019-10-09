open relationalModel
open util/relation

// At most one table is assigned to each class 
assert tableForEachClass{
	all c:Class| lone c.~tAssociate
}


// no table is associated to abstract classes
assert noTableForAbstractClasses{
	all c:Class| c.isAbstract=Yes => no c.~tAssociate
}

// no class can be a parent of itself
assert noClassIsParentItself{
	all c:Class| ! c in c.^parent
}

assert idInAttributeSet{
	all c:Class| c.id in c.*parent.attrSet
}


// Each table encompasses relational fields corresponding to attributes of the related class.
// We specify that expression as "c.~tAssociate.tAssociate.attrSet.~fAssociate" in Alloy---rather than simply defining 
// it as "c.attrSet.~fAssociate"---as in case of applying the UnionSuperclass strategy, 
// one table is assigned to a set of classes within an inheritance hierarchy, and
// the associated class contains fields for attributes of all those classes.
// tAssociate is a relation from a table to a corresponding class. using the reverse join operator, ~, "c.~tAssociate" states  
// the table associated to the class c, and then another join, ".tAssociate", returns a set of all classes handled by that table. 

// if a class is being mapped by UnionSuperclass strategy, then the associated table also contains a separate field 
// to indicate the most specific class for the object represented by each tuple. This field is of type DType.

//// The reason that we specify that expression as "c.~tAssociate.tAssociate.attrSet.~fAssociate" in Alloy is that
//// in case of applying UnionSuperclass strategy, one table is assigned to a set of classes within an inheritance hierarchy,
//// The associated class contains associated fields for attributes of all those classes.
assert tableFields{
	all c:Class| c.~tAssociate.fields in
					c.~tAssociate.tAssociate.attrSet.~fAssociate + 
					c.*parent.attrSet.~fAssociate +c.~tAssociate.foreignKey + DType.~fAssociate
}

//You also want that each table encompassing not only all, but also only, those fields that correspond to attributes to the related class or association.

// fAssociate relates fields of tables to attributes of classes
assert attributeForEachFieldAssociate{
	Field.fAssociate in Attribute
}

// foreignKeys of each table should be in primaryKeys of other related tables 
assert foreignKey{
	foreignKey.fAssociate.ran in
	foreignKey.ran.fAssociate.~attrSet.id + foreignKey.dom.tAssociate.dst.id + foreignKey.dom.tAssociate.src.id
}


// for one-to-one associations there are solutions that include both zero and one tables assigned to that association; same for 1-N; 
// and  for N-N associations, there is exactly one such table.
// and each assigned table is only assigned to that association 
assert tableforAssociation{
	all a:Association| lone a.~tAssociate
	all a:Association| (Association.src_multiplicity = MANY) implies one a.~tAssociate
	all a:Association| one a.~tAssociate.tAssociate iff one a.~tAssociate
}
