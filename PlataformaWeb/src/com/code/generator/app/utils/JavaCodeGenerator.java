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

	 
	public boolean fileOptions(FileOptions options){
		boolean value=false;
		if(options == FileOptions.CREATE) {
			value=true;
		}
		return value;
	} 
	 
	 
	
	public  void generateCode(String packageBase,FileOptions createDto,
			FileOptions createDao,FileOptions createDelegate,FileOptions createService) {
		

		generatePackage(packageBase,createDto, createDao,createDelegate,createService);
		
		
		
		List<TableDto> tables=kioskeroWebDao.getAllTables(SCHEMA);
    	
    	for(TableDto table : tables) {
    		List<ColumnDto> columns=kioskeroWebDao.getAllColumnFromTable(table.getTableName(), SCHEMA);
    		generateJavaCode(table.getTableName(),columns,packageBase,(createDto),(createDao),(createDelegate),(createService));
    	}
		
	}
	
	public void generateJavaCode(String tableName,List<ColumnDto> columns,
			String packageBase,FileOptions createDto,
			FileOptions createDao,FileOptions createDelegate,FileOptions createService) {
		if(FileOptions.CREATE==createDto || FileOptions.CREATE_OR_REPLACE==createDto) {
		FileCreator.createDtoClassJavaFile(tableName, columns, packageBase+PACKAGE_DTO,fileOptions(createDto));
		}
		if(FileOptions.CREATE==createDao || FileOptions.CREATE_OR_REPLACE==createDao) {
		FileCreator.createDaoInterfaceJavaFile(tableName,  packageBase+PACKAGE_DAO,fileOptions(createDao));
		FileCreator.createDaoClassJavaFile(tableName,  packageBase+PACKAGE_DAO_IMPL,columns,fileOptions(createDao));
		}
		if(FileOptions.CREATE==createDelegate || FileOptions.CREATE_OR_REPLACE==createDelegate) {
			FileCreator.createDelegateInterfaceJavaFile(tableName,  packageBase+PACKAGE_DELEGATE,fileOptions(createDelegate));
			FileCreator.createDelegateClassJavaFile(tableName,  packageBase+PACKAGE_DELEGATE_IMPL,fileOptions(createDelegate));
		}
		if(FileOptions.CREATE_WEB_CLIENT==createDelegate) {
		FileCreator.createDelegateInterfaceJavaFile(tableName,  packageBase+PACKAGE_DELEGATE,fileOptions(createDelegate));
		FileCreator.createDelegateClassForWsJavaFile(tableName,  packageBase+PACKAGE_DELEGATE_IMPL,fileOptions(createDelegate));
		}
		if(FileOptions.CREATE==createService || FileOptions.CREATE_OR_REPLACE==createService) {
		FileCreator.createServiceClassJavaFile(tableName,  packageBase+PACKAGE_SERVICE,columns,fileOptions(createService));
		}
	}

	
	
	
	public  void generatePackage(String packageBase,FileOptions createDto,
			FileOptions createDao,FileOptions createDelegate,FileOptions createService) {
		PackageCreator.createPackage(packageBase);
		if(FileOptions.CREATE==createDto) {
			PackageCreator.createPackage(packageBase+PACKAGE_DTO);
		}
		if(FileOptions.CREATE==createDao ) {
			PackageCreator.createPackage(packageBase+PACKAGE_DAO);
			PackageCreator.createPackage(packageBase+PACKAGE_DAO_IMPL);
		}
		if(FileOptions.CREATE==createDelegate ) {
			PackageCreator.createPackage(packageBase+PACKAGE_DELEGATE);
			PackageCreator.createPackage(packageBase+PACKAGE_DELEGATE_IMPL);
		}
		if(FileOptions.CREATE==createService ) {
			PackageCreator.createPackage(packageBase+PACKAGE_SERVICE);
			PackageCreator.createPackage(packageBase+PACKAGE_UTILS);
		}
		if(FileOptions.CREATE==createService || FileOptions.CREATE_OR_REPLACE==createService || FileOptions.CREATE_WEB_CLIENT==createService ) {
			FileCreator.createEnumEstatusWsResponse(packageBase+PACKAGE_UTILS,fileOptions( createService));
			FileCreator.createResponseObject(packageBase+PACKAGE_UTILS, fileOptions(createService));
			FileCreator.createWsOperationValidation(packageBase+PACKAGE_UTILS, fileOptions(createService));
			FileCreator.createErrorMessage(packageBase+PACKAGE_UTILS, fileOptions(createService));
			FileCreator.createFileToArrayCreator(packageBase+PACKAGE_UTILS, fileOptions(createService));
			FileCreator.createFileBaseUrl(packageBase+PACKAGE_UTILS, fileOptions(createService));
			FileCreator.createFileJsonConverter(packageBase+PACKAGE_UTILS, fileOptions(createService));
		}
	}
}
