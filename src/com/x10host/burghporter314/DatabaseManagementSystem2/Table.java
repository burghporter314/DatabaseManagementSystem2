package com.x10host.burghporter314.DatabaseManagementSystem2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Table {

	private String name;
	private Connection connection;
	private TableField[] fields;
	private Statement statement;
	
	/**
	 * Assumes this Table Already Exists
	 * @param name
	 * @param connection is our connection to the Access DB
	 * @throws SQLException 
	 */
	
	public Table(String name, Connection connection) throws SQLException {
		
		this.name = name;
		this.connection = connection;
		this.statement = this.connection.createStatement();
		
	}
	
	/**
	 * Assumes this Table does not Already Exist
	 * @param name Specifies name of the database to be created
	 * @param fields is the associated metadata
	 * @param connection is our connection to the Access DB
	 * @throws SQLException 
	 */
	
	public Table(String name, TableField[] fields, Connection connection) throws SQLException {
		this(name, connection);
		this.fields = fields;
	}
	
	/**
	 * Creates the Table with the data given through constructor
	 * @throws SQLException 
	 */
	
	public void createTable() throws SQLException {
		
		String sql = "CREATE TABLE " + this.name + " (";
		for(int i = 0; i < this.fields.length; i++) {
			
			sql += fields[i].getName()+" VARCHAR("+fields[i].getSize()+")";
			
			/*Check to see if we are at end of statement -- if so, add a ')'... if not, add a ','*/
			if(i == this.fields.length - 1) { sql += ")"; } 
			else { sql += ", "; }
		}
		
		this.statement.executeUpdate(sql);

	}
	
	/**
	 * Iterates through the provided fields and inserts into the table
	 * @param map maps a field to a value
	 * @throws SQLException 
	 */
	
	public void includeEntry(TableFieldValueMap valueMap) throws SQLException {
		
		String sql = "INSERT INTO " + this.name + " (";
		
		HashMap<TableField, String> map = valueMap.getEntries();
		TableField[] headerFields = valueMap.getFields();
		
		int size = headerFields.length;
		
		for(int i = 0; i < size; i++) {
			
			sql += headerFields[i].getName();
			if(i != size - 1) { sql += ","; }
			
		}
		
		sql += ") VALUES (";
		for(int i = 0; i < size; i++) {
			
			String value = map.get(headerFields[i]);
			
			if(value.length() > headerFields[i].getSize()) {
				value = value.substring(0, headerFields[i].getSize());
			}
			
			sql += "'" + value + "'";
			if(i != size - 1) { sql += ","; }
			
		}
		
		sql += ")";
		this.statement.executeUpdate(sql);
		
	}
	
	/**
	 * Deletes all records that match all fields in TableFieldValueMap
	 * @param map maps a field to a value
	 * @throws SQLException 
	 */
	
	public void deleteEntries(TableFieldValueMap valueMap) throws SQLException {
		
		String sql = "DELETE FROM " + this.name + " WHERE ";
		
		HashMap<TableField, String> map = valueMap.getEntries();
		TableField[] headerFields = valueMap.getFields();
		
		int size = headerFields.length;
		boolean initialField = true;
		
		for(int i = 0; i < size; i++) {
			String value = map.get(headerFields[i]);
			if(!value.isEmpty()) {
				if(!initialField) { sql += " AND "; }
				sql += headerFields[i].getName() + "=" + "'" + value + "'";
				initialField = false;
			}
		}
		this.statement.executeUpdate(sql);
	}
	
	/**
	 * Lists all entries in the table
	 * @throws SQLException 
	 */
	public void listTable() throws SQLException {
		
		ResultSet data = this.statement
									.executeQuery("SELECT * FROM " + this.name);
		
		TableField[] headers = getHeaders(data);
		ArrayList<String[]> rows = getRows(data);
		
		printTableResult(headers, rows);
	}
	
	/**
	 * Returns the headers associated with this file
	 * @return headers
	 * @throws SQLException
	 */
	
	public TableField[] getHeaders() throws SQLException {
		ResultSet data = this.statement
				.executeQuery("SELECT * FROM " + this.name);
		
		return getHeaders(data);
	}
	
	
	/**
	 * Returns a result set from the sql statement
	 * @param sql
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet getResults(String sql, Connection connection) throws SQLException {
		
		Statement statement = connection.createStatement();
		ResultSet data = statement.executeQuery(sql);
		
		return data;
	}
	
	public static void printTableResult(TableField[] headers, ArrayList<String[]> rows) {
		
		for(int i = 0; i < headers.length; i++) {
			System.out.print(headers[i].getName() + "\t\t");
		}
		
		System.out.println("");
		for(int i = 0; i < rows.size(); i++) {
			for(String s : rows.get(i)) {
				System.out.print(s + "\t\t");
			}
			System.out.println("");
		}
		System.out.println();
		
	}
	
	/**
	 * Lists metadata of the result set.
	 * @param results created from a query
	 * @return String[] arr represents all headers in table
	 * @throws SQLException
	 */
	
	public static TableField[] getHeaders(ResultSet results) throws SQLException {
		
		ResultSetMetaData metadata = results.getMetaData();
		int columnCount = metadata.getColumnCount();
		
		TableField[] arr = new TableField[columnCount];
		
		for(int i = 1; i <= columnCount; i++) {
			arr[i-1] = new TableField(metadata.getColumnName(i), metadata.getColumnDisplaySize(i));
		}
		
		return arr;
		
	}
	
	
	/**
	 * Returns the results of a query minus the metadata.
	 * @param results
	 * @return the results from the query
	 * @throws SQLException
	 */
	public static ArrayList<String[]> getRows(ResultSet results) throws SQLException {
		
		ArrayList<String[]> arr = new ArrayList<String[]>();
		int columnLength = results.getMetaData().getColumnCount();
		
		while(results.next()) {
			
			String[] data = new String[columnLength];
			for(int i = 1; i <= columnLength; i++) {
				data[i-1] = results.getString(i);
			}
			
			arr.add(data);
		}
		
		return arr;
	}
	
	/**
	 * Returns if there are results returned on a query
	 * @param query sql statement
	 * @param connection representing our jdbc connection
	 * @return true if results are returned from the query
	 * @throws SQLException
	 */
	public static boolean checkIfExists(String query, Connection connection) throws SQLException {
		
		Statement statement = connection.createStatement();
		return statement.executeQuery(query).next();
		
	}
	
	/**
	 * Does an inner join on two tables
	 * @param table1 is the table being joined on
	 * @param table2 is the table that we are joining to the first table
	 * @param onCondition is the condition for the join
	 * @param projectParams represents the fields that we want
	 * @param connection to jdbc
	 * @return ResultSet
	 * @throws SQLException 
	 */
	public static ResultSet innerJoin(String table1, String table2, String table3, String onCondition, String onCondition2, String projectParams, Connection connection) throws SQLException {
		
		ResultSet resultSet;
		String query = "SELECT " + projectParams + " FROM " + table1 
				+ " INNER JOIN " + table2 + " ON " + onCondition
				+ " INNER JOIN " + table3 + " ON " + onCondition2;
		
		Statement statement = connection.createStatement();
		resultSet = statement.executeQuery(query);
				
		return resultSet;
		
	}
	
}
