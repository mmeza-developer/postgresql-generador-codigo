package com.code.generator.app.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.code.generator.app.dao.DbObjectDao;
import com.code.generator.app.dto.ColumnDto;
import com.code.generator.app.dto.TableDto;

@Component
public class JavaCodeGenerator {
	
	 @Autowired
	 DbObjectDao kioskeroWebDao;
	 public static final String SCHEMA="public";
	 public static final String PACKAGE_DTO=".dto";
	 public static final String PACKAGE_DAO=".dao";
	 public static final String PACKAGE_DAO_IMPL=".dao.impl";
	 public static final String PACKAGE_DELEGATE=".delegate";
	 public static final String PACKAGE_DELEGATE_IMPL=".delegate.impl";
	 public static final String PACKAGE_SERVICE=".service";
	 public static final String PACKAGE_UTILS=".utils";
	 
	
	public  void generateCode(String packageBase,boolean createDto,
			boolean createDao,boolean createDelegate,boolean createService) {
		

		generatePackage(packageBase,createDto, createDao,createDelegate,createService);
		
		
		
		List<TableDto> tables=kioskeroWebDao.getAllTables(SCHEMA);
    	
    	for(TableDto table : tables) {
    		List<ColumnDto> columns=kioskeroWebDao.getAllColumnFromTable(table.getTableName(), SCHEMA);
    		generateJavaCode(table.getTableName(),columns,packageBase,createDto,createDao,createDelegate,createService);
    	}
		
	}
	
	public void generateJavaCode(String tableName,List<ColumnDto> columns,String packageBase,boolean createDto,
			boolean createDao,boolean createDelegate,boolean createService) {
		
    			FileCreator.createDtoClassJavaFile(tableName, columns, packageBase+PACKAGE_DTO,createDto);
    			
    			FileCreator.createDaoInterfaceJavaFile(tableName,  packageBase+PACKAGE_DAO,createDao);
    			FileCreator.createDaoClassJavaFile(tableName,  packageBase+PACKAGE_DAO_IMPL,columns,createDao);
    			
    			FileCreator.createDelegateInterfaceJavaFile(tableName,  packageBase+PACKAGE_DELEGATE,createDelegate);
    			FileCreator.createDelegateClassJavaFile(tableName,  packageBase+PACKAGE_DELEGATE_IMPL,createDelegate);
    			
    			FileCreator.createServiceClassJavaFile(tableName,  packageBase+PACKAGE_SERVICE,createService);
		
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
			PackageCreator.createPackage(packageBase+".utils");
			FileCreator.createEnumEstatusWsResponse(packageBase+PACKAGE_UTILS, createService);
			FileCreator.createResponseObject(packageBase+PACKAGE_UTILS, createService);
		}
	}
}
