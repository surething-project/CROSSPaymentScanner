package pt.ulisboa.tecnico.crosspaymentscanner.api.user;

import pt.ulisboa.tecnico.cross.contract.gamification.PaymentScanning.Transaction;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface PaymentService {

  @PUT("payment")
  Call<Void> payment(@Body Transaction transaction);
}
