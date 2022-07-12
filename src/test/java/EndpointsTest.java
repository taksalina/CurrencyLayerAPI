import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class EndpointsTest {

    public static Response response;

    @Test
    public void listOfAvailableCurrenciesTest() {
        response = given().get(Constants.CURRENCY_LAYER_URL + Constants.LIST + "?" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("currencies.EUR", equalTo("Euro"));
        response.then().body("currencies.USD", equalTo("United States Dollar"));
        response.then().body("currencies.RUB", equalTo("Russian Ruble"));
        response.then().body("currencies.CAD", equalTo("Canadian Dollar"));
    }

    @Test
    public void liveConversionTest() {
        response = given().get(Constants.CURRENCY_LAYER_URL + Constants.LIVE + "?" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("quotes.USDEUR", equalTo(0.98175f));
        response.then().body("quotes.USDRUB", equalTo(64.9999f));
        response.then().body("quotes.USDILS", equalTo(3.459205f));
        response.then().body("quotes.USDCAD", equalTo(1.296805f));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "999999"})
    public void USDToCADConversionTest(String amount) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.CONVERT + "to=" + Constants.CAD + "&from=" + Constants.USD + "&amount=" + amount + "&" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("query.to", equalTo("CAD"));
        response.then().body("query.from", equalTo("USD"));
        response.then().body("query.amount", notNullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "9999999"})
    public void USDToEURBigSumConversionTest(String amount) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.CONVERT + "to=" + Constants.EUR + "&from=" + Constants.USD + "&amount=" + amount + "&" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("query.to", equalTo("EUR"));
        response.then().body("query.from", equalTo("USD"));
        response.then().body("query.amount", notNullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "9999999"})
    public void USDToRUBBigSumConversionTest(String amount) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.CONVERT + "to=" + Constants.RUB + "&from=" + Constants.USD + "&amount=" + amount + "&" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("query.to", equalTo("RUB"));
        response.then().body("query.from", equalTo("USD"));
        response.then().body("query.amount", notNullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "9999999"})
    public void USDToILSBigSumConversionTest(String amount) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.CONVERT + "to=" + Constants.ILS + "&from=" + Constants.USD + "&amount=" + amount + "&" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("query.to", equalTo("ILS"));
        response.then().body("query.from", equalTo("USD"));
        response.then().body("query.amount", notNullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000-01-01", "2021-07-07", "2011-07-21"})
    public void historicalConversionTest(String date) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.HISTORICAL + "?date=" + date + "&" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("historical", equalTo(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1991-01-01", "1981-07-07", "1961-07-21"})
    public void historicalConversionWrongQueryErrTest(String date) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.HISTORICAL + "?date=" + date + "&" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("error.code", equalTo(106));
        response.then().body("success", equalTo(false));
        response.then().body("error.info", containsString("Your query did not return any results."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000-01-01", "2021-07-07", "2011-07-21"})
    public void historicalConversionCadEurRubIlsTest(String date) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.HISTORICAL + "?date=" + date + "&" + Constants.TOKEN + "&source=" + Constants.USD + Constants.CURRENCIES + "EUR,CAD,RUB,ILS");
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("historical", equalTo(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CAD", "EUR", "RUB", "ILS"})
    public void timeFrameConversionTest(String currencies) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.TIMEFRAME + "?" + Constants.STARTDATE + "2021-07-07" + "&" + Constants.ENDDATE + "2022-07-07" + "&" + Constants.TOKEN + Constants.CURRENCIES + currencies);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("start_date", equalTo("2021-07-07"));
        response.then().body("end_date", equalTo("2022-07-07"));
        response.then().body("source", equalTo("USD"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CAD", "EUR", "RUB", "ILS"})
    public void longTimeFrameErrMessageTest(String currencies) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.TIMEFRAME + "?" + Constants.STARTDATE + "1991-07-07" + "&" + Constants.ENDDATE + "2022-07-07" + "&" + Constants.TOKEN + Constants.CURRENCIES + currencies);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(505));
        response.then().body("error.info", containsString("The Time-Frame you entered is too long."));
    }

    @Test
    public void invalidCurrencyCodeErrMessageTest() {
        response = given().get(Constants.CURRENCY_LAYER_URL + Constants.LIVE + "?" + Constants.TOKEN + "&source=" + Constants.USD + Constants.CURRENCIES + "=DAC");
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(202));
        response.then().body("error.info", containsString("You have provided one or more invalid Currency Codes"));
    }

    @Test
    public void invalidStartDataTimeframeErrMessageTest() {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.TIMEFRAME + "?" + Constants.STARTDATE + "07072020" + "&" + Constants.ENDDATE + "2022-07-07" + "&" + Constants.TOKEN + Constants.CURRENCIES + "CAD,EUR,RUB,ILS");
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(502));
        response.then().body("error.info", containsString("You have specified an invalid start date."));
    }

    @Test
    public void invalidEndDataTimeframeErrMessageTest() {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.TIMEFRAME + "?" + Constants.STARTDATE + "2021-07-09" + "&" + Constants.ENDDATE + "07-07" + "&" + Constants.TOKEN + Constants.CURRENCIES + "CAD,EUR,RUB,ILS");
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(503));
        response.then().body("error.info", containsString("You have specified an invalid end date."));
    }

    @Test
    public void wrongDateFormatErrMessageTest() {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.HISTORICAL + "?date=23-07-08" + "&" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(302));
        response.then().body("error.info", containsString("You have entered an invalid date."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", ".", "", " "})
    public void invalidAmountErrMessageTest(String amount) {
        response = given().contentType("application/json").get(Constants.CURRENCY_LAYER_URL + Constants.CONVERT + "to=" + Constants.CAD + "&from=" + Constants.USD + "&amount=" + amount + "&" + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(false));
        response.then().body("error.code", equalTo(403));
        response.then().body("error.info", containsString("You have not specified an amount to be converted"));
    }
}
