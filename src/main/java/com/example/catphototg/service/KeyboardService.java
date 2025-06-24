package com.example.catphototg.service;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.entity.Cat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.catphototg.constants.BotConstants.*;

@Service
@RequiredArgsConstructor
public class KeyboardService {

    public InlineKeyboardMarkup mainMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton(SHOW_CATS_COMMAND, VIEW_CATS_ACTION));
        row1.add(createButton(ADD_CAT_COMMAND, ADD_CAT_ACTION));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton(MY_CATS_COMMAND, MY_CATS_ACTION));
        row2.add(createButton(CHANGE_NAME_COMMAND, CHANGE_NAME_ACTION));

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup createCatsKeyboard(Page<Cat> catPage, int currentPage) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Cat cat : catPage.getContent()) {
            rows.add(Collections.singletonList(
                    createButton(cat.getName(), BotConstants.CAT_DETAILS_PREFIX + cat.getId())
            ));
        }
        List<InlineKeyboardButton> navRow = new ArrayList<>();
        if (currentPage > 0) {
            navRow.add(createButton(BotConstants.PREV_PAGE_BUTTON, BotConstants.PREV_PAGE_ACTION));
        }
        navRow.add(createButton(BotConstants.BACK_TO_MENU_BUTTON, BotConstants.BACK_TO_MENU_ACTION));
        if (catPage.hasNext()) {
            navRow.add(createButton(BotConstants.NEXT_PAGE_BUTTON, BotConstants.NEXT_PAGE_ACTION));
        }
        rows.add(navRow);

        return new InlineKeyboardMarkup(rows);
    }
    public InlineKeyboardMarkup cancelKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(CANCEL_BUTTON, CANCEL_ACTION));

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup confirmationKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(CONFIRM_BUTTON, CONFIRM_CAT_ACTION));
        row.add(createButton(CANCEL_BUTTON, CANCEL_CAT_ACTION));

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
    }
}
