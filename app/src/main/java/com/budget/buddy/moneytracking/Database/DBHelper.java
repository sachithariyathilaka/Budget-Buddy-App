package com.budget.buddy.moneytracking.Database;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.budget.buddy.moneytracking.Entities.DaoMaster;
import com.budget.buddy.moneytracking.Entities.DaoSession;
import com.budget.buddy.moneytracking.Entities.MoneyItem;
import com.budget.buddy.moneytracking.Entities.MoneyItemDao;
import com.budget.buddy.moneytracking.Entities.PlannedItem;
import com.budget.buddy.moneytracking.Entities.PlannedItemDao;

import org.greenrobot.greendao.database.Database;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;



public class DBHelper {

    private static final String DB_NAME = "moneytrackDB";
    private DaoMaster.DevOpenHelper helper;
    private DaoSession daoSession;
    private DaoMaster daoMaster;
    private Database db;

    public DBHelper(Context context) {
        helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        db = helper.getWritableDb();

        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


    public double getTotal(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            total += listIterator.next().getAmount();
        }
        return total;
    }

    public double getTotalExpense(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.lt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            total += listIterator.next().getAmount();
        }
        return total;
    }

    public double getTotalProfit(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.gt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            total += listIterator.next().getAmount();
        }
        return total;
    }

    public double getAVGProfit(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.gt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        if (l.size() == 0)
            return 0.0;
        else {
            double total = 0.0;
            ListIterator<MoneyItem> listIterator = l.listIterator();
            while (listIterator.hasNext()) {
                total += listIterator.next().getAmount();
            }
            return total / l.size();
        }
    }

    public double getAVGExpense(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.lt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        if (l.size() == 0)
            return 0.0;
        else {
            double total = 0.0;

            ListIterator<MoneyItem> listIterator = l.listIterator();
            while (listIterator.hasNext()) {
                total += listIterator.next().getAmount();
            }
            return total / l.size();
        }
    }

    public double getMAXProfit(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.gt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double max = 0.0, amount = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            amount = listIterator.next().getAmount();
            if (amount > max)
                max = amount;
        }
        return max;
    }

    public double getMINExpense(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.lt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        if (l.size() == 0)
            return 0.0;
        else {
            double min = 0.0, amount = 0.0;
            ListIterator<MoneyItem> listIterator = l.listIterator();
            while (listIterator.hasNext()) {
                amount = listIterator.next().getAmount();
                if (amount < min)
                    min = amount;
            }
            return min;
        }
    }

    public PlannedItem popPlanned() {
        PlannedItemDao plannedItemDao = getDaoSession().getPlannedItemDao();
        List<PlannedItem> plannedItemList = plannedItemDao.queryBuilder().orderAsc(PlannedItemDao.Properties.Date).list();
        if(plannedItemList.size() > 0) {
            PlannedItem p = plannedItemList.listIterator().next();
            return p;
        }else{
            return  null;
        }
    }


    private boolean deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    private void clear_preference(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public boolean clearReport() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MoneyTrack";
        return deleteFiles(path);
    }


    public void clearAllData(Context c) {
        daoMaster.dropAllTables(db, true);
        daoMaster.createAllTables(db, true);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MoneyTrack";
        deleteFiles(path);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        clear_preference(prefs);
    }


    public JSONArray getCategoryProfitExpense(LocalDate start, LocalDate end) {

        JSONArray arr = new JSONArray();
        String queryProfit = "( SELECT SUM(G.AMOUNT) FROM MONEY_ITEM G WHERE G.AMOUNT >= 0 AND A.CATEGORY_ID == G.CATEGORY_ID  AND G.DATE >= " + String.valueOf(start.toDate().getTime()) + " AND G.DATE <= " + String.valueOf(end.toDate().getTime()) + " ) AS PROFIT  ";
        String queryExpense = "( SELECT SUM(B.AMOUNT) FROM MONEY_ITEM B WHERE B.AMOUNT < 0 AND A.CATEGORY_ID == B.CATEGORY_ID  AND B.DATE >= " + String.valueOf(start.toDate().getTime()) + " AND B.DATE <= " + String.valueOf(end.toDate().getTime()) + " ) AS EXPENSE  ";
        String megaQuery = "SELECT CATEGORY.NAME, " + queryProfit + " , " + queryExpense + " FROM MONEY_ITEM A INNER JOIN CATEGORY ON A.CATEGORY_ID = CATEGORY._id GROUP BY A.CATEGORY_ID ";


        Cursor c = getDaoSession().getDatabase().rawQuery(megaQuery, null);
        c.moveToFirst();
        JSONObject obj = new JSONObject();

        while (!c.isAfterLast()) {
            String cat_name = c.getString(c.getColumnIndex("NAME"));
            String cat_expense = c.getType(c.getColumnIndex("EXPENSE")) != 0 ? c.getString(c.getColumnIndex("EXPENSE")) : "0";
            String cat_profit = c.getType(c.getColumnIndex("PROFIT")) != 0 ? c.getString(c.getColumnIndex("PROFIT")) : "0";

            try {
                obj.put("name", cat_name);
                obj.put("profit", cat_profit);
                obj.put("expense", cat_expense);
                arr.put(obj);
                obj = new JSONObject();
            } catch (JSONException e) {
            }
            c.moveToNext();
        }
        c.close();
        return arr;
        //[{"name":"Food","profit":"10","expense":"-6"},{"name":"Drink","profit":"2","expense":""}]
    }


}