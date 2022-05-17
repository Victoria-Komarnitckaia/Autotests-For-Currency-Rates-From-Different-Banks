package assertions;

import io.qameta.allure.Step;

    /**
     * Для отображения ожиданий в аллюр отчете переопределяем методы Assertions
     */
    public class Assertions {

        @Step("Проверяю, что нет ошибки: '{message}'")
        public static void assertTrue(boolean condition, String message) {

            org.junit.jupiter.api.Assertions.assertTrue(condition, message);
        }

        @Step("Проверяю, что есть ошибка: '{message}'")
        public static void assertFalse(boolean condition, String message) {

            org.junit.jupiter.api.Assertions.assertFalse(condition, message);
        }
    }



