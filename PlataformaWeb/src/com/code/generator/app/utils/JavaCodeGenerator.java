package com.code.generator.app.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.code.generator.app.dao.KioskeroWebDao;
import com.code.generator.app.dto.ColumnDto;
import com.code.generator.app.dto.TableDto;

@Component
public class JavaCodeGenerator {
	
	 @Autowired
	 KioskeroWebDao kioskeroWebDao;
	 public static final String SCHEMA="public";
	 public static final String PACKAGE_DTO=".dto";
	 public static final String PACKAGE_DAO=".dao";
	 public static final String PACKAGE_DAO_IMPL=".dao.impl";
	 public static final String PACKAGE_DELEGATE=".delegate";
	 public static final String PACKAGE_DELEGATE_IMPL=".delegate.impl";
	 public static final String PACKAGE_SERVICE=".service";
	 
	
	public  void generateCode(String packageBase,boolean createDto,
			boolean createDao,boolean createDelegate,boolean createService) {
		

		generatePackage("cl.package.www",createDto, createDao,createDelegate,createService);
		
		List<TableDto> tables=kioskeroWebDao.getAllTables(SCHEMA);
    	
    	for(TableDto table : tables) {
    		List<ColumnDto> columns=kioskeroWebDao.getAllColumnFromTable(table.getTableName(), SCHEMA);
    		generateJavaCode(table.getTableName(),columns,packageBase,createDto,createDao,createDelegate,createService);
    	}
		
	}
	
	public void generateJavaCode(String tableName,List<ColumnDto> columns,String packageBase,boolean createDto,
			boolean createDao,boolean createDelegate,boolean createService) {
		
    		if(createDto) {
    			FileCreator.createDtoClassJavaFile(tableName, columns, packageBase+PACKAGE_DTO);
    		}
    		if(createDao) {
    			FileCreator.createDaoInterfaceJavaFile(tableName,  packageBase+PACKAGE_DAO);
    			FileCreator.createDaoClassJavaFile(tableName,  packageBase+PACKAGE_DAO_IMPL);
    		}
    		if(createDelegate) {
    			FileCreator.createDelegateInterfaceJavaFile(tableName,  packageBase+PACKAGE_DELEGATE);
    			FileCreator.createDelegateClassJavaFile(tableName,  packageBase+PACKAGE_DELEGATE_IMPL);
    		}
    		if(createService) {
    			FileCreator.createServiceClassJavaFile(tableName,  packageBase+PACKAGE_SERVICE);
    		}
		
	}
	
	
	
	public  void generatePackage(String packageBase,boolean createDto,
			boolean createDao,boolean createDelegate,boolean createService) {
		PackageCreator.createPackage(packageBase);
		if(createDto) {
			PackageCreator.createPackage(packageBase+".dto");
		}
		if(createDao) {
			PackageCreator.createPackage(packageBase+".dao");
			PackageCreator.createPackage(packageBase+".dao.impl");
		}
		if(createDelegate) {
			PackageCreator.createPackage(packageBase+".delegate");
			PackageCreator.createPackage(packageBase+".delegate.impl");
		}
		if(createService) {
			PackageCreator.createPackage(packageBase+".service");
		}
	}
}
