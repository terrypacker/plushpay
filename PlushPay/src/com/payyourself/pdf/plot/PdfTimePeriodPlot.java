package com.payyourself.pdf.plot;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;



import com.lowagie.text.Image;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;


public class PdfTimePeriodPlot {
	
    /** A chart. */
    private JFreeChart chart;

    private int width;
    private int height;
    private String yAxisLabel;
    private String xAxisLabel;
    private String chartTitle;

	private TimePeriodValuesCollection dataset;
    private ChartTheme currentTheme;
    private String dateFormat; //A format used in SimpleDateFormat to display dates.
    private DateAxis xAxis;
	private PdfContentByte cb;
    
	
	
	public PdfTimePeriodPlot(String chartTitle, int height, int width,
			String xAxisLabel, String yAxisLabel, String dateFormat,Color bgColor,
		PdfContentByte cb) {
		super();
		this.chart = null;
		this.chartTitle = chartTitle;
		this.dataset = new TimePeriodValuesCollection();
		this.height = height;
		this.width = width;
		this.cb = cb;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		//Will eventually move below code to separate method and use themes, not 
		// sure how to do this yet...
		this.currentTheme = new TrafficLightTheme("TrafficLightXY",bgColor);
		this.dateFormat = dateFormat;
		this.xAxis = new DateAxis();
        this.xAxis.setDateFormatOverride(new SimpleDateFormat(this.dateFormat));

		
	}


	/**
	 * Add a time series to the plot.  The series is created using times in the form
	 * of Long (milli since epoch) in a list. p1,p2,p3...pN Period1 = p2-p1
	 * The yValue is defined as y1,y2,y3...yN. Where value for Period1 = y1 ,Period2 = y2
	 * @param timeData
	 * @param yData
	 * @param title
	 */
	public void addTimeSeries(List<Long> timeData, List<Integer> yData, String title){
	    TimePeriodValues v1 = new TimePeriodValues(title);
	    SimpleTimePeriod period = null;
	    Date d1 = null;
	    Date d2 = null;
	    //Starting at 2nd entry, set the entries
	    for(int i=1; i<timeData.size(); i++){
	    	d1 = new Date(timeData.get(i-1));
	    	d2 = new Date(timeData.get(i));
	    	period = new SimpleTimePeriod(d1,d2);
	    	v1.add(period, yData.get(i-1));
	    }
	    this.dataset.addSeries(v1);
 	}

	public void addTimeSeriesFloat(List<Long> timeData, List<Float> yData, String title, int emissionID){
	    TimePeriodValues v1 = new TimePeriodValues(title);
	    SimpleTimePeriod period = null;
	    Date d1 = null;
	    Date d2 = null;
	    //Starting at 2nd entry, set the entries
	    for(int i=1; i<timeData.size(); i++){
	    	d1 = new Date(timeData.get(i-1));
	    	d2 = new Date(timeData.get(i));
	    	
	    	/* for testing - turns out it was in base query Time needs to be set to HH not hh (which converts 12 to 00)
	    	 * if(d2.compareTo(d1)<0){
	    	
	    		System.out.println(i);
	    		System.out.println("EmissionID: "+ emissionID);
	    		System.out.println("date 1: "+d1);
	    		System.out.println("date 2: "+d2);
	    		
	    		
	    	}
	    	 */
	    	
	    	
	    	period = new SimpleTimePeriod(d1,d2);
	    	Float y = yData.get(i-1);
	    	v1.add(period, y.intValue());
	    }
	    this.dataset.addSeries(v1);
 	}
	
	
	
	/**
	 * Generate the plot using all previously set members. Don't forget to add data.
	 * 
	 * @return Image object for insertion into Itext Pdf document.
	 * @throws Exception if no data present in dataset member of class
	 */
	public Image generatePlot() throws Exception{
		
	
		if(this.dataset == null)
			throw new Exception("No Data to plot!");
        
        // create the chart...
        this.chart = this.createTimePeriodChart(
            this.chartTitle,  // chart title
            this.xAxisLabel,
            this.yAxisLabel,
            this.dataset,         // data
            PlotOrientation.VERTICAL,
            true,            // include legend
            false,            // tooltips
            false            // urls
        );

     
        
	    //PdfContentByte cb = this.writer.getDirectContent();
	    PdfTemplate tp = this.cb.createTemplate(this.width, this.height);
	    java.awt.Graphics2D g2 = tp.createGraphics(this.width, this.height, new DefaultFontMapper());	   
	    Image graphImg = Image.getInstance(tp);
	    if(this.chart == null){
	    	throw new Exception("Chart Data Not Defined!");
	    }
	    this.chart.draw(g2, new Rectangle2D.Float(0, 0, this.width, this.height),null,
	                null);
	    //cb.addTemplate(tp, 0, 0);
	    g2.dispose();

		
		return graphImg;
	}
		

	
    public JFreeChart createTimePeriodChart(String title,
            String xAxisLabel,
            String yAxisLabel,
            XYDataset dataset,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls) 
    {

		if (orientation == null) {
			throw new IllegalArgumentException("Null 'orientation' argument.");
		}
		
		
		
		NumberAxis yAxis = new NumberAxis(yAxisLabel);
		
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		XYPlot plot = new XYPlot(dataset, this.xAxis, yAxis, renderer);
		
		plot.setOrientation(orientation);
		
		if (tooltips) {
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		}
		
		if (urls) {
			renderer.setURLGenerator(new StandardXYURLGenerator());
		}
		
		Font font = new Font("Serif", Font.PLAIN, 10);		
		JFreeChart chart = new JFreeChart(title, font,plot, legend);
		
		
	
		this.currentTheme.apply(chart);
		
		return chart;
    }
    
    
    /**
     * Set scale to monthly along x axis (overrides the initial date format)
     */
    public void setTimeScaleToMonth(){
    	// set the tick size to one week, with formatting...
    	DateFormat formatter = new SimpleDateFormat("MMM-yyyy");
    	DateTickUnit unit = new DateTickUnit(DateTickUnit.MONTH, 1, formatter);
    	this.xAxis.setTickUnit(unit);
    }

    /**
     * Set scale to week, override initial date format of constructor
     */
    public void setTimeScaleToWeek(){
    	// set the tick size to one week, with formatting...
    	DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy");
    	DateTickUnit unit = new DateTickUnit(DateTickUnit.DAY, 7, formatter);
    	this.xAxis.setTickUnit(unit);
    	
    }
    
    
    /**
     * Set Scale to day, override initial date format of constructor.
     */
    public void setTimeScaleToDay(){
    	// set the tick size to one week, with formatting...
    	DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy");
    	DateTickUnit unit = new DateTickUnit(DateTickUnit.DAY, 1, formatter);
    	this.xAxis.setTickUnit(unit);
    	
    }


	/**
	 * @param cb the cb to set
	 */
	public void setCb(PdfContentByte cb) {
		this.cb = cb;
	}


	/**
	 * @return the cb
	 */
	public PdfContentByte getCb() {
		return cb;
	}
}