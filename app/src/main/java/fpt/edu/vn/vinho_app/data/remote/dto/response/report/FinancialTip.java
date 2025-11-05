package fpt.edu.vn.vinho_app.data.remote.dto.response.report;

public class FinancialTip {private String id;
    private String title;
    private String description;
    private String impact; // "HighImpact", "MediumImpact"
    private String type;

    // Getters and Setters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImpact() { return impact; }
}
