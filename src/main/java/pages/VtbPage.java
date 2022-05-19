package pages;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class VtbPage extends ExchangePage {

    protected String selectorTableHeaders = "//div[@class[contains(.,'Row-currency-exchange')]]" +
            "/div[@class[contains(.,'Column-currency-exchange')]]/preceding-sibling::div";
    protected String selectorTableRows = "//div[@class[contains(.,'Row-currency-exchange')]]/div" +
            "[@class[contains(.,'Column-currency-exchange')]]";

    protected String buyOperationByBank = "Покупаем";
    protected String sellOperationByBank = "Продаем ";
    protected String headerCurrencyName = "Валюта";
    protected String bankOperationRow = "Курс";
    protected String bankName = "Vtb";


    /**
     * более подробное описание метода тут {@link ExchangePage#getCurrencyRate(String, String)}
     *
     * @param currencyName валюта USD или EUR
     * @param currencyType название операции: банк покупает или продает валюту
     * @return наименее выголный курс покупки/продажи со стороны потребителя, а не банка
     */
    @Override
    public Double getCurrencyRate(String currencyName, String currencyType) {

        setAllParameters(headerCurrencyName,
                selectorTableHeaders,
                selectorTableRows,
                bankOperation,
                buyOperationByBank,
                sellOperationByBank,
                bankName
        );
        currencyRate = getUnprofitableCurrencyRateResult(currencyName, currencyType);

        return currencyRate;
    }

    /**
     * Перепишем метод получения таблицы с курсами для страницы втб банка.
     * Введем дополнительно ключи (заголовки таблицы) "Валюта" и "Курс", по которым в дальнейшем будем делать выборку.
     * более подробное описание метода тут {@link ExchangePage#getCollectExchangeRates()}
     *
     * @return Таблица с курсами
     */
    @Override
    protected List<Map<String, String>> getCollectExchangeRates() {

        tableHeaders = driver.findElements(By.xpath(getSelectorTableHeaders()));
        tableRows = driver.findElements(By.xpath(getSelectorTableRows()));

        for (int i = 0; i < tableRows.size(); i++) {
            Map<String, String> collectRow = new HashMap<>();

            collectRow.put(
                    "Валюта",
                    tableHeaders.get(i).getText()
            );
            collectRow.put(
                    "Курс",
                    tableRows.get(i).getText()
            );
            collectExchangeRatesList.add(collectRow);
        }
        return collectExchangeRatesList;
    }

    /**
     * более подробное описание метода тут {@link ExchangePage#getCurrencyRateList(String)}
     *
     * @param currencyName наименование валюты USD, EUR
     * @return лист со всеми найденными значениями курсов для установленной валюты и типа операции
     */
    @Override
    protected List<Double> getCurrencyRateList(String currencyName) {
            currencyRateList = getCollectExchangeRates().stream()
                    .filter(x -> x.get(headerCurrencyName).contains(currencyName))
                    .filter(x -> x.get(bankOperationRow).contains(bankOperation))
                    .map(x -> Double.parseDouble(x.get(bankOperationRow)
                            .split("П")[0]
                            .replace(",", ".")))
                    .collect(Collectors.toList());

            return currencyRateList;
    }
}
