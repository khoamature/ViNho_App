package fpt.edu.vn.vinho_app.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.GetBudgetResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.category.GetCategoryResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.GetConversationResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.message.GetMessageResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.GetReportResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.GetTransactionResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.user.GetProfileResponse;
import fpt.edu.vn.vinho_app.domain.model.Budget;
import fpt.edu.vn.vinho_app.domain.model.Category;
import fpt.edu.vn.vinho_app.domain.model.Conversation;
import fpt.edu.vn.vinho_app.domain.model.Message;
import fpt.edu.vn.vinho_app.domain.model.Report;
import fpt.edu.vn.vinho_app.domain.model.Transaction;
import fpt.edu.vn.vinho_app.domain.model.User;

public class DataMapper {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static long dateToTimestamp(String dateString) {
        if (dateString == null) {
            return 0;
        }
        try {
            return sdf.parse(dateString).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static User mapUser(GetProfileResponse response) {
        User user = new User();
        user.setUserId(response.getId());
        user.setEmail(response.getEmail());
        user.setFullName(response.getFullName());
        user.setCreatedAt(dateToTimestamp(response.getCreatedAt()));
        user.setActive(response.isActive());
        return user;
    }

    public static Transaction mapTransaction(GetTransactionResponse response) {
        Transaction transaction = new Transaction();
        transaction.setId(response.getId());
        transaction.setUserId(response.getUserId());
        transaction.setCategoryId(response.getCategoryId());
        transaction.setAmount(response.getAmount());
        transaction.setDescription(response.getDescription());
        transaction.setTransactionDate(dateToTimestamp(response.getTransactionDate()));
        transaction.setNotificationEnabled(response.isNotificationEnabled());
        transaction.setNotificationSentAt(dateToTimestamp(response.getNotificationSentAt()));
        transaction.setAddedToReports(response.isAddedToReports());
        return transaction;
    }

    public static Category mapCategory(GetCategoryResponse response) {
        Category category = new Category();
        category.setId(response.getId());
        category.setUserId(response.getUserId());
        category.setName(response.getName());
        category.setDescription(response.getDescription());
        category.setType(response.getCategoryType());
        return category;
    }

    public static Budget mapBudget(GetBudgetResponse response) {
        Budget budget = new Budget();
        budget.setId(response.getId());
        budget.setUserId(response.getUserId());
        budget.setCategoryId(response.getCategoryId());
        budget.setLimitAmount(response.getLimitAmount());
        budget.setMonth(response.getMonth());
        return budget;
    }

    public static Report mapReport(GetReportResponse response) {
        Report report = new Report();
        report.setId(response.getId());
        report.setUserId(response.getUserId());
        report.setMonth(response.getMonth());
        report.setAiSuggestion(response.getAiSuggestion());
        report.setTotalIncome(response.getTotalIncome());
        report.setTotalExpense(response.getTotalExpense());
        return report;
    }

    public static Conversation mapConversation(GetConversationResponse response) {
        Conversation conversation = new Conversation();
        conversation.setId(response.getId());
        conversation.setUserId(response.getUserId());
        conversation.setTitle(response.getTitle());
        conversation.setCreatedAt(dateToTimestamp(response.getCreatedAt()));
        conversation.setUpdatedAt(dateToTimestamp(response.getUpdatedAt()));
        return conversation;
    }

    public static Message mapMessage(GetMessageResponse response) {
        Message message = new Message();
        message.setId(response.getId());
        message.setConversationId(response.getConversationId());
        message.setSender(response.getSender());
        message.setContent(response.getContent());
        message.setCreatedAt(dateToTimestamp(response.getCreatedAt()));
        message.setUpdatedAt(dateToTimestamp(response.getUpdatedAt()));
        return message;
    }
}
