package com.dergoogler.phh.flashlight;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.dergoogler.phh.flashlight.controllers.FlashlightController;
import com.dergoogler.phh.flashlight.util.HexColorValidator;
import com.dergoogler.phh.flashlight.util.Link;
import com.dergoogler.phh.flashlight.util.Shell;
import com.dergoogler.phh.flashlight.util.SystemProperties;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends Activity {
    private MaterialButton turn_on_off_button;
    private Slider flash_strength_slider;
    private TextView flash_strength_state;
    private MaterialToolbar action_bar;
    private FlashlightController fc;
    private Link link;
    private TextInputEditText fingerprint_color_input;
    private TextInputLayout fingerprint_color_input_layout;
    private MaterialButton set_fingerprint_color;
    private MaterialButton set_fingerprint_default_color;
    private boolean isError = false;
    private String SamsungFOD = SystemProperties.get("persist.sys.phh.fod.samsung");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        checkSystem();

        fc = new FlashlightController(this);
        link = new Link(this);

        // Try getting root access
        if (!Shell.rootAccess()) {
            try {
                Shell.exec("ls");
            } catch (Exception e) {
                toast(e.toString());
            }
        }

        // Elements - Flashlight
        turn_on_off_button = findViewById(R.id.turn_on_off_button);
        flash_strength_slider = findViewById(R.id.flash_strength_slider);
        flash_strength_state = findViewById(R.id.flash_strength_state);
        action_bar = findViewById(R.id.action_bar);

        // Change state to current value
        String strengthState = SystemProperties.get("persist.sys.phh.flash_strength");
        flash_strength_slider.setValue(Float.parseFloat(strengthState));
        flash_strength_state.setText(String.format(getString(R.string.flash_strength_count), strengthState));

        action_bar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.open_github) {
                link.open("https://github.com/DerGoogler/a70_flashlight_strength_controller");
            } else {
                return false;
            }
            return true;
        });

        turn_on_off_button.setOnClickListener((v) -> {
            try {
                if (fc.hasFlashlight() || fc.isAvailable()) {
                    fc.setFlashlight(!fc.isEnabled());
                } else {
                    toast("Unable to turn on flashlight");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (!fc.isEnabled()) {
            turn_on_off_button.setText(String.format(getString(R.string.flashlight_state), "off"));
        }

        fc.addListener(new FlashlightController.FlashlightListener() {
            @Override
            public void onFlashlightChanged(boolean enabled) {
                if (enabled) {
                    turn_on_off_button.setText(String.format(getString(R.string.flashlight_state), "on"));
                } else {
                    turn_on_off_button.setText(String.format(getString(R.string.flashlight_state), "off"));
                }
            }

            @Override
            public void onFlashlightError() {

            }

            @Override
            public void onFlashlightAvailabilityChanged(boolean available) {

            }
        });

        flash_strength_slider.addOnChangeListener((slider, value, fromUser) -> {
            String currentValue = String.valueOf(value);
            try {
                SystemProperties.set("persist.sys.phh.flash_strength", currentValue);
                flash_strength_state.setText(String.format(getString(R.string.flash_strength_count), currentValue));
                if (fc.isEnabled()) {
                    fc.setFlashlight(false);
                    fc.setFlashlight(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Fingerprint
        // Elements - Fingerprint
        fingerprint_color_input_layout = findViewById(R.id.fingerprint_color_input_layout);
        fingerprint_color_input = findViewById(R.id.fingerprint_color_input);
        set_fingerprint_color = findViewById(R.id.set_fingerprint_color);
        set_fingerprint_default_color = findViewById(R.id.set_fingerprint_default_color);

        String input = fingerprint_color_input_layout.getEditText().getText().toString();
        String prefixText = Objects.requireNonNull(fingerprint_color_input_layout.getPrefixText()).toString();

        if (input.equals("")) {
            set_fingerprint_color.setEnabled(false);
        }

        fingerprint_color_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!HexColorValidator.validate(charSequence.toString())) {
                    isError = true;
                    fingerprint_color_input.setError(prefixText + charSequence + " is not valid");
                } else {
                    isError = false;
                }
                set_fingerprint_color.setEnabled(!isError);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        set_fingerprint_color.setOnClickListener(view -> {
            if (SamsungFOD.equals("") || SamsungFOD.equals("false")) {
                toast("'persist.sys.phh.fod.samsung' are not existing or is set to false");
            } else {
                SystemProperties.set("persist.sys.phh.fod_color", prefixText + input);
                toast("Use " + prefixText + input + " from now");
            }
        });
    }

    private void checkSystem() {
        // Checking if system is an phh gsi system
        if (SystemProperties.get("persist.sys.phh.dynamic_superuser").equals("")) {
            MaterialAlertDialogBuilder builder =
                    new MaterialAlertDialogBuilder(this);
            builder
                    .setTitle("Unable")
                    .setMessage("Your system isn't an Phh GSI System :(")
                    .setCancelable(false)
                    .setPositiveButton(R.string.quit, (x, y) -> finish());
            builder.show();
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
