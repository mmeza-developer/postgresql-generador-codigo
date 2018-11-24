package com.code.generator.app.utils;

import java.io.File;
import java.util.List;

import org.ainslec.picocog.PicoWriter;

import com.code.generator.app.dto.ColumnDto;

public class DBJavaConverter {
	
	public static String JavaTypeConverter(String type) {
		String javaType=null;
		if(type!=null && !type.equals("")) {
			if(type.equals("date")) {
				javaType="Date";
			}else if(type!=null && type.equals("character varying")) {
				javaType="String";
			}else if(type!=null && type.equals("bigint")) {
				javaType="Long";
			}else if(type!=null && type.equals("real")) {
				javaType="Float";
			}else if(type!=null && type.equals("ARRAY")) {
				javaType="Object[]";
			}else if(type!=null && type.equals("boolean")) {
				javaType="Boolean";
			}else if(type!=null && type.equals("integer")) {
				javaType="Integer";
			}else if(type!=null && type.equals("text")) {
				javaType="String";
			}
		}
		
		return javaType;
	}
	
	public static String JavaTypeConverterForSqlOperations(String type) {
		String javaType=null;
		if(type!=null && !type.equals("")) {
			if(type.equals("date")) {
				javaType="Date";
			}else if(type!=null && type.equals("character varying")) {
				javaType="String";
			}else if(type!=null && type.equals("bigint")) {
				javaType="Long";
			}else if(type!=null && type.equals("real")) {
				javaType="Float";
			}else if(type!=null && type.equals("ARRAY")) {
				javaType="Array";
			}else if(type!=null && type.equals("boolean")) {
				javaType="Boolean";
			}else if(type!=null && type.equals("integer")) {
				javaType="Int";
			}else if(type!=null && type.equals("text")) {
				javaType="String";
			}
		}
		
		return javaType;
	}
	
}
