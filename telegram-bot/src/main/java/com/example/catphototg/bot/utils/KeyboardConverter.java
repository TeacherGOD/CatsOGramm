// KeyboardConverter.java
package com.example.catphototg.bot.utils;


import com.example.catphototg.bot.entity.ui.Button;
import com.example.catphototg.bot.entity.ui.Keyboard;
import com.example.catphototg.bot.entity.ui.KeyboardRow;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardConverter {
    public InlineKeyboardMarkup convert(Keyboard keyboard) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (KeyboardRow row : keyboard.rows()) {
            List<InlineKeyboardButton> telegramRow = new ArrayList<>();

            for (Button button : row.buttons()) {
                InlineKeyboardButton telegramButton = new InlineKeyboardButton(button.text());
                telegramButton.setCallbackData(button.action());
                telegramRow.add(telegramButton);
            }

            rows.add(telegramRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }
}