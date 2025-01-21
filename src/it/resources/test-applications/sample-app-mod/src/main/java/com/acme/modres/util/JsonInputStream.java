package com.acme.modres.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;

public class JsonInputStream extends FileInputStream {
    
    private File file;

    public JsonInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    public Object parseJsonAs(Class<?> cls) {
        if (file.exists()){
            JsonInputStream is = null;
            Object jsonObject = null;
            try {
                is = new JsonInputStream(file);
                Gson gson = new Gson();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                jsonObject = gson.fromJson(reader, cls); 
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                        is.read(); // test if file is closed
                    } catch (IOException e) {
                        // closed successfully
                        return jsonObject; 
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }
		return null;
	}
    
}
