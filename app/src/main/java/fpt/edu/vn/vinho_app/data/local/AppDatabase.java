package fpt.edu.vn.vinho_app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import fpt.edu.vn.vinho_app.data.local.dao.BudgetDao;
import fpt.edu.vn.vinho_app.data.local.dao.CategoryDao;
import fpt.edu.vn.vinho_app.data.local.dao.ConversationDao;
import fpt.edu.vn.vinho_app.data.local.dao.MessageDao;
import fpt.edu.vn.vinho_app.data.local.dao.ReportDao;
import fpt.edu.vn.vinho_app.data.local.dao.TransactionDao;
import fpt.edu.vn.vinho_app.data.local.dao.UserDao;
import fpt.edu.vn.vinho_app.domain.model.Budget;
import fpt.edu.vn.vinho_app.domain.model.Category;
import fpt.edu.vn.vinho_app.domain.model.Conversation;
import fpt.edu.vn.vinho_app.domain.model.Message;
import fpt.edu.vn.vinho_app.domain.model.Report;
import fpt.edu.vn.vinho_app.domain.model.Transaction;
import fpt.edu.vn.vinho_app.domain.model.User;

@Database(entities = {User.class, Budget.class, Transaction.class, Report.class, Category.class, Conversation.class, Message.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract BudgetDao budgetDao();
    public abstract TransactionDao transactionDao();
    public abstract ReportDao reportDao();
    public abstract CategoryDao categoryDao();
    public abstract ConversationDao conversationDao();
    public abstract MessageDao messageDao();
}
