/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class EmbeddedTransactionDAO implements TransactionDAO {
    SQLiteDatabase db;

    public EmbeddedTransactionDAO(SQLiteDatabase db){

        this.db=db;
    }


    @Override
    //insert values into transaction table
    public void logTransaction(Date date1, String accountNo, ExpenseType expenseType, double amount1){



        String insert_query = "INSERT INTO TransactionD (Acc_No,Type,amount,date) VALUES (?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(insert_query);

        statement.bindString(1,accountNo);
        statement.bindLong(2,(expenseType == ExpenseType.EXPENSE) ? 0 : 1);
        statement.bindDouble(3,amount1);
        statement.bindLong(4,date1.getTime());

        statement.executeInsert();



    }

    @Override
    //get all transactions
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();

        String TRANSACTION_DETAIL_SELECT_QUERY = "SELECT * FROM TransactionD";
        Cursor cursor = db.rawQuery(TRANSACTION_DETAIL_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Transaction trans=new Transaction(
                            new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                            cursor.getString(cursor.getColumnIndex("Acc_No")),
                            (cursor.getInt(cursor.getColumnIndex("Type")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                            cursor.getDouble(cursor.getColumnIndex("amount")));

                    transactions.add(trans);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }


        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        //int size =int numRows = DatabaseUtils.queryNumEntries( Table_1);;


        List<Transaction> transdetail = new ArrayList<>();

        String TRANS_DETAIL_SELECT_QUERY = "SELECT * FROM TransactionD LIMIT"+limit;


        Cursor cursor = db.rawQuery(TRANS_DETAIL_SELECT_QUERY, null);


        if (cursor.moveToFirst()) {
            do {
                Transaction trans=new Transaction(
                        new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                        cursor.getString(cursor.getColumnIndex("Acc_No")),
                        (cursor.getInt(cursor.getColumnIndex("Type")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        cursor.getDouble(cursor.getColumnIndex("amount")));


                transdetail.add(trans);

            } while (cursor.moveToNext());
        }

        return  transdetail;
    }
}