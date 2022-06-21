package com.dergoogler.phh.flashlight;

import static com.dergoogler.phh.flashlight.util.SuperUser.BINARY_PHH_GSI;
import static com.dergoogler.phh.flashlight.util.SuperUser.BINARY_SU;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import com.dergoogler.phh.flashlight.controllers.FlashlightController;
import com.dergoogler.phh.flashlight.util.Link;
import com.dergoogler.phh.flashlight.util.SuperUser;
import com.dergoogler.phh.flashlight.util.SystemProperties;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

public class MainActivity extends Activity {
    private MaterialButton turn_on_off_button;
    private Slider flash_strength_slider;
    private TextView flash_strength_state;
    private MaterialToolbar action_bar;
    private FlashlightController fc;
    private Link link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        checkRoot();
        checkSystem();

        fc = new FlashlightController(this);
        link = new Link(this);

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
    }

    private void checkRoot() {
        // Try getting root access
        if (!SuperUser.checkBinary(BINARY_SU)) {
            MaterialAlertDialogBuilder builder =
                    new MaterialAlertDialogBuilder(this);
            builder
                    .setTitle("No Root")
                    .setMessage("The app wasn't able to find your su binary")
                    .setCancelable(false)
                    .setPositiveButton(R.string.quit, (x, y) -> finish());
            builder.show();
        }
    }

    private void checkSystem() {
        // Checking if system is an phh gsi system
        if (!SuperUser.checkBinary(BINARY_PHH_GSI)) {
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
