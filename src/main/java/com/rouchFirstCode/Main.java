package com.rouchFirstCode;

import com.github.javafaker.Faker;
import com.rouchFirstCode.Customer.Customer;
import com.rouchFirstCode.Customer.CustomerRepository;
import com.rouchFirstCode.Customer.GenderEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Main.class,args);
        //printBeans(applicationContext);
    }

@Bean
    CommandLineRunner runner(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Faker faker=new Faker();
            LocalDate birthDate = faker.date().birthday(18, 65).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            int age = Period.between(birthDate, LocalDate.now()).getYears();

            //Set random gender to the user
            List<GenderEnum> enums = List.of(GenderEnum.MALE,GenderEnum.FEMALE);
            int index = RANDOM.nextInt(enums.size());
            String pwdBeforeEncoding = UUID.randomUUID().toString();
            System.out.println(pwdBeforeEncoding);
            Customer customer = new Customer(faker.name().fullName(),faker.internet().emailAddress(), passwordEncoder.encode(pwdBeforeEncoding), age,enums.get(index));
           customerRepository.save(customer);
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
