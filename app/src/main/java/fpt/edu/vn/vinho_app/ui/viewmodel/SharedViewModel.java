package fpt.edu.vn.vinho_app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel{
    // LiveData để chứa "sự kiện" cần làm mới transaction
    private final MutableLiveData<Boolean> transactionAddedEvent = new MutableLiveData<>();

    // Phương thức để các fragment khác có thể quan sát
    public LiveData<Boolean> getTransactionAddedEvent() {
        return transactionAddedEvent;
    }

    // Phương thức để AddTransactionFragment kích hoạt sự kiện
    public void notifyTransactionAdded() {
        transactionAddedEvent.setValue(true);
    }

    // Phương thức để reset sự kiện sau khi đã xử lý
    public void onTransactionEventHandled() {
        transactionAddedEvent.setValue(false);
    }
}
