package tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.provider.Arguments;
import pages.*;
import properties.TestDataProps;
import java.util.List;
import java.util.stream.Stream;

public class TestData {

    static String sberUrl = TestDataProps.propsUrl.baseUrlSberBank();
    static String vtbUrl = TestDataProps.propsUrl.baseUrlVtbBank();
    static String openUrl = TestDataProps.propsUrl.baseUrlOpenBank();
    static String alfaUrl = TestDataProps.propsUrl.baseUrlAlfaBank();


    /**
     * Тестовые данные для получения самого невыгодного курса валюты на сайте банка
     *
     * @return
     * - банк (класс)
     * - url банка
     * - валюта
     * - тип операции
     */
    @Tag("getWorstCurrencyRateFromSpecificBank")
    public static Stream<Arguments> getWorstRate() {

        return Stream.of(
                Arguments.of(new VtbPage(), vtbUrl, "USD", "Банк покупает")
//                ,
//                Arguments.of(new VtbPage(), vtbUrl, "USD", "Банк продает"),
//                Arguments.of(new SberPage(), sberUrl, "USD", "Банк покупает"),
//                Arguments.of(new SberPage(), sberUrl, "EUR", "Банк продает")
        );
    }


    /**
     * Тестовые данные для сравнения самых невыгодных курсов валют на сайтах разных банков
     *
     * @return
     * - лист классов (страниц) банков, которые будем тестить,
     * - url лист банков,
     * - валюта
     * - тип операции
     */
    @Tag("compareCurrencyRatesTest")
    public static Stream<Arguments> compareTestParams() {

        return Stream.of(
                Arguments.of(
                        List.of(new VtbPage(), new SberPage(), new AlfaPage(), new OpenBankPage()),
                        List.of(vtbUrl, sberUrl, alfaUrl, openUrl),
                        "USD", "Банк покупает")
//                ,
//                Arguments.of(
//                        List.of(new VtbPage(), new SberPage(), new AlfaPage(), new OpenBankPage()),
//                        List.of(sberUrl, vtbUrl, openUrl, alfaUrl),
//                        "EUR", "Банк покупает"),
//                Arguments.of(
//                        List.of(new VtbPage(), new SberPage(), new AlfaPage(), new OpenBankPage()),
//                        List.of(sberUrl, vtbUrl, openUrl, alfaUrl),
//                        "EUR", "Банк продает"),
//                Arguments.of(
//                        List.of(new VtbPage(), new SberPage(), new AlfaPage(), new OpenBankPage()),
//                        List.of(sberUrl, vtbUrl, openUrl, alfaUrl),
//                        "USD", "Банк продает")
        );
    }

    /**
     * Тестовые данные для теста на значение курса выше порогового значения
     *
     * @return
     * - лист классов (страниц) банков, которые будем тестить,
     * - url лист банков,
     * - валюта
     * - тип операции
     * - пороговое значение
     */
    @Tag("testBias")
    public static Stream<Arguments> testBiasParams() {

        return Stream.of(
                Arguments.of(
                        List.of(new SberPage(), new VtbPage(), new OpenBankPage(), new AlfaPage()),
                        List.of(sberUrl, vtbUrl, openUrl, alfaUrl),
                        "USD", "Банк покупает", 75.1)
        );
    }
}