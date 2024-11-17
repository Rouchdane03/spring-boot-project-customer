package com.rouchFirstCode;

import com.github.javafaker.Faker;
import com.rouchFirstCode.Customer.Customer;
import com.rouchFirstCode.Customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.Period;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Main.class,args);
        //printBeans(applicationContext);
}

@Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker=new Faker();
            LocalDate birthDate = faker.date().birthday(18, 65).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            Customer customer = new Customer(faker.name().fullName(),faker.internet().emailAddress(), age);
           customerRepository.save(customer);
            /*
            List<Customer> customers = new ArrayList<>();
            customers.add(new Customer("rouch","adissa",21));
            customers.add(new Customer("amakpe","hanane",19));
            customerRepository.saveAll(customers);
             */

        };
    }

   @Bean
    public Foo getFoo(){
        return new Foo("bar");
    }
    record Foo(String name){}

    private static void printBeans(ConfigurableApplicationContext ctx) {
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        for (String b : beanDefinitionNames
        ) {
            System.out.println("un bean: " + b);
        }
    }

}
