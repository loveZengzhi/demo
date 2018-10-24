package cn.xumi.iov.utils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * csv工具类
 */
public class CSVUtils {
    private static final Logger logger = LogManager.getLogger(CSVUtils.class);
    public static List<String[]> read(File file){
        List<String[]> list = new ArrayList<String[]>();
        InputStreamReader inputStreamReader = null;
        CSVReader reader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
            reader = new CSVReader(inputStreamReader, CSVWriter.DEFAULT_SEPARATOR);
            list = reader.readAll();
            inputStreamReader.close();
            reader.close();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }finally {
            if (inputStreamReader!=null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
            if (reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }

            }
        }
        return list;
    }

    public static void write(List<String[]> list,String path,String fileName) throws IOException {
        File csv = new File(path+fileName);
        if (!csv.exists())csv.createNewFile();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(csv)),CSVWriter.DEFAULT_SEPARATOR);
        writer.writeAll(list);
        writer.flush();
        writer.close();
    }
}
