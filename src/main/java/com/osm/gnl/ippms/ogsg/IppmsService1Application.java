package com.osm.gnl.ippms.ogsg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@ComponentScan(basePackages="com.osm.gnl.ippms.ogsg")
public class IppmsService1Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(IppmsService1Application.class, args);
	}


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(IppmsService1Application.class);
	}


}
