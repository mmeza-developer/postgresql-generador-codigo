package com.code.generator.app.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ainslec.picocog.PicoWriter;

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
		mainMethodFind.writeln_r("public " + classFileNameDto + " find("+classFileNameDto+" data);");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln_r("public List<" + classFileNameDto + "> findAll();");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln_r("public void insert("+classFileNameDto+" data);");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln_r("public void insertAll(List<"+classFileNameDto+"> dataList);");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln_r("public void update("+classFileNameDto+" data);");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln_r("public void delete("+classFileNameDto+" data);");
		

		
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
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import " +packageImportDto+"."+ classFileNameDto + ";");
		topWriter.writeln ("import " +packageImportDao+"."+ classFileNameDao + ";");
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import org.springframework.beans.factory.annotation.Autowired;");
		topWriter.writeln ("import org.springframework.stereotype.Repository;");
		topWriter.writeln ("import org.springframework.jdbc.core.JdbcTemplate;");
		topWriter.writeln ("import org.springframework.jdbc.core.BeanPropertyRowMapper;");
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
		mainMethodFind.writeln_r("public " + classFileNameDto + " find("+classFileNameDto+" data){");
		
		List<ColumnDto> columns=null;
	
		mainMethodFind.writeln(classFileNameDto+" objtReturn=null;");
		mainMethodFind.writeln("objtReturn=("+classFileNameDto+")jdbcTemplate.query(\"SELECT \"+");
		mainMethodFind.writeln(getListColumnsForSqlQuery(columnsList));
		mainMethodFind.writeln("\"FROM "+className+" \"");
		mainMethodFind.writeln(",new Object[]{}");
		mainMethodFind.writeln(",new BeanPropertyRowMapper("+classFileNameDto+".class));");
		mainMethodFind.writeln("return objtReturn;");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@Override");
		mainMethodFindAll.writeln_r("public List<" + classFileNameDto + "> findAll(){");
		mainMethodFindAll.writeln("List<"+classFileNameDto+"> columns=null;");
		mainMethodFindAll.writeln("columns=jdbcTemplate.query(\"SELECT \"+");
		mainMethodFindAll.writeln(getListColumnsForSqlQuery(columnsList));
		mainMethodFindAll.writeln("\" FROM "+className+"\"");
		mainMethodFindAll.writeln(",new Object[]{}");
		mainMethodFindAll.writeln(",new BeanPropertyRowMapper("+classFileNameDto+".class));");
		mainMethodFindAll.writeln("return columns;");
		mainMethodFindAll.writeln_l("}");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln("@Override");
		mainMethodInsert.writeln_r("public void insert("+classFileNameDto+" data){");
		mainMethodInsert.writeln_l("}");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln("@Override");
		mainMethodInsertAll.writeln_r("public void insertAll(List<"+classFileNameDto+"> dataList){");
		mainMethodInsertAll.writeln_l("}");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln("@Override");
		mainMethodUpdate.writeln_r("public void update("+classFileNameDto+" data){");
		mainMethodUpdate.writeln_l("}");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln("@Override");
		mainMethodDelete.writeln_r("public void delete("+classFileNameDto+" data){");
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
		mainMethodFind.writeln_r("public " +classFileNameDto+" find("+classFileNameDto+" data);");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln_r("public List<" + classFileNameDto+"> findAll();");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln_r("public void insert("+classFileNameDto+" data);");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln_r("public void insertAll(List<"+classFileNameDto+"> dataList);");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln_r("public void update("+classFileNameDto+" data);");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln_r("public void delete("+classFileNameDto+" data);");
		

		
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
		varSection.writeln("private ResposeStatus status;");
		varSection.writeln("private int code;");
		varSection.writeln("");
		
		Constructor.writeln_r("ResponseObject(ResposeStatus status,Object data,String message,int code){");
		Constructor.writeln("this.status=status;");
		Constructor.writeln("this.data=data;");
		Constructor.writeln("this.message=message;");
		Constructor.writeln("this.code=code;");
		Constructor.writeln_l("}");
		Constructor.writeln_l("");
		
		Constructor1.writeln_r("ResponseObject(ResposeStatus status,Object data,String message){");
		Constructor1.writeln("this.status=status;");
		Constructor1.writeln("this.data=data;");
		Constructor1.writeln("this.message=message;");
		Constructor1.writeln_l("}");
		Constructor1.writeln_l("");
		
		Constructor2.writeln_r("ResponseObject(ResposeStatus status,Object data){");
		Constructor2.writeln("this.status=status;");
		Constructor2.writeln("this.data=data;");
		Constructor2.writeln_l("}");
		
		
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
		mainMethodSetStatus.writeln_r("public void setStatus(ResposeStatus data){");
		mainMethodSetStatus.writeln("this.status=data;");
		mainMethodSetStatus.writeln_l("}");
		
		mainMethodGetStatus.writeln("");
		mainMethodGetStatus.writeln_r("public ResposeStatus getStatus(){");
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
		mainMethodFind.writeln_r("public " + classFileNameDto +" find("+classFileNameDto+" data){");
		mainMethodFind.writeln("return "+classVarFileNameDao+".find(data);");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@Override");
		mainMethodFindAll.writeln_r("public List<" +classFileNameDto+"> findAll(){");
		mainMethodFindAll.writeln("return "+classVarFileNameDao+".findAll();");
		mainMethodFindAll.writeln_l("}");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln("@Override");
		mainMethodInsert.writeln_r("public void insert("+classFileNameDto+" data){");
		mainMethodInsert.writeln_r(classVarFileNameDao+".insert(data);");
		mainMethodInsert.writeln_l("}");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln("@Override");
		mainMethodInsertAll.writeln_r("public void insertAll(List<"+classFileNameDto+"> dataList){");
		mainMethodInsertAll.writeln_r(classVarFileNameDao+".insertAll(dataList);");
		mainMethodInsertAll.writeln_l("}");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln("@Override");
		mainMethodUpdate.writeln_r("public void update("+classFileNameDto+" data){");
		mainMethodUpdate.writeln_r(classVarFileNameDao+".update(data);");
		mainMethodUpdate.writeln_l("}");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln("@Override");
		mainMethodDelete.writeln_r("public void delete("+classFileNameDto+" data){");
		mainMethodDelete.writeln_r(classVarFileNameDao+".delete(data);");
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

		topWriter.writeln ("public enum ResposeStatus {"+
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
			createFile("ResposeStatus",path,topWriter.toString());
		}else{
			createFileOrReplace("ResposeStatus",path,topWriter.toString());
		}
		printStatus("ResposeStatus",true);
	}
	public static void createServiceClassJavaFile(String className,String packageName,boolean createService) {
		PicoWriter topWriter = new PicoWriter();
		
		String classFileName=TextFormat.underLineNameToCamelCase(className)+"Service";
		String classFileNameDto=TextFormat.underLineNameToCamelCase(className)+"Dto";
		String classFileNameDelegate=TextFormat.underLineNameToCamelCase(className)+"Delegate";
		String varNameDelegate=TextFormat.underLineNameToCamelCaseNonFirstLetter(className)+"Delegate";
		String packageImportDto=PackageCreator.findDtoPackage(packageName);
		String packageImportDelegate=PackageCreator.findDelegatePackage(packageName);
		String packageImportUtils=PackageCreator.findServicePackage(packageName);
		
		
		topWriter.writeln ("package " + packageName + ";");
		topWriter.writeln ("import " +packageImportDto+"."+ classFileNameDto + ";");
		topWriter.writeln ("import " +packageImportDelegate+"."+ classFileNameDelegate + ";");
		topWriter.writeln ("import " +packageImportUtils+".ResposeStatus;");
		topWriter.writeln ("import " +packageImportUtils+".ResponseObject;");
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import org.springframework.beans.factory.annotation.Autowired;");
		
		topWriter.writeln ("import java.util.List;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestBody;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestMapping;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestMethod;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RequestParam;");
		topWriter.writeln ("import org.springframework.web.bind.annotation.RestController;");
		topWriter.writeln ("");
		
		topWriter.writeln ("@RestController");
		topWriter.writeln ("@RequestMapping(\"/"+className.toLowerCase()+"\")");
		topWriter.writeln_r ("public class "+classFileName+"{"
		);
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
		mainMethodFind.writeln("@RequestMapping(value = \"/find"+className+"ById\", method = RequestMethod.GET)");
		mainMethodFind.writeln_r("public ResponseObject findById(int id){");
		mainMethodFind.writeln("return null;");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@RequestMapping(value = \"/findAll"+className+"\", method = RequestMethod.GET)");
		mainMethodFindAll.writeln_r("public ResponseObject findAll(){");
		mainMethodFindAll.writeln("return null;");
		mainMethodFindAll.writeln_l("}");
		
		mainMethodInsert.writeln("");
		mainMethodInsert.writeln("@RequestMapping(value = \"/add"+className+"\", method = RequestMethod.POST)");
		mainMethodInsert.writeln_r("public ResponseObject insert("+classFileNameDto+" data){");
		mainMethodInsert.writeln_l("}");
		
		mainMethodInsertAll.writeln("");
		mainMethodInsertAll.writeln("@RequestMapping(value = \"/addAll"+className+"\", method = RequestMethod.POST)");
		mainMethodInsertAll.writeln_r("public ResponseObject insertAll(List<"+classFileNameDto+"> dataList){");
		mainMethodInsertAll.writeln_l("}");
		
		mainMethodUpdate.writeln("");
		mainMethodUpdate.writeln("@RequestMapping(value = \"/update"+className+"\", method = RequestMethod.POST)");
		mainMethodUpdate.writeln_r("public ResponseObject update("+classFileNameDto+" data){");
		mainMethodUpdate.writeln("return null;");
		mainMethodUpdate.writeln_l("}");
		
		mainMethodDelete.writeln("");
		mainMethodDelete.writeln("@RequestMapping(value = \"/delete"+className+"\", method = RequestMethod.POST)");
		mainMethodDelete.writeln_r("public  ResponseObject delete("+classFileNameDto+" data){");
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
				returnString+="\""+column.getColumnName()+",\"\n+";
			}else {
				returnString+=column.getColumnName()+"\"\n";
			}
		}
		
		return returnString;
	}
	
	
	
}
