package com.payyourself.pdf;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;


import com.lowagie.text.BadElementException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;



public class PdfHeader {
	
	private Image logo;
	

	public PdfHeader() throws Exception {
		setLogo();
		
	}

	public PdfPTable createPDFHeader() throws Exception {
		
		PdfPTable header = new PdfPTable(2);
		
		header.setWidthPercentage(100);
		
		String titleString = new String();
		
		Image logo  = this.logo;
		logo.scaleToFit(84, 56);
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		FileReader fr = new FileReader(loader.getResource("resources/address.txt").toURI().getPath());
		BufferedReader br = new BufferedReader(fr);
		String businessName = br.readLine();
		String addressLn1 = br.readLine();
		String addressLn2 = br.readLine();
		String addressLn3 = br.readLine();
		String addressLn4 = br.readLine();
		String phone = br.readLine();
		String fax = br.readLine();
		
		
		/*if (!(addressLn1.equals(null))){
			titleString = addressLn1+"\n";
		}
		if (!(addressLn2.equals(null))){
			titleString = addressLn1+"\n";
		}
		if (!(addressLn3.equals(null))){
			titleString = addressLn1+"\n";
		}
		if (!(addressLn4.equals(null))){
			titleString = addressLn4+"\n";
		}
		if (!(phone.equals(null))){
			titleString = phone+"\n";
		}
		if (!(fax.equals(null))){
			titleString = fax+"\n";
		}*/
		
		
		
		titleString = titleString + addressLn1+"\n";
		titleString = titleString + addressLn2+"\n";
		titleString = titleString + addressLn3+"\n";
		titleString = titleString + addressLn4+"\n";
		titleString = titleString + phone+"\n";
		titleString = titleString + fax+"\n";
		
		if (!(businessName.equals(null))){
			titleString = " ";
		}
		/*
		 * Style sheet
		 */
		ReportFormat formatter = new ReportFormat();
				
		/*
		 * The text to be displayed as the title of the document
		 */
		
		PdfPCell titleCell = new PdfPCell(new Paragraph(titleString, formatter.getSummaryFont()));
		titleCell.setPadding(5f);
		titleCell.setBorder(0);
		titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		titleCell.setBackgroundColor(formatter.getWhite());
		
		/*
		 * Image to be placed on the top right of the page
		 */
		
		PdfPCell imageCell = new PdfPCell(this.logo);
		imageCell.setFixedHeight(28);
		imageCell.setBorder(0);
		imageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		/*
		 * Add this color to the formatter
		 */
		
		
					
		
		
		/*
		 * Add the elements to the table 
		 */
		header.addCell(titleCell);
		header.addCell(imageCell);
		
		/*
		 * Set spacing
		 */
		header.setSpacingBefore(5f);
		header.setSpacingAfter(5f);
		
		return header; // return the summary report

	}

	/**
	 * 
	 * @throws Exception
	 */
	public void setLogo() throws Exception {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String path = loader.getResource("/resources/logo.jpg").toURI().getPath();
		
		this.logo = Image.getInstance(path);
	}

	/**
	 * @return the logo
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws BadElementException 
	 */
	public Image getLogo() throws Exception {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String path = loader.getResource("/resources/logo.jpg").toURI().getPath();
		
		return Image.getInstance(path);
	}
	
	

}
