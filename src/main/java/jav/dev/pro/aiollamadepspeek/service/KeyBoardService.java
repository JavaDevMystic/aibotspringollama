package jav.dev.pro.aiollamadepspeek.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyBoardService {


    /// //////////////   Create phone button  //////////////////////////
    public ReplyKeyboardMarkup createPhoneRequestKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        KeyboardButton contactButton = new KeyboardButton("📞 Telefon raqamni yuborish");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup keyboardButtonUserHelper() {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("📜 Show Question History"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("⏱ Start Pamidor Timer"));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("🧑‍💻 Delete User"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Pamidor Timer");
        // ❌ callbackData olib tashlanadi
        button.setWebApp(new WebAppInfo("https://magenta-travesseiro-bd64aa.netlify.app/"));

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(row);

        markup.setKeyboard(buttons);
        return markup;
    }


}
