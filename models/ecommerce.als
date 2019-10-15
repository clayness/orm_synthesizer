module ecommerce
open Declaration

one sig Customer extends Class{}{
attrSet = customerID
id=customerID
isAbstract = No
no parent
}

one sig customerID extends Integer{}


one sig Order extends Class{}{
attrSet = orderID
id=orderID
isAbstract = No
no parent
}

one sig orderID extends Integer{}


one sig CustomerOrderAssociation extends Association{}{
src = Customer
dst = Order
src_multiplicity = ONE
dst_multiplicity = MANY
}

one sig ShippingCart extends Class{}{
attrSet = shippingCartID
id=shippingCartID
isAbstract = No
no parent
}

one sig shippingCartID extends Integer{}


one sig CustomerShippingCartAssociation extends Association{}{
src = Customer
dst = ShippingCart
src_multiplicity = ONE
dst_multiplicity = MANY
}


one sig Item extends Class{}{
attrSet = ItemID+quantity
id=ItemID
isAbstract = No
no parent
}

one sig ItemID extends Integer{}
one sig quantity extends Integer{}

one sig CartItem extends Class{}{
attrSet = cartItemID
one parent
id=ItemID
isAbstract = No
parent in Item
}

one sig cartItemID extends Integer{}


one sig ShippingCartItemAssociation extends Association{}{
src = ShippingCart
dst = Item
src_multiplicity = ONE
dst_multiplicity = MANY
}

one sig OrderItem extends Class{}{
attrSet = orderItemID+status
one parent
id=ItemID
isAbstract = No
parent in Item
}

one sig status extends Integer{}
one sig orderItemID extends Integer{}


one sig OrderItemAssociation extends Association{}{
src = Order
dst = Item
src_multiplicity = ONE
dst_multiplicity = MANY
}

one sig Product extends Class{}{
attrSet = productID+productName+description+price
id=productID
isAbstract = No
no parent
}


one sig productID extends Integer{}
one sig productName extends string{}
one sig description extends string{}
one sig price extends Real{}


one sig ProductItemAssociation extends Association{}{
src = Product
dst = Item
src_multiplicity = MANY
dst_multiplicity = MANY
}


pred show{}
run show for 48
