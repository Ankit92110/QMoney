
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  RestTemplate restTemplate;

  StockQuotesService s;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  
  protected PortfolioManagerImpl(StockQuotesService s){
    this.s=s;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF

  public static String getToken(){
    return "0ebb5832b9e1624cc372710e2355810f10fea3cb";
  }

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.
  class annualListComparator implements Comparator<AnnualizedReturn>{
    @Override
    public int compare(AnnualizedReturn t1,AnnualizedReturn t2){
       if(t1.getAnnualizedReturn()==t2.getAnnualizedReturn()) return 0;
       else if(t1.getAnnualizedReturn()<t2.getAnnualizedReturn()) return 1;
       return -1;
    }
  }

  private Double getOpeningPriceOnStartDate(List<Candle> candles) {
    
    return candles.get(0).getOpen();
 }

 public  Double getClosingPriceOnEndDate(List<Candle> candles) {
    int n=candles.size()-1;
    return candles.get(n).getClose();
  
 }
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        String url=buildUri(symbol,from,to);
        TiingoCandle tiingoCandle[] = restTemplate
        .getForObject(url , TiingoCandle[].class);
        return Arrays.asList(tiingoCandle);
  }

  /*my code */
  public  List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
  LocalDate endDate) throws  StockQuoteServiceException{
    List<AnnualizedReturn>annualList=new ArrayList<AnnualizedReturn>();
    for(PortfolioTrade trade:portfolioTrades){
      List<Candle> candles=new ArrayList<>();
      try {
        candles = s.getStockQuote(trade.getSymbol(),trade.getPurchaseDate(),endDate);
         annualList.add(calculateAnnualizedReturns(endDate,trade,getOpeningPriceOnStartDate(candles),getClosingPriceOnEndDate(candles)));
    
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
        throw new StockQuoteServiceException(e.getMessage());
      }
        }

    Collections.sort(annualList,getComparator());
 return annualList;

}

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        LocalDate startDate=trade.getPurchaseDate();
        double year = startDate.until(endDate, ChronoUnit.DAYS)/365.24;
      double  totalReturn = (sellPrice - buyPrice) / buyPrice;
      double  annualized_returns=Math.pow(1+totalReturn,1/ year)-1;
        return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
      
  }

  /*my code end */
 
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {


            String url="https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate+"&endDate="+endDate+"&token="+getToken();
            return url;
  }

 
 




  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

 // My conde Module 8

 public AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endLocalDate) throws StockQuoteServiceException{
  String symbol = trade.getSymbol();

  Double buyPrice=0.0, sellPrice =0.0;
  try {
    LocalDate startLocalDate = trade.getPurchaseDate();
    List<Candle> stockEndToFull = s.getStockQuote(symbol, startLocalDate, endLocalDate);
    Collections.sort(stockEndToFull,(Candle1,Candle2)-> {return Candle1.getDate().compareTo(Candle2.getDate());});

    Candle stockStartDate = stockEndToFull.get(0);
    Candle stockLastDate = stockEndToFull.get(stockEndToFull.size()-1);
    buyPrice =stockStartDate.getOpen();
    sellPrice = stockLastDate.getClose();
    endLocalDate = stockLastDate.getDate();

  } catch (JsonProcessingException e) {
    //TODO: handle exception
    e.printStackTrace();
  }

  double totalReturn = (sellPrice-buyPrice)/buyPrice;
  double numDays = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endLocalDate);
  double annualizedReturn =Math.pow((1+totalReturn), (1/(numDays/365)))-1;

 return new AnnualizedReturn(symbol,annualizedReturn,totalReturn);
}

 public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
    List<PortfolioTrade> portfolioTrades,
    LocalDate endDate, int numThreads) throws InterruptedException,
    StockQuoteServiceException{

      List<AnnualizedReturn> annualizedReturn = new ArrayList<AnnualizedReturn>();
  
    List<Future<AnnualizedReturn>> annReturn = new ArrayList<Future<AnnualizedReturn>>();
    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);

    for(int i =0; i<portfolioTrades.size();i++){
      PortfolioTrade trade = portfolioTrades.get(i);

      Callable <AnnualizedReturn> callableTask = () ->{return getAnnualizedReturn( trade,endDate);};
      Future<AnnualizedReturn> futureReturns = threadPool.submit(callableTask);
      annReturn.add(futureReturns);

    }


    for(int i = 0;i<portfolioTrades.size();i++){
      Future<AnnualizedReturn> futureReturns = annReturn.get(i);

      try{
        AnnualizedReturn returns = futureReturns.get();
        annualizedReturn.add(returns);
      }catch(ExecutionException e){
        throw new StockQuoteServiceException("Error while calling api",e);
        // e.printStackTrace();
      }

    }

    Collections.sort(annualizedReturn,getComparator());

    return annualizedReturn;

  
    }

   

    
}



