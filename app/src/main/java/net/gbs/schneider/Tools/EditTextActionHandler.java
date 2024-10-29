package net.gbs.schneider.Tools;

import android.view.KeyEvent;

import com.google.android.material.textfield.TextInputLayout;

public class EditTextActionHandler {
    public static void OnEnterKeyPressed (TextInputLayout textInputLayout,OnEnterKeyPressed onEnterKeyPressed){
        textInputLayout.getEditText().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            {
                onEnterKeyPressed.OnKeyPressed();
                return true;
            }
            return false;
        });
    }
    public interface OnEnterKeyPressed {
        void OnKeyPressed();
    }
}
