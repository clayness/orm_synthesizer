module AssociationMappings

open relationalModel
open Declaration

one sig ForeignKeyEmbeddingStrategy extends Strategy {} {
  no assignees & Class
  all a : Association | a in assignees <=> ForeignKeyEmbedding[a]
}

one sig OwnAssociationTableStrategy extends Strategy {} {
  no assignees & Class
  all a : Association | a in assignees <=> OwnAssociationTable[a]
}

one sig MergingOneTableStrategy extends Strategy {} {
  no assignees & Class
  all a : Association | a in assignees <=> MergingOneTable[a]
}

one sig AssociationMappingStrategies {
  FKE : set Association,
  OAT : set Association,
  MOT : set Association  
} {
  all a : Association {
    a in FKE <=> ForeignKeyEmbedding[a]
    a in OAT <=> OwnAssociationTable[a]
    a in MOT <=> MergingOneTable[a]
  }
}

pred ForeignKeyEmbedding[asc:Association]{
	one t:Table| t.tAssociate=asc.dst
	no t:Table|t.tAssociate=asc
	!((asc.src.~tAssociate = asc.src.~parent.~tAssociate) || (asc.src.~tAssociate = asc.src.parent.~tAssociate)) =>
		one t:Table| t.tAssociate=asc.src

	no asc.~tAssociate 
	one asc.dst.~tAssociate 

	one asc.src.~tAssociate 


	all a:asc.src.attrSet|a not in Class => {
		one f:Field| f.fAssociate=a && (one f.fAssociate) && (one a.~fAssociate)  &&
		f in a.~attrSet.~tAssociate.fields
	}

	asc.src.~tAssociate.fields in 
	(asc.src.~tAssociate.foreignKey + asc.src.~tAssociate.tAssociate.attrSet.~fAssociate +DType.~fAssociate) 
										

	all a:asc.dst.attrSet|a not in Class => {
		one f:Field| f.fAssociate=a && (one f.fAssociate) && (one a.~fAssociate)  &&
		f in a.~attrSet.~tAssociate.fields
	}

	asc.dst.~tAssociate.fields in 
	(asc.dst.~tAssociate.foreignKey + asc.dst.~tAssociate.tAssociate.attrSet.~fAssociate +DType.~fAssociate) 

	//all t:Table| t.primaryKey = t.tAssociate.id.~fAssociate
	asc.src.~tAssociate.primaryKey = asc.src.id.~fAssociate
	asc.dst.~tAssociate.primaryKey = asc.dst.id.~fAssociate

	asc.src.~tAssociate.primaryKey in asc.dst.~tAssociate.fields

	asc.src.~tAssociate.primaryKey in (asc.dst.~tAssociate).foreignKey 
	#(asc.dst.~tAssociate).foreignKey = #{a:Association|a.dst=asc.dst and a.dst_multiplicity = MANY and no a.~tAssociate}
	
	# (asc.src.~tAssociate).foreignKey = #{a:Association|a.dst=asc.src and a.dst_multiplicity = MANY and no a.~tAssociate}

}


pred OwnAssociationTable[asc:Association]{

	one t:Table|asc.src in t.tAssociate
	one t:Table|asc.dst in t.tAssociate
	one t:Table|t.tAssociate=asc

	one asc.~tAssociate 
	one asc.src.~tAssociate 
	one asc.dst.~tAssociate 

	all a:asc.src.attrSet|a not in Class => {
		one f:Field|f.fAssociate=a &&  (one f.fAssociate) && (one a.~fAssociate)  &&
		f in a.~attrSet.~tAssociate.fields
	}

	asc.src.~tAssociate.fields in 
	(asc.src.~tAssociate.foreignKey + asc.src.~tAssociate.tAssociate.attrSet.~fAssociate +DType.~fAssociate) 

	all a:asc.dst.attrSet|a not in Class => {
		one f:Field|f.fAssociate=a &&  (one f.fAssociate) && (one a.~fAssociate)  &&
		f in a.~attrSet.~tAssociate.fields
	}


	asc.dst.~tAssociate.fields in 
	(asc.dst.~tAssociate.foreignKey + asc.dst.~tAssociate.tAssociate.attrSet.~fAssociate +DType.~fAssociate) 


	asc.src.~tAssociate.primaryKey = asc.src.id.~fAssociate
	asc.dst.~tAssociate.primaryKey = asc.dst.id.~fAssociate

	asc.~tAssociate.primaryKey = asc.src.~tAssociate.primaryKey + asc.dst.~tAssociate.primaryKey
	asc.~tAssociate.foreignKey = asc.src.id.~fAssociate + asc.dst.id.~fAssociate
	asc.~tAssociate.fields = asc.src.~tAssociate.primaryKey + asc.dst.~tAssociate.primaryKey
	#asc.~tAssociate.fields = 2	


	#(asc.dst.~tAssociate).foreignKey = #{a:Association|a.dst=asc.dst and a.dst_multiplicity = MANY and no a.~tAssociate}	
	# (asc.src.~tAssociate).foreignKey = #{a:Association|a.dst=asc.src and a.dst_multiplicity = MANY and no a.~tAssociate}

}

pred MergingOneTable[asc:Association]{

	one t:Table|(t.tAssociate=asc +asc.src + asc.dst)&&(#t.tAssociate=3)
	# asc.~tAssociate = 1


	// Each basic attribute is assigned to a field of the related table
	all a:asc.src.attrSet|a not in Class => {one f:Field|f.fAssociate=a && 
	f in a.~attrSet.~tAssociate.fields}
	all a:asc.dst.attrSet|a not in Class => {one f:Field|f.fAssociate=a && 
	f in a.~attrSet.~tAssociate.fields}



	one f:Field|f.fAssociate=asc.src && f in asc.~tAssociate.fields

	one f:Field|f.fAssociate=asc.dst && f in asc.~tAssociate.fields

	// Each Field handles only one association
	#Field = #Field.fAssociate


	// In one-to-one association relationships, the primary key of the table associated 
	// to the combination of Classes and their Association can be the primary key of
	// either of classes, here it is assigned to the primary key of the src of the association
	asc.~tAssociate.primaryKey = Field<:(asc.src.id.~fAssociate)
}

pred mixedAssociationMapping[asc:Association]{
	asc.src_multiplicity = ONE => {OwnAssociationTable[asc] or 
													ForeignKeyEmbedding[asc]} 
	else 	OwnAssociationTable[asc]
//	ForeignKeyEmbedding[asc]
}


pred mixedAssociationStrategy[asc:set Association]{
	all c:asc| mixedAssociationMapping[c]
}



// Each Class or Association is handled by just one Table
// At most one Table is assigned to each Class or Association
// In  "Meging into single" and "Own association Table" strategies it would be exactly one Table for each Class or Association
// In  "Foreign Key embedding" strategy it would be exactly one Table per Class, and no Table per Association
fact OneTablePerClassAndAssociation{
	// At most one Table is assigned to each Class involved in an association
	all asc:Association|#(Table<:asc.src.~tAssociate)=1 && #(Table<:asc.dst.~tAssociate)=1
}


pred show{}
run show for 12
