package edu.virginia.cs.Synthesizer;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class PrintOrder {

	private static ArrayList<String> printOrder = new ArrayList<String>();

	public static ArrayList<String> getOutPutOrders(String instFile) {
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String instFiles[] = instFile.split(pattern);
		String fileName = instFiles[instFiles.length - 1];

		if (fileName.contains("customer")) {
			printOrder.add("Customer");
			printOrder.add("PreferredCustomer");
			printOrder.add("Order");
			printOrder.add("CustomerOrderAssociation");

		} else if (fileName.contains("CSOS")) {
			printOrder.add("Channel");
			printOrder.add("Principal");
			printOrder.add("Role");
			printOrder.add("ProcessStateMachine");
			printOrder.add("ProcessStateMachineState");
			printOrder.add("ProcessStateMachineAction");
			printOrder.add("ProcessStateMachineEvent");
			printOrder.add("ProcessStateMachineTransition");
			printOrder.add("ProcessStateMachineExecution");
			printOrder.add("EmailChannel");
			printOrder.add("SMSChannel");
			printOrder.add("ProcessQueryResponse");
			printOrder.add("ProcessQueryResponseAction");
			printOrder.add("ProcessQueryResponseExecution");
			printOrder.add("PrincipalProxy");
			printOrder.add("PrincipalRole");
			printOrder.add("MachineStates");
			printOrder.add("TerminalStates");
			printOrder.add("StateMachineEvents");
			printOrder.add("StateMachineTransitions");
		} else if (fileName.contains("ecommerce")) {
			printOrder.add("Customer");
			printOrder.add("Asset");
			printOrder.add("Order");
			printOrder.add("ShippingCart");
			printOrder.add("Item");
			printOrder.add("Category");
			printOrder.add("Catalog");
			printOrder.add("Product");
			printOrder.add("CartItem");
			printOrder.add("OrderItem");
			printOrder.add("PhysicalProduct");
			printOrder.add("ElectronicProduct");
			printOrder.add("Service");
			printOrder.add("Media");
			printOrder.add("Documents");
			printOrder.add("CustomerOrderAssociation");
			printOrder.add("CustomerShippingCartAssociation");
			printOrder.add("ShippingCartItemAssociation");
			printOrder.add("OrderItemAssociation");
			printOrder.add("ProductCategoryAssociation");
			printOrder.add("ProductCatalogAssociation");
			printOrder.add("ProductItemAssociation");
			printOrder.add("ProductAssetAssociation");

		} else if (fileName.contains("decider")) {
			printOrder.add("User");
			printOrder.add("NameSpace");
			printOrder.add("Variable");
			printOrder.add("Relationship");
			printOrder.add("Role");
			printOrder.add("Cluster");
			printOrder.add("DecisionSpace");
			printOrder.add("roleBindings");
			printOrder.add("Participants");
			printOrder.add("DSN");
			printOrder.add("NameSpaceOwnerAssociation");
			printOrder.add("varInAssociation");
			printOrder.add("varOutAssociation");
			printOrder.add("clusterVariableAssociation");
			printOrder.add("userDecisionSpaceAssociation");
			printOrder.add("descisionSpaceRoleBindingsAssociation");
			printOrder.add("descisionSpaceParticipantsAssociation");
			printOrder.add("descisionSpaceVariablesAssociation");
			printOrder.add("descisionSpaceRoleAssociation");
			printOrder.add("DSNUserAssociation");
			printOrder.add("DSNNamespaceAssociation");
			printOrder.add("DSNDecisionSpaceAssociation");

		} else if (fileName.contains("person")) {
			printOrder.add("Person");
			printOrder.add("Student");
			printOrder.add("Employee");
			printOrder.add("Clerk");
			printOrder.add("Manager");

		} else if (fileName.contains("wordpress")) {
			printOrder.add("CommentMeta");
			printOrder.add("Comments");
			printOrder.add("Links");
			printOrder.add("PostMeta");
			printOrder.add("Posts");
			printOrder.add("Pages");
			printOrder.add("UserMeta");
			printOrder.add("Users");
			printOrder.add("Terms");
			printOrder.add("Tags");
			printOrder.add("Category");
			printOrder.add("PostCategory");
			printOrder.add("LinkCategory");
			printOrder.add("CommentPostAssociation");
			printOrder.add("CommentUserAssociation");
			printOrder.add("PostUserAssociation");
			printOrder.add("TermPostsAssociation");
			printOrder.add("TermLinksAssociation");

		} else if (fileName.contains("moodle")) {
			printOrder.add("Course");
			printOrder.add("GradeItem");
			printOrder.add("Grades");
			printOrder.add("ScaleGrades");
			printOrder.add("PointGrades");
			printOrder.add("GradeSettings");
			printOrder.add("ImportNewItem");
			printOrder.add("ImportValues");
			printOrder.add("CourseGradeItemAssociation");
			printOrder.add("CourseGradeSettingsAssociation");

		} else if (fileName.contains("ke")) {
			printOrder.add("Response");

		} else { // this is the default case, for any other object model, the
					// print order is empty string list
		}
		return printOrder;
	}
}
