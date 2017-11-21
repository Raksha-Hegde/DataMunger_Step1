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
	 * extract the name of the file from the query. File name can be found after a
	 * space after "from" clause. Note: ----- CSV file can contain a field that
	 * contains from as a part of the column name. For eg: from_date,from_hrs etc.
	 * 
	 * Please consider this while extracting the file name in this method.
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
	 * 
	 * Note: ------- 1. the query might not contain where clause but contain order
	 * by or group by clause 2. the query might not contain where, order by or group
	 * by clause 3. the query might not contain where, but can contain both group by
	 * and order by clause
	 */
	public String getBaseQuery(String queryString) {

		String[] temp = null;
		temp = queryString.split("where|order\\s+by|group\\s+by");
		String baseQueryString = temp[0];

		return baseQueryString;

	}

	/*
	 * This method is used to extract the conditions part from the query string. The
	 * conditions part contains starting from where keyword till the next keyword,
	 * which is either group by or order by clause. In case of absence of both group
	 * by and order by clause, it will contain till the end of the query string.
	 * Note: ----- 1. The field name or value in the condition can contain keywords
	 * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
	 * might not contain where clause at all.
	 */
	public String getConditionsPartQuery(String queryString) {

		String conditionPart = null;
		String[] temp = queryString.toLowerCase().split("where");

		conditionPart = temp[1];
		if (conditionPart.toLowerCase().contains("order by"))
			temp = conditionPart.split("order\\s+by");
		else if (conditionPart.toLowerCase().contains("group by"))
			temp = conditionPart.split("group\\s+by");

		conditionPart = temp[1];

		return conditionPart;

	}

	/*
	 * This method will extract condition(s) from the query string. The query can
	 * contain one or multiple conditions. In case of multiple conditions, the
	 * conditions will be separated by AND/OR keywords. for eg: Input: select
	 * city,winner,player_match from ipl.csv where season > 2014 and city
	 * ='Bangalore'
	 * 
	 * This method will return a string array ["season > 2014","city ='Bangalore'"]
	 * and print the array
	 * 
	 * Note: ----- 1. The field name or value in the condition can contain keywords
	 * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
	 * might not contain where clause at all.
	 */
	public String[] getConditions(String queryString) {

		String conditionPartQuery = getConditionsPartQuery(queryString);
		String[] conditions = null;

		if (conditionPartQuery.toLowerCase().contains(" and ") || conditionPartQuery.toLowerCase().contains(" or "))
			conditions = conditionPartQuery.trim().split("( and )|( or )");
		else
			conditions = conditionPartQuery.trim().split("");

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

		String[] splitCondition = getSplitStrings(getConditionsPartQuery(queryString));

		int operatorCounter = 0;

		int loopCounter = 0;
		while (loopCounter < splitCondition.length) {

			if (splitCondition[loopCounter].toLowerCase().equals("and")
					|| splitCondition[loopCounter].toLowerCase().equals("or")) {
				splitCondition = splitCondition[loopCounter].split("( and )|( or )");
				operatorCounter++;

			}
			loopCounter += 1;

		}
		String[] logicalOp = new String[operatorCounter];
		loopCounter = 0;
		int i = 0;
		while (loopCounter < splitCondition.length) {

			if (splitCondition[loopCounter].toLowerCase().equals("and")
					|| splitCondition[loopCounter].toLowerCase().equals("or")) {

				logicalOp[i++] = splitCondition[loopCounter].toLowerCase();

			}
			loopCounter += 1;

		}

		return logicalOp;

	}

	/*
	 * This method will extract the fields to be selected from the query string. The
	 * query string can have multiple fields separated by comma. The extracted
	 * fields will be stored in a String array which is to be printed in console as
	 * well as to be returned by the method
	 * 
	 * Note: ------ 1. The field name or value in the condition can contain keywords
	 * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The field
	 * name can contain '*'
	 * 
	 */
	public String[] getFields(String queryString) {

		String fields[] = null;
		String[] temp1 = queryString.trim().split("\\s+from");

		temp1 = temp1[0].trim().split("select\\s+");
		for (int i = 0; i < temp1.length; i++) {
			if (temp1[i].contains("*")) {
				fields = temp1[i].trim().split(",");
				break;
			} else

			if (temp1[i].contains(","))
				fields = temp1[i].trim().split(",");

		}

		return fields;

	}

	/*
	 * This method extracts the order by fields from the query string. Note: ------
	 * 1. The query string can contain more than one order by fields. 2. The query
	 * string might not contain order by clause at all. 3. The field names,condition
	 * values might contain "order" as a substring. For eg:order_number,job_order
	 * Consider this while extracting the order by fields
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
	 * This method extracts the group by fields from the query string. Note: ------
	 * 1. The query string can contain more than one group by fields. 2. The query
	 * string might not contain group by clause at all. 3. The field names,condition
	 * values might contain "group" as a substring. For eg: newsgroup_name
	 * 
	 * Consider this while extracting the group by fields
	 */
	public String[] getGroupByFields(String queryString) {

		String[] groupBy = null;

		groupBy = queryString.trim().split("\\s+group\\s+by\\s+");

		for (int i = 0; i < groupBy.length; i++) {
			if (groupBy[i].contains(","))
				groupBy = groupBy[i].trim().split(",");

		}

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
		String baseQueryString = getBaseQuery(queryString);
		String functions[] = { "sum", "min", "max", "count", "avg"};
		
		for(String fun : functions)
			aggregateFunction = baseQueryString.split(fun);
		

		return aggregateFunction;
	}

}