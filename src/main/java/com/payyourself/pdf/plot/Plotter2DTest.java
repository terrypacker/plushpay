package com.payyourself.pdf.plot;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JComponent;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class Plotter2DTest extends JComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws Exception{
		
		createXYPlot();
		createYPlot();
		createTimePeriodPlot();
	    

	}

	//Generate a Y plot if you only need to set in the Y values, X is auto numbered
	public static void createYPlot() throws DocumentException, FileNotFoundException{
		Document report = new Document(PageSize.A4);;
		OutputStream os = new FileOutputStream("C:\\pdfPlotterPdfYPlot.pdf");
		PdfWriter writer = PdfWriter.getInstance(report,os);


		//Create Plot Stuff
		PdfYPlot plot = new PdfYPlot("Test Y Plot","s", "Series Prefix", 300, 500,"X Axis Label", "Y Axis Label",
				writer);
		
		double[] data1 = {10,20,30,40,50,60};
		plot.addDataSet(data1);

		
		//Now add everything to document
		report.open();	
	    
		
		
		PdfPTable summary = new PdfPTable(2);
		summary.setWidthPercentage(100);
				
		Font titleFont = new Font(Font.HELVETICA, 12, Font.BOLD);
		
		//Create title and add colors/borders
		PdfPCell title = new PdfPCell(new Paragraph("I'm a title",titleFont));
		title.setPadding(10f);
		title.setColspan(2);
		title.setBorder(Rectangle.BOX);
		title.setBackgroundColor(Color.decode("#EEEEEE"));
		title.setBorderColor(Color.decode("#4682B4"));
		title.setHorizontalAlignment(Element.ALIGN_CENTER);			
		summary.addCell(title);
	 			
		//******************************************
		// Create Report Summary
		Font summaryTitleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
		PdfPCell summaryTitle = new PdfPCell(new Paragraph("Report Summary",summaryTitleFont));
		summaryTitle.setColspan(2);
		summaryTitle.setPaddingTop(5f);
		summaryTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
		summaryTitle.setBorder(0);
		summary.addCell(summaryTitle);
		
		Font summaryFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
		/*
		PdfPCell numEventsTitle = new PdfPCell(new Paragraph("Number of events:",summaryFont));
		numEventsTitle.setBorder(0);
		numEventsTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		summary.addCell(numEventsTitle);
		
		PdfPCell numEvents = new PdfPCell(new Paragraph("10",summaryFont));
		numEvents.setBorder(0);
		numEvents.setHorizontalAlignment(Element.ALIGN_LEFT);
		summary.addCell(numEvents);
		*/
		PdfPCell dateTitle = new PdfPCell(new Paragraph("Date Created:",summaryFont));
		dateTitle.setBorder(0);
		dateTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		summary.addCell(dateTitle);
		
		//Get date and format it
		
		PdfPCell date = new PdfPCell(new Paragraph("11/22/98",summaryFont));
		date.setBorder(0);
		date.setHorizontalAlignment(Element.ALIGN_LEFT);
		summary.addCell(date);
		
		//Do table spacing
		summary.setSpacingBefore(10f);
		summary.setSpacingAfter(10f);
		
		report.add(summary);
		
	    try {
			report.add(plot.generatePlot());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		report.close();

	}
	
	//Generate an XY Plot if you need access to set X and Y coordinates
	public static void createXYPlot() throws Exception{
		Document report = new Document(PageSize.A4);;
		OutputStream os = new FileOutputStream("C:\\pdfPlotterPdfXYPlot.pdf");
		PdfWriter writer = PdfWriter.getInstance(report,os);
		
		//Now add everything to document
		report.open();	
	    
		
		
		PdfPTable summary = new PdfPTable(2);
		summary.setWidthPercentage(100);
				
		Font titleFont = new Font(Font.HELVETICA, 12, Font.BOLD);
		
		//Create title and add colors/borders
		PdfPCell title = new PdfPCell(new Paragraph("I'm a title",titleFont));
		title.setPadding(10f);
		title.setColspan(2);
		title.setBorder(Rectangle.BOX);
		title.setBackgroundColor(Color.decode("#EEEEEE"));
		title.setBorderColor(Color.decode("#4682B4"));
		title.setHorizontalAlignment(Element.ALIGN_CENTER);			
		summary.addCell(title);
	 			
		//******************************************
		// Create Report Summary
		Font summaryTitleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
		PdfPCell summaryTitle = new PdfPCell(new Paragraph("Report Summary",summaryTitleFont));
		summaryTitle.setColspan(2);
		summaryTitle.setPaddingTop(5f);
		summaryTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
		summaryTitle.setBorder(0);
		summary.addCell(summaryTitle);
		
		Font summaryFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
		/*
		PdfPCell numEventsTitle = new PdfPCell(new Paragraph("Number of events:",summaryFont));
		numEventsTitle.setBorder(0);
		numEventsTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		summary.addCell(numEventsTitle);
		
		PdfPCell numEvents = new PdfPCell(new Paragraph("10",summaryFont));
		numEvents.setBorder(0);
		numEvents.setHorizontalAlignment(Element.ALIGN_LEFT);
		summary.addCell(numEvents);
		*/
		PdfPCell dateTitle = new PdfPCell(new Paragraph("Date Created:",summaryFont));
		dateTitle.setBorder(0);
		dateTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		summary.addCell(dateTitle);
		
		//Get date and format it
		
		PdfPCell date = new PdfPCell(new Paragraph("11/22/98",summaryFont));
		date.setBorder(0);
		date.setHorizontalAlignment(Element.ALIGN_LEFT);
		summary.addCell(date);
		
		//Do table spacing
		summary.setSpacingBefore(10f);
		summary.setSpacingAfter(10f);
		
		report.add(summary);
		
		//*************************************
		//This is where we add the plot
		//*************************************
		Color color = new Color(0.5f, 0.5f, 0.5f);
		//Create Plot Stuff
		PdfXYPlot plot = new PdfXYPlot("PlotTitle",300,500,"X Axis Label","Y Axis Label",color,writer.getDirectContent());

		//Add a data set
		double[] xData = {10,20,30,40,50,60};
		double[] yData = {33,12,22,55,77,89};
		plot.addDataSet(xData,yData,"Series 1");

		//Add another data set
		double[] x1Data = {10,20,30,40,50,60};
		double[] y1Data = {1,23,236,77,43,20};
		plot.addDataSet(x1Data,y1Data,"Series 2");
		
		//Generate the image
		Image myPlotImage = plot.generatePlot();
		
		//Add the image to the plot
		report.add(myPlotImage);
		
		
		report.close();
		
	}

	//Generate a Y plot if you only need to set in the Y values, X is auto numbered
	public static void createTimePeriodPlot() throws DocumentException, FileNotFoundException{
		Document report = new Document(PageSize.A4);;
		OutputStream os = new FileOutputStream("C:\\pdfPlotterPdfTimePlot.pdf");
		PdfWriter writer = PdfWriter.getInstance(report,os);


		//Create Plot Stuff
		PdfTimePeriodPlot plot = new PdfTimePeriodPlot("Test Time Period Plot",
				300, 500,"X Axis Label", "Y Axis Label", "dd-MMM-yyyy", Color.white, writer.getDirectContent());
		
		ArrayList<Long> timePeriods = new ArrayList<Long>(4);
		Calendar today = Calendar.getInstance();
		long oneDay = 24*60*60*1000; //One day in milliseconds.

		timePeriods.add(today.getTimeInMillis()-oneDay*3);
		timePeriods.add(today.getTimeInMillis()-oneDay*2);
		timePeriods.add(today.getTimeInMillis()-oneDay);
		timePeriods.add(today.getTimeInMillis());
		
		
		ArrayList<Integer> yData = new ArrayList<Integer>(4);
		yData.add(12);
		yData.add(14);
		yData.add(15);
		yData.add(17);
		
		plot.addTimeSeries(timePeriods, yData, "Test Series");
		
		//At any time you can ovrride the initial date display 
		// by showing x axis values on a Day scale, month, or week.
		plot.setTimeScaleToDay();
		
		//Now add everything to document
		report.open();	
	    
		
		
		PdfPTable summary = new PdfPTable(2);
		summary.setWidthPercentage(100);
				
		Font titleFont = new Font(Font.HELVETICA, 12, Font.BOLD);
		
		//Create title and add colors/borders
		PdfPCell title = new PdfPCell(new Paragraph("I'm a title",titleFont));
		title.setPadding(10f);
		title.setColspan(2);
		title.setBorder(Rectangle.BOX);
		title.setBackgroundColor(Color.decode("#EEEEEE"));
		title.setBorderColor(Color.decode("#4682B4"));
		title.setHorizontalAlignment(Element.ALIGN_CENTER);			
		summary.addCell(title);
	 			
		//******************************************
		// Create Report Summary
		Font summaryTitleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
		PdfPCell summaryTitle = new PdfPCell(new Paragraph("Report Summary",summaryTitleFont));
		summaryTitle.setColspan(2);
		summaryTitle.setPaddingTop(5f);
		summaryTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
		summaryTitle.setBorder(0);
		summary.addCell(summaryTitle);
		
		Font summaryFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
		/*
		PdfPCell numEventsTitle = new PdfPCell(new Paragraph("Number of events:",summaryFont));
		numEventsTitle.setBorder(0);
		numEventsTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		summary.addCell(numEventsTitle);
		
		PdfPCell numEvents = new PdfPCell(new Paragraph("10",summaryFont));
		numEvents.setBorder(0);
		numEvents.setHorizontalAlignment(Element.ALIGN_LEFT);
		summary.addCell(numEvents);
		*/
		PdfPCell dateTitle = new PdfPCell(new Paragraph("Date Created:",summaryFont));
		dateTitle.setBorder(0);
		dateTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		summary.addCell(dateTitle);
		
		//Get date and format it
		
		PdfPCell date = new PdfPCell(new Paragraph("11/22/98",summaryFont));
		date.setBorder(0);
		date.setHorizontalAlignment(Element.ALIGN_LEFT);
		summary.addCell(date);
		
		//Do table spacing
		summary.setSpacingBefore(10f);
		summary.setSpacingAfter(10f);
		
		report.add(summary);
		
	    try {
			report.add(plot.generatePlot());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		report.close();

	}

	
}
