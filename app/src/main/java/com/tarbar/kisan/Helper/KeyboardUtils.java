package com.tarbar.kisan.Helper;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtils {
    public static void setHindiKeyboard(EditText editText) {
        editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);

        // Add an InputFilter to allow only Hindi characters
        InputFilter hindiFilter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!isHindiCharacter(source.charAt(i))) {
                    return ""; // Block non-Hindi characters
                }
            }
            return null; // Allow Hindi characters
        };
        editText.setFilters(new InputFilter[]{hindiFilter});

        // Show the input method picker (optional)
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showInputMethodPicker();
        }
    }

    private static boolean isHindiCharacter(char c) {
        // Check if the character is within the Hindi Unicode range
        return (c >= 0x0900 && c <= 0x097F) || (c >= 0x1CD0 && c <= 0x1CFF);
    }
}