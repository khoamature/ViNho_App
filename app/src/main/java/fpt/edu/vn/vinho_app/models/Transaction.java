package fpt.edu.vn.vinho_app.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions",
        // Define the foreign key
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "name",   // The column in the parent table (Category)
                childColumns = "category_name", // The column in this table (Transaction)
                onDelete = ForeignKey.CASCADE), // Optional: If a category is deleted, delete its transactions
        // Add an index on the foreign key column for better performance
        indices = {@Index("category_name")})
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "amount")
    private double amount;       // Số tiền
    @ColumnInfo(name = "type")
    private String type;         // "income" or "expense"
    @ColumnInfo(name = "category_name")
    private String categoryName; // Liên kết Category
    @ColumnInfo(name = "date")
    private String date;         // Ngày
    @ColumnInfo(name = "note")
    private String note;         // Ghi chú

    public Transaction(double amount, String type, String categoryName, String date, String note) {
        this.amount = amount;
        this.type = type;
        this.categoryName = categoryName;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
