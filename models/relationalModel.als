module relationalModel
open util/relation

open Declaration

sig Table{
	fields: set Field,
	primaryKey: set Field,
	foreignKey: set Field,
	tAssociate: some Class+Association
}{
	primaryKey in fields
	foreignKey in fields 
}

sig Field{
	fAssociate: one needHandle
}{
	
}

fact {
	all n:needHandle| lone n.~fAssociate
	all c:Class| lone c.~tAssociate
	all a:Association| lone a.~tAssociate
	Field in Table.fields
	no (Field.fAssociate & Association)
	no (Field.fAssociate & Class)
	Attribute in (Field.fAssociate + Class.isAbstract + Class)

	foreignKey.fAssociate.ran in	id.ran 


	all c:Class| !((c.~tAssociate = c.~parent.~tAssociate) || (c.~tAssociate = c.parent.~tAssociate)) => //.~tAssociate & c.~tAssociate) => //(no c.parent ||  
		c.~tAssociate.fields in (c.~tAssociate.foreignKey + c.attrSet.~fAssociate  + c.^parent.attrSet.~fAssociate)


}

pred show{}

run show for 20
