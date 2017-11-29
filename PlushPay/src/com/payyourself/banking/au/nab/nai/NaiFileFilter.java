package com.payyourself.banking.au.nab.nai;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class NaiFileFilter implements FileFilter{

	private static String extension = ".nai";
	
	/**
	 * Only return files that end in {@link com.payyourself.banking.nab.nai.NaiFileFilter.extension}
	 * @param file
	 * @return
	 */
	public boolean accept(File file){
		return file.getName().endsWith(NaiFileFilter.extension);
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(NaiFileFilter.extension);
	}
	
	public String getExtension(){
		return NaiFileFilter.extension;
	}
}
