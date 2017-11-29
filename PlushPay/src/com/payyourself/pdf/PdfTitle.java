package com.payyourself.pdf;

import java.io.IOException;
import com.lowagie.text.Element;

import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;



public class PdfTitle {
	
	private String title;
	private String startDate;
	private String endDate;
	private String dateCreated;
	

	public PdfTitle() throws IOException{
		
	}
	
	public PdfTitle(String title, String startDate, String endDate, String dateCreated) throws Exception {
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dateCreated = dateCreated;
		
		
	}

	public PdfPTable createPDFTitle() throws Exception {
		
		PdfPTable summary = new PdfPTable(8);
		summary.setWidthPercentage(100);
		
		//LineSeparator hr = new LineSeparator();
		
		
		
		
		/*
		 * Style sheet
		 */
		ReportFormat formatter = new ReportFormat();
		
		
		/*
		 * The text to be displayed as the title of the document
		 */
		
		PdfPCell titleCell = new PdfPCell(new Paragraph(this.title, formatter.getTitleFont()));
		
		titleCell.setPadding(5f);
		titleCell.setColspan(8);
		titleCell.setBorder(Rectangle.BOX);
		titleCell.setBackgroundColor(formatter.getLightBlue());
		titleCell.setBorderColor(formatter.getDarkBlue());
		titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);			
		
		
		PdfPCell startDateTitleCell = new PdfPCell(new Paragraph("Start Date: ", formatter.getSummaryTitleFont()));
		startDateTitleCell.setBorder(0);
		startDateTitleCell.setBackgroundColor(formatter.getWhite());
		startDateTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		PdfPCell endDateTitleCell = new PdfPCell(new Paragraph("End Date: ", formatter.getSummaryTitleFont()));
		endDateTitleCell.setBorder(0);
		endDateTitleCell.setBackgroundColor(formatter.getWhite());
		endDateTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		PdfPCell createdTitleCell = new PdfPCell(new Paragraph("Report Created: ", formatter.getSummaryTitleFont()));
		createdTitleCell.setBorder(0);
		createdTitleCell.setBackgroundColor(formatter.getWhite());
		createdTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		PdfPCell startDateCell = new PdfPCell(new Paragraph(this.startDate, formatter.getSummaryFont()));
		startDateCell.setBorder(0);
		startDateCell.setBackgroundColor(formatter.getWhite());
		startDateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		PdfPCell endDateCell = new PdfPCell(new Paragraph(this.endDate, formatter.getSummaryFont()));
		endDateCell.setBorder(0);
		endDateCell.setBackgroundColor(formatter.getWhite());
		endDateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		PdfPCell createdCell = new PdfPCell(new Paragraph(this.dateCreated, formatter.getSummaryFont()));
		createdCell.setBorder(0);
		createdCell.setBackgroundColor(formatter.getWhite());
		createdCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		
		/*
		 * Add the elements to the table 
		 */
		summary.addCell(titleCell);
		
		summary.addCell(startDateTitleCell);
		summary.addCell(startDateCell);
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		
		summary.addCell(endDateTitleCell);
		summary.addCell(endDateCell);
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		
		summary.addCell(createdTitleCell);
		summary.addCell(createdCell);
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());
		summary.addCell(formatter.getEmptyCell());


		
		summary.setSpacingBefore(5f);
		summary.setSpacingAfter(5f);
		
		return summary; // return the summary report

	}

	

}
