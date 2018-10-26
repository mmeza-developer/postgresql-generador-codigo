package com.code.generator.app.dto;

public class TableDto {
	private String tableName;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "TableDto [tableName=" + tableName + "]";
	}

	public TableDto(String tableName) {
		super();
		this.tableName = tableName;
	}
	
	
	
}
