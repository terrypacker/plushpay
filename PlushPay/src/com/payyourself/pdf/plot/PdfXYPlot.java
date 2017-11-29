package com.payyourself.pdf.plot;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * PdfXYPlot is used to generate an X,Y plot of lines on a graph using X and Y points
 * supplied in the addDataset method.
 * @author Terry Packer
 *
 */
public class PdfXYPlot {
	
    /** A chart. */
    private JFreeChart chart;

    private int width;
    private int height;
    private String yAxisLabel;
    private String xAxisLabel;
    private String chartTitle;
	
    //************************
    // we will have to change this ...
    private PdfWriter writer;
	private PdfContentByte cb;
	
	
	private XYSeriesCollection dataset;
	private boolean invertXAxis;
    private ChartTheme currentTheme;
    
	
	/**
	 * Construct a PdfXYPlot object.  Use the addDataSet method to add data sets to the plot.
	 * Then use the generatePlot method to return an embeddable image in the PDF.
	 * @param chartTitle 
	 * @param height - height of chart in pts?
	 * @param width - width of chart in pts?
	 * @param xAxisLabel - units label for X axis
	 * @param yAxisLabel - units label for Y Axis
	 * @param writer - the PDF Writer that is writing the document, used to help generate the image
	 * 					note that nothing is written to the document with this class you get an image 
	 * 					back to use in the document.
	 */
    @Deprecated
	public PdfXYPlot(String chartTitle, int height, int width,
			String xAxisLabel, String yAxisLabel,
			PdfWriter writer) {
		super();
		this.chart = null;
		this.chartTitle = chartTitle;
		this.dataset = new XYSeriesCollection();
		this.height = height;
		this.width = width;
		this.writer = writer;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		//Will eventually move below code to separate method and use themes, not 
		// sure how to do this yet...
		//this.currentTheme = new TrafficLightTheme("TrafficLightXY");
	
		this.invertXAxis = false;
	}
	
	/**
	 * PdfXYPlot - Create a chart allowing use of background Color
	 * 
	 * @param chartTitle - String
	 * @param height - Height of plot
	 * @param width - Width of plot
	 * @param xAxisLabel - String label for plot
	 * @param yAxisLabel - String label for plot
	 * @param bgColor - Color of background
	 * @param writer - PdfWriter to use to generate plot
	 */
	@Deprecated
	public PdfXYPlot(String chartTitle, int height, int width,
			String xAxisLabel, String yAxisLabel,Color bgColor,
			PdfWriter writer) {
		super();
		this.chart = null;
		this.chartTitle = chartTitle;
		this.dataset = new XYSeriesCollection();
		this.height = height;
		this.width = width;
		this.writer = writer;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		//Will eventually move below code to separate method and use themes, not 
		// sure how to do this yet...
		//this.currentTheme = new TrafficLightTheme("TrafficLightXY",bgColor);
		this.invertXAxis = false;
		
	}


	/**
	 * Create a Pdf Chart using a content byte instead of a writer.
	 * 
	 * @param chartTitle
	 * @param height
	 * @param width
	 * @param xAxisLabel
	 * @param yAxisLabel
	 * @param bgColor
	 * @param cb - An already open ContentByte
	 */
	public PdfXYPlot(String chartTitle, int height, int width,
			String xAxisLabel, String yAxisLabel,Color bgColor,
			PdfContentByte cb) {
		super();
		this.chart = null;
		this.chartTitle = chartTitle;
		this.dataset = new XYSeriesCollection();
		this.height = height;
		this.width = width;
		this.writer = null;
		this.cb = cb;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		//Will eventually move below code to separate method and use themes, not 
		// sure how to do this yet...
		//.currentTheme = new TrafficLightTheme("TrafficLightXY",bgColor);
		this.invertXAxis = false;
		
	}
	
	/**
	 * Set boolean to invert the XAxis during @link {PdfXYPlot.generatePlot()}
	 * This is a toggle switch. Calling it twice will reset the value to its 
	 * original value.
	 * 
	 */
	public void invertXAxis(){
		this.invertXAxis = !this.invertXAxis;
	}

	/**
	 * Add a dataset to the plot.
	 * 
	 * @param xValues - array of doubles (a one to one relationship to the yValues array)
	 * @param yValues - array of doubles (a one to one relationship to the xValues array)
	 * @param seriesTitle - Title shown on plot
	 * @throws Exception
	 */
	public void addDataSet(double[] xValues, double[] yValues, String seriesTitle) throws Exception{
		
		if(xValues.length != yValues.length)
			throw new Exception("X and Y vectors are not the same length when adding data to plot!");
		XYSeries newSeries = new XYSeries(seriesTitle);
		int i = 0;
		while(i<xValues.length){
			newSeries.add(xValues[i],yValues[i]);
			i++;
		}
		this.dataset.addSeries(newSeries);
	}
	
