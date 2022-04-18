package main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;



@Configuration
@EnableWebMvc
@ComponentScan("main")
public class MyConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    @Autowired
    public MyConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("classpath:templates/");
        templateResolver.setSuffix(".html");

        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/js/**").addResourceLocations("classpath:/assets/js/");
        registry.addResourceHandler("/assets/css/**").addResourceLocations("classpath:/assets/css/");
        registry.addResourceHandler("/assets/plg/**").addResourceLocations("classpath:/assets/plg/");
        registry.addResourceHandler("/assets/img/icons/**").addResourceLocations("classpath:/assets/img/icons/");
        registry.addResourceHandler("/assets/fonts/Montserrat/**").addResourceLocations("classpath:/assets/fonts/Montserrat/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setContentType("text/html;charset=UTF-8");
        registry.viewResolver(resolver);
    }
}
