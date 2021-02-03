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

package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.EmbeddedAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.EmbeddedTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 *
 */
public class EmbeddedDemoExpenseManager extends ExpenseManager{


    Context context;
    public EmbeddedDemoExpenseManager(Context ctx)  {
        context=ctx;
        try {

            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }
    @Override

    public void setup() throws ExpenseManagerException {

        SQLiteDatabase db = context.openOrCreateDatabase("180648R", context.MODE_PRIVATE, null);

        // create the databases.
        db.execSQL("CREATE TABLE IF NOT EXISTS Account(" +
                "Acc_No VARCHAR PRIMARY KEY," +
                "Bank VARCHAR," +
                "Acc_holder VARCHAR," +
                "balance REAL" +
                " );");


        db.execSQL("CREATE TABLE IF NOT EXISTS TransactionD(" +
                "id INTEGER PRIMARY KEY," +
                "Acc_No VARCHAR," +
                "Type INT," +
                "amount REAL," +
                "date DATE," +
                "FOREIGN KEY (Acc_No) REFERENCES Account(Acc_No)" +
                ");");

        EmbeddedAccountDAO accountDAO = new EmbeddedAccountDAO(db);

        setAccountsDAO(accountDAO);

        setTransactionsDAO(new EmbeddedTransactionDAO(db));
        // dummy data
       /* Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
      /*  getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);*/

        /*** End ***/
    }
}