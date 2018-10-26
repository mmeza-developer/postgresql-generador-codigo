package com.code.generator.app.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.code.generator.app.utils.JavaCodeGenerator;

@SpringBootApplication
@ComponentScan("com.code.generator.app")
public class Application implements CommandLineRunner{
	@Autowired
	JavaCodeGenerator javaCodeGenerator;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
    public void run(String... strings) throws Exception {
    	javaCodeGenerator.generateCode("cl.mypackage.www",true,true,true,false);
    }
}
