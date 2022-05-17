package tests;

import driver.Manager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;

public class BaseTest {

    public static WebDriver chromedriver;

    @BeforeAll
    public static void before() {
        Manager.initChrome();
        chromedriver = Manager.getCurrentDriver();
    }

    @AfterAll
    public static void closeAllTest() {
        Manager.killCurrentDriver();
    }
}


