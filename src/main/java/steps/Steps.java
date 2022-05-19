package steps;

import driver.Manager;

import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import pages.*;

import java.util.*;

public class Steps {

    @Step("Перейти на страницу {url}")
    public static void goPage(String url) {
        Manager.getCurrentDriver().get(url);
    }

    @Step("Получить наименее выгодный курс {currencyName} по типу {currencyType}")
    public static Double getCurrencyRate(ExchangePage page, String currencyName, String currencyType) {
        page.preActions();
        return page.getCurrencyRate(currencyName, currencyType);
    }

    @Step("Сравнить между собой самые невыгодные предложения для потребителя " +
            "по {currencyName} в каждом из банков, когда {currencyType} и \n" +
            "\n Определить наилучшее и наихудшее значение курса \n" +
            "\n Убедиться, что разница составляет не более 1 рубля за единицу валюты.")
    public static void compareCurrencyRates(List<ExchangePage> pages,
                                            List<String> urls,
                                            String currencyName,
                                            String currencyType) {

// результат наилуч. и наихудш. представим в виде одного списка из мап (ключ - название банка, значение - его курс)
        List<Map<String, Double>> resultBestAndWorstRates = new ArrayList<>();
        Map<String, Double> resultBest = new HashMap();
        Map<String, Double> resultWorst = new HashMap();

        Map<String, Double> collectResults = Steps.getCollectedRates(pages, urls, currencyName, currencyType);

        List<Double> sortedRatestList = Steps.getSortedRatesList(collectResults);

        for (String key : collectResults.keySet()) {
            switch (currencyType) {
                case "Банк покупает":
                    double bestRateBuy = Collections.max(sortedRatestList);
                    double worstRateBuy = Collections.min(sortedRatestList);

                    if (collectResults.get(key).equals(bestRateBuy)) {
                        resultBest.put(key, bestRateBuy);
                        System.out.println("Наилучшее значение курса, когда банк покупает " + currencyName + " у : " + resultBest);
                    }
                    if (collectResults.get(key).equals(worstRateBuy)) {
                        resultWorst.put(key, worstRateBuy);
                        System.out.println("Наихудшее значение курса, когда банк покупает " + currencyName + " у : " + resultWorst);
                    }
                    break;
                case "Банк продает":
                    double bestRateSell = Collections.min(sortedRatestList);
                    double worstRateSell = Collections.max(sortedRatestList);

                    if (collectResults.get(key).equals(bestRateSell)) {
                        resultBest.put(key, bestRateSell);
                        System.out.println("Наилучшее значение курса, когда банк продает " + currencyName + " у : " + resultBest);
                    }
                    if (collectResults.get(key).equals(worstRateSell)) {
                        resultWorst.put(key, worstRateSell);
                        System.out.println("Наихудшее значение курса, когда банк продает " + currencyName + " у : " + resultWorst);
                    }
                    break;
            }
        }
        resultBestAndWorstRates.add(resultBest);
        resultBestAndWorstRates.add(resultWorst);
        System.out.println(resultBestAndWorstRates);

        for (int i = 0; i < sortedRatestList.size() - 1; i++) {
            Assertions.assertTrue(
                    (sortedRatestList.get(i) - sortedRatestList.get(i + 1) <= 1) &&
                            (sortedRatestList.get(sortedRatestList.size() - 1) - sortedRatestList.get(0) <= 1),
                    "Разница курса между минимум двумя банками составляет более 1 рубля за единицу валюты");
        }
    }

    //берем не только самый невыгодный, а все найденные курсы (их может быть несколько) на странице банка по искомой валюте
    @Step("Убедиться, что на сайтах всех банков курс {currencyName}, когда {currencyType}, не превышает {bias}, когда {currencyType}")
    public static void checkCurrencyRateBias(List<ExchangePage> pages,
                                             List<String> urls,
                                             String currencyName,
                                             String currencyType,
                                             Double bias) {

        List<List<Double>> collectResults = new ArrayList<>();

        for (ExchangePage page : pages) {
            Steps.goPage(
                    urls.stream()
                            .filter(x -> x.toLowerCase(Locale.ROOT)
                                    .contains(page.getBankName().toLowerCase(Locale.ROOT)))
                            .findFirst().get());

            page.preActions();

            collectResults.add(
                    page.getCurrencyRateListByOperation(currencyName, currencyType));
        }

        Assertions.assertFalse(collectResults.stream().anyMatch(x -> x.stream().anyMatch(w -> w >= bias)),
                "НЕ на всех сайтах банков, среди указанных, курс " + currencyName + " не превышает " + bias);
    }


    /**
     * Метод получения курсов валют от указанных банков в виде пары ключ-название банка, значение-курс валюты
     *
     * @param pages        страницы (классы) банков, по которым будем искать
     * @param urls         адреса сайтов банков, среди которых будем искать
     * @param currencyName валюта, кот. интересует (USD или EUR)
     * @param currencyType тип операции, кот. интересует (Банк покупает или Банк продает)
     * @return ключ-название банка, значение-курс валюты
     */
    private static Map<String, Double> getCollectedRates(List<ExchangePage> pages,
                                                         List<String> urls,
                                                         String currencyName,
                                                         String currencyType) {

        Map<String, Double> collectResults = new HashMap();

        for (ExchangePage page : pages) {
            Steps.goPage(
                    urls.stream()
                            .filter(x -> x.toLowerCase(Locale.ROOT)
                                    .contains(page.getBankName().toLowerCase(Locale.ROOT)))
                            .findFirst().get());

            collectResults.put(
                    page.getBankName(),
                    Steps.getCurrencyRate(page, currencyName, currencyType));
        }
        return collectResults;
    }


    /**
     * Метод получения отсортированного списка по всем полученным курсам валют
     *
     * @param collectedRates ключ - название банка, значение - курс валюты
     * @return отсортированный список в порядке возрастания с курсами валют по всем банкам
     */
    private static List<Double> getSortedRatesList(Map<String, Double> collectedRates) {

        List<Double> list = new ArrayList<>(collectedRates.values());
        Collections.sort(list);

        return list;
    }
}

