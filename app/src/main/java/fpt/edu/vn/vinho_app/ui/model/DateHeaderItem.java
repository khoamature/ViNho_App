package fpt.edu.vn.vinho_app.ui.model;

public class DateHeaderItem implements DisplayableItem{
    private String date;
    private double dailyTotal;

    public DateHeaderItem(String date, double dailyTotal) {
        this.date = date;
        this.dailyTotal = dailyTotal;
    }

    public String getDate() {
        return date;
    }

    public double getDailyTotal() {
        return dailyTotal;
    }
}
