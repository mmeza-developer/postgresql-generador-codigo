package com.code.generator.app.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.code.generator.app.dto.ColumnDto;
import com.code.generator.app.dto.TableDto;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class DbObjectDao  {
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	 public List<TableDto> getAllTables(String schema){
		 List<TableDto> tables=null;
		 tables=jdbcTemplate.query("select table_name from information_schema.tables where table_schema=?",new Object[] {schema},
				 (rs, rowNum) -> new TableDto( rs.getString("table_name")));
		 return tables;
	 }
	 
	 public List<ColumnDto> getAllColumnFromTable(String tableName,String schema){
		 List<ColumnDto> columns=null;
		 columns=jdbcTemplate.query("SELECT column_name,data_type " + 
		 		"FROM information_schema.columns " + 
		 		"WHERE table_schema = ? " + 
		 		"  AND table_name   = ? ",new Object[] {schema,tableName},
				 (rs, rowNum) -> new ColumnDto( rs.getString("column_name"),rs.getString("data_type")));
		 return columns;
	 }
}
