package com.x10host.burghporter314.DatabaseManagementSystem2;

import java.util.HashMap;
import java.util.Map.Entry;

public class TableFieldValueMap {
	
	private HashMap<TableField, String> map;
	private HashMap<String, String> map2;
	
	public TableFieldValueMap() {
		this.map = new HashMap<TableField, String>();
		this.map2 = new HashMap<String, String>();
	}
	
	/**
	 * Add an entry field to this object
	 * @param field object that is mapped to value
	 * @param value associated with field
	 */
	public void addEntry(TableField field, String value) {
		this.map.put(field, value);
		this.map2.put(field.getName(), value);
	}
	
	/**
	 * Add an entry field to this object
	 * @param fieldName name of the Field
	 * @param fieldSize size of the Field
	 * @param value associated with the field
	 */
	public void addEntry(String fieldName, int fieldSize, String value) {
		this.map.put(new TableField(fieldName, fieldSize), value);
		this.map2.put(fieldName, value);
	}
	
	/**
	 * Returns all the keys associated with this object
	 * @return an Array of TableField objects
	 */
	public TableField[] getFields() {
		
		TableField[] fields = new TableField[this.map.size()];
		
		int i = 0;
		for(Entry<TableField, String> entry : this.map.entrySet()) {
			fields[i++] = entry.getKey();
		}
		
		return fields;
		
	}
	
	/**
	 * Returns the associated map with this object
	 * @return a map that associates TableField with String
	 */
	public HashMap<TableField, String> getEntries() {
		return this.map;
	}
	
	/**
	 * Returns a column string matching to a value;
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> getKeyValuePair() {
		return this.map2;
	}
	
}