	/**
	 * Add a dataset to the plot.
	 * Create a data set from a list, using the list index as the X value
	 * @param yValues
	 * @param seriesTitle
	 */
	public void addDataSet(List<Float> yValues,String seriesTitle){
		if(yValues == null)
			return;
		if(yValues.size() == 0)
			return;
		XYSeries newSeries = new XYSeries(seriesTitle);
		
		for(int i=0; i<yValues.size(); i++){
			newSeries.add(i,yValues.get(i));
		}
		this.dataset.addSeries(newSeries);
		
	}

	/**
	 * Add a dataset to the plot.
	 * Create a data set from a list, using the list index as the X value
	 * @param yValues - Hash Map
	 * @param seriesTitle
	 */
	public void addDataSet(HashMap<Integer,Float> yValues,String seriesTitle){
		if(yValues == null)
			return;
		if(yValues.size() == 0)
			return;
		XYSeries newSeries = new XYSeries(seriesTitle);
		
		Set<Integer> keySet = yValues.keySet();
		Iterator<Integer> iter = keySet.iterator();
		int key = 0;
		
		while(iter.hasNext()){
			key = (Integer) iter.next();
			
			newSeries.add(key, yValues.get(key));
		}
		
		this.dataset.addSeries(newSeries);
		
	}
	
	
	/**
	 * Add dataset from a List<Float> in reverse order, from end of list to start.
	 * @param yValues
	 * @param seriesTitle
	 */
	public void addDataSetReverse(List<Float> yValues,String seriesTitle){
		if(yValues == null)
			return;
		if(yValues.size() == 0)
			return;
		XYSeries newSeries = new XYSeries(seriesTitle);
		
		for(int i=yValues.size()-1; i>=0; i--){
			newSeries.add(i,yValues.get(i));
		}
		this.dataset.addSeries(newSeries);
		
	}

	
	/**
	 * Create a data set from a list, using the list index as the X value
	 * @param yValues
	 * @param seriesTitle
	 */
	public void addDataSet(List<Float> yValues,int xStartValue, String seriesTitle){
		if(yValues == null)
			return;
		if(yValues.size() == 0)
			return;
		XYSeries newSeries = new XYSeries(seriesTitle);
		
		for(int i=0; i<yValues.size(); i++){
			newSeries.add(i+xStartValue,yValues.get(i));
		}
		this.dataset.addSeries(newSeries);
		
	}

	/**
	 * Add a dataset that consists of a line at one y value, the set will have
	 * length number of entries
	 * @param value
	 * @param length
	 * @param seriesTitle
	 */
	public void addDataSet(double value,int length,String seriesTitle){
		if(length == 0)
			return;
		XYSeries newSeries = new XYSeries(seriesTitle);

		for(int i=0; i<length; i++){
			newSeries.add(i,value);
		}
		this.dataset.addSeries(newSeries);

	}
	
	
	/**
	 * Generate the plot using all previously set members. Don't forget to add data.
	 * 
	 * @return Image object for insertion into Itext Pdf document.
	 * @throws Exception if no data present in dataset member of class
	 */
	public Image generatePlot() throws Exception{
		if(this.dataset.getSeries() == null)
			throw new Exception("No Data to plot!");
        
        // create the chart...
        this.chart = this.createXYLineChart(
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
		
	/**
	 * Generate the plot using all previously set members. Don't forget to add data.
	 * 
	 * @return Image object for insertion into Itext Pdf document.
	 * @throws Exception if no data present in dataset member of class
	 */
	@Deprecated
	public Image printPlotToCB() throws Exception{
		
		if(this.dataset.getSeries() == null)
			throw new Exception("No Data to plot!");
        
        // create the chart...
        this.chart = this.createXYLineChart(
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

	
    public JFreeChart createXYLineChart(String title,
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
		NumberAxis xAxis = new NumberAxis(xAxisLabel);
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		//Are we to reverse the x axis?
		if(this.invertXAxis)
			xAxis.setInverted(true);
		
		
		NumberAxis yAxis = new NumberAxis(yAxisLabel);
		yAxis.setAutoRange(true);
		
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
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

}
