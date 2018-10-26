package com.code.generator.app.utils;

import java.io.File;

public class PackageCreator {
	
	public static final String RELATIVE_SRC_PATH = ".\\src";

	public static void createPackage(String newPackageName) {

		try {
			packageToDirectory(newPackageName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createDirectory(String directory) {
		boolean result = false;
		try {

			System.out.println(directory);
			File theDir = new File(directory);

			if (!theDir.exists()) {
				System.out.println("creating directory: " + theDir.getName());

				try {
					theDir.mkdir();
					result = true;
				} catch (SecurityException se) {
					// handle it
				}
				if (result) {
					System.out.println("DIR created");
				}
			} else {
				result = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String packageToDirectory(String packageName) throws Exception {
		String path = RELATIVE_SRC_PATH;
		if (packageName != null && !packageName.equals("") && packageName.contains(".")) {
			String[] directories = packageName.split("\\.");
			for (String directory : directories) {
				path += "\\" + directory;
				createDirectory(path);
			}
		} else {
			throw new Exception("Package en formato incorrecto: " + packageName);
		}

		return path;
	}
	
	public static String findDtoPackage(String packageName) {
		String dtoPackage=null;
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DAO)) {
			dtoPackage=packageName.replace(JavaCodeGenerator.PACKAGE_DAO, JavaCodeGenerator.PACKAGE_DTO);
		}
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DAO_IMPL)) {
			dtoPackage=packageName.replace(JavaCodeGenerator.PACKAGE_DAO_IMPL, JavaCodeGenerator.PACKAGE_DTO);
		}
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DELEGATE)) {
			dtoPackage=packageName.replace(JavaCodeGenerator.PACKAGE_DELEGATE, JavaCodeGenerator.PACKAGE_DTO);
		}
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DELEGATE_IMPL)) {
			dtoPackage=packageName.replace(JavaCodeGenerator.PACKAGE_DELEGATE_IMPL, JavaCodeGenerator.PACKAGE_DTO);
		}
		return dtoPackage;
	}
	
	public static String findDaoPackage(String packageName) {
		String daoPackage=null;
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DAO_IMPL)) {
			daoPackage=packageName.replace(JavaCodeGenerator.PACKAGE_DAO_IMPL, JavaCodeGenerator.PACKAGE_DAO);
		}
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DAO_IMPL)) {
			daoPackage=packageName.replace(JavaCodeGenerator.PACKAGE_DAO_IMPL, JavaCodeGenerator.PACKAGE_DAO);
		}
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DELEGATE)) {
			daoPackage=packageName.replace(JavaCodeGenerator.PACKAGE_DELEGATE, JavaCodeGenerator.PACKAGE_DAO);
		}
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DELEGATE_IMPL)) {
			daoPackage=packageName.replace(JavaCodeGenerator.PACKAGE_DELEGATE_IMPL, JavaCodeGenerator.PACKAGE_DAO);
		}
		return daoPackage;
	}
	public static String findDelegatePackage(String packageName) {
		String delegatePackage=null;
		if(packageName.contains(JavaCodeGenerator.PACKAGE_SERVICE)) {
			delegatePackage=packageName.replace(JavaCodeGenerator.PACKAGE_SERVICE, JavaCodeGenerator.PACKAGE_DELEGATE);
		}
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DELEGATE_IMPL)) {
			delegatePackage=packageName.replace(JavaCodeGenerator.PACKAGE_DELEGATE_IMPL, JavaCodeGenerator.PACKAGE_DELEGATE);
		}
		if(packageName.contains(JavaCodeGenerator.PACKAGE_DELEGATE_IMPL)) {
			delegatePackage=packageName.replace(JavaCodeGenerator.PACKAGE_DELEGATE_IMPL, JavaCodeGenerator.PACKAGE_DELEGATE);
		}
		return delegatePackage;
	}
	
}
