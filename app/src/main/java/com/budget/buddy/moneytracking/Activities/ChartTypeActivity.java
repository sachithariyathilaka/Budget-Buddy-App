package com.budget.buddy.moneytracking.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.budget.buddy.moneytracking.Database.DBHelper;
import com.budget.buddy.moneytracking.Entities.MoneyItemDao;
import com.budget.buddy.moneytracking.R;

import org.joda.time.LocalDate;


public class ChartTypeActivity extends AppCompatActivity {

    Toolbar type_chart_toolbar;
    Spinner chart_spinner;
    Button chart_button;
    LocalDate start, end;
    DBHelper dbHelper;

    MoneyItemDao moneyItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_chart);

        dbHelper = new DBHelper(this);
        moneyItemDao = dbHelper.getDaoSession().getMoneyItemDao();

        init_toolbar();
        init_spinner();
        init_button_chart();

    }

    private void init_button_chart() {
        chart_button = (Button) findViewById(R.id.generate_chart_button);
        chart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long item_count = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).count();
                if (item_count == 0) {
                    Toast.makeText(ChartTypeActivity.this, "There aren't sufficient item for a chart ", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(ChartTypeActivity.this, ShowChartActivity.class);
                    intent.putExtra("start", start);
                    intent.putExtra("end", end);
                    startActivity(intent);
                }
            }
        });
    }

    private void init_toolbar() {
        type_chart_toolbar = (Toolbar) findViewById(R.id.toolbar_type_chart);
        setSupportActionBar(type_chart_toolbar);
        getSupportActionBar().setTitle("Generate Charts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void init_spinner() {
        chart_spinner = (Spinner) findViewById(R.id.chart_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.report_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chart_spinner.setAdapter(adapter);

        chart_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                LocalDate dt = new LocalDate(LocalDate.now());
                switch (position) {
                    case 0: // Day
                        start = dt;
                        end = dt;
                        break;
                    case 1: // week
                        start = dt.dayOfWeek().withMinimumValue();
                        end = dt.dayOfWeek().withMaximumValue();
                        break;
                    case 2: // month
                        start = dt.dayOfMonth().withMinimumValue();
                        end = dt.dayOfMonth().withMaximumValue();
                        break;
                    case 3: // year
                        start = dt.dayOfYear().withMinimumValue();
                        end = dt.dayOfYear().withMaximumValue();
                        break;
                    case 4: // all
                        start = new LocalDate(0);
                        end = dt;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

    }

}