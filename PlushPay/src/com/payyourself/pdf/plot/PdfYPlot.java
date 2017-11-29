package com.payyourself.pdf.plot;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The PdfYPlot is used to generate an X,Y plot using only Y values supplied
 * in the addDataset method.  The X values are auto generated and can be labelled.
 * 
 * @author Terry Packer
 *
 */
public class PdfYPlot {
	
    /** A chart. */
    private JFreeChart chart;

    private int width;
    private int height;
    private String dataSetPrefix;
    private String samplePrefix;
    private String yAxisLabel;
    private String xAxisLabel;
    private String chartTitle;
	private PdfWriter writer;
	private List<double[]> data;
    
	
	/**
	 * 
	 * @param chartTitle
	 * @param samplePrefix - a string prefix that can be added to each value on the x axis
	 * @param dataSetPrefix - the series title
	 * @param height - height of image in pts?
	 * @param width - height of image in pts?
	 * @param xAxisLabel - units for xAxis
	 * @param yAxisLabel - units for yAxis
	 * @param writer - PDF Writer
	 */
	public PdfYPlot(String chartTitle,
			String samplePrefix, String dataSetPrefix, int height, int width,
			String xAxisLabel, String yAxisLabel,
			PdfWriter writer) {
		super();
		this.chart = null;
		this.chartTitle = chartTitle;
		this.data = new ArrayList<double[]>(0);
		this.samplePrefix = samplePrefix;
		this.dataSetPrefix = dataSetPrefix;
		this.height = height;
		this.width = width;
		this.writer = writer;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
	}

	
	public void addDataSet(double[] values){
		
		int i = 0;
		double[] dataSet = new double[values.length];
		while(i<values.length){
			dataSet[i] = values[i];
			i++;
		}
		this.data.add(dataSet);
	}
	
	public Image generatePlot() throws Exception{
		
		int sets = this.data.size();
		double[][] plotData = new double[sets][];
		
		int setCtr = 0;
		while(setCtr < sets){
			plotData[setCtr] = new double[this.data.get(setCtr).length];
			plotData[setCtr] = this.data.get(setCtr);
			setCtr++;
		}
		
		//Generate the dataset
        CategoryDataset newData = DatasetUtilities.createCategoryDataset(this.dataSetPrefix, this.samplePrefix, plotData);

        // create the chart...
        this.chart = ChartFactory.createLineChart(
            this.chartTitle,  // chart title
            this.xAxisLabel,
            this.yAxisLabel,
            newData,         // data
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
	    g2.dispose();

		
		return graphImg;
	}
		


}
