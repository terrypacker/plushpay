package com.payyourself.pdf;

import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class PdfReportEvents extends PdfPageEventHelper{

	/** This is the contentbyte object of the writer */
	PdfContentByte cb;

	/** we will put the final number of pages in a template */
	PdfTemplate template;

	/** this is the BaseFont we are going to use for the header / footer */
	BaseFont bf = null;
	
	
    /**
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
             try {
				bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252,
						BaseFont.NOT_EMBEDDED);
				cb = writer.getDirectContent();
				template = cb.createTemplate(50, 50);
			} 
            catch (DocumentException de) {}
			catch (IOException ioe) {}

    
    }    

	/**
	 * After the content of the page is written, we put page X of Y
	 * at the bottom of the page and we add either "Romeo and Juliet"
	 * of the title of the current act as a header.
	 * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
	 */
	public void onEndPage(PdfWriter writer, Document document) {
		int pageN = writer.getPageNumber();
		String text = "Page " + pageN + " of ";
		float len = bf.getWidthPoint(text, 8);
		cb.beginText();
		cb.setFontAndSize(bf, 8);
		cb.setTextMatrix(280, 30);
		cb.showText(text);
		cb.endText();
		cb.addTemplate(template, 280 + len, 30);
		cb.beginText();
		cb.setFontAndSize(bf, 8);
		cb.setTextMatrix(280, 820);
		cb.endText();
	}
    
	/**
	 * Just before the document is closed, we add the final number of pages to
	 * the template.
	 * @see com.lowagie.text.pdf.PdfPageEventHelper#onCloseDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
	 */
	public void onCloseDocument(PdfWriter writer, Document document) {
		template.beginText();
		template.setFontAndSize(bf, 8);
		template.showText(String.valueOf(writer.getPageNumber() - 1));
		template.endText();
	}
    
	
	
	
}
