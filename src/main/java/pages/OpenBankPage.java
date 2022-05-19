package pages;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class OpenBankPage extends ExchangePage {

    protected String selectorTableHeaders = "//tbody/tr[contains(@class,'header')]/td";
    protected String selectorTableRows = "//tbody/tr[contains(@class,'row')]";

    protected String buyOperationByBank = "Банк покупает";
    protected String sellOperationByBank = "Банк продает";
    protected String headerCurrencyName = "Валюта обмена";

    protected String bankName = "Open";


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

       currencyRate= getUnprofitableCurrencyRateResult(currencyName, currencyType);
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
                .filter(x -> x.get(getHeaderCurrencyName()).contains(currencyName))
                .map(x -> Double.parseDouble(x.get(getBankOperation()).replace(",", ".")))
                .collect(Collectors.toList());

        return currencyRateList;
    }
}
