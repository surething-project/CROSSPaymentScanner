package pt.ulisboa.tecnico.crosspaymentscanner.api.user;

import com.google.protobuf.ByteString;

import java.io.IOException;

import pt.ulisboa.tecnico.cross.contract.gamification.PaymentScanning;
import pt.ulisboa.tecnico.crosspaymentscanner.api.CallWrapper;
import retrofit2.Retrofit;

public class PaymentAPI {

  private final PaymentService paymentService;

  public PaymentAPI(Retrofit retrofit) {
    paymentService = retrofit.create(PaymentService.class);
  }

  public void payment(byte[] encryptedJwt, int gems) throws IOException {
    PaymentScanning.Transaction transaction =
        PaymentScanning.Transaction.newBuilder()
            .setEncryptedJwt(ByteString.copyFrom(encryptedJwt))
            .setGems(gems)
            .build();
    new CallWrapper<>(paymentService.payment(transaction)).execute();
  }
}
