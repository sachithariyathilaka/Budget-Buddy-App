package com.budget.buddy.moneytracking.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.budget.buddy.moneytracking.Adapters.MoneyItemAdapter;
import com.budget.buddy.moneytracking.Database.DBHelper;
import com.budget.buddy.moneytracking.Entities.MoneyItem;
import com.budget.buddy.moneytracking.Entities.MoneyItemDao;
import com.budget.buddy.moneytracking.R;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;




public class TabFragment extends Fragment {
    DBHelper dbHelper;
    MoneyItemDao moneyItemDao;
    MoneyItemAdapter adapter;

    public static TabFragment newInstance(int numtab) {
        TabFragment myFragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt("numtab", numtab);
        myFragment.setArguments(args);
        return myFragment;
    }


    public TabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        JodaTimeAndroid.init(getContext());

        dbHelper = new DBHelper(getContext());
        moneyItemDao = dbHelper.getDaoSession().getMoneyItemDao();
    }


    private List<MoneyItem> getItems() {
        List<MoneyItem> input = new ArrayList<>();
        int tab = getArguments().getInt("numtab");
        LocalDate dt = new LocalDate();

        switch (tab) {
            case 1:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.toDate(), dt.toDate())).list();
                break;
            case 2:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.dayOfWeek().withMinimumValue().toDate(), dt.dayOfWeek().withMaximumValue().toDate())).list();
                break;
            case 3:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.dayOfMonth().withMinimumValue().toDate(), dt.dayOfMonth().withMaximumValue().toDate())).list();
                break;
        }
        return input;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_money_item_list, container, false);
        List<MoneyItem> data = getItems();

        adapter = new MoneyItemAdapter(data);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view_money);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        return rootView;
    }


}