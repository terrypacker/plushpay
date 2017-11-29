package com.payyourself.pdf;

import java.awt.Color;
import java.io.IOException;


import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;



public class PdfFooter {
	
	private String footerText;
	private String dateCreated;
	

	public PdfFooter() throws IOException{
		
	}
	
	public PdfFooter(String footerText, String dateCreated) {
		this.footerText = footerText;
		this.dateCreated = dateCreated;
		}

	public PdfPTable createPDFFooter() {
		
		PdfPTable summary = new PdfPTable(2);
		summary.setWidthPercentage(100);
		
		ReportFormat formatter = new ReportFormat();
		
		//LineSeparator hr = new LineSeparator();
		
		String footerString = this.footerText +"\t"+this.dateCreated;
		
		PdfPCell titleCell = new PdfPCell(new Paragraph(footerString, formatter.getTitleFont()));
		titleCell.setPadding(5f);
		titleCell.setColspan(2);
		titleCell.setBorder(Rectangle.BOX);
		titleCell.setBackgroundColor(Color.decode("#EEEEEE"));
		titleCell.setBorderColor(Color.decode("#4682B4"));
		titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);			
		summary.addCell(titleCell);
	 		
		summary.setSpacingBefore(5f);
		summary.setSpacingAfter(5f);
		
		return summary; // return the summary report

	}

	/**
	 * @return the footerText
	 */
	public String getFooterText() {
		return footerText;
	}

	/**
	 * @param footerText the footerText to set
	 */
	public void setFooterText(String footerText) {
		this.footerText = footerText;
	}

	/**
	 * @return the dateCreated
	 */
	public String getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	

}
