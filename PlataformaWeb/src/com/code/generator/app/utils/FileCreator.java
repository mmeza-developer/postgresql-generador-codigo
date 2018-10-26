package com.code.generator.app.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ainslec.picocog.PicoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
	public static void createDtoClassJavaFile(String className,List<ColumnDto> columnsList,String packageName) {
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
		System.out.println(topWriter.toString());
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createFile(classFileName,path,topWriter.toString());
		printStatus(classFileName,true);
	}
	
	
	public static void createDaoInterfaceJavaFile(String className,String packageName) {
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
		createFile(classFileName,path,topWriter.toString());
		System.out.println(topWriter.toString());
		printStatus(classFileName,true);
	}
	
	
	public static void createDaoClassJavaFile(String className,String packageName) {
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
		mainMethodFind.writeln("return null;");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@Override");
		mainMethodFindAll.writeln_r("public List<" + classFileNameDto + "> findAll(){");
		mainMethodFindAll.writeln("return null;");
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
		createFile(classFileName,path,topWriter.toString());
		System.out.println(topWriter.toString());
		printStatus(classFileName,true);
	}
	
	public static void createDelegateInterfaceJavaFile(String className,String packageName) {
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
		createFile(classFileName,path,topWriter.toString());
		System.out.println(topWriter.toString());
		printStatus(classFileName,true);
	}
	public static void createDelegateClassJavaFile(String className,String packageName) {
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
		topWriter.writeln ("import org.springframework.jdbc.core.JdbcTemplate;");
		topWriter.writeln ("");
		
		topWriter.writeln ("@Service");
		topWriter.writeln_r ("public class "+classFileName+" implements "
		+TextFormat.underLineNameToCamelCase(className)+"Delegate {");

		PicoWriter methodSection = topWriter.createDeferredWriter();
		PicoWriter mainMethodFind = methodSection.createDeferredWriter();
		PicoWriter mainMethodFindAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsert = methodSection.createDeferredWriter();
		PicoWriter mainMethodInsertAll = methodSection.createDeferredWriter();
		PicoWriter mainMethodUpdate = methodSection.createDeferredWriter();
		PicoWriter mainMethodDelete = methodSection.createDeferredWriter();
		PicoWriter varSection = topWriter.createDeferredWriter();
		
		varSection.writeln("@Autowired");
		varSection.writeln("private "+classFileNameDao+" "+classVarFileNameDao+";");


		mainMethodFind.writeln("");
		mainMethodFind.writeln("@Override");
		mainMethodFind.writeln_r("public " + classFileNameDto +" find("+classFileNameDto+" data){");
		mainMethodFind.writeln("return null;");
		mainMethodFind.writeln_l("}");
		
		mainMethodFindAll.writeln("");
		mainMethodFindAll.writeln("@Override");
		mainMethodFindAll.writeln_r("public List<" +classFileNameDto+"> findAll(){");
		mainMethodFindAll.writeln("return null;");
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
		System.out.println(topWriter.toString());
		String path=null;
		try {
			path=PackageCreator.packageToDirectory(packageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createFile(classFileName,path,topWriter.toString());
		printStatus(classFileName,true);
	}
	
	public static void printStatus(String className,boolean status) {
		System.out.println(className+" was create "+(status?"Successfull":"Fail"));
	}
	
	
	public static void createFilev2(String className,String path,String fileText) {
		String fileData = "Pankaj Kumar";
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(path+"\\"+className+".java");
			fos.write(fileText.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void createFile(String className,String path,String fileText) {
		
		System.out.println("Create File path: "+path);
		File file = new File(path+"\\"+className+".java");
		try {
			if(file.createNewFile()){
			    System.out.println("Archivo "+className+".java fue creado en "+path);
			}else{
				System.out.println("El archivo ya existe en el directorio, ser� reemplazado");
			}
			FileWriter fw = new FileWriter(path+"\\"+className+".java", false);
			fw.write(fileText);	
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
