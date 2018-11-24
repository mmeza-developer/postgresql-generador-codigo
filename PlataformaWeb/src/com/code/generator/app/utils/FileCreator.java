package com.code.generator.app.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ainslec.picocog.PicoWriter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.code.generator.app.dto.ColumnDto;





public class FileCreator {
	
	public static List<String> importLibraries(List<ColumnDto> columnsList) {
		List<String> libraryImport=new ArrayList<String>();
		HashMap<String,Boolean> hashmap=new HashMap<String,Boolean>();
		
		for(ColumnDto column : columnsList) {
			if(column.getColumnType()!=null && !column.getColumnType().equals("")) {
				if(column.getColumnType().equalsIgnoreCase("date") && !hashmap.containsKey("date") ) {
					libraryImport.add("import java.sql.Date;");
					hashmap.put("date", true);
				}
				if(column.getColumnType().equalsIgnoreCase("array") && !hashmap.containsKey("array") ) {
					libraryImport.add("import java.util.List;");
					hashmap.put("array", true);
				}
			}
		}
		
		return libraryImport;
	}
	public static void createDtoClassJavaFile(String className,List<ColumnDto> columnsList,String packageName,boolean createDto) {
		PicoWriter topWriter = new PicoWriter();
		
		String classFileName=TextFormat.underLineNameToCamelCase(className)+"Dto";
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("");
		
		List<String> libraryImport=importLibraries(columnsList);
		
		for(String library : libraryImport) {
			topWriter.writeln (library);
		}
		topWriter.writeln ("");
		topWriter.writeln_r ("public class "+classFileName+" {");
		
		
		
		topWriter.writeln("");
		
		for(ColumnDto column : columnsList) {
			
			PicoWriter memvarWriter = topWriter.createDeferredWriter();
			
			String typeString = DBJavaConverter.JavaTypeConverter(column.getColumnType());
			String nameStringFirstLowCase = TextFormat.columnNameToCamelCaseFirstLowCase(column.getColumnName());
			String nameStringFirstUpperCase = TextFormat.columnNameToCamelCase(column.getColumnName());
			memvarWriter.writeln("private "+typeString + " " + nameStringFirstLowCase + ";");
			
		}
		
		
		for(ColumnDto column : columnsList) {
			
			PicoWriter methodSection = topWriter.createDeferredWriter();
			PicoWriter mainMethodGet = methodSection.createDeferredWriter();
			PicoWriter mainMethodSet = methodSection.createDeferredWriter();
			
			String typeString = DBJavaConverter.JavaTypeConverter(column.getColumnType());
			String nameStringFirstLowCase = TextFormat.columnNameToCamelCaseFirstLowCase(column.getColumnName());
			String nameStringFirstUpperCase = TextFormat.columnNameToCamelCase(column.getColumnName());
			
			mainMethodGet.writeln(""); 
			mainMethodGet.writeln_r("public " + typeString + " get" + nameStringFirstUpperCase + "() {");
			mainMethodGet.writeln("return " + nameStringFirstLowCase + ";");
			mainMethodGet.writeln_l("}");
			
			mainMethodSet.writeln(""); 
			mainMethodSet.writeln_r(
					"public void  set" + nameStringFirstUpperCase + "(" + typeString + " data) {");
			System.out.println("typeString: "+typeString+" nameStringFirstLowCase: "+nameStringFirstLowCase);
			if(!typeString.equals("float") && !typeString.equals("int") && !typeString.equals("double") 
					&& !typeString.equals("char") && !typeString.equals("long") && !typeString.equals("short")
					&& !typeString.equals("byte")) {
			mainMethodSet.writeln_r("if (data!= null) {");
			mainMethodSet.writeln(nameStringFirstLowCase + "=data;");
			mainMethodSet.writeln_l("}");
			}else {
				mainMethodSet.writeln(nameStringFirstLowCase + "=data;");
			}
			
			mainMethodSet.writeln_l("}");
		}
		
		topWriter.writeln_l("}");
		// To extract the source code, call .toString()
		//System.out.println(topWriter.toString());
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createDto) {
			createFile(classFileName,path,topWriter.toString());
		}else{
			createFileOrReplace(classFileName,path,topWriter.toString());
		}
		printStatus(classFileName,true);
	}
	
	
	public static void createDaoInterfaceJavaFile(String className,String packageName,boolean createDao) {
		PicoWriter topWriter = new PicoWriter();
		
		String classFileName=TextFormat.underLineNameToCamelCase(className)+"Dao";
		String classFileNameDto=TextFormat.underLineNameToCamelCase(className)+"Dto";
		String packageImportDto=PackageCreator.findDtoPackage(packageName);
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import " +packageImportDto+"."+ classFileNameDto + ";");
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("");
		topWriter.writeln_r ("public interface "+classFileName+" {");

		PicoWriter methodSection = topWriter.createDeferredWriter();
		PicoWriter mainMethodFind = methodSection.createDeferredWriter();
		PicoWriter mainMethodFindAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsert = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsertAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodUpdate = methodSection.createDeferredWriter();
		PicoWriter mainMethodDelete = methodSection.createDeferredWriter();


		mainMethodFind.writeln("");
		mainMethodFind.writeln_r("public " + classFileNameDto + " findById("+classFileNameDto+" data);");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln_r("public List<" + classFileNameDto + "> findAll("+classFileNameDto+" data);");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln_r("public "+classFileNameDto+" insert("+classFileNameDto+" data);");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln_r("public List<"+classFileNameDto+"> insertAll(List<"+classFileNameDto+"> dataList);");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln_r("public "+classFileNameDto+" update("+classFileNameDto+" data);");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln_r("public "+classFileNameDto+" delete("+classFileNameDto+" data);");
		

		
		topWriter.writeln_l("}");
		// To extract the source code, call .toString()
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createDao) {
			createFile(classFileName,path,topWriter.toString());
		}else{
			createFileOrReplace(classFileName,path,topWriter.toString());
		}
		//System.out.println(topWriter.toString());
		printStatus(classFileName,true);
		
	}
	
	
	public static void createDaoClassJavaFile(String className,String packageName,List<ColumnDto> columnsList,boolean createDao) {
		PicoWriter topWriter = new PicoWriter();
		
		String classFileName=TextFormat.underLineNameToCamelCase(className)+"DaoImpl";
		String classFileNameDto=TextFormat.underLineNameToCamelCase(className)+"Dto";
		String classFileNameDao=TextFormat.underLineNameToCamelCase(className)+"Dao";
		String packageImportDto=PackageCreator.findDtoPackage(packageName);
		String packageImportDao=PackageCreator.findDaoPackage(packageName);
		String primaryKeyRaw=findPrimaryKeyRaw(columnsList,className);
		String primaryKey=findPrimaryKey(columnsList,className);
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import " +packageImportDto+"."+ classFileNameDto + ";");
		topWriter.writeln ("import " +packageImportDao+"."+ classFileNameDao + ";");
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import org.springframework.beans.factory.annotation.Autowired;");
		topWriter.writeln ("import org.springframework.stereotype.Repository;");
		topWriter.writeln ("import org.springframework.jdbc.core.JdbcTemplate;");
		topWriter.writeln ("import org.springframework.jdbc.core.BeanPropertyRowMapper;");
		topWriter.writeln ("import org.springframework.jdbc.support.GeneratedKeyHolder;");
		topWriter.writeln ("import org.springframework.jdbc.support.KeyHolder;");
		topWriter.writeln ("import java.sql.Connection;");
		topWriter.writeln ("import java.sql.PreparedStatement;");
		topWriter.writeln ("import org.springframework.jdbc.core.PreparedStatementCreator;");
		topWriter.writeln ("import java.sql.SQLException;");
		topWriter.writeln ("");
		
		topWriter.writeln ("@Repository");
		topWriter.writeln_r ("public class "+classFileName+" implements "
		+TextFormat.underLineNameToCamelCase(className)+"Dao {");
		
		PicoWriter varSection = topWriter.createDeferredWriter();
		PicoWriter methodSection = topWriter.createDeferredWriter();
		PicoWriter mainMethodFind = methodSection.createDeferredWriter();
		PicoWriter mainMethodFindAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsert = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsertAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodUpdate = methodSection.createDeferredWriter();
		PicoWriter mainMethodDelete = methodSection.createDeferredWriter();
		
		
		varSection.writeln("@Autowired");
		varSection.writeln("JdbcTemplate jdbcTemplate;");

		mainMethodFind.writeln("");
		mainMethodFind.writeln("@Override");
		mainMethodFind.writeln_r("public " + classFileNameDto + " findById("+classFileNameDto+" data){");
		
	
		mainMethodFind.writeln(classFileNameDto+" objtReturn=null;");
		mainMethodFind.writeln("try{");
		mainMethodFind.writeln("objtReturn=("+classFileNameDto+")jdbcTemplate.query(\"SELECT \"+");
		mainMethodFind.writeln(getListColumnsForSqlQuery(columnsList));
		mainMethodFind.writeln("\" FROM \\\""+className+"\\\" WHERE \\\""+primaryKeyRaw+"\\\"=?\"");
		mainMethodFind.writeln(",new Object[]{data.get"+primaryKey+"()}");
		mainMethodFind.writeln(",new BeanPropertyRowMapper("+classFileNameDto+".class)).get(0);");
		mainMethodFind.writeln("}catch(Exception e){");
		mainMethodFind.writeln("e.printStackTrace();");
		mainMethodFind.writeln("}");
		mainMethodFind.writeln("return objtReturn;");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@Override");
		mainMethodFindAll.writeln_r("public List<" + classFileNameDto + "> findAll("+classFileNameDto+" data){");
		mainMethodFindAll.writeln("List<"+classFileNameDto+"> columns=null;");
		mainMethodFindAll.writeln("columns=jdbcTemplate.query(\"SELECT \"+");
		mainMethodFindAll.writeln(getListColumnsForSqlQuery(columnsList));
		mainMethodFindAll.writeln("\" FROM \\\""+className+"\\\"\"");
		mainMethodFindAll.writeln(",new Object[]{}");
		mainMethodFindAll.writeln(",new BeanPropertyRowMapper("+classFileNameDto+".class));");
		mainMethodFindAll.writeln("return columns;");
		mainMethodFindAll.writeln_l("}");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln("@Override");
		mainMethodInsert.writeln_r("public "+classFileNameDto+" insert("+classFileNameDto+" data){");
		mainMethodInsert.writeln("KeyHolder holder = new GeneratedKeyHolder();");
		mainMethodInsert.writeln("String sql=\"INSERT INTO public.\\\""+className+"\\\"( \"+\n");
		mainMethodInsert.writeln(getListColumnsForSqlInsertQuery(columnsList,className));
		mainMethodInsert.writeln(" \" )VALUES \" +\n");
		mainMethodInsert.writeln(getListSignForSqlQuery(columnsList,className));
		
		mainMethodInsert.writeln("jdbcTemplate.update(new PreparedStatementCreator() { ");
		
		
		mainMethodInsert.writeln_r(" @Override" ); 
				mainMethodInsert.writeln("          public PreparedStatement createPreparedStatement(Connection connection)" ); 
				mainMethodInsert.writeln_r("                  throws SQLException {" );
				mainMethodInsert.writeln("              PreparedStatement ps = connection.prepareStatement(sql.toString()," );
				mainMethodInsert.writeln("            		  new String[] { \""+primaryKeyRaw+"\" }); ");
		mainMethodInsert.writeln(getListParamsToInsertGetMethosOfDto(columnsList,className));
		mainMethodInsert.writeln_l("},holder);");
		
		/*
		mainMethodInsert.writeln(" jdbcTemplate.update(sql, new Object[]{");
		mainMethodInsert.writeln(getListGetMethosOfDto(columnsList,className));
		mainMethodInsert.writeln(",new BeanPropertyRowMapper("+TextFormat.underLineNameToCamelCase(className)+"Dto.class),holder);");
		String primaryKey=findPrimaryKey(columnsList,className);
		*/
		if(primaryKey!=null && !primaryKey.equals("")) {
			mainMethodInsert.writeln(" data.set"+primaryKey+"(holder.getKey().longValue());");
		}
		mainMethodInsert.writeln("return data;");
		mainMethodInsert.writeln_l("}");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln("@Override");
		mainMethodInsertAll.writeln_r("public List<"+classFileNameDto+"> insertAll(List<"+classFileNameDto+"> dataList){");
		mainMethodInsertAll.writeln("return null;");
		mainMethodInsertAll.writeln_l("}");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln("@Override");
		mainMethodUpdate.writeln_r("public "+classFileNameDto+" update("+classFileNameDto+" data){");
		mainMethodUpdate.writeln("String sql=\"UPDATE TABLE  public.'"+className+"'\"+\n");
		mainMethodUpdate.writeln(getListColumnsForSqlUpdateQuery(columnsList,className));
		mainMethodUpdate.writeln(" jdbcTemplate.update(sql, new Object[]{");
		mainMethodUpdate.writeln(getListGetMethosOfDto(columnsList,className));
		mainMethodUpdate.writeln(");");
		mainMethodUpdate.writeln("return data;");
		mainMethodUpdate.writeln_l("}");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln("@Override");
		mainMethodDelete.writeln_r("public "+classFileNameDto+" delete("+classFileNameDto+" data){");
		mainMethodDelete.writeln("String sql=\"DELETE public.'"+className+"' WHERE \";");
		mainMethodDelete.writeln(" jdbcTemplate.update(sql, new Object[]{");
		mainMethodDelete.writeln(getListGetMethosOfDto(columnsList,className));
		mainMethodDelete.writeln(");");
		mainMethodDelete.writeln("return data;");
		mainMethodDelete.writeln_l("}");
		

		
		topWriter.writeln_l("}");
		// To extract the source code, call .toString()
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(createDao) {
			createFile(classFileName,path,topWriter.toString());
		}else{
			createFileOrReplace(classFileName,path,topWriter.toString());
		}
		//System.out.println(topWriter.toString());
		printStatus(classFileName,true);
	}
	
	
	public static void createDelegateInterfaceJavaFile(String className,String packageName,boolean createDelegate) {
		PicoWriter topWriter = new PicoWriter();
		
		String classFileName=TextFormat.underLineNameToCamelCase(className)+"Delegate";
		String classFileNameDto=TextFormat.underLineNameToCamelCase(className)+"Dto";
		String packageImportDto=PackageCreator.findDtoPackage(packageName);
		String packageImportDao=PackageCreator.findDaoPackage(packageName);
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import " +packageImportDto+"."+ classFileNameDto + ";");
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("");
		topWriter.writeln_r ("public interface "+classFileName+" {");

		PicoWriter methodSection = topWriter.createDeferredWriter();
		PicoWriter mainMethodFind = methodSection.createDeferredWriter();
		PicoWriter mainMethodFindAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsert = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsertAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodUpdate = methodSection.createDeferredWriter();
		PicoWriter mainMethodDelete = methodSection.createDeferredWriter();


		mainMethodFind.writeln("");
		mainMethodFind.writeln_r("public " +classFileNameDto+" findById("+classFileNameDto+" data);");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln_r("public List<" + classFileNameDto+"> findAll("+classFileNameDto+" data);");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln_r("public "+classFileNameDto+" insert("+classFileNameDto+" data);");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln_r("public List<"+classFileNameDto+"> insertAll(List<"+classFileNameDto+"> dataList);");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln_r("public "+classFileNameDto+" update("+classFileNameDto+" data);");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln_r("public "+classFileNameDto+" delete("+classFileNameDto+" data);");
		

		
		topWriter.writeln_l("}");
		// To extract the source code, call .toString()
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(createDelegate) {
			createFile(classFileName,path,topWriter.toString());
		}else{
			createFileOrReplace(classFileName,path,topWriter.toString());
		}
		//System.out.println(topWriter.toString());
		printStatus(classFileName,true);
	}
	
	public static void createResponseObject(String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
	
		
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("");
		
		topWriter.writeln_r ("public class ResponseObject {");
				

		 

		PicoWriter varSection = topWriter.createDeferredWriter();
		PicoWriter methodSection = topWriter.createDeferredWriter();
		PicoWriter Constructor = methodSection.createDeferredWriter();
		PicoWriter Constructor1 = methodSection.createDeferredWriter();
		PicoWriter Constructor2 = methodSection.createDeferredWriter();
		PicoWriter Constructor3 = methodSection.createDeferredWriter();
		PicoWriter mainMethodSetMessage = methodSection.createDeferredWriter();
		PicoWriter mainMethodGetMessage = methodSection.createDeferredWriter();
		PicoWriter mainMethodSetData = methodSection.createDeferredWriter();
		PicoWriter mainMethodGetData = methodSection.createDeferredWriter();
		PicoWriter mainMethodSetStatus = methodSection.createDeferredWriter();
		PicoWriter mainMethodGetStatus = methodSection.createDeferredWriter();
		PicoWriter mainMethodSetCode = methodSection.createDeferredWriter();
		PicoWriter mainMethodGetCode = methodSection.createDeferredWriter();
		
		
		varSection.writeln("private String message;");
		varSection.writeln("private Object data;");
		varSection.writeln("private ResponseStatus status;");
		varSection.writeln("private int code;");
		varSection.writeln("");
		
		Constructor.writeln_r("public ResponseObject(ResponseStatus status,Object data,String message,int code){");
		Constructor.writeln("this.status=status;");
		Constructor.writeln("this.data=data;");
		Constructor.writeln("this.message=message;");
		Constructor.writeln("this.code=code;");
		Constructor.writeln_l("}");
		Constructor.writeln_l("");
		
		Constructor1.writeln_r("public ResponseObject(ResponseStatus status,Object data,String message){");
		Constructor1.writeln("this.status=status;");
		Constructor1.writeln("this.data=data;");
		Constructor1.writeln("this.message=message;");
		Constructor1.writeln_l("}");
		Constructor1.writeln_l("");
		
		Constructor2.writeln_r("public ResponseObject(ResponseStatus status,Object data){");
		Constructor2.writeln("this.status=status;");
		Constructor2.writeln("this.data=data;");
		Constructor2.writeln_l("}");
		
		Constructor3.writeln_r("public ResponseObject(){");
		Constructor3.writeln_l("}");
		
		
		mainMethodSetMessage.writeln("");
		mainMethodSetMessage.writeln_r("public void setMessage(String data){");
		mainMethodSetMessage.writeln("this.message=data;");
		mainMethodSetMessage.writeln_l("}");
		
		mainMethodGetMessage.writeln("");
		mainMethodGetMessage.writeln_r("public String getMessage(){");
		mainMethodGetMessage.writeln("return message;");
		mainMethodGetMessage.writeln_l("}");
		
		mainMethodSetData.writeln("");
		mainMethodSetData.writeln_r("public void getData(Object data){");
		mainMethodSetData.writeln("this.data=data;");
		mainMethodSetData.writeln_l("}");
		
		mainMethodGetData.writeln("");
		mainMethodGetData.writeln_r("public Object getData(){");
		mainMethodGetData.writeln("return data;");
		mainMethodGetData.writeln_l("}");
		
		mainMethodSetCode.writeln("");
		mainMethodSetCode.writeln_r("public void setCode(int data){");
		mainMethodSetCode.writeln("this.code=data;");
		mainMethodSetCode.writeln_l("}");
		
		mainMethodGetCode.writeln("");
		mainMethodGetData.writeln_r("public int getCode(){");
		mainMethodGetData.writeln("return this.code;");
		mainMethodGetData.writeln_l("}");
		
		mainMethodSetStatus.writeln("");
		mainMethodSetStatus.writeln_r("public void setStatus(ResponseStatus data){");
		mainMethodSetStatus.writeln("this.status=data;");
		mainMethodSetStatus.writeln_l("}");
		
		mainMethodGetStatus.writeln("");
		mainMethodGetStatus.writeln_r("public ResponseStatus getStatus(){");
		mainMethodGetStatus.writeln("return this.status;");
		mainMethodGetStatus.writeln_l("}");
		
		
		
		topWriter.writeln_l("}");
		// To extract the source code, call .toString()
		//System.out.println(topWriter.toString());
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(createService) {
			createFile("ResponseObject",path,topWriter.toString());
		}else{
			createFileOrReplace("ResponseObject",path,topWriter.toString());
		}
		printStatus("ResponseObject",true);
	}
	
	public static void createDelegateClassJavaFile(String className,String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		String classFileName=TextFormat.underLineNameToCamelCase(className)+"DelegateImpl";
		String classFileNameDto=TextFormat.underLineNameToCamelCase(className)+"Dto";
		String classFileNameDao=TextFormat.underLineNameToCamelCase(className)+"Dao";
		String classVarFileNameDao=TextFormat.underLineNameToCamelCaseNonFirstLetter(className)+"Dao";
		String classFileNameDelegate=TextFormat.underLineNameToCamelCase(className)+"Delegate";
		String packageImportDto=PackageCreator.findDtoPackage(packageName);
		String packageImportDelegate=PackageCreator.findDelegatePackage(packageName);
		String packageImportDao=PackageCreator.findDaoPackage(packageName);
		
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import " +packageImportDto+"."+ classFileNameDto + ";");
		topWriter.writeln ("import " +packageImportDelegate+"."+ classFileNameDelegate + ";");
		topWriter.writeln ("import " +packageImportDao+"."+ classFileNameDao + ";");
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import org.springframework.beans.factory.annotation.Autowired;");
		topWriter.writeln ("import org.springframework.stereotype.Service;");
		topWriter.writeln ("");
		
		topWriter.writeln ("@Service");
		topWriter.writeln_r ("public class "+classFileName+" implements "
		+TextFormat.underLineNameToCamelCase(className)+"Delegate {");

		PicoWriter varSection = topWriter.createDeferredWriter();
		PicoWriter methodSection = topWriter.createDeferredWriter();
		PicoWriter mainMethodFind = methodSection.createDeferredWriter();
		PicoWriter mainMethodFindAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsert = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsertAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodUpdate = methodSection.createDeferredWriter();
		PicoWriter mainMethodDelete = methodSection.createDeferredWriter();
		
		
		varSection.writeln("@Autowired");
		varSection.writeln("private "+classFileNameDao+" "+classVarFileNameDao+";");


		mainMethodFind.writeln("");
		mainMethodFind.writeln("@Override");
		mainMethodFind.writeln_r("public " + classFileNameDto +" findById("+classFileNameDto+" data){");
		mainMethodFind.writeln("return "+classVarFileNameDao+".findById(data);");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@Override");
		mainMethodFindAll.writeln_r("public List<" +classFileNameDto+"> findAll("+classFileNameDto+" data){");
		mainMethodFindAll.writeln("return "+classVarFileNameDao+".findAll(data);");
		mainMethodFindAll.writeln_l("}");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln("@Override");
		mainMethodInsert.writeln_r("public "+classFileNameDto+" insert("+classFileNameDto+" data){");
		mainMethodInsert.writeln_r("return "+classVarFileNameDao+".insert(data);");
		mainMethodInsert.writeln_l("}");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln("@Override");
		mainMethodInsertAll.writeln_r("public List<"+classFileNameDto+"> insertAll(List<"+classFileNameDto+"> dataList){");
		mainMethodInsertAll.writeln_r("return "+classVarFileNameDao+".insertAll(dataList);");
		mainMethodInsertAll.writeln_l("}");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln("@Override");
		mainMethodUpdate.writeln_r("public "+classFileNameDto+" update("+classFileNameDto+" data){");
		mainMethodUpdate.writeln_r("return "+classVarFileNameDao+".update(data);");
		mainMethodUpdate.writeln_l("}");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln("@Override");
		mainMethodDelete.writeln_r("public "+classFileNameDto+" delete("+classFileNameDto+" data){");
		mainMethodDelete.writeln_r("return "+classVarFileNameDao+".delete(data);");
		mainMethodDelete.writeln_l("}");
		

		
		topWriter.writeln_l("}");
		// To extract the source code, call .toString()
		//System.out.println(topWriter.toString());
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(createService) {
			createFile(classFileName,path,topWriter.toString());
		}else{
			createFileOrReplace(classFileName,path,topWriter.toString());
		}
		printStatus(classFileName,true);
	}
	
	
	public static void createDelegateClassForWsJavaFile(String className,String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		String classNameCamelCase=TextFormat.underLineNameToCamelCase(className);
		String classFileName=TextFormat.underLineNameToCamelCase(className)+"DelegateImpl";
		String classFileNameDto=TextFormat.underLineNameToCamelCase(className)+"Dto";
		String classFileNameDao=TextFormat.underLineNameToCamelCase(className)+"Dao";
		String classVarFileNameDao=TextFormat.underLineNameToCamelCaseNonFirstLetter(className)+"Dao";
		String classFileNameDelegate=TextFormat.underLineNameToCamelCase(className)+"Delegate";
		String packageImportDto=PackageCreator.findDtoPackage(packageName);
		String packageImportDelegate=PackageCreator.findDelegatePackage(packageName);
		String packageImportUtils=PackageCreator.findUtilsPackage(packageName);
		String packageImportDao=PackageCreator.findDaoPackage(packageName);
		String lowClassName=classNameCamelCase.toLowerCase();
		
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import " +packageImportDto+"."+ classFileNameDto + ";");
		topWriter.writeln ("import " +packageImportDelegate+"."+ classFileNameDelegate + ";");
		topWriter.writeln ("import " +packageImportUtils+".BaseUrl;");
		topWriter.writeln ("import " +packageImportUtils+".ResponseObject;");
		topWriter.writeln ("import " +packageImportUtils+".JsonConverter;");
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import org.springframework.beans.factory.annotation.Autowired;");
		topWriter.writeln ("import org.springframework.stereotype.Service;");
		topWriter.writeln ("import org.springframework.http.HttpEntity;");
		topWriter.writeln ("import org.springframework.http.HttpHeaders;");
		topWriter.writeln ("import org.springframework.http.HttpMethod;");
		topWriter.writeln ("import org.springframework.http.MediaType;");
		topWriter.writeln ("import org.springframework.http.ResponseEntity;");
		topWriter.writeln ("import org.springframework.stereotype.Service;");
		topWriter.writeln ("import org.springframework.web.client.RestTemplate;");
		topWriter.writeln ("import org.springframework.web.util.UriComponentsBuilder;");
		topWriter.writeln ("");
		
		topWriter.writeln ("@Service");
		topWriter.writeln_r ("public class "+classFileName+" implements "
		+TextFormat.underLineNameToCamelCase(className)+"Delegate {");
		
		topWriter.writeln_r ("@Autowired\n RestTemplate restTemplate;");
		topWriter.writeln_r ("@Autowired\n JsonConverter jsonConverter;");

		PicoWriter varSection = topWriter.createDeferredWriter();
		PicoWriter methodSection = topWriter.createDeferredWriter();
		PicoWriter mainMethodFind = methodSection.createDeferredWriter();
		PicoWriter mainMethodFindAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsert = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsertAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodUpdate = methodSection.createDeferredWriter();
		PicoWriter mainMethodDelete = methodSection.createDeferredWriter();
		
		
		
		
		mainMethodFind.writeln("");
		mainMethodFind.writeln("@Override");
		mainMethodFind.writeln_r("public " + classFileNameDto +" findById("+classFileNameDto+" data){");
		mainMethodFind.writeln("HttpHeaders headers = new HttpHeaders();");
		mainMethodFind.writeln("headers.setContentType(MediaType.APPLICATION_JSON);");
		mainMethodFind.writeln("String url=BaseUrl.URL+\""+lowClassName+"/find"+classNameCamelCase+"ById\";");
		mainMethodFind.writeln("UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)\r\n" + 
				"		        .queryParam(\"id\", data.getCod"+classNameCamelCase+"());");
		mainMethodFind.writeln(" HttpEntity<String> entity = new HttpEntity<String>( headers);");
		mainMethodFind.writeln("  ResponseEntity<ResponseObject> response = \r\n" + 
				"			   restTemplate.exchange(builder.toUriString(),\r\n" + 
				"					  HttpMethod.GET\r\n" + 
				"					   , entity,ResponseObject.class);");
		mainMethodFind.writeln("return  ("+classFileNameDto+") jsonConverter.objectToObject(response.getBody().getData(),"+classFileNameDto+".class);");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@Override");
		mainMethodFindAll.writeln_r("public List<" +classFileNameDto+"> findAll("+classFileNameDto+" data){");
		mainMethodFindAll.writeln("HttpHeaders headers = new HttpHeaders();");
		mainMethodFindAll.writeln("headers.setContentType(MediaType.APPLICATION_JSON);");
		mainMethodFindAll.writeln("String url=BaseUrl.URL+\""+lowClassName+"/findAll"+classNameCamelCase+"\";");
		mainMethodFindAll.writeln("UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)\r\n" + 
				"		        .queryParam(\"id\", data.getCod"+classNameCamelCase+"());");
		mainMethodFindAll.writeln(" HttpEntity<String> entity = new HttpEntity<String>(jsonConverter.toJson(data), headers);");
		mainMethodFindAll.writeln("  ResponseEntity<ResponseObject> response = \r\n" + 
				"			   restTemplate.exchange(builder.toUriString(),\r\n" + 
				"					  HttpMethod.GET\r\n" + 
				"					   , entity,ResponseObject.class);");
		mainMethodFindAll.writeln(" return (List<"+classFileNameDto+">) response.getBody().getData();");
		mainMethodFindAll.writeln_l("}");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln("@Override");
		mainMethodInsert.writeln_r("public "+classFileNameDto+" insert("+classFileNameDto+" data){");
		mainMethodInsert.writeln("HttpHeaders headers = new HttpHeaders();");
		mainMethodInsert.writeln("headers.setContentType(MediaType.APPLICATION_JSON);");
		mainMethodInsert.writeln("String url=BaseUrl.URL+\""+lowClassName+"/add"+classNameCamelCase+"\";");
		mainMethodInsert.writeln("UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)\r\n" + 
				"		        .queryParam(\"id\", data.getCod"+classNameCamelCase+"());");
		mainMethodInsert.writeln(" HttpEntity<String> entity = new HttpEntity<String>(jsonConverter.toJson(data), headers);");
		mainMethodInsert.writeln("  ResponseEntity<ResponseObject> response = \r\n" + 
				"			   restTemplate.exchange(builder.toUriString(),\r\n" + 
				"					  HttpMethod.POST\r\n" + 
				"					   , entity,ResponseObject.class);");
		mainMethodInsert.writeln(" return ("+classFileNameDto+") jsonConverter.objectToObject(response.getBody().getData(),"+classFileNameDto+".class);");

		mainMethodInsert.writeln_l("}");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln("@Override");
		mainMethodInsertAll.writeln_r("public List<"+classFileNameDto+"> insertAll(List<"+classFileNameDto+"> dataList){");
		mainMethodInsertAll.writeln("return null;");
		mainMethodInsertAll.writeln_l("}");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln("@Override");
		mainMethodUpdate.writeln_r("public "+classFileNameDto+" update("+classFileNameDto+" data){");
		mainMethodUpdate.writeln("return null;");
		mainMethodUpdate.writeln_l("}");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln("@Override");
		mainMethodDelete.writeln_r("public "+classFileNameDto+" delete("+classFileNameDto+" data){");
		mainMethodDelete.writeln("return null;");
		mainMethodDelete.writeln_l("}");
		

		
		topWriter.writeln_l("}");
		// To extract the source code, call .toString()
		//System.out.println(topWriter.toString());
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(createService) {
			createFile(classFileName,path,topWriter.toString());
		}else{
			createFileOrReplace(classFileName,path,topWriter.toString());
		}
		printStatus(classFileName,true);
	}
	
	public static void createEnumEstatusWsResponse(String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		topWriter.writeln ("package " + packageName + ";");
		
		topWriter.writeln ("public enum ResponseStatus {"+
				"ERROR,"+
				"SUCCESSFUL,"+
				"WARNING;"+
				"}");
		
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createService) {
			createFile("ResponseStatus",path,topWriter.toString());
		}else{
			createFileOrReplace("ResponseStatus",path,topWriter.toString());
		}
		printStatus("ResponseStatus",true);
	}
	
	
	public static void createFileJsonConverter(String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		topWriter.writeln ("package " + packageName + ";");
		
		topWriter.writeln (
				"\r\n" + 
				"import java.io.IOException;\r\n" + 
				"import java.util.List;\r\n" + 
				"\r\n" + 
				"import org.springframework.stereotype.Component;\r\n" + 
				"\r\n" + 
				"import com.fasterxml.jackson.core.JsonParseException;\r\n" + 
				"import com.fasterxml.jackson.core.JsonProcessingException;\r\n" + 
				"import com.fasterxml.jackson.core.type.TypeReference;\r\n" + 
				"import com.fasterxml.jackson.databind.JsonMappingException;\r\n" + 
				"import com.fasterxml.jackson.databind.ObjectMapper;\r\n" + 
				"@Component\r\n" + 
				"public class JsonConverter<T> {\r\n" + 
				"	public String toJson(T objt) {\r\n" + 
				"		ObjectMapper mapper = new ObjectMapper();\r\n" + 
				"		\r\n" + 
				"		String jsonInString=\"\";\r\n" + 
				"\r\n" + 
				"		//Object to JSON in String\r\n" + 
				"		try {\r\n" + 
				"			 jsonInString = mapper.writeValueAsString(objt);\r\n" + 
				"		} catch (JsonProcessingException e) {\r\n" + 
				"			e.printStackTrace();\r\n" + 
				"		}\r\n" + 
				"		return jsonInString;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public T fromJson(String json, Class<T> classType) {\r\n" + 
				"		ObjectMapper mapper = new ObjectMapper();\r\n" + 
				"		//JSON from String to Object\r\n" + 
				"		T obj=null;\r\n" + 
				"		try {\r\n" + 
				"			obj = mapper.readValue(json, classType);\r\n" + 
				"		} catch (JsonParseException e) {\r\n" + 
				"			e.printStackTrace();\r\n" + 
				"		} catch (JsonMappingException e) {\r\n" + 
				"			e.printStackTrace();\r\n" + 
				"		} catch (IOException e) {\r\n" + 
				"			e.printStackTrace();\r\n" + 
				"		}\r\n" + 
				"		return obj;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public T objectToObject(Object object, Class<T> classType) {\r\n" + 
				"		ObjectMapper mapper = new ObjectMapper();\r\n" + 
				"		//JSON from String to Object\r\n" + 
				"		T myObjects=null;\r\n" + 
				"		myObjects = mapper.convertValue(object, classType);\r\n" + 
				"		return myObjects;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public List<T> fromJsonList(String json, Class<T> classType) {\r\n" + 
				"		ObjectMapper mapper = new ObjectMapper();\r\n" + 
				"		//JSON from String to Object\r\n" + 
				"		List<T> myObjects=null;\r\n" + 
				"		try {\r\n" + 
				"			 myObjects = mapper.readValue(json, new TypeReference<List<T>>(){});\r\n" + 
				"		} catch (JsonParseException e) {\r\n" + 
				"			e.printStackTrace();\r\n" + 
				"		} catch (JsonMappingException e) {\r\n" + 
				"			e.printStackTrace();\r\n" + 
				"		} catch (IOException e) {\r\n" + 
				"			e.printStackTrace();\r\n" + 
				"		}\r\n" + 
				"		return myObjects;\r\n" + 
				"	}\r\n" + 
				"}\r\n" + 
				"");
		
		
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createService) {
			createFile("JsonConverter",path,topWriter.toString());
		}else{
			createFileOrReplace("JsonConverter",path,topWriter.toString());
		}
		printStatus("JsonConverter",true);
	}
	public static void createFileToArrayCreator(String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		topWriter.writeln ("package " + packageName + ";");
		
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import java.sql.SQLException;");
		topWriter.writeln ("import org.springframework.jdbc.core.JdbcTemplate;");
		
		topWriter.writeln ("public class ObjectToArray {\r\n" + 
				"	\r\n" + 
				"	public static java.sql.Array createLongSqlArray(List<Long> list, JdbcTemplate jdbcTemplate){\r\n" + 
				"		java.sql.Array intArray = null;\r\n" + 
				"		try {\r\n" + 
				"			intArray = jdbcTemplate.getDataSource().getConnection().createArrayOf(\"bigint\", list.toArray());\r\n" + 
				"		} catch (SQLException ignore) {\r\n" + 
				"		}\r\n" + 
				"		return intArray;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public static java.sql.Array createStringSqlArray(List<String> list, JdbcTemplate jdbcTemplate){\r\n" + 
				"	    java.sql.Array intArray = null;\r\n" + 
				"	    try {\r\n" + 
				"	        intArray = jdbcTemplate.getDataSource().getConnection().createArrayOf(\"string\", list.toArray());\r\n" + 
				"	    } catch (SQLException ignore) {\r\n" + 
				"	    }\r\n" + 
				"	    return intArray;\r\n" + 
				"	}\r\n" + 
				"}");
		
		
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createService) {
			createFile("ObjectToArray",path,topWriter.toString());
		}else{
			createFileOrReplace("ObjectToArray",path,topWriter.toString());
		}
		printStatus("ObjectToArray",true);
	}
	
	public static void createFileBaseUrl(String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		topWriter.writeln ("package " + packageName + ";");
		
		topWriter.writeln ("public class BaseUrl {"+
				"public static final String URL=\"localhost:8080/AdminWsRestful/\";"+
				"}");
		
		
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createService) {
			createFile("BaseUrl",path,topWriter.toString());
		}else{
			createFileOrReplace("BaseUrl",path,topWriter.toString());
		}
		printStatus("BaseUrl",true);
	}
	public static void createErrorMessage(String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		topWriter.writeln ("package " + packageName + ";");
		
		topWriter.writeln ("public class ErrorMessage {"+
			//Successful
			"public static final String OPERACION_SUCCESSFUL=\"Operaci�n terminada con �xito\";"+
			//Errores
			"public static final String OPERACION_ERROR=\"Hubo un error en la operaci�n\";"+
			"public static final String REQUEST_ERROR=\"Datos del request incorrectos\";"+
		"}");
		
		
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createService) {
			createFile("ErrorMessage",path,topWriter.toString());
		}else{
			createFileOrReplace("ErrorMessage",path,topWriter.toString());
		}
		printStatus("ErrorMessage",true);
	}
	
	
	public static void createWsOperationValidation(String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import java.util.List;");

		topWriter.writeln ("");
		topWriter.writeln ("public class WsOperationValidation {");
		topWriter.writeln_r ("public static ResponseObject validateObject(Object objt ) {");
		topWriter.writeln ("ResponseObject response=null;");
		topWriter.writeln_r ("  if(objt!=null) {");
		topWriter.writeln (" response=new ResponseObject(ResponseStatus.SUCCESSFUL,objt,ErrorMessage.OPERACION_SUCCESSFUL);");
		topWriter.writeln (" }else {");
		topWriter.writeln (" response=new ResponseObject(ResponseStatus.ERROR,objt,ErrorMessage.OPERACION_ERROR);");
		topWriter.writeln_l (" }");
		topWriter.writeln ("return response;");
		topWriter.writeln_l ("}");
		
		topWriter.writeln ("");
		topWriter.writeln_r ("public static ResponseObject validateListObjects(List<?> listObject) {");
		topWriter.writeln_r (" ResponseObject response=null;");
		topWriter.writeln_r ("  if(listObject!=null && listObject.size()>0) {");
		topWriter.writeln (" response=new ResponseObject(ResponseStatus.SUCCESSFUL,listObject,ErrorMessage.OPERACION_SUCCESSFUL);");
		topWriter.writeln_r (" }else {");
		topWriter.writeln (" response=new ResponseObject(ResponseStatus.ERROR,listObject,ErrorMessage.OPERACION_ERROR);");
		topWriter.writeln_l (" }");
		topWriter.writeln ("return response;");
		topWriter.writeln_l ("}");
		
		topWriter.writeln ("");
		topWriter.writeln_r ("public static boolean inputObjectValidation(Object objt ) {");
		topWriter.writeln (" boolean value=false;");
		topWriter.writeln_r ("  if(objt!=null) {");
		topWriter.writeln (" value=true;");
		topWriter.writeln_l (" }");
		topWriter.writeln (" return value;");
		topWriter.writeln_l ("}");
		
		topWriter.writeln ("");
		topWriter.writeln_r ("public static boolean inputObjectValidation(long data ) {");
		topWriter.writeln (" boolean value=false;");
		topWriter.writeln_r ("  if(data>0) {");
		topWriter.writeln (" value=true;");
		topWriter.writeln_l(" }");
		topWriter.writeln (" return value;");
		topWriter.writeln_l ("}");
		topWriter.writeln ("}");
		
		    
		
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createService) {
			createFile("WsOperationValidation",path,topWriter.toString());
		}else{
			createFileOrReplace("WsOperationValidation",path,topWriter.toString());
		}
		printStatus("WsOperationValidation",true);
	}
	public static void createServiceClassJavaFile(String className,String packageName,List<ColumnDto> columns,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		String classNameCamelCase=TextFormat.underLineNameToCamelCase(className);
		String classFileName=TextFormat.underLineNameToCamelCase(className)+"Service";
		String classFileNameDto=TextFormat.underLineNameToCamelCase(className)+"Dto";
		String classFileNameDelegate=TextFormat.underLineNameToCamelCase(className)+"Delegate";
		String varNameDelegate=TextFormat.underLineNameToCamelCaseNonFirstLetter(className)+"Delegate";
		String varNameDto=TextFormat.underLineNameToCamelCaseNonFirstLetter(className).toLowerCase()+"Dto";
		String packageImportDto=PackageCreator.findDtoPackage(packageName);
		String packageImportDelegate=PackageCreator.findDelegatePackage(packageName);
		String packageImportUtils=PackageCreator.findServicePackage(packageName);
		String classNameLowCase=classNameCamelCase.toLowerCase();
		
		String keyVarName=findPrimaryKey(columns, className);
		
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import " +packageImportDto+"."+ classFileNameDto + ";");
		topWriter.writeln ("import " +packageImportDelegate+"."+ classFileNameDelegate + ";");
		topWriter.writeln ("import " +packageImportUtils+".ResponseStatus;");
		topWriter.writeln ("import " +packageImportUtils+".ResponseObject;");
		topWriter.writeln ("import " +packageImportUtils+".WsOperationValidation;");
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import org.springframework.beans.factory.annotation.Autowired;");
		
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestBody;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestMapping;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestMethod;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestParam;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RestController;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestBody;");
		topWriter.writeln ("");
		
		topWriter.writeln ("@RestController");
		topWriter.writeln ("@RequestMapping(\"/"+classNameLowCase+"\")");
		topWriter.writeln_r ("public class "+classFileName+"{");
		PicoWriter varSection = topWriter.createDeferredWriter();
		PicoWriter methodSection = topWriter.createDeferredWriter();
		PicoWriter mainMethodFind = methodSection.createDeferredWriter();
		PicoWriter mainMethodFindAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsert = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsertAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodUpdate = methodSection.createDeferredWriter();
		PicoWriter mainMethodDelete = methodSection.createDeferredWriter();
	
		
		varSection.writeln("@Autowired");
		varSection.writeln("private "+classFileNameDelegate+" "+varNameDelegate+";");


		mainMethodFind.writeln("");
		mainMethodFind.writeln("@RequestMapping(value = \"/find"+classNameCamelCase+"ById\", method = RequestMethod.GET)");
		mainMethodFind.writeln_r("public ResponseObject findById(@RequestParam(\"id\") long id){");
		mainMethodFind.writeln("ResponseObject response=null;");
		mainMethodFind.writeln(classFileNameDto+" "+varNameDto+"=new "+classFileNameDto+"();");
		mainMethodFind.writeln(varNameDto+".set"+keyVarName+"(id);");
		mainMethodFind.writeln(classFileNameDto+" objt="+varNameDelegate+".findById("+varNameDto+");");
		mainMethodFind.writeln(" response=WsOperationValidation.validateObject(objt); ");
		mainMethodFind.writeln("return response;");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@RequestMapping(value = \"/findAll"+classNameCamelCase+"\", method = RequestMethod.GET)");
		mainMethodFindAll.writeln_r("public ResponseObject findAll(){");
		mainMethodFindAll.writeln(" ResponseObject response=null;");
		mainMethodFindAll.writeln(classFileNameDto+" "+varNameDto+"=new "+classFileNameDto+"();");
		mainMethodFindAll.writeln("List<"+classFileNameDto+"> "+varNameDto+"List="+varNameDelegate+".findAll("+varNameDto+");");
		mainMethodFindAll.writeln("response=WsOperationValidation.validateListObjects("+varNameDto+"List);");
		mainMethodFindAll.writeln("return response;");
		mainMethodFindAll.writeln_l("}");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln("@RequestMapping(value = \"/add"+classNameCamelCase+"\", method = RequestMethod.POST)");
		mainMethodInsert.writeln_r("public ResponseObject insert(@RequestBody "+classFileNameDto+" data){");
		mainMethodInsert.writeln("ResponseObject response=null;");
		mainMethodInsert.writeln(classFileNameDto+" "+varNameDto+"="+varNameDelegate+".insert(data);");
		mainMethodInsert.writeln("response=WsOperationValidation.validateObject("+varNameDto+");;");
		mainMethodInsert.writeln("return response;");
		mainMethodInsert.writeln_l("}");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln("@RequestMapping(value = \"/addAll"+classNameCamelCase+"\", method = RequestMethod.POST)");
		mainMethodInsertAll.writeln_r("public ResponseObject insertAll(@RequestBody List<"+classFileNameDto+"> dataList){");
		mainMethodInsertAll.writeln("return null;");
		mainMethodInsertAll.writeln_l("}");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln("@RequestMapping(value = \"/update"+classNameCamelCase+"\", method = RequestMethod.POST)");
		mainMethodUpdate.writeln_r("public ResponseObject update(@RequestBody "+classFileNameDto+" data){");
		mainMethodUpdate.writeln("return null;");
		mainMethodUpdate.writeln_l("}");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln("@RequestMapping(value = \"/delete"+classNameCamelCase+"\", method = RequestMethod.POST)");
		mainMethodDelete.writeln_r("public  ResponseObject delete(@RequestBody "+classFileNameDto+" data){");
		mainMethodDelete.writeln("return null;");
		mainMethodDelete.writeln_l("}");
		

		
		topWriter.writeln_l("}");
		// To extract the source code, call .toString()
		//System.out.println(topWriter.toString());
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createService) {
			createFile(classFileName,path,topWriter.toString());
		}else{
			createFileOrReplace(classFileName,path,topWriter.toString());
		}
		printStatus(classFileName,true);
	}
	
	
	
	public static void printStatus(String className,boolean status) {
		System.out.println(className+" was create "+(status?"Successfull":"Fail")+"\n");
	}
	

	
	
	public static void createFileOrReplace(String className,String path,String fileText) {
		
		System.out.println("Creando archivo: "+path+"\\"+className+".java");
		File file = new File(path+"\\"+className+".java");
		try {
			if(file.createNewFile()){
				System.out.println("Archivo "+className+".java fue creado en "+path);
			}else{
				System.out.println("El archivo "+className+" ya existe en el directorio, sera reemplazado");
				file.delete();
				System.out.println("El archivo "+className+" fue elimnado para ser reemplazado");
				
			}
			FileWriter fw = new FileWriter(path+"\\"+className+".java", false);
			fw.write(fileText);	
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void createFile(String className,String path,String fileText) {
		
		System.out.println("Creando archivo: "+path+"\\"+className+".java");
		File file = new File(path+"\\"+className+".java");
		try {
			if(file.createNewFile()){
			    System.out.println("Archivo "+className+".java fue creado en "+path);
			
			FileWriter fw = new FileWriter(path+"\\"+className+".java", false);
			fw.write(fileText);	
			fw.flush();
			fw.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static String getListColumnsForSqlQuery(List<ColumnDto> columnsList) {
		String  returnString="";
		int lastElement=columnsList.size();
		int count=0;
		for(ColumnDto column: columnsList) {
			if(count<lastElement-1) {
				returnString+="		\"\\\""+column.getColumnName()+"\\\",\"+\n";
			}else {
				returnString+="		\"\\\""+column.getColumnName()+"\\\"\"+\n";
			}
			count++;
		}
		
		return returnString;
	}
	
	public static String getListColumnsForSqlInsertQuery(List<ColumnDto> columnsList,String tableName) {
		String  returnString="";
		int lastElement=columnsList.size();
		int count=0;
		for(ColumnDto column: columnsList) {
			if(!column.getColumnName().toLowerCase().contains("cod_"+tableName.toLowerCase())) {
				if(count==0) {
					returnString+="		\"\\\""+column.getColumnName()+"\\\"\"+\n";
				}else {
					returnString+="		\",\\\""+column.getColumnName()+"\\\"\"+\n";
				}
				count++;
			}
		}
		
		return returnString;
	}
	public static String getListColumnsForSqlUpdateQuery(List<ColumnDto> columnsList,String tableName) {
		String  returnString="";
		int lastElement=columnsList.size();
		int count=0;
		for(ColumnDto column: columnsList) {
			if(!column.getColumnName().toLowerCase().contains("cod_"+tableName.toLowerCase())) {
				if(count==0) {
					returnString+="	\""+column.getColumnName()+"=?\"+\n";
				}else {
					returnString+=" \","+column.getColumnName()+"=?\"+\n";
				}
				count++;
			}
		}
		returnString+="\" WHERE\";";
		return returnString;
	}
	
	public static String getListSignForSqlQuery(List<ColumnDto> columnsList,String tableName) {
		String  returnString=" \"(";
		int lastElement=columnsList.size();
		int count=0;
		for(ColumnDto column: columnsList) {
			if(!column.getColumnName().toLowerCase().contains("cod_"+tableName.toLowerCase())) {
				if(count==0) {
					returnString+="?";
				}else {
					returnString+=",?";
				}
				count++;
			}
		}
		returnString+=")\";";
		
		return returnString;
	}
	
	public static String getListGetMethosOfDto(List<ColumnDto> columnsList,String tableName) {
		String  returnString=" ";
		int lastElement=columnsList.size();
		int count=0;
		for(ColumnDto column: columnsList) {
			if(!column.getColumnName().toLowerCase().contains("cod_"+tableName.toLowerCase())) {
				if(count==0) {
					returnString+="data.get"+TextFormat.columnNameToCamelCase(column.getColumnName())+"()";
				}else {
					returnString+=",data.get"+TextFormat.columnNameToCamelCase(column.getColumnName())+"()";
				}
				count++;
			}
		}
		returnString+="}";
		
		return returnString;
	}
	
	public static String getListParamsToInsertGetMethosOfDto(List<ColumnDto> columnsList,String tableName) {
		String  returnString=" ";
		int lastElement=columnsList.size();
		int count=1;
		for(ColumnDto column: columnsList) {
			if(!column.getColumnName().toLowerCase().contains("cod_"+tableName.toLowerCase())) {
				returnString+= "				ps.set"+DBJavaConverter.JavaTypeConverterForSqlOperations(column.getColumnType())+"("+count+",data.get"+TextFormat.columnNameToCamelCase(column.getColumnName())+"());\n";
				count++;
			}
		}
		returnString+="  				return ps;";
		 returnString+=" 		}";
		
		return returnString;
	}
	public static String findPrimaryKey(List<ColumnDto> columnsList,String tableName) {
		String  returnString=" ";
		int lastElement=columnsList.size();
		for(ColumnDto column: columnsList) {
			if(column.getColumnName().toLowerCase().contains("cod_"+tableName.toLowerCase())) {
				returnString=TextFormat.columnNameToCamelCase(column.getColumnName());
			}
		}
		
		return returnString;
	}
	
	public static String findPrimaryKeyRaw(List<ColumnDto> columnsList,String tableName) {
		String  returnString=" ";
		int lastElement=columnsList.size();
		for(ColumnDto column: columnsList) {
			if(column.getColumnName().toLowerCase().contains("cod_"+tableName.toLowerCase())) {
				returnString=column.getColumnName();
			}
		}
		
		return returnString;
	}
	
	
	
	
	
}
