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

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class EmbeddedAccountDAO implements AccountDAO {
    SQLiteDatabase db;

    public EmbeddedAccountDAO(SQLiteDatabase db) {
        this.db=db;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<>();

        String ACCOUNT_NUMBERS_SELECT_QUERY = "SELECT Acc_No FROM Account";

        Cursor cursor = db.rawQuery(ACCOUNT_NUMBERS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String accountNo=cursor.getString(cursor
                            .getColumnIndex("Acc_No"));
                    accountNumbers.add(accountNo);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountdetail = new ArrayList<>();

        String ACCOUNT_DETAIL_SELECT_QUERY = "SELECT * FROM Account";

        Cursor cursor = db.rawQuery(ACCOUNT_DETAIL_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Account acc=new Account(
                            cursor.getString(cursor.getColumnIndex("Acc_No")),
                            cursor.getString(cursor.getColumnIndex("Bank")),
                            cursor.getString(cursor.getColumnIndex("Acc_holder")),
                            cursor.getDouble(cursor.getColumnIndex("balance")));

                    accountdetail.add(acc);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return accountdetail;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String ACCOUNT_SELECT_QUERY = "SELECT * FROM Account where Acc_No = "+accountNo;

        Cursor cursor = db.rawQuery(ACCOUNT_SELECT_QUERY, null);
        if (cursor != null)
            cursor.moveToFirst();

        Account account = new Account(
                cursor.getString(cursor.getColumnIndex("Acc_No")),
                cursor.getString(cursor.getColumnIndex("Bank")),
                cursor.getString(cursor.getColumnIndex("Acc_holder")),
                cursor.getDouble(cursor.getColumnIndex("balance")));


        return account;

    }

    //add account
    @Override
    public void addAccount(Account account) {
        String sql = "INSERT INTO Account (Acc_No,Bank,Acc_holder,balance) VALUES (?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);



        statement.bindString(1, account.getAccountNo());
        statement.bindString(2, account.getBankName());
        statement.bindString(3, account.getAccountHolderName());
        statement.bindDouble(4, account.getBalance());

        statement.executeInsert();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        String sqlquery = "DELETE FROM Account WHERE Acc_No = ?";
        SQLiteStatement statement = db.compileStatement(sqlquery);

        statement.bindString(1,accountNo);

        statement.executeUpdateDelete();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void updateBalance(String accountNo, ExpenseType expense_Type, double _amount) throws InvalidAccountException {

        String sqlquery = "UPDATE Account SET balance = balance + ?";
        SQLiteStatement statement = db.compileStatement(sqlquery);
        if(expense_Type == ExpenseType.EXPENSE){
            statement.bindDouble(1,-_amount);
        }else{
            statement.bindDouble(1,_amount);
        }

        statement.executeUpdateDelete();
    }
}

