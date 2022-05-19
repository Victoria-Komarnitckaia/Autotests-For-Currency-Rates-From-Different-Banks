package pages;

import driver.Waits;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Getter
@Setter
public class AlfaPage extends ExchangePage {

    protected String selectorTableHeaders = "//thead/tr/th";
    protected String selectorTableRows = "//tbody/tr[contains(., 'за')]";
    protected String usdSelector = "//button[@data-test-id='currency-USD']";
    protected String eurSelector = "//button[@data-test-id='currency-EUR']";
    protected String rubSelector = "//button[@data-test-id='currency-RUB']";
    protected String visibleElemForEurButton = "(//button[@data-test-selected='true' and contains(., 'EUR')])[1]";
    protected String visibleElemForUsdButton = "(//button[@data-test-selected='true' and contains(., 'USD')])[1]";
    protected String visibleElemForRubButton = "//button[@data-test-id='currency-RUB' and @data-test-selected='true']";

    protected String bankOperation = "Курс и дата обновления";
    protected String headerCurrencyName = "Валюта";
    protected String bankName = "Alfa";



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
                .filter(x -> x.get(headerCurrencyName).contains(currencyName))
                .map(x -> Double.parseDouble(x.get(bankOperation)
                        .split("₽")[0]
                        .replace(",", ".")))
                .collect(Collectors.toList());

        return currencyRateList;
    }


    /**
     * Перепишем метод получения  списка с курсами валют в зависимости от операции покупки или продажи, т.к. в альфа-банке
     * для операции покупки валюты банком необходимо дополнительно прожать кнопки для перехода на необходимую валюту
     * <p>
     * более подробное описание метода тут {@link ExchangePage#getCurrencyRateListByOperation(String, String)}
     *
     * @param currencyName валюта USD или EUR
     * @param currencyType название операции: банк покупает или продает валюту
     * @return наименее выголный курс покупки/продажи
     */
    @Override
    public List<Double> getCurrencyRateListByOperation(String currencyName, String currencyType) {
        try {
            switch (currencyType) {
                case "Банк покупает":
                    clickOnCurrencyButton(currencyName);
                    currencyName = "RUB";
                    currencyRateList = getCurrencyRateList(currencyName);
//                    System.out.println(currencyRateList);
                    return currencyRateList;
                case "Банк продает":
                    clickOnButton(rubSelector, visibleElemForRubButton);
                    currencyRateList = getCurrencyRateList(currencyName);

                    return currencyRateList;

                default:
                    throw new IllegalStateException("Unexpected value: " + "'" + currencyType + "'" +
                            ". Введите корректный тип операции: 'Банк покупает' или 'Банк продает'");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Операции " + "'" + currencyType + "'" +
                    " и/или валюты " + "'" + currencyName + "'" + " не существует для данного банка");
        }
        return currencyRateList;
    }


    /**
     * Нажатие на кнопку указанной валюты евро или доллар
     *
     * @param currencyName наименование валюты
     */
    private void clickOnCurrencyButton(String currencyName) {
        switch (currencyName) {
            case "USD":
                clickOnButton(usdSelector, visibleElemForUsdButton);
                return;
            case "EUR":
                clickOnButton(eurSelector, visibleElemForEurButton);
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + currencyName);
        }
    }


    /**
     * Нажатие на кнопку по указанному селектору
     *
     * @param selector     селектор необходиомй кнопки, на которую нужно нажать
     * @param waitSelector селектор для прогрузки страницы после нажатия
     */
    private void clickOnButton(String selector, String waitSelector) {
        WebElement button = driver.findElement(By.xpath(selector));
        Waits.waitUntilElementBeClickable(button);
        button.click();
        Waits.waitUntilElementBeVisible(waitSelector);
    }
}

