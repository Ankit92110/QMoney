
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;

import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

/*my code */
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.client.RestTemplate;
/*my code end */
public class AlphavantageService implements StockQuotesService {

  private RestTemplate restTemplate;
  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.
  class StartDateComparator implements Comparator<AlphavantageCandle>{
    @Override
    public int compare(AlphavantageCandle t1,AlphavantageCandle t2){
       if(t1.getDate().isBefore(t2.getDate()))return -1;
       return 1;
    }  
 }

  public static String getToken(){
    return "E64ORN8I6UES4SSO";
  }
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

   // https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=IBM&outputsize=full&apikey=<your_API_key>
    String url="https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="+symbol+"&outputsize=full&apikey="+getToken();
    return url;
   
}
 @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws StockQuoteServiceException {

        List<Candle> ans = new ArrayList<>();

      try{

        String url=buildUri(symbol,from,to);
        // AlphavantageCandle alphavantageCandle[] = restTemplate
        // .getForObject(url , AlphavantageDailyResponse.class).getCandles();
        // return Arrays.asList(alphavantageCandle);

        Map<LocalDate, AlphavantageCandle> candles   = restTemplate
        .getForObject(url , AlphavantageDailyResponse.class).getCandles();
        
        List<AlphavantageCandle> alphavantageCandleslist=new ArrayList<>();
        for (Map.Entry<LocalDate, AlphavantageCandle> entry : candles.entrySet()){
          // System.out.println("Key = " + entry.getKey() +
          // ", Value = " + entry.getValue());
          // check the date is between start date and end date or not
          if(entry.getKey().isAfter(from.minusDays(1)) && entry.getKey().isBefore(to.plusDays(1)))
          {
           
              AlphavantageCandle t=new AlphavantageCandle();
              t.setOpen(entry.getValue().getOpen());
              t.setClose(entry.getValue().getClose());
              t.setHigh(entry.getValue().getHigh());
              t.setLow(entry.getValue().getLow());
              t.setDate(entry.getKey());
              alphavantageCandleslist.add(t);

          }
        } 
        
        // return tiingoCandleslist.stream().sorted(Comparator.comparing(Candle::getDate)).collect(Collectors.toList());
         Collections.sort(alphavantageCandleslist,new StartDateComparator());
       
        for(AlphavantageCandle val : alphavantageCandleslist){
          ans.add(val);
        }

      }catch(Exception e){
        // System.out.println(e.getMessage());
        throw new StockQuoteServiceException(e.getMessage());
      }

      //  return List.of(tiingoCandleslist);
         return ans;
        
  }
   
 
  

  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //   1. Update the method signature to match the signature change in the interface.
  //   2. Start throwing new StockQuoteServiceException when you get some invalid response from
  //      Alphavantage, or you encounter a runtime exception during Json parsing.
  //   3. Make sure that the exception propagates all the way from PortfolioManager, so that the
  //      external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF

}

