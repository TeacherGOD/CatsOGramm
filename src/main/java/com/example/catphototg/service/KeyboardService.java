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

@Service
@RequiredArgsConstructor
public class KeyboardService {

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

    private InlineKeyboardButton createButton(String text, String callback) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callback);
        return button;
    }
    public InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton(BotConstants.SHOW_CATS_COMMAND, BotConstants.VIEW_CATS_ACTION));
        row1.add(createButton(BotConstants.ADD_CAT_COMMAND, BotConstants.ADD_CAT_ACTION));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton(BotConstants.MY_CATS_COMMAND, BotConstants.MY_CATS_ACTION));
        row2.add(createButton(BotConstants.CHANGE_NAME_COMMAND, BotConstants.CHANGE_NAME_ACTION));

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }
}
