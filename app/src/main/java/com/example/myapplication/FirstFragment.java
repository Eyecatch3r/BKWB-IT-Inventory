package com.example.myapplication;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.FragmentFirstBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ResultAdapter resultAdapter;
    private HorizontalScrollView horizontalScrollView;
    private TableLayout tableLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private boolean isDarkMode(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageInverter.invertImageInDarkMode(binding.imageView,isDarkMode(requireContext()));
        requireActivity().setTitle("Your actionbar title");
        horizontalScrollView = binding.horizontalScrollView;
        tableLayout = binding.tableLayout;

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fileURL = "https://webdav.bkwb.org/Groups/IT-Support/scosta/Inventar IT Büro/Inventar 212.accdb";
                String username = BuildConfig.username;
                String password = BuildConfig.password;
                String tableName = "Tabelle1";

                new FetchInputStreamTask(fileURL, username, password, tableName).execute();
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class FetchInputStreamTask extends AsyncTask<Void, Void, InputStream> {
        private final String fileURL;
        private final String username;
        private final String password;
        private final String tableName;

        public FetchInputStreamTask(String fileURL, String username, String password, String tableName) {
            this.fileURL = fileURL;
            this.username = username;
            this.password = password;
            this.tableName = tableName;
        }

        @Override
        protected InputStream doInBackground(Void... voids) {
            try {
                URL url = new URL(fileURL);
                URLConnection connection = url.openConnection();
                String encodedCredentials = username + ":" + password;
                String basicAuth = "Basic " + android.util.Base64.encodeToString(encodedCredentials.getBytes(), android.util.Base64.NO_WRAP);
                connection.setRequestProperty("Authorization", basicAuth);
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if (inputStream != null) {
                AccessDBReader accessDBReader = new AccessDBReader(new AccessDBReader.AccessDBReaderCallback() {
                    @Override
                    public void onAccessDBRead(List<String> data) {
                        tableLayout.removeAllViews();

                        TableRow headerRow = new TableRow(requireContext());
                        headerRow.setBackgroundColor(getResources().getColor(R.color.table_header_color));
                        String[] descriptions = new String[5];

                        descriptions[0] = "Beschreibung";
                        descriptions[1] = "Regal Fach Nr";
                        descriptions[2] = "Kiste/Box Nr";
                        descriptions[3] = "Schrank Nr";
                        descriptions[4] = "STK Zahl";
                        // Add column descriptions
                        for (int i = 0; i < descriptions.length; i++) {
                            addColumnDescription(headerRow, descriptions[i]);
                        }

                        // Add more column descriptions as needed

                        tableLayout.addView(headerRow);

                        String testString = String.valueOf(binding.editTextText.getText());

                        for (String item : data) {
                            if (item.toLowerCase().contains(testString.toLowerCase())) {
                                String[] itemParts = item.split(" - ");
                                if (itemParts.length > 1) {
                                    TableRow row = new TableRow(requireContext());
                                    for (int i = 1; i < itemParts.length; i++) {
                                        addColumnData(row, itemParts[i]);

                                    }
                                    // Add data for each column


                                    // Add more data for each column as needed

                                    tableLayout.addView(row);
                                }
                            }
                        }
                    }

                }, inputStream, "", "", tableName);

                accessDBReader.execute();
                // Scroll the horizontal scroll view to the start
                horizontalScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        horizontalScrollView.scrollTo(0, 0);
                    }
                });
            }
        }

        private void addColumnDescription(TableRow row, String description) {
            TextView textView = new TextView(requireContext());
            textView.setText(description);
            textView.setPadding(16, 16, 16, 16);
            int textColor = isDarkMode(requireContext())? ContextCompat.getColor(requireContext(), android.R.color.primary_text_dark) : ContextCompat.getColor(requireContext(), android.R.color.primary_text_light);
            textView.setTextColor(textColor);
            row.addView(textView);
        }


        private void addColumnData(TableRow row, String data) {
            TextView textView = new TextView(requireContext());
            textView.setText(data);
            textView.setPadding(16, 16, 16, 16);
            int textColor = isDarkMode(requireContext())? ContextCompat.getColor(requireContext(), android.R.color.primary_text_dark) : ContextCompat.getColor(requireContext(), android.R.color.primary_text_light);
            textView.setTextColor(textColor);

            // Apply the text appearance based on the current theme
            TextViewCompat.setTextAppearance(textView, android.R.style.TextAppearance);


            // Apply the text appearance based on the current theme
            TextViewCompat.setTextAppearance(textView, android.R.style.TextAppearance);
            row.addView(textView);
        }
    }
}

