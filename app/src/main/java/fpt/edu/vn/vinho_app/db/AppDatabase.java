package fpt.edu.vn.vinho_app.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import fpt.edu.vn.vinho_app.daos.BudgetDao;
import fpt.edu.vn.vinho_app.daos.CategoryDao;
import fpt.edu.vn.vinho_app.daos.ReportDao;
import fpt.edu.vn.vinho_app.daos.TransactionDao;
import fpt.edu.vn.vinho_app.models.Budget;
import fpt.edu.vn.vinho_app.models.Category;
import fpt.edu.vn.vinho_app.models.Report;
import fpt.edu.vn.vinho_app.models.Transaction;

@Database(entities = {Budget.class, Transaction.class, Report.class, Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BudgetDao budgetDao();
    public abstract TransactionDao transactionDao();
    public abstract ReportDao reportDao();
    public abstract CategoryDao categoryDao();
}
