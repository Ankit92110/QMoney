
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.parser.ParseException;
import org.springframework.web.client.RestTemplate;

class ClosingPriceComparator implements Comparator<TotalReturnsDto>{
   @Override
   public int compare(TotalReturnsDto t1,TotalReturnsDto t2){
      if(t1.getClosingPrice()>t2.getClosingPrice()) return 1;
      else if(t1.getClosingPrice()<t2.getClosingPrice()) return -1;
      return 0;
   }

   
}

class annualListComparator implements Comparator<AnnualizedReturn>{
  @Override
  public int compare(AnnualizedReturn t1,AnnualizedReturn t2){
     if(t1.getAnnualizedReturn()==t2.getAnnualizedReturn()) return 0;
     else if(t1.getAnnualizedReturn()<t2.getAnnualizedReturn()) return 1;
     return -1;
  }
}
public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Task:
  //       - Read the json file provided in the argument[0], The file is available in the classpath.
  //       - Go through all of the trades in the given file,
  //       - Prepare the list of all symbols a portfolio has.
  //       - if "trades.json" has trades like
  //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  //  Hints:
  //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  //       Check if they are of any help to you.
  //    2. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
   File f= resolveFileFromResources(args[0]);
    ObjectMapper objectMapper=getObjectMapper();
    PortfolioTrade[] trades=objectMapper.readValue(f, PortfolioTrade[].class);
    List<String> ls1 = new ArrayList<String>();
    for(PortfolioTrade trade:trades){
       ls1.add(trade.getSymbol());
    }
     return ls1;
    
  }


  // My function to return list of trades
  public static List<PortfolioTrade> getListOfPortfolioTrades(String[] args) throws IOException, URISyntaxException {
   File f= resolveFileFromResources(args[0]);
    ObjectMapper objectMapper=getObjectMapper();
    PortfolioTrade[] trades=objectMapper.readValue(f, PortfolioTrade[].class);
    return Arrays.asList(trades);
     
    
  }

  // my function to get closing price
  public static double getClosingPrice(TiingoCandle[] tingoCandles){
   
    return tingoCandles[tingoCandles.length-1].getClose();
    
  }
  





  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.














  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/ankit-mishra-criodo-ME_QMONEY_V2/qmoney/src/main/resources/trades.json";
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@6150c3ec";
     String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
     String lineNumberFromTestFileInStackTrace = "29";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }
   //My method//
  public static String getToken(){
   // return "0ebb5832b9e1624cc372710e2355810f10fea3cb";
    return "025746b248803c0b2eda7f8c3c3ef797eea121e0";
  }

   //My method end

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws  IOException ,URISyntaxException{
   List<PortfolioTrade> trades = new ArrayList<PortfolioTrade>();
  //  trades= getListOfPortfolioTrades(args);
   trades=readTradesFromJson(args[0]);
   //convert String to LocalDate
   LocalDate endDate = LocalDate.parse(args[1]);
   String token=getToken();
   List<TotalReturnsDto>totalReturnsDtos=new ArrayList<>();
   for(PortfolioTrade trade:trades){
      String url=prepareUrl(trade, endDate, token);
      RestTemplate restTemplate=new RestTemplate();
      TiingoCandle tiingoCandle[] = restTemplate
      .getForObject(url , TiingoCandle[].class);

      double closing_price=getClosingPrice(tiingoCandle);
      totalReturnsDtos.add(new TotalReturnsDto(trade.getSymbol(), closing_price));

   }

   Collections.sort(totalReturnsDtos,new ClosingPriceComparator());
   
    /*https://api.tiingo.com/tiingo/daily/aapl/prices?startDate=2019-01-02&endDate=2019-01-06&token=025746b248803c0b2eda7f8c3c3ef797eea121e0 */
    
    /*Foo foo = restTemplate
  .getForObject(fooResourceUrl + "/1", Foo.class);
Assertions.assertNotNull(foo.getName());
Assertions.assertEquals(foo.getId(), 1L);
 */
List<String> ls1 = new ArrayList<String>();
for(TotalReturnsDto t:totalReturnsDtos){
   ls1.add(t.getSymbol());
}
 return ls1;
    
    // return Collections.emptyList();
  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    File f= resolveFileFromResources(filename);
    ObjectMapper objectMapper=getObjectMapper();
    PortfolioTrade[] trades=objectMapper.readValue(f, PortfolioTrade[].class);
    return Arrays.asList(trades);
    
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
     String url="https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices?startDate="+trade.getPurchaseDate()+"&endDate="+endDate+"&token="+token;
     return url;
  }
  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    
     return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     int n=candles.size()-1;
     return candles.get(n).getClose();

     
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {

    String url=prepareUrl(trade, endDate, token);
    RestTemplate restTemplate=new RestTemplate();
    // Candle candles[] = restTemplate
    // .getForObject(url , Candle[].class);

    TiingoCandle tiingoCandle[] = restTemplate
    .getForObject(url , TiingoCandle[].class);
    
    //List<Candle> candles=new ArrayList<TiingoCandle>();

    return Arrays.asList(tiingoCandle);
    //return Collections.emptyList();

  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
       List<AnnualizedReturn>annualList=new ArrayList<>();
       List<PortfolioTrade> trades = new ArrayList<PortfolioTrade>();
        trades=readTradesFromJson(args[0]);
        LocalDate endDate = LocalDate.parse(args[1]);
        String token=getToken();
        for(PortfolioTrade trade:trades){
          List<Candle>candles= fetchCandles(trade,endDate,token);
             annualList.add(calculateAnnualizedReturns(endDate,trade,getOpeningPriceOnStartDate(candles),getClosingPriceOnEndDate(candles)));
        }

        Collections.sort(annualList,new annualListComparator());
     return annualList;
    // return Collections.emptyList();
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        LocalDate startDate=trade.getPurchaseDate();
        double year = startDate.until(endDate, ChronoUnit.DAYS)/365.24;
      

      double  totalReturn = (sellPrice - buyPrice) / buyPrice;
      double  annualized_returns=Math.pow(1+totalReturn,1/ year)-1;

     
        
        return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
      
  }






















  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
/*my code */
private static File readFileAsString(String filename) throws URISyntaxException {
  return Paths.get(
      Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
}
/*my code */
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
      //  String file = args[0];
        //LocalDate endDate = LocalDate.parse(args[1]);
      //  file contents = readFileAsString(file);
      //  ObjectMapper objectMapper = getObjectMapper();
      //  return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
      LocalDate endDate = LocalDate.parse(args[1]);
      File f= resolveFileFromResources(args[0]);
      ObjectMapper objectMapper=getObjectMapper();
      PortfolioTrade[] portfolioTrades=objectMapper.readValue(f, PortfolioTrade[].class);
      PortfolioManager portfolioManager=PortfolioManagerFactory.getPortfolioManager(new RestTemplate());
      return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }











  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadFile(args));


    printJsonObject(mainReadQuotes(args));



    printJsonObject(mainCalculateSingleReturn(args));




    printJsonObject(mainCalculateReturnsAfterRefactor(args));



  }
}

