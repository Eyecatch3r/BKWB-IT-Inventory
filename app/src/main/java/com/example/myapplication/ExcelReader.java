package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {
    private static final String TAG = ExcelReader.class.getSimpleName();

    public interface ExcelReadListener {
        void onExcelRead(List<String> excelData);
    }

    public static void readExcelFileFromURLWithAuthorization(String fileURL, String username, String password, ExcelReadListener listener) {
        new ExcelReadTask(fileURL, username, password, listener).execute();
    }

    private static class ExcelReadTask extends AsyncTask<Void, Void, List<String>> {
        private String fileURL;
        private String username;
        private String password;
        private ExcelReadListener listener;

        public ExcelReadTask(String fileURL, String username, String password, ExcelReadListener listener) {
            this.fileURL = fileURL;
            this.username = username;
            this.password = password;
            this.listener = listener;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> list = new ArrayList<>();
            try {
                // Open a connection to the remote file URL
                URL url = new URL(fileURL);
                URLConnection connection = url.openConnection();

                // Set authorization credentials
                String authString = username + ":" + password;
                String encodedAuthString = java.util.Base64.getEncoder().encodeToString(authString.getBytes());
                connection.setRequestProperty("Authorization", "Basic " + encodedAuthString);

                // Create an InputStream from the connection
                InputStream inputStream = connection.getInputStream();

                // Create a Workbook object from the InputStream
                Workbook workbook = WorkbookFactory.create(inputStream);
                inputStream.close();

                // Get the first sheet in the workbook
                Sheet sheet = workbook.getSheetAt(0);

                // Iterate over the rows of the sheet
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Iterate over the cells of the row
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();

                        // Get the cell value and add it to the list
                        list.add(cell.toString());
                    }
                }

                // Close the workbook to release resources
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
                list.add(String.valueOf(e));
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<String> excelData) {
            if (listener != null) {
                listener.onExcelRead(excelData);
            }
        }
    }
}
