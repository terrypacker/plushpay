package com.payyourself.pdf.plot;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

/**
 * PdfXYPlot is used to generate an X,Y plot of lines on a graph using X and Y points
 * supplied in the addDataset method.
 * @author Terry Packer
 *
 */
public class PdfBarPlot {
	
    /** A chart. */
    private JFreeChart chart;

    private int width;
    private int height;
    private String yAxisLabel;
    private String xAxisLabel;
    private String chartTitle;
	private PdfWriter writer;
	private DefaultCategoryDataset dataset;
	
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
	public PdfBarPlot(String chartTitle, int height, int width,
			String xAxisLabel, String yAxisLabel, Color bgColor,
			PdfWriter writer) {
		super();
		this.chart = null;
		this.chartTitle = chartTitle;
		this.dataset = new DefaultCategoryDataset();
		this.height = height;
		this.width = width;
		this.writer = writer;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		//Will eventually move below code to separate method and use themes, not 
		// sure how to do this yet...
		this.currentTheme = new TrafficLightTheme("TrafficLightBar",bgColor);
		
	}

	public void addCategory(String series, String category, double value) throws Exception{
		this.dataset.addValue(value, series, category);
	}
		
	/**
	 * Generate the plot using all previously set members. Don't forget to add data.
	 * 
	 * @return Image object for insertion into Itext Pdf document.
	 * @throws Exception if no data present in dataset member of class
	 */
	public Image generatePlot() throws Exception{
		
	
		if(this.dataset.getColumnCount() == 0)
			throw new Exception("No Data to plot!");
        
        // create the chart...
        this.chart = this.createBarChart(
            this.chartTitle,  // chart title
            this.xAxisLabel,
            this.yAxisLabel,
            this.dataset,         // data
            PlotOrientation.VERTICAL,
            true,            // include legend
            false,            // tooltips
            false            // urls
        );

     
        
	    PdfContentByte cb = this.writer.getDirectContent();
	    PdfTemplate tp = cb.createTemplate(this.width, this.height);
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
		

	
    public JFreeChart createBarChart(String title,
            String categoryAxisLabel,
            String valueAxisLabel,
            CategoryDataset dataset,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls) {

	if (orientation == null) {
		throw new IllegalArgumentException("Null 'orientation' argument.");
	}
	CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
	ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
	
	BarRenderer renderer = new BarRenderer();
	if (orientation == PlotOrientation.HORIZONTAL) {
		ItemLabelPosition position1 = new ItemLabelPosition(
			ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT);
		renderer.setBasePositiveItemLabelPosition(position1);
		ItemLabelPosition position2 = new ItemLabelPosition(
			ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_RIGHT);
		renderer.setBaseNegativeItemLabelPosition(position2);
	}
	else if (orientation == PlotOrientation.VERTICAL) {
		ItemLabelPosition position1 = new ItemLabelPosition(
				ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
		renderer.setBasePositiveItemLabelPosition(position1);
		ItemLabelPosition position2 = new ItemLabelPosition(
				ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
		renderer.setBaseNegativeItemLabelPosition(position2);
	}
	if (tooltips) {
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
	}
	if (urls) {
		renderer.setBaseItemURLGenerator(new StandardCategoryURLGenerator());
	}
	
	CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,renderer);
	plot.setOrientation(orientation);
	JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,plot, legend);
	this.currentTheme.apply(chart);
	return chart;

}

}