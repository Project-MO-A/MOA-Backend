package com.moa.domain;


import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureTestDatabase(replace = NONE)
@DataJpaTest
public @interface RepositoryTest {
}
