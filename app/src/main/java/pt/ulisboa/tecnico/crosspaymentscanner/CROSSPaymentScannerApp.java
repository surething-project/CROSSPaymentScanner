package pt.ulisboa.tecnico.crosspaymentscanner;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Properties;

import timber.log.Timber;

public class CROSSPaymentScannerApp extends Application {

  private final Properties properties = new Properties();

  private static CROSSPaymentScannerApp instance;

  public static CROSSPaymentScannerApp get() {
    return instance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;

    plantTimber();
    loadAppProperties();
  }

  private void plantTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(
          new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(@NonNull StackTraceElement element) {
              return String.format(
                  "%s[%s:%s:%s]",
                  getString(R.string.app_name).replaceAll("\\s+", ""),
                  super.createStackElementTag(element),
                  element.getMethodName(),
                  element.getLineNumber());
            }
          });
    }
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  private void loadAppProperties() {
    try {
      properties.load(getAssets().open("CROSSPaymentScannerApp.properties"));
    } catch (IOException e) {
      Timber.e(e, "Error loading properties.");
      System.exit(1);
    }
  }

  public void showToast(String message) {
    if (Looper.getMainLooper().isCurrentThread()) createToast(message).show();
    else new Handler(Looper.getMainLooper()).post(() -> createToast(message).show());
    Timber.w("Showing toast message: %s", message);
  }

  private Toast createToast(String message) {
    return Toast.makeText(this, message, Toast.LENGTH_LONG);
  }
}
