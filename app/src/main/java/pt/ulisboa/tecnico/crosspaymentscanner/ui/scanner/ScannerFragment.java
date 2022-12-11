package pt.ulisboa.tecnico.crosspaymentscanner.ui.scanner;

import static com.journeyapps.barcodescanner.ScanOptions.QR_CODE;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;
import java.util.Base64;

import pt.ulisboa.tecnico.crosspaymentscanner.CROSSPaymentScannerApp;
import pt.ulisboa.tecnico.crosspaymentscanner.R;
import pt.ulisboa.tecnico.crosspaymentscanner.api.APIManager;
import pt.ulisboa.tecnico.crosspaymentscanner.databinding.FragmentScannerBinding;
import pt.ulisboa.tecnico.crosspaymentscanner.ui.fullscreen.FullscreenFragment;
import timber.log.Timber;

public class ScannerFragment extends FullscreenFragment {

  private FragmentScannerBinding binding;

  private final ActivityResultLauncher<ScanOptions> scanner =
      registerForActivityResult(
          new ScanContract(), result -> new Thread(() -> onScanned(result)).start());

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScannerBinding.inflate(inflater, container, false);
    onFragmentBindingCreated(binding.contentLayout, null);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.scan.setOnClickListener(this::onScan);
    binding.price.addTextChangedListener(
        new AfterTextChangedListener() {
          @Override
          public void afterTextChanged(Editable s) {
            if (binding.price.length() > 0) enableButton(binding.scan);
            else disableButton(binding.scan);
          }
        });
  }

  private void onScan(View view) {
    disableButton(binding.scan);
    binding.loading.setVisibility(View.VISIBLE);
    ScanOptions scanOptions = new ScanOptions();
    scanOptions.setPrompt(getString(R.string.scan));
    scanOptions.setDesiredBarcodeFormats(QR_CODE);
    scanner.launch(scanOptions);
  }

  private void onScanned(ScanIntentResult result) {
    if (result.getContents() == null) {
      CROSSPaymentScannerApp.get().showToast("Scanning failed!");
    } else {
      CROSSPaymentScannerApp.get().showToast("Scanning successful!");

      byte[] encryptedJwt = Base64.getDecoder().decode(result.getContents());
      int gems = Integer.parseInt(binding.price.getText().toString());
      try {
        APIManager.get().getPaymentAPI().payment(encryptedJwt, gems);

        requireActivity()
            .runOnUiThread(
                () -> {
                  Drawable drawable =
                      DrawableCompat.wrap(
                          ContextCompat.getDrawable(
                              getContext(), R.drawable.baseline_thumb_up_alt_24));
                  int blue = ContextCompat.getColor(getContext(), R.color.green);
                  DrawableCompat.setTint(drawable, blue);
                  AlertDialog alertDialog =
                      new AlertDialog.Builder(getContext())
                          .setCancelable(true)
                          .setIcon(drawable)
                          .setTitle("Payment")
                          .setMessage("Payment successful!")
                          .create();
                  alertDialog.show();
                  enableButton(binding.scan);
                  binding.loading.setVisibility(View.GONE);
                });
      } catch (IOException e) {
        Timber.e(e);

        requireActivity()
            .runOnUiThread(
                () -> {
                  Drawable drawable =
                      DrawableCompat.wrap(
                          ContextCompat.getDrawable(
                              getContext(), R.drawable.baseline_thumb_down_alt_24));
                  int blue = ContextCompat.getColor(getContext(), R.color.red);
                  DrawableCompat.setTint(drawable, blue);
                  AlertDialog alertDialog =
                      new AlertDialog.Builder(getContext())
                          .setCancelable(true)
                          .setIcon(drawable)
                          .setTitle("Payment")
                          .setMessage("Payment failed: " + e.getMessage())
                          .create();
                  alertDialog.show();
                  enableButton(binding.scan);
                  binding.loading.setVisibility(View.GONE);
                });
      }
    }
  }

  public static void disableButton(Button button) {
    button.setEnabled(false);
    button.setBackgroundColor(ContextCompat.getColor(button.getContext(), R.color.grey_lighten));
    button.setAlpha(.8F);
  }

  public static void enableButton(Button button) {
    button.setAlpha(1);
    button.setBackgroundColor(ContextCompat.getColor(button.getContext(), R.color.blue));
    button.setEnabled(true);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  public abstract static class AfterTextChangedListener implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
  }
}
