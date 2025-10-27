package fpt.edu.vn.vinho_app.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "name",
                childColumns = "category_name",
                onDelete = ForeignKey.CASCADE), // Optional: If a category is deleted, delete its budgets
        indices = {@Index("category_name")})
public class Budget {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "category_name")
    private String categoryName;  // Liên kết với Category
    @ColumnInfo(name = "limit_amount")
    private double limitAmount;
    @ColumnInfo(name = "month")
    private String month;         // Ví dụ: "2025-10"

    public Budget(String categoryName, double limitAmount, String month) {
        this.categoryName = categoryName;
        this.limitAmount = limitAmount;
        this.month = month;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
