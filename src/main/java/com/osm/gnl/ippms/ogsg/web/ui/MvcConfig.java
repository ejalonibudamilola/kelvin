package com.osm.gnl.ippms.ogsg.web.ui;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    final Environment environment;


    public MvcConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/securedLoginForm").setViewName("secure/securedLoginForm");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registry.addViewController("/securedLogoutForm").setViewName("secure/securedLogoutForm");
        registry.addViewController("/index").setViewName("secure/index");

    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        String location = environment.getProperty("app.file.storage.mapping");

        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
    @Bean
    public Formatter<LocalDate> localDateFormatter() {
        return new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String text, Locale locale) throws ParseException {
                return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return DateTimeFormatter.ofPattern("dd-MM-yyyy").format(object);
            }
        };
    }
    @Bean
    public ServletRegistrationBean customServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new DisplayPassportImageServlet(), "/viewPassportImage");
        return bean;
    }
   /* @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver(){
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        return multipartResolver;
    }*/
 }
