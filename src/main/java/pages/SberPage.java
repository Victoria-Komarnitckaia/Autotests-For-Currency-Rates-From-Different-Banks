package pages;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SberPage extends ExchangePage {

    protected String selectorTableHeaders = "//thead/tr/th";
    protected String selectorTableRows = "//tbody/tr[@class[contains(., 'firstRow')]]";
    protected String bankName = "Sber";
    protected String buyOperationByBank = "Продать";
    protected String sellOperationByBank = "Купить";
    protected String headerCurrencyName = "Валюта";


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
     * более подробное описание метода тут {@link ExchangePage#getCurrencyRateList(String)}
     *
     * @param currencyName наименование валюты USD, EUR
     * @return лист со всеми найденными значениями курсов для установленной валюты и типа операции
     */
    @Override
    protected List<Double> getCurrencyRateList(String currencyName) {
        currencyRateList = getCollectExchangeRates().stream()
                .filter(x -> x.get(getHeaderCurrencyName()).contains(revertCurrencyName(currencyName)))
                .map(x -> Double.parseDouble(x.get(getBankOperation()).replace(",", ".")))
                .collect(Collectors.toList());
        System.out.println(currencyRateList);

        return currencyRateList;
    }


    /**
     * Метод по замене наименования валюты с международного на российское
     *
     * @param currencyName международное наименование валюты
     * @return российское наименование валюты
     */
    private String revertCurrencyName(String currencyName) {
        String reversedCurrencyName = "";
        switch (currencyName) {
            case "USD":
                reversedCurrencyName = "Доллар США";
                return reversedCurrencyName;
            case "EUR":
                reversedCurrencyName = "Евро";
                return reversedCurrencyName;
            default:
                throw new IllegalStateException("Unexpected value: " + "'" + currencyName + "'" +
                        ". Введите корректный тип валюты: 'EUR' или 'USD'");
        }
    }
}
