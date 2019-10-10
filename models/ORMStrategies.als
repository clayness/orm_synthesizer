module ORMStrategies

open relationalModel
open Declaration

one sig UnionSubclassStrategy extends Strategy {}
{
    no assignees & Association
    all c : Class | c in assignees <=> UnionSubclass[c]
}
one sig JoinedSubclassStrategy extends Strategy {}
{
    no assignees & Association
    all c : Class | c in assignees <=> JoinedSubclass[c]
}
one sig UnionSuperclassStrategy extends Strategy {}
{
    no assignees & Association
    all c : Class | c in assignees <=> UnionSuperclass[c]
}

pred UnionSubclass[c:Class]{

// Assinging a table to the concrete class 
c  in (isAbstract.No) =>{
	one t:Table| (t.tAssociate=c)&&(#t.tAssociate=1)
}

# c.~tAssociate = 1

// Each table encompasses relational fields corresponding to both attributes of the associated class 
// and its inherited attributes.
(c.isAbstract=No) =>{ 
	all a:Attribute|a in c.attrSet => 
	{	one f:Field|f.fAssociate=a 
		&& f in (c.~tAssociate.fields)
	} 
}

// The associated table of each concrete class should also include the fields of its abstract parent's attributes
(c.isAbstract=No) && (c.^parent != none) => {
	all a:Attribute | a in c.^parent.attrSet => {
	 	one f:Field|f.fAssociate=a && 
		f in c.~tAssociate.fields
	}	
}
#c.~tAssociate.fields = # (c.*parent).attrSet

(c.isAbstract=No) =>{
	one f:Field|f.fAssociate=c.id && 
	f in c.~tAssociate.fields
}

// Assigning the primary Key
(c.~tAssociate).primaryKey = (c.id.~fAssociate)
// Assigning the foreign Key
(c.~tAssociate).foreignKey = none
}

pred JoinedSubclass[c:Class]{

one t:Table| (t.tAssociate=c)&&(#t.tAssociate=1)

# c.~tAssociate = 1

// Each table encompasses relational fields corresponding to non-inherited attributes of the related class
//all a:Class.attrSet|one f:Field|f.associate=a && f in a.~attrSet.~associate.fields
all a:Attribute|a in c.attrSet => 
{	one f:Field|f.fAssociate=a 
	&& f in (c.~tAssociate.fields)
} 

(c.parent!=none)=>{
	#c.~tAssociate.fields =  (#c.attrSet)+1
	}else{
	#c.~tAssociate.fields =  (#c.attrSet)
}

one f:Field|f.fAssociate=c.id && 
f in c.~tAssociate.fields

// Assigning the primary Key
(c.~tAssociate).primaryKey = (c.id.~fAssociate)

// Assigning the foreign Key
(c.parent != none) =>{
	(c.~tAssociate).foreignKey = c.parent.id.~fAssociate
	}else{
	(c.~tAssociate).foreignKey = none
}
(c.parent != none)
}


pred UnionSuperclass[c:Class]{

// If c has a child mapped by UnionSuperClass strategy, then just map this class 
// to the table to which the child is mapped
c  in (isAbstract.No) =>{
	one Table<:c.~tAssociate	
}


// Each table encompasses relational fields corresponding to both attributes of the associated class 
// and its inherited attributes.
(c.isAbstract=No) =>{ 
	all a:Attribute|a in c.*parent.attrSet => 
	{	one f:Field|f.fAssociate=a 
		&& f in (a.~attrSet.~tAssociate.fields)
	} 
}


// The associated table has a type attribute to indicate the most specific class for the object represented by a tuple
//one f:Field| f.fAssociate in c.*parent  && f in c.~tAssociate.fields
one f:Field| f.fAssociate in DType  && f in c.~tAssociate.fields &&
c.~tAssociate.foreignKey + c.~tAssociate.tAssociate.attrSet.~fAssociate +f = c.~tAssociate.fields 

//#c.~tAssociate.fields =  (#c.~tAssociate.tAssociate.attrSet)+1+(#c.~tAssociate.foreignKey)


// when a subclass is assigned to be mapped by UnionSuperclass strategy, all its parents in the same hierarchy
// are needed to be mapped using the same strategy. 
(c.*parent).~tAssociate = c.~tAssociate


// Assigning the primary Key
(c.~tAssociate).primaryKey = (c.id.~fAssociate)

// Assigning the foreign Key
(no a:Association| a.dst in c.*parent) =>{ 
	no (c.~tAssociate).foreignKey
}
}

pred SRInheritance[cs:set Class]{
all c:cs| c  in (isAbstract.No) =>{
	one Table<:c.~tAssociate	
}

all c:cs| (c.isAbstract=No) =>{ 
all a:Attribute|a in c.attrSet => 
{	one f:Field|f.fAssociate=a 
	&& f in (a.~attrSet.~tAssociate.fields)
} 
}

all c:cs| (c.isAbstract=No) && (c.^parent != none) => {
	all a:Attribute | a in c.^parent.attrSet => {
	 	one f:Field|f.fAssociate=a && 
		f in c.~tAssociate.fields
	}	
}

// The associated table has a type attribute to indicate the most specific class for the object represented by a tuple
one f:Field| f.fAssociate in cs  && f in cs.~tAssociate.fields

all c:cs| (c.~tAssociate).primaryKey = (c.id.~fAssociate)

// Assigning the foreign Key
all c:cs| no (c.~tAssociate).foreignKey
}

fact{
	all t:Table| t.tAssociate in Class+Association 
}

//recently added for enabling DSE
pred mixedStrategy[cs:set Class]{
	all c:cs| UnionSubclass[c] or JoinedSubclass[c] or UnionSuperclass[c]
}

pred show{}
run show for 12
