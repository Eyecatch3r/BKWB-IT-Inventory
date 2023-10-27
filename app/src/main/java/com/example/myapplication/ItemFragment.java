package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.placeholder.PlaceholderContent;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // Fetch and display monitored devices
            new FetchMonitoredDevicesTask(recyclerView).execute();

        }
        return view;
    }

    private class FetchMonitoredDevicesTask extends AsyncTask<Void, Void, List<String[]>> {
        private final RecyclerView recyclerView;

        public FetchMonitoredDevicesTask(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        protected List<String[]> doInBackground(Void... voids) {
            String fileURL = "https://webdav.bkwb.org/Groups/Domain Admins/Monitoring/Rohdaten/APMonitor.csv";
            String username = BuildConfig.username;
            String password = BuildConfig.password;

            try {
                URL url = new URL(fileURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " + android.util.Base64.encodeToString((username + ":" + password).getBytes(), android.util.Base64.NO_WRAP));
                connection.connect();
                CSVReader reader = new CSVReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_16));
                List<String[]> data = reader.readAll();
                reader.close();

                return data;
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String[]> itemList) {
            if (itemList != null) {
                List<PlaceholderContent.PlaceholderItem> placeholderItems = convertToPlaceholderItems(itemList);

                if (placeholderItems != null) {
                    recyclerView.setAdapter(new MonitorActivityAdapter(placeholderItems));
                }  // Handle the case when data conversion fails

            }  // Handle the case when data retrieval fails

        }
    }

    private List<PlaceholderContent.PlaceholderItem> convertToPlaceholderItems(List<String[]> itemList) {
        List<PlaceholderContent.PlaceholderItem> placeholderItems = new ArrayList<>();

        for (String[] item : itemList) {
            if (!item[0].equals("")) {
                String title = item[0];
                String content = item[0];

                // Create a new PlaceholderContent.PlaceholderItem with the extracted data
                PlaceholderContent.PlaceholderItem placeholderItem = new PlaceholderContent.PlaceholderItem(
                        String.valueOf(itemList.indexOf(item)), // You can generate a unique ID here
                        title,
                        content
                );

                placeholderItems.add(placeholderItem);
            }
        }

        return placeholderItems;
    }

}