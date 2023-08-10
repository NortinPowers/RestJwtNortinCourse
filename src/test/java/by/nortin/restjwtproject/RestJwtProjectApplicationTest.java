package by.nortin.restjwtproject;

import by.nortin.restjwtproject.controller.AdminController;
import by.nortin.restjwtproject.controller.AuthController;
import by.nortin.restjwtproject.controller.BookController;
import by.nortin.restjwtproject.controller.MainController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RestJwtProjectApplicationTest {

    @Autowired
    private AdminController adminController;

    @Autowired
    private AuthController authController;

    @Autowired
    private BookController bookController;

    @Autowired
    private MainController mainController;

    @Test
    void test_adminController_isNotNull() {
        Assertions.assertThat(adminController).isNotNull();
    }

    @Test
    void test_authController_isNotNull() {
        Assertions.assertThat(authController).isNotNull();
    }

    @Test
    void test_bookController_isNotNull() {
        Assertions.assertThat(bookController).isNotNull();
    }

    @Test
    void test_mainController_isNotNull() {
        Assertions.assertThat(mainController).isNotNull();
    }
}
