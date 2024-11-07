package com.rouchFirstCode;

import com.rouchFirstCode.Customer.Customer;
import com.rouchFirstCode.Customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext
       = SpringApplication.run(Main.class,args);
        //printbeans(applicationContext);
}
@Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            List<Customer> customers = new ArrayList<>();
            customers.add(new Customer("rouch","adissa",21));
            customers.add(new Customer("amakpe","hanane",19));

            customerRepository.saveAll(customers);
        };
    }
   @Bean
    public Foo getFoo(){
        return new Foo("bar");
    }
    record Foo(String name){}
    private static void printBeans (ConfigurableApplicationContext ctx) {
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        for (String b : beanDefinitionNames
        ) {
            System.out.println("un bean: " + b);
        }
    }
}
