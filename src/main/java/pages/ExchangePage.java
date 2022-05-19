package pages;

import driver.Manager;
import driver.Waits;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;

@Getter
@Setter
public abstract class ExchangePage {

    public WebDriver driver;

    public String selectorTableHeaders;
    public String selectorTableRows;
    public String bankOperation;
    public String headerCurrencyName;
    public String buyOperationByBank;
    public String sellOperationByBank;
    public String bankName;

    public List<Map<String, String>> collectExchangeRatesList = new ArrayList<>();
    public List<WebElement> tableHeaders = new ArrayList<>();
    public List<WebElement> tableRows = new ArrayList<>();
    public List<Double> currencyRateList = new ArrayList<>();

    double currencyRate = 0.00;


    public ExchangePage() {
        this.driver = Manager.getCurrentDriver();
    }

    /**
     * Получаем лист с курсами для определенной валюты (EUR, USD) по искомой операции
     * запись в лист - т.к. для покупки/продажи одной и той же валюты может быть установлено сразу несколько различных курсов
     *
     * @param currencyName наименование валюты USD, EUR
     * @return лист со всеми найденными значениями курсов для установленной валюты и типа операции
     */
    protected abstract List<Double> getCurrencyRateList(String currencyName);


    /**
     * Метод получения наименее выгодного курса валюты с учетом установки сеттеров
     *
     * @param currencyName валюта USD или EUR
     * @param currencyType название операции: банк покупает или продает валюту
     * @return наименее выгодный курс покупки/продажи
     */
    public abstract Double getCurrencyRate(String currencyName, String currencyType);


    /**
     * Метод для подготовительных действий на странице перед получением курсов валют
     */
    public void preActions() {
        Waits.waitUntilElementBeVisible(getSelectorTableHeaders());
    }

    /**
     * Метод получения курсов валют в виде таблицы, где
     * ключ - заголовок колонки, значение - соответствующее значение курса, валюты и др. в зависимости от названия заголовка
     *
     * @return значения курсов валют на странице банка в виде таблицы
     */
    protected List<Map<String, String>> getCollectExchangeRates() {
        tableHeaders = driver.findElements(By.xpath(getSelectorTableHeaders()));
        tableRows = driver.findElements(By.xpath(getSelectorTableRows()));

        for (int i = 0; i < tableRows.size(); ++i) {
            Map<String, String> collectRow = new HashMap<>();
            for (int j = 0; j < tableHeaders.size(); ++j) {
                collectRow.put(
                        tableHeaders.get(j).getText(),
                        tableRows.get(i).findElement(By.xpath("./td[" + (j + 1) + "]")).getText()
                );
            }
            collectExchangeRatesList.add(collectRow);
        }
        return collectExchangeRatesList;
    }


    /**
     * Метод для получения списка с курсами валют в зависимости от операции покупки или продажи
     * Устанавливаем в качестве одного из заголовков таблицы (ключа), в котором содержится инфо об операции покупки или продажи,
     * конкретное значение: банк покупает или продает, после чего получаем лист всех найденных курсов на странице банка
     *
     * @param currencyName валюта USD или EUR
     * @param currencyType название операции: банк покупает или продает валюту
     * @return наименее выголный курс покупки/продажи
     */
    public List<Double> getCurrencyRateListByOperation(String currencyName, String currencyType) {
        switch (currencyType) {
            case "Банк покупает":
                setBankOperation(getBuyOperationByBank());
                currencyRateList = getCurrencyRateList(currencyName);
                return currencyRateList;
            case "Банк продает":
                setBankOperation(getSellOperationByBank());
                currencyRateList = getCurrencyRateList(currencyName);
                return currencyRateList;
            default:
                throw new IllegalStateException("Unexpected value: " + "'" + currencyType + "'" +
                        ". Введите корректный тип операции: 'Банк покупает' или 'Банк продает'");
        }
    }


    /**
     * Метод получения наименее выгодного курса валюты со стороны потребителя, а не банка
     * Если хотим наименее выгодный курс для банка  - меняем местами функции min и max
     *
     * @param currencyName валюта USD или EUR
     * @param currencyType название операции: банк покупает или продает валюту
     * @return наименее выголный курс покупки/продажи
     */
    protected Double getUnprofitableCurrencyRateResult(String currencyName, String currencyType) {
        try {
            currencyRateList = getCurrencyRateListByOperation(currencyName, currencyType);

            if (currencyType.equals("Банк покупает")) {
                currencyRate = Collections.min(currencyRateList);
            }
            if (currencyType.equals("Банк продает")) {
                currencyRate = Collections.max(currencyRateList);
            }
            return currencyRate;

        } catch (NoSuchElementException e) {
            System.out.println("Операции " + "'" + currencyType + "'" +
                    " и/или валюты " + "'" + currencyName + "'" + " не существует для данного банка");
        }
        return currencyRate;
    }


    /**
     * Метод для установления сеттеров для всех параметров единовременно
     *
     * @param headerCurrencyName   название заголовка таблицы с курсами (ключ)
     * @param selectorTableHeaders селектор, по которому ищем заголовок таблицы
     * @param selectorTableRows    селектор, по которому ищем строки со значениями курсов, названия валют и др данные в таблице
     * @param bankOperation        название заголовка таблицы (общее), где указаны типы операций (Покупка/Продажа) (ключ)
     * @param buyCurrencyByBank    колонка (заголовок) с курсами, где банк покупает
     * @param sellCurrencyByBank   колонка (заголовок) с курсами, где банк продает
     */
    protected void setAllParameters(
            String headerCurrencyName,
            String selectorTableHeaders,
            String selectorTableRows,
            String bankOperation,
            String buyCurrencyByBank,
            String sellCurrencyByBank,
            String bankName
    ) {
        setHeaderCurrencyName(headerCurrencyName);
        setSelectorTableHeaders(selectorTableHeaders);
        setSelectorTableRows(selectorTableRows);
        setBankOperation(bankOperation);
        setBuyOperationByBank(buyCurrencyByBank);
        setSellOperationByBank(sellCurrencyByBank);
        setBankName(bankName);
    }
}




