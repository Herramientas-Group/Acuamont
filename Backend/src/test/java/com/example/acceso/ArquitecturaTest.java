package com.example.acceso;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArquitecturaTest {

    private static JavaClasses clases;

    @BeforeAll
    static void setUp() {
        clases = new ClassFileImporter().importPackages("com.example.acceso");
    }

    @Test
    void repository_shouldOnlyBeAccessedByService() {
        ArchRule rule = noClasses()
                .that().resideOutsideOfPackage("..service..")
                .and().resideOutsideOfPackage("..security..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("com.example.acceso.repository..");

        rule.check(clases);
    }

    @Test
    void model_shouldNotDependOnAnyOtherLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..service..", "com.example.acceso.repository..", "..controller..");

        rule.check(clases);
    }

    @Test
    void controller_shouldNotAccessRepositoryDirectly() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("com.example.acceso.repository..");

        rule.check(clases);
    }

    @Test
    void serviceImpl_shouldNotBeInterfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage("..service.Implements..")
                .should().notBeInterfaces();

        rule.check(clases);
    }

    @Test
    void serviceInterfaces_shouldResideInInterfacesPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areInterfaces()
                .should().resideInAPackage("..service.Interfaces..");

        rule.check(clases);
    }
}
