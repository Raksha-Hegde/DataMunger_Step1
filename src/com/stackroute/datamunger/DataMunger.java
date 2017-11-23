package com.stackroute.datamunger;

import java.util.Scanner;

public class DataMunger {

	static String queryString;

	public static void main(String[] args) {

		DataMunger datamunger = new DataMunger();

		// read the query from the user into queryString variable
		System.out.println("Enter your query:");
		Scanner sc = new Scanner(System.in);
		queryString = sc.nextLine();

		sc.close();

		// call the parseQuery method and pass the queryString variable as a parameter
		datamunger.parseQuery(queryString);

	}

	/*
	 * This methods is used to add white spaces
	 */

	public String replaceCharacters() {
		queryString = queryString.replace(";", "");
		queryString = queryString.replace("=", " = ");
		queryString = queryString.replace(">", " > ");
		queryString = queryString.replace("<", " < ");
		queryString = queryString.replace(">=", " >= ");
		queryString = queryString.replace("<=", " <= ");
		queryString = queryString.replace("!=", " != ");

		return queryString;

	}

	/*
	 * we are creating multiple methods, each of them are responsible for extracting
	 * a specific part of the query. However, the problem statement requires us to
	 * print all elements of the parsed queries. Hence, to reduce the complexity, we
	 * are using the parseQuery() method. From inside this method, we are calling
	 * all the methods together, so that we can call this method only from main()
	 * method to print the entire output in console
	 */
	public void parseQuery(String queryString) {

		if (!queryString.isEmpty()) {

			queryString = replaceCharacters();

			// call the methods
			getSplitStrings(queryString); // ----> More work to do
			getFile(queryString); // ----> More work to do
			getBaseQuery(queryString);
			getConditionsPartQuery(queryString);
			getConditions(queryString);
			getLogicalOperators(queryString);
			getFields(queryString);
			getOrderByFields(queryString);
			getGroupByFields(queryString);
			getAggregateFunctions(queryString);
		} else
			System.out.println("Query empty");

	}

	/*
	 * this method will split the query string based on space into an array of words
	 * and display it on console
	 */
	public String[] getSplitStrings(String queryString) {

		queryString = queryString.toLowerCase();
		String[] queryParts = queryString.split("\\s+");

		for (int i = 0; i < queryParts.length; i++)
			System.out.println(queryParts[i]);

		return queryParts;
	}

	/*
	 * extract the name of the file from the query.
	 */
	public String getFile(String queryString) {

		String[] temp = queryString.split("from\\s+");
		String fileName = null;

		if ((temp[1].contains("where"))) {

			temp = temp[1].split("where");
			fileName = temp[0];
		}

		else {

			fileName = temp[1];
		}

		return fileName.trim();
	}

	/*
	 * This method is used to extract the baseQuery from the query string. BaseQuery
	 * contains from the beginning of the query till the where clause
	 */
	public String getBaseQuery(String queryString) {

		String[] temp = null;
		temp = queryString.split("where|order\\s+by|group\\s+by");
		String baseQueryString = temp[0];

		return baseQueryString;

	}

	/*
	 * This method is used to extract the conditions part from the query string.
	 */
	public String getConditionsPartQuery(String queryString) {

		String conditionPart = null;
		String[] temp = queryString.toLowerCase().split("where");

		conditionPart = temp[1];
		if (conditionPart.toLowerCase().contains("order by"))
			temp = conditionPart.split("order\\s+by");
		else if (conditionPart.toLowerCase().contains("group by"))
			temp = conditionPart.split("group\\s+by");

		conditionPart = temp[0];

		return conditionPart;

	}

	/*
	 * This method will extract condition(s) from the query string.
	 */
	public String[] getConditions(String queryString) {

		String conditionPartQuery = getConditionsPartQuery(queryString).trim();
		String[] conditions = null;

		if (conditionPartQuery.toLowerCase().contains(" and ") || conditionPartQuery.toLowerCase().contains(" or "))
			conditions = conditionPartQuery.trim().split("( and )|( or )");
		else
			conditions = new String[] { conditionPartQuery };

		return conditions;

	}

