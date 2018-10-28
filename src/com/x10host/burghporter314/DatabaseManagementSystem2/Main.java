package com.x10host.burghporter314.DatabaseManagementSystem2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static final String PEOPLE_DATABASE = "people";
	public static final String BOOKS_DATABASE = "books";
	public static final String CHECKED_OUT_BOOKS_DATABASE="checkedOutBooks";
	
	public static void main(String[] args) throws SQLException {

		Connection connection = DriverManager.getConnection("jdbc:ucanaccess://c:/javafiles/stuff.accdb");
		Statement statement = connection.createStatement();
		
		/*Source Program*/
		Scanner input = new Scanner(System.in);
		String userInput;
		
		Outer:
		while(true) {
			
			System.out.println("A. Create a table \nB. Include an entry\nC. Delete an entry/entries\n"
					+ "D. List a table\n1. Include a new book in the collection\n2. Include a new user"
					+ "\n3. List Library users with books they checked out\n4. Check out a book\n"
					+ "5. Return a book\n6. Exit");
			
			userInput = input.nextLine().trim();
			Table table = null; TableFieldValueMap map;
			String name, columnData, proceed, ssn, query;
			
			switch(userInput) {
						
				case "A":
					
					System.out.print("Enter Table Name: ");
					name = input.nextLine();
					ArrayList<TableField> fields = new ArrayList<TableField>();
					
					while(true) {
						
						System.out.print("Enter Column Name and its Length: ");
						columnData = input.nextLine();
						
						if(columnData.isEmpty()) { break; }
						fields.add(new TableField(columnData));
						
					}
					
					table = new Table(name, fields.toArray(new TableField[fields.size()]), connection);
					table.createTable();
					
					System.out.println("Table Created\n");
					break;
					
				case "B":
					
					table = loadTable(input, connection);
					map = getMap(table, input, false, "");
					
					table.includeEntry(map);
					System.out.println("Entry Added\n");
					break;
					
				case "C":
					
					table = loadTable(input, connection);
					map = getMap(table, input, false, "");
					
					table.deleteEntries(map);
					System.out.println("Entries Deleted\n");
					
					break;
					
				case "D":
					
					table = loadTable(input, connection);
					table.listTable();

					break;
					
				case "1":
					
					table = new Table(BOOKS_DATABASE, connection);
					
					while(true) {
						
						map = getMap(table, input, false, "");
						table.includeEntry(map);
						
						System.out.print("Enter More Books (Y/n)? ");
						proceed = input.nextLine().trim().toLowerCase();
						
						if(!proceed.equals("y")) { break; }
						
					}
					
					break;
					
				case "2":
					
					table = new Table(PEOPLE_DATABASE, connection);
					
					while(true) {
						
						map = getMap(table, input, false, "");
						table.includeEntry(map);
						
						System.out.print("Enter More People (Y/n)? ");
						proceed = input.nextLine().trim().toLowerCase();
						
						if(!proceed.equals("y")) { break; }
						
					}
					
					break;
					
				case "3":
					
					ResultSet results = Table.innerJoin(PEOPLE_DATABASE, CHECKED_OUT_BOOKS_DATABASE, BOOKS_DATABASE,
											PEOPLE_DATABASE+".SSN="+CHECKED_OUT_BOOKS_DATABASE+".SSN",
											CHECKED_OUT_BOOKS_DATABASE+".callNo="+BOOKS_DATABASE+".callNo",
											"name AS Name, author AS Author, title AS Title", connection);
					
					System.out.println("People with checked-out books: ");
					Table.printTableResult(Table.getHeaders(results), Table.getRows(results));
					
					ResultSet resultSet = Table.getResults("SELECT name FROM " + PEOPLE_DATABASE
											+ " WHERE NOT EXISTS(SELECT * FROM " + CHECKED_OUT_BOOKS_DATABASE 
											+ " WHERE " + PEOPLE_DATABASE + ".SSN=" + CHECKED_OUT_BOOKS_DATABASE + ".SSN)", connection);
					
					System.out.println("People without checked-out books: ");
					Table.printTableResult(Table.getHeaders(resultSet), Table.getRows(resultSet));

					break;
					
				case "4":
					
					table = new Table(CHECKED_OUT_BOOKS_DATABASE, connection);
					
					System.out.print("Enter person's SSN: ");
					ssn = input.nextLine().trim();
					
					if(!ssnExists(ssn, PEOPLE_DATABASE, connection)) {
						System.out.println("Invalid SSN");
						continue;
					}
					
					while(true) {
						
						map = getMap(table, input, true, ssn);
						table.includeEntry(map);
						
						System.out.print("More books checked out (Y/n)? ");
						proceed = input.nextLine().trim().toLowerCase();
						
						if(!proceed.equals("y")) { break; }
						
					}
					
					break;
					
				case "5":
					
					table = new Table(CHECKED_OUT_BOOKS_DATABASE, connection);
					
					System.out.print("Enter person's SSN: ");
					ssn = input.nextLine();
					
					if(!ssnExists(ssn, PEOPLE_DATABASE, connection)) {
						System.out.println("Invalid SSN");
						continue;
					}
					
					while(true) {
						
						map = getMap(table, input, true, ssn);
						String callNo = map.getKeyValuePair().get("callNo");
						
						if(!bookExists(ssn, callNo, CHECKED_OUT_BOOKS_DATABASE, connection)) {
							System.out.println("Callno is not on list of user's checked out books.");
							continue;
						}
						
						table.deleteEntries(map);
						
						System.out.print("More books Returned (Y/n)? ");
						proceed = input.nextLine().trim().toLowerCase();
						
						if(!proceed.equals("y")) { break; }
						
					}
					
					break;
					
				case "6":
					connection.close();
					break Outer;
				
				default:
					System.out.println("Invalid Input!");
					break;
			}
			
		}
		
	}
	
	public static Table loadTable(Scanner input, Connection connection) throws SQLException {
		
		System.out.print("Enter Table Name: ");
		String name = input.nextLine();
		
		return new Table(name, connection);
			
	}
	
	public static TableFieldValueMap getMap(Table table, Scanner input, boolean replaceFirst, String value) throws SQLException {

		TableField[] headerFields = table.getHeaders();
		TableFieldValueMap map = new TableFieldValueMap();
		
		for(TableField header : headerFields) {
			if(replaceFirst) {
				replaceFirst = false; 
				map.addEntry(header, value);
				continue;
			}
			System.out.print(header.getName() + ": ");
			map.addEntry(header, input.nextLine().trim());
		}
		
		return map;
		
	}
	
	public static boolean ssnExists(String ssn, String database, Connection connection) throws SQLException {

		String query = "SELECT * FROM " + database + " WHERE SSN=" + "'" + ssn + "'";
		if(!Table.checkIfExists(query, connection)) {
			return false;
		}
		
		return true;
		
	}
	
	public static boolean bookExists(String ssn, String callNo, String database, Connection connection) throws SQLException {
		
		String query = "SELECT * FROM " + database + " WHERE SSN=" + "'" + ssn + "'" +
													" AND callNo=" + "'" + callNo + "'";
		
		if(!Table.checkIfExists(query, connection)) {
			return false;
		}
		
		return true;	
	}

}
