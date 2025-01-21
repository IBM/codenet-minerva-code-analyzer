package com.acme.modres.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


public class ZipValidator extends ZipFile {
    
    public ZipValidator(File file) throws ZipException, IOException {
        super(file);
        this.file = file;
    }

    private File file;

    public boolean isValid() throws Throwable {
        if (file.exists()){
            ZipValidator zipFile = new ZipValidator(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if(!entries.hasMoreElements()) {
                return true;
            }
            zipFile.close();
        }
		return false;
	}
    
}