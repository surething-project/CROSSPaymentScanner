package pt.ulisboa.tecnico.crosspaymentscanner.api;

import pt.ulisboa.tecnico.crosspaymentscanner.api.user.PaymentAPI;
import retrofit2.Retrofit;

public class APIManager {

  private final PaymentAPI paymentAPI;

  public static APIManager get() {
    return APIManagerHolder.INSTANCE;
  }

  private APIManager() {
    Retrofit retrofit = APIClient.newRetrofit();
    paymentAPI = new PaymentAPI(retrofit);
  }

  public PaymentAPI getPaymentAPI() {
    return paymentAPI;
  }

  private static class APIManagerHolder {
    private static final APIManager INSTANCE = new APIManager();
  }
}
