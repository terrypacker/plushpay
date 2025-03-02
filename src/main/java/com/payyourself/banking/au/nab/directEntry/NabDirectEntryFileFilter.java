package com.payyourself.banking.au.nab.directEntry;

import java.io.File;
import java.io.FileFilter;

public class NabDirectEntryFileFilter implements FileFilter{

	private static String extension = ".nab";
	
	/**
	 * Only return files that end in {@link com.payyourself.banking.nab.nai.NaiFileFilter.extension}
	 * @param file
	 * @return
	 */
	public boolean accept(File file){
		return file.getName().endsWith(NabDirectEntryFileFilter.extension);
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(NabDirectEntryFileFilter.extension);
	}

	public String getExtension() {
		return NabDirectEntryFileFilter.extension;
	}

}
