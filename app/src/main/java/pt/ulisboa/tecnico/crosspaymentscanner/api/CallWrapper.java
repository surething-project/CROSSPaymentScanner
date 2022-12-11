package pt.ulisboa.tecnico.crosspaymentscanner.api;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class CallWrapper<T> {

  private final Call<T> call;

  public CallWrapper(Call<T> call) {
    this.call = call;
  }

  public T execute() throws IOException {
    try {
      Response<T> response = call.execute();
      if (response.isSuccessful()) {
        Timber.i("%s: %s", response, response.body());
        return response.body();
      } else {
        String errorMessage = response.errorBody().string();
        Timber.e("%s: %s", response, errorMessage);
        throw new IOException(errorMessage);
      }
    } catch (IOException e) {
      Timber.e(e, "%s call failed to execute.", call.request().method());
      throw e;
    }
  }
}
