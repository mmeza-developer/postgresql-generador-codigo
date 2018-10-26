package com.code.generator.app.utils;

public class TextFormat {

	public static String underLineNameToCamelCase(String underLineName) {
		String camelCaseName="";
		if(underLineName!=null && !underLineName.equals("")) {
				if(underLineName.contains("_")) {
					String [] camelCaseAux=underLineName.split("_");
					for(String text: camelCaseAux) {
						camelCaseName+=text.substring(0, 1).toUpperCase() + text.substring(1);
					}
				}else {
					camelCaseName=underLineName;
				}
		}
		return camelCaseName;
	}
	
	public static String columnNameToCamelCase(String underLineName) {
		String camelCaseName="";
		if(underLineName!=null && !underLineName.equals("")) {
			if(underLineName.contains("_")) {
				String [] camelCaseAux=underLineName.split("_");
				for(String text: camelCaseAux) {
					camelCaseName+=text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
				}
			}else {
				camelCaseName=underLineName.substring(0, 1).toUpperCase() + underLineName.substring(1).toLowerCase();
			}
		}
		return camelCaseName;
	}
	
	
	public static String columnNameToCamelCaseFirstLowCase(String underLineName) {
		String camelCaseName="";
		if(underLineName!=null && !underLineName.equals("")) {
			if(underLineName.contains("_")) {
				String [] camelCaseAux=underLineName.split("_");
				int cont=0;
				for(String text: camelCaseAux) {
						
						if(cont==0) {
							camelCaseName+=text.toLowerCase();
						}else {
							camelCaseName+=text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
						}
						cont++;
				}
			}else {
				camelCaseName=underLineName.toLowerCase();
			}
		}
		return camelCaseName;
	}
	
	public static String underLineNameToCamelCaseNonFirstLetter(String underLineName) {
		String camelCaseName="";
		if(underLineName!=null && !underLineName.equals("")) {
				if(underLineName.contains("_")) {
					String [] camelCaseAux=underLineName.split("_");
					int cont=0;
					for(String text: camelCaseAux) {
						if(cont==0) {
							camelCaseName+=text.substring(0, 1).toLowerCase() + text.substring(1);
						}else {
							camelCaseName+=text.substring(0, 1).toUpperCase() + text.substring(1);
						}
						cont++;
					}
				}else {
					camelCaseName=underLineName;
				}
		}
		return camelCaseName;
	}
	
}
