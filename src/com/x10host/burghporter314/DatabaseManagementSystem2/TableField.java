package com.x10host.burghporter314.DatabaseManagementSystem2;

public class TableField {

	private String name;
	private int size;
	
	public TableField(String name, int size) {
		this.name = name;
		this.size = size;
	}
	
	public TableField(String data) {
		String[] arr = data.split(" ");
		this.name = arr[0];
		this.size = Integer.parseInt(arr[1]);
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public boolean equals(Object o) {
		
		if(this == o) {
			return true;
		}
		
		if(o == null) {
			return false;
		}
		
		TableField field = (TableField) o;
		return (this.name.equals(field.getName()) && this.size == field.getSize());
		
	}
	
}
