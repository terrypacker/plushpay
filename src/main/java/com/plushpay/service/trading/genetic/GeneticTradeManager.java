package com.plushpay.service.trading.genetic;

import com.plushpay.repository.trade.Trade;
import com.plushpay.repository.trade.TradeStatus;
import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.trader.TraderStatus;
import com.plushpay.repository.tradergroup.TraderGroup;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.trade.TradeService;
import com.plushpay.service.trading.tradeGenerator.TradeEmailGenerator;
import com.plushpay.service.trader.TraderService;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeneticTradeManager extends Thread {

    private static final String CSV_HEADER = "Algo Interval,ROI,Gain,Cost,Number Buyers,NumberSellers,Fitness,Evolution Time,Population Size\n";
    private Log log = LogFactory.getLog(getClass());
    private static final int DESTROY_COUNT = 100;

    private Population population;


    private List<Trader> currentSellers;
    private List<Trader> currentBuyers;

    private volatile boolean shutdown;


    private boolean writeToFile;


    private BufferedWriter paramOutputFile;


    private long minGain;


    private long maxCost;

    private long lastEvolutionTime;

    private int apocalypse;

    private final TradeService tradeService;
    private final TraderService traderService;

    /**
     * Empty Constructor for bean use.
     */
    public GeneticTradeManager(TradeService tradeService, TraderService traderService) {
        super("Genetic Trade Manager");
        this.tradeService = tradeService;
        this.traderService = traderService;
        this.reset();
    }

    /**
     *
     */
    public void run() {

        this.log.debug("Starting Genetic Trade Manager.");

        //Setup write to CSV
        if (this.writeToFile) {
            String temp = null;
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                temp = loader.getResource("com").toURI().getPath();
                temp = temp + "../../../csvfiles/";

                Calendar now = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");

                FileWriter writer = new FileWriter(
                    temp + sdf.format(now.getTime()) + "paramData.csv");
                this.paramOutputFile = new BufferedWriter(writer);

                //Write the header line
                this.paramOutputFile.write(GeneticTradeManager.CSV_HEADER);

            } catch (URISyntaxException e) {
                //We have a real problem
                this.log.debug("Unable to Start Writing Param Data.", e);
            } catch (IOException e) {
                this.log.debug("Unable to Start Writing Param Data.", e);
            }

        }

        this.reset(); //Setup the initial params

        while (!this.shutdown) {

            //Need a way to determine if we need to rebuild the tree and adjust tuning params.
            //this.resetTree();

            //THEN CROP for best results.
            try {
                this.evolveSolution();
            } catch (Exception e1) {
                this.log.error("Problem inserting trader!", e1);
            }

            //Never creating trades for now.
            Creature best = this.foundTrade();
            if (best != null) {

                this.log.debug(
                    "Creating Trade: Gain of " + best.getGain() + " Cost of " + best.getCost());

                //TradeLink link = new TradeLink();
                //link.setBuyerBuyCurrencyMismatch(Math.abs(best.getBuyers().getCurrencyToBuy().getValue()-best.getSellers().getCurrencyToSell().getValue()));
                //link.setBuyerSellCurrencyMismatch(Math.abs(best.getBuyers().getCurrencyToSell().getValue()-best.getSellers().getCurrencyToBuy().getValue()));
                //link.setBuyers(buyersGroup);

                /*Remove the 0 value trader AND set all buyers status to DEPOSIT*/
                for (int i = 0; i < best.getBuyers().size(); i++) {
                    //Set all trader status to DEPOSIT, awaiting deposit.
                    best.getBuyers().get(i).setStatus(TraderStatus.DEPOSIT);

                }

                //Create Buyers Group
                TraderGroup buyersGroup = best.getBuyerAsTraderGroup();


                /*Remove the 0 value trader AND set sellers status to DEPOST*/
                for (int i = 0; i < best.getSellers().size(); i++) {
                    best.getSellers().get(i).setStatus(TraderStatus.DEPOSIT);

                }

                //Create the sellers Group
                TraderGroup sellersGroup = best.getSellersAsTraderGroup();


                /*Create Trade In DB*/
                Trade newTrade = new Trade();
                newTrade.setBuyerGroup(buyersGroup);
                newTrade.setSellerGroup(sellersGroup);
                newTrade.setDateCreated(Calendar.getInstance());
                newTrade.setStatus(TradeStatus.DEPOSIT); //Set the trade status
                //Should set the expiry date too

                //Compute the profit and log it
                long buyerProfit =
                    buyersGroup.getCurrencyToSell().getValue() - sellersGroup.getCurrencyToBuy()
                        .getValue();
                long sellerProfit =
                    sellersGroup.getCurrencyToSell().getValue() - buyersGroup.getCurrencyToBuy()
                        .getValue();
                this.log.debug(
                    "Trade Profits: " + buyerProfit + buyersGroup.getCurrencyToSell().getType()
                        .getCode() +
                        " " + sellerProfit + sellersGroup.getCurrencyToSell().getType().getCode());

                newTrade = tradeService.save(newTrade).get();

                //WRite an empty line to the file to indicate a trade was made

                //Notify the members of this trade that they need to put their funds into the Bank
                this.log.debug("Sending Trade Emails");
                TradeEmailGenerator genEmail = new TradeEmailGenerator(newTrade);
                genEmail.sendEmails();

                //link.setSellers(sellersGroup);

                //Add to existing trade chain or create new

                this.reset();

            } else {//end if we got a trade.
                if (this.apocalypse == GeneticTradeManager.DESTROY_COUNT) {
                    this.reset();
                } else {
                    this.apocalypse++;
                }
                //No Trade found this iteration
            }//end else if we got a trade

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

                if (this.shutdown) {
                    break;
                }
                this.log.error("Interrupted while sleeping.", e);
            }//end catch
        }//end while

        if (this.writeToFile) {
            try {
                this.paramOutputFile.flush();
                this.paramOutputFile.close();
            } catch (IOException e1) {
                this.log.error("Unable to close logfile.", e1);
            }
        }

        this.log.info("Shutting Down.");

    }


    private void writeStats(Creature best) throws IOException {

        if (this.writeToFile) {
            String data =
                this.apocalypse + "," + best.getRoi() + "," + best.getGain() + "," + best.getCost()
                    + "," +
                    +best.getBuyers().size() + "," +
                    best.getSellers().size() + ","
                    + best.getFitness() + "," + this.lastEvolutionTime + ","
                    + this.population.size() + "\n";
            this.paramOutputFile.write(data);
            this.paramOutputFile.flush();
        }

    }

    /**
     * Evolve the solution using the available traders
     *
     * @throws
     */
    private void evolveSolution() {

        this.population.addAvailableBuyers(this.getNewBuyers());
        this.population.addAvaiableSellers(this.getNewSellers());

        try {
            long now = System.nanoTime();
            this.population.evolve();
            this.lastEvolutionTime = System.nanoTime() - now;
        } catch (Exception e) {
            this.log.error("Evolution Failed: ", e);
        }

        this.log.info("Population Size: " + this.population.size());


    }

    private Creature foundTrade() {

        Creature best = this.population.getBest();

        if (best == null) {
            return null;
        }

        try {
            this.writeStats(best);
        } catch (IOException e) {
            this.log.error(e);
        }

        this.log.info(
            "Best Gain at Interval (" + this.apocalypse + "):" + best.getGain() + " Best Cost: "
                + best.getCost() + " Best Fitness: " + best.getFitness());

        if ((best.getCost() < this.maxCost) && (best.getGain() > this.minGain)) {
            return best;
        } else {
            return null;
        }

    }

    private void reset() {

        this.log.info("Resetting the population.");

        this.currentBuyers = new ArrayList<Trader>();
        this.currentSellers = new ArrayList<Trader>();

        this.population = new Population(10000);
        this.minGain = 100000;
        this.maxCost = 10000;
        this.apocalypse = 0; //Reset the impending doom for all creatures.
        this.writeToFile = true; //Log the operation

    }

    /**
     * Collect sellers from DB that are not already in the tree
     *
     * @return
     */
    private List<Trader> getNewSellers() {
        //Load in all free sellers of this currency Type and lock them
        List<Trader> newSellers = traderService.getFreeTradersExcept(CurrencyCodeEnum.AUD,
            CurrencyCodeEnum.USD, currentSellers).toList();
        this.log.debug("Adding " + newSellers.size() + " Sellers.");
        this.currentSellers.addAll(newSellers);
        return newSellers;
    }

    /**
     * Collect buyers from the DB that are not already in the tree
     *
     * @return
     */
    private List<Trader> getNewBuyers() {
        List<Trader> newBuyers = traderService.getFreeTradersExcept(CurrencyCodeEnum.USD,
            CurrencyCodeEnum.AUD, this.currentBuyers).toList();
        this.log.debug("Adding " + newBuyers.size() + " Buyers.");
        this.currentBuyers.addAll(newBuyers);
        return newBuyers;
    }


    /**
     * Shutdown of matcher
     */
    public void shutDown() {
        this.shutdown = true;
        this.interrupt();
    }

    public void startUp() {
        // TODO Check thread state before starting
        this.shutdown = false;
        this.start();

    }


}
