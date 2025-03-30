package me.s1204.benesse.touch.test.service.ct3;

import android.app.Activity;
import android.content.Intent;
import android.os.BenesseExtension;
import android.os.Bundle;
import android.os.IBenesseExtensionService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class Tester extends Activity implements View.OnClickListener {

    static IBenesseExtensionService mBenesseExtensionService;

    private void makeText(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    private void setOnClickListener(int resId) {
        findViewById(resId).setOnClickListener(this);
    }

    private void changeLayout(int layout, int btnId) {
        setContentView(layout);
        findViewById(btnId).setOnClickListener(this);
        findViewById(R.id.backHome).setOnClickListener(this);
    }

    private String getBoxText(int resId) {
        return ((EditText) findViewById(resId)).getText().toString();
    }

    private String getPullText(int resId) {
        return ((Spinner) findViewById(resId)).getSelectedItem().toString();
    }

    private static final int[] FUNC_LIST = {
            R.id.btn_checkPassword,
            R.id.btn_checkUsbCam,
            R.id.btn_getBaseDisplaySize,
            R.id.btn_getDchaState,
            R.id.btn_getInitialDisplaySize,
            R.id.btn_getLcdSize,
            R.id.btn_getString,
            R.id.btn_setDchaState,
            R.id.btn_setForcedDisplaySize
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mBenesseExtensionService = IBenesseExtensionService.Stub.asInterface(ServiceManager.getService("benesse_extension"));
        } catch (RuntimeException ignored) {
            makeText("Runtime Exception");
        }
        setContentView(R.layout.layout);
        for (int resId: FUNC_LIST) setOnClickListener(resId);
    }

    @Override
    public void onClick(final View v) {
        final int resId = v.getId();
        try {
            if (resId == R.id.backHome) {
                //noinspection deprecation
                onBackPressed();
            } else if (resId == R.id.btn_checkPassword) {
                changeLayout(R.layout.layout_checkpassword, R.id.exec_checkPassword);
            } else if (resId == R.id.exec_checkPassword) {
                String passwordText = getBoxText(R.id.passwordText);
                makeText("checkPassword：" + mBenesseExtensionService.checkPassword(passwordText));
            } else if (resId == R.id.btn_checkUsbCam) {
                makeText("checkUsbCam：" + mBenesseExtensionService.checkUsbCam());
            } else if (resId == R.id.btn_getBaseDisplaySize) {
                makeText("getBaseDisplaySize：" + BenesseExtension.getBaseDisplaySize());
            } else if (resId == R.id.btn_getDchaState) {
                makeText("getDchaState：" + mBenesseExtensionService.getDchaState());
            } else if (resId == R.id.btn_getInitialDisplaySize) {
                makeText("getInitialDisplaySize：" + BenesseExtension.getInitialDisplaySize());
            } else if (resId == R.id.btn_getLcdSize) {
                makeText("getLcdSize：" + BenesseExtension.getLcdSize());
            } else if (resId == R.id.btn_getString) {
                changeLayout(R.layout.layout_getstring, R.id.exec_getString);
            } else if (resId == R.id.exec_getString) {
                String name = getPullText(R.id.name_getString);
                makeText(name.isEmpty() ? "値を入力してください" : "getString：" + mBenesseExtensionService.getString(name));
            } else if (resId == R.id.btn_setDchaState) {
                changeLayout(R.layout.layout_setdchastate, R.id.setDchaState_0);
                Arrays.asList(R.id.setDchaState_1, R.id.setDchaState_2, R.id.setDchaState_3)
                        .forEach(this::setOnClickListener);
            } else if (resId == R.id.setDchaState_0) {
                mBenesseExtensionService.setDchaState(0);
            } else if (resId == R.id.setDchaState_1) {
                mBenesseExtensionService.setDchaState(1);
            } else if (resId == R.id.setDchaState_2) {
                mBenesseExtensionService.setDchaState(2);
            } else if (resId == R.id.setDchaState_3) {
                mBenesseExtensionService.setDchaState(3);
            } else if (resId == R.id.btn_setForcedDisplaySize) {
                changeLayout(R.layout.layout_setforceddisplaysize, R.id.exec_setForcedDisplaySize);
            } else if (resId ==R.id.exec_setForcedDisplaySize) {
                String width = getBoxText(R.id.width);
                String height = getBoxText(R.id.height);
                makeText(width.isEmpty() || height.isEmpty() ? "値を入力してください" : "setForcedDisplaySize：" + BenesseExtension.setForcedDisplaySize(Integer.parseInt(width), Integer.parseInt(height)));
            }
        } catch (RemoteException ignored) {
            makeText("");
        } catch (NoClassDefFoundError ignored) {
            makeText("BenesseExtension が存在しません");
            finish();
        } catch (NoSuchMethodError ignored) {
            makeText("関数が存在しません");
        } catch (SecurityException ignored) {
            makeText("関数の実行に失敗しました");
        }
    }

    /** @noinspection DeprecatedIsStillUsed*/
    @Override
    @Deprecated
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                .setPackage(getPackageName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /** @noinspection NullableProblems*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if (itemId == R.id.menu_about) {
            setContentView(R.layout.about);
            return true;
        } else if (itemId == R.id.menu_settings) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
            return true;
        } else if (itemId == R.id.menu_devopts) {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
