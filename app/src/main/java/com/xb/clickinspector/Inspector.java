package com.xb.clickinspector;

/**
 * Created by xb on 23/2/2017.
 */

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Inspector implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        findAndHookMethod("android.view.View", lpparam.classLoader, "performClick", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // Before performClick
                View element = (View) param.thisObject;
                String className = element.getClass().getName();
                String resourceId = String.valueOf(element.getTag());
                String contentDesc = String.valueOf(element.getContentDescription());
                String text = "null";
                try {
                    Button btn = (Button) element;
                    text = btn.getText().toString();
                } catch (Exception e) {
                    //Not a button
                }
                try {
                    EditText editText = (EditText) element;
                    text = editText.getText().toString();
                } catch (Exception e) {
                    //Not an EditText
                }
                try {
                    TextView textView = (TextView) element;
                    text = textView.getText().toString();
                } catch (Exception e) {
                    //Not a TextView
                }

                XposedBridge.log(String.format("Click: <%1$s, %2$s, %3$s, %4$s>", className, resourceId, text, contentDesc));
            }
        });
        findAndHookMethod("android.app.Activity", lpparam.classLoader, "startActivity", Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Intent intent = (Intent) param.args[0];
                String activity = intent.getComponent().getClassName();
                String action = intent.getAction();
                String data = intent.getDataString();
                XposedBridge.log(String.format("Start activity: <%1$s, %2$s, %3$s>", activity, action, data));
            }
        });
    }
}