	/*
	 * This method will extract logical operators(AND/OR) from the query string. The
	 * extracted logical operators will be stored in a String array which will be
	 * returned by the method and the same will be printed Note: ------- 1. AND/OR
	 * keyword will exist in the query only if where conditions exists and it
	 * contains multiple conditions. 2. AND/OR can exist as a substring in the
	 * conditions as well. For eg: name='Alexander',color='Red' etc. Please consider
	 * these as well when extracting the logical operators.
	 * 
	 */

	public String[] getLogicalOperators(String queryString) {

		String[] logicalOp = null;

		String[] splitCondition = getSplitStrings(getConditionsPartQuery(queryString).trim().toLowerCase());
		int operatorCounter = 0;

		for (int i = 0; i < splitCondition.length; i++)
			if (splitCondition[i].equals("and") | splitCondition[i].equals("or")) {
				operatorCounter++;

			}

		logicalOp = new String[operatorCounter];
		for (int i = 0, j = 0; i < splitCondition.length; i++)
			if (splitCondition[i].equals("and") | splitCondition[i].equals("or")) {
				logicalOp[j++] = splitCondition[i];

			}

		return logicalOp;

	}

	/*
	 * This method will extract the fields to be selected from the query string. The
	 * query string can have multiple fields separated by comma. The extracted
	 * fields will be stored in a String array which is to be printed in console as
	 * well as to be returned by the method
	 * 
	 */
	public String[] getFields(String queryString) {

		String fields[] = null;
		String[] temp1 = queryString.trim().split("\\s+from");

		temp1 = temp1[0].trim().split("select\\s+");
		for (int i = 0; i < temp1.length; i++) {
			if (temp1[i].contains("*")) {
				fields = new String[] { "*" };
				break;
			} else if (temp1[i].contains(","))
				fields = temp1[i].trim().split(",");

		}
		for (int i = 0; i < fields.length; i++) {
			System.out.println(fields[i]);
		}

		return fields;

	}

	/*
	 * This method extracts the order by fields from the query string.
	 */
	public String[] getOrderByFields(String queryString) {

		String[] orderBy = null;

		orderBy = queryString.trim().split("\\s+order\\s+by\\s+");

		for (int i = 0; i < orderBy.length; i++) {
			if (orderBy[i].contains(","))
				orderBy = orderBy[i].trim().split(",");

		}

		return orderBy;
	}

	/*
	 * This method extracts the group by fields from the query string.
	 */
	public String[] getGroupByFields(String queryString) {

		String[] groupBy = null;

		String[] temp = queryString.trim().split("\\s+group\\s+by\\s+");
		groupBy = temp[1].trim().split(",");

		return groupBy;

	}

	/*
	 * This method extracts the aggregate functions from the query string. Note:
	 * ------ 1. aggregate functions will start with "sum"/"count"/"min"/"max"/"avg"
	 * followed by "(" 2. The field names might
	 * contain"sum"/"count"/"min"/"max"/"avg" as a substring. For eg:
	 * account_number,consumed_qty,nominee_name
	 * 
	 * Consider this while extracting the aggregate functions
	 */
	public String[] getAggregateFunctions(String queryString) {

		String aggregateFunction[] = null;
		String[] fieldsString = getFields(queryString.toLowerCase());
		int counter = 0;

		for (int i = 0 ; i < fieldsString.length; i++) {
			if ((fieldsString.length == 1) && (fieldsString[0].equals("*"))) {
				aggregateFunction = null;
				break;
			} else if (fieldsString[i].contains("("))
				counter++;

		}
		if (counter != 0) {
			aggregateFunction = new String[counter];
			for (int i = (fieldsString.length - 1), j = (counter - 1); i >= 0 && j >= 0; i--, j--) {
				if (fieldsString[i].contains("("))
					aggregateFunction[j] = fieldsString[i];

			}
		}

		return aggregateFunction;
	}

}