package com.code.generator.app.dto;

public class ColumnDto {
	private String columnName;
	private String columnType;
	
	
	
	public ColumnDto(String columnName, String columnType) {
		super();
		this.columnName = columnName;
		this.columnType = columnType;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	
	@Override
	public String toString() {
		return "ColumnDto [columnName=" + columnName + ", columnType=" + columnType + "]";
	}
	
	
}
