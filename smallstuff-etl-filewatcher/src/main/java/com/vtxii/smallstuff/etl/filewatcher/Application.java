package com.vtxii.smallstuff.etl.filewatcher;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	@Bean
	protected ServletContextListener listener() {
		return new ContextListener();
	}

	// Parameters for watching files
	// TODO:  improve configuration mechanism - hard coding in the class is not cool
	@Bean
	public ServletContextInitializer initializer() {
		return new ServletContextInitializer() {
			@Override
			public void onStartup(ServletContext servletContext)
					throws ServletException {
				servletContext.setInitParameter("processor-class-name",
						"com.vtxii.smallstuff.etl.filewatcher.DefaultProcessor");
				servletContext.setInitParameter("filter-class-name",
						"com.vtxii.smallstuff.etl.filewatcher.DefaultFilter");
				servletContext.setInitParameter("directories",
						"/home/devuser/test;/home/devuser/test2");
				servletContext.setInitParameter("polling-interval", "10");
			}
		};
	}
	
	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
