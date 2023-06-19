package com.example.myapplication;
import android.os.AsyncTask;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AccessDBReader extends AsyncTask<Void, Void, List<String>> {
    private AccessDBReaderCallback callback;
    private InputStream inputStream;
    private String username;
    private String password;
    private String tableName;

    public AccessDBReader(AccessDBReaderCallback callback, InputStream inputStream, String username, String password, String tableName) {
        this.callback = callback;
        this.inputStream = inputStream;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        List<String> data = new ArrayList<>();

        try {
            // Create a temporary file to store the Access database
            File tempFile = File.createTempFile("temp_db", ".accdb");

            // Write the input stream to the temporary file
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.close();

            // Open the database using the temporary file and the specified username and password
            Database database = DatabaseBuilder.open(tempFile);

            // Get the specified table from the database
            com.healthmarketscience.jackcess.Table table = database.getTable(tableName);

            // Get the column index or name to retrieve data from
            int columnIndex = 0; // Modify this based on the column index you want to retrieve data from
            // String columnName = "ColumnName"; // Modify this based on the column name you want to retrieve data from

            // Iterate over the rows in the table
            for (Row row : table) {
                // Get the value of the specified column in the row and add it to the data list
                String inventarNr = row.getString("Inventar Nr");
                String beschreibung = row.getString("Beschreibung");
                Integer regalFachNr = row.getInt("Regal Fach Nr");
                String kistenBoxNr = row.getString("Kisten oder Box Nr");
                String schrankNr = row.getString("Schrank Nr");
                Double stkZahl = row.getDouble("STK Zahl");

                // Check for null values before adding to the data list
                StringBuilder rowData = new StringBuilder();
                if (inventarNr != null) {
                    rowData.append(inventarNr);
                }
                rowData.append(" - ");
                if (beschreibung != null) {
                    rowData.append(beschreibung);
                }
                rowData.append(" - ");
                if (regalFachNr != null) {
                    rowData.append(regalFachNr.intValue());
                }
                rowData.append(" - ");
                if (kistenBoxNr != null) {
                    rowData.append(kistenBoxNr);
                }
                rowData.append(" - ");
                if (schrankNr != null) {
                    rowData.append(schrankNr);
                }
                rowData.append(" - ");
                if (stkZahl != null) {
                    rowData.append(stkZahl.intValue());
                }

                data.add(rowData.toString());
                // data.add(row.getString(columnName)); // Use this if retrieving data based on column name
            }


            // Close the database and delete the temporary file
            database.close();
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(List<String> data) {
        callback.onAccessDBRead(data);
    }

    public interface AccessDBReaderCallback {
        void onAccessDBRead(List<String> data);
    }
}