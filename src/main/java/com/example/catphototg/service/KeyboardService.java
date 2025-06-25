package com.example.catphototg.service;

import com.example.catphototg.entity.ui.Button;
import com.example.catphototg.entity.ui.Keyboard;
import com.example.catphototg.entity.ui.KeyboardRow;
import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.entity.Cat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.catphototg.constants.BotConstants.*;

@Service
@RequiredArgsConstructor
public class KeyboardService {

    public Keyboard mainMenuKeyboard() {
        return new Keyboard(List.of(
                new KeyboardRow(List.of(
                        new Button(SHOW_CATS_COMMAND, VIEW_CATS_ACTION),
                        new Button(ADD_CAT_COMMAND, ADD_CAT_ACTION)
                )),
                new KeyboardRow(List.of(
                        new Button(MY_CATS_COMMAND, MY_CATS_ACTION),
                        new Button(CHANGE_NAME_COMMAND, CHANGE_NAME_ACTION)
                ))
        ));
    }

    public Keyboard cancelKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();
        List<Button> row = new ArrayList<>();
        row.add(new Button(CANCEL_BUTTON, CANCEL_ACTION));
        rows.add(new KeyboardRow(row));
        return new Keyboard(rows);
    }

    public Keyboard confirmationKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();
        List<Button> row = new ArrayList<>();
        row.add(new Button(CONFIRM_BUTTON, CONFIRM_CAT_ACTION));
        row.add(new Button(CANCEL_BUTTON, CANCEL_CAT_ACTION));
        rows.add(new KeyboardRow(row));
        return new Keyboard(rows);
    }

    public Keyboard createCatsKeyboard(Page<Cat> catPage, int currentPage) {
        List<KeyboardRow> rows = new ArrayList<>();

        for (Cat cat : catPage.getContent()) {
            Button catButton = new Button(cat.getName(), BotConstants.CAT_DETAILS_PREFIX + cat.getId());
            rows.add(new KeyboardRow(Collections.singletonList(catButton)));
        }

        List<Button> navButtons = new ArrayList<>();
        if (currentPage > 0) {
            navButtons.add(new Button(BotConstants.PREV_PAGE_BUTTON, BotConstants.PREV_PAGE_ACTION));
        }
        navButtons.add(new Button(BotConstants.BACK_TO_MENU_BUTTON, BotConstants.BACK_TO_MENU_ACTION));
        if (catPage.hasNext()) {
            navButtons.add(new Button(BotConstants.NEXT_PAGE_BUTTON, BotConstants.NEXT_PAGE_ACTION));
        }
        rows.add(new KeyboardRow(navButtons));

        return new Keyboard(rows);
    }


}