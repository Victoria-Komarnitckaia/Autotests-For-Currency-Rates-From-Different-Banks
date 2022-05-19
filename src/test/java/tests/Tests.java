package tests;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import pages.*;
import steps.Steps;

import java.util.List;

@Feature("Проверка курсов валют на сайтах разных банков")
public class Tests extends BaseTest {

    @Tag("getWorstCurrencyRateFromSpecificBank")
    @DisplayName("Получение наименее выгодного курса продажи и покупки основных валют на сайте банка")
    @ParameterizedTest(name = "{displayName}, {arguments}")
    @MethodSource("tests.TestData#getWorstRate")
    public void currencyRateTest(ExchangePage bankPage, String url, String currencyName, String currencyType) {
        Steps.goPage(url);
        Steps.getCurrencyRate(bankPage, currencyName, currencyType);
    }

    @Tag("compareCurrencyRatesTest")
    @DisplayName("Сравнение самых невыгодных курсов валют между сайтами разных банков")
    @ParameterizedTest(name = "{displayName}, {arguments}")
    @MethodSource("tests.TestData#compareTestParams")
    public void compareCurrencyRateTest(List<ExchangePage> bankPages, List<String> urls, String currencyName, String currencyType) {

        Steps.compareCurrencyRates(bankPages, urls, currencyName, currencyType);

    }

    @Tag("testBias")
    @DisplayName("Проверка курсов валют на сайтах разных банков не выше порогового значения")
    @ParameterizedTest(name = "{displayName}, {arguments}")
    @MethodSource("tests.TestData#testBiasParams")
    public void checkCurrencyRateBiasTest(List<ExchangePage> bankPages, List<String> urls, String currencyName, String currencyType, Double bias) {

        Steps.checkCurrencyRateBias(bankPages, urls, currencyName, currencyType, bias);
    }
}




