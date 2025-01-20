package com.rene98c.bittyprice.core;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PersistentAppObject {
    private static final Logger logger = Logger.getLogger(PersistentAppObject.class.getName());
    private final Context parent;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private Object outputObject;
    private String filePath;

    public PersistentAppObject(Context c){
        parent = c;
    }

    public List<PortfolioItem> readPortfolio(){
        Object obj = readObject("portfolio");
        List<PortfolioItem> items =  (List<PortfolioItem>)obj;
        if(items == null) return  new ArrayList<>();
        return items;
    }

    public void writePortfolio(List<PortfolioItem> portfolio){
        writeObject(portfolio,"portfolio");
    }

    public Object readObject(String fileName){
        try {
            filePath = parent.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
            FileInputStream fileIn = new FileInputStream(filePath);
            objectIn = new ObjectInputStream(fileIn);
            outputObject = objectIn.readObject();
        } catch (FileNotFoundException e) {
            logger.severe("File not found error occurred:");
            logger.severe(e.toString());
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("General IO error occurred:");
            logger.severe(e.toString());
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    logger.severe("General IO error occurred, cannot close the file:");
                    logger.severe(e.toString());
                }
            }
        }
        return outputObject;
    }

    public void writeObject(Object inputObject, String fileName){
        try {
            filePath = parent.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
            FileOutputStream fileOut = new FileOutputStream(filePath);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(inputObject);
            fileOut.getFD().sync();
        } catch (IOException e) {
            logger.severe("General IO error occurred:");
            logger.severe(e.toString());
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    logger.severe("General IO error occurred, cannot close the file:");
                    logger.severe(e.toString());
                }
            }
        }
    }
}