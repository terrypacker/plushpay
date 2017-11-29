package com.payyourself.pdf;

import java.awt.Color;

import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;

public class ReportFormat {
	
	private PdfPCell emptyCell;
	private PdfPCell emptyRow6;
	private Font summaryFont;
	private Font summaryTitleFont;
	private Font titleFont;
	private Color lightBlue;
	private Color white;
	private Color darkBlue;
	
	public ReportFormat(){
		
		/*
		 * Create fonts to be used in the PDF
		 */
		
		this.titleFont = new Font(Font.HELVETICA, 10, Font.BOLD);

		this.summaryFont = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
		this.summaryTitleFont = new Font(Font.TIMES_ROMAN,8,Font.BOLD);
		
		/*
		 * Create some colors
		 */
		
		this.lightBlue = Color.decode("#DBEAF5");
		this.white = Color.decode("#FFFFFF");
		this.darkBlue = Color.decode("#4682B4");
		/*
		 * Empty place holder cell
		 */
		this.emptyCell = new PdfPCell(new Paragraph(""));
		emptyCell.setBorder(0);
		
		this.emptyRow6 = new PdfPCell(new Paragraph(""));
		this.emptyRow6.setBorder(0);
		this.emptyRow6.setColspan(6);

	}

	public PdfPCell getEmptyRow6() {
		return emptyRow6;
	}

	public void setEmptyRow6(PdfPCell emptyRow6) {
		this.emptyRow6 = emptyRow6;
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	public PdfPCell getEmptyCell() {
		return emptyCell;
	}

	public void setEmptyCell(PdfPCell emptyCell) {
		this.emptyCell = emptyCell;
	}

	public Font getSummaryFont() {
		return summaryFont;
	}

	public void setSummaryFont(Font summaryFont) {
		this.summaryFont = summaryFont;
	}

	public Font getSummaryTitleFont() {
		return summaryTitleFont;
	}

	public void setSummaryTitleFont(Font summaryTitleFont) {
		this.summaryTitleFont = summaryTitleFont;
	}

	/**
	 * @param lightBlue the lightBlue to set
	 */
	public void setLightBlue(Color lightBlue) {
		this.lightBlue = lightBlue;
	}

	/**
	 * @return the lightBlue
	 */
	public Color getLightBlue() {
		return lightBlue;
	}

	/**
	 * @param white the white to set
	 */
	public void setWhite(Color white) {
		this.white = white;
	}

	/**
	 * @return the white
	 */
	public Color getWhite() {
		return white;
	}

	/**
	 * @param darkBlue the darkBlue to set
	 */
	public void setDarkBlue(Color darkBlue) {
		this.darkBlue = darkBlue;
	}

	/**
	 * @return the darkBlue
	 */
	public Color getDarkBlue() {
		return darkBlue;
	}

	
	

}
