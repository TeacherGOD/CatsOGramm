package com.example.catphototg.service;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.ui.Button;
import com.example.catphototg.entity.ui.Keyboard;
import com.example.catphototg.entity.ui.KeyboardRow;
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
            Button catButton = new Button(cat.getName(), CAT_DETAILS_PREFIX + cat.getId());
            rows.add(new KeyboardRow(Collections.singletonList(catButton)));
        }

        List<Button> navButtons = new ArrayList<>();
        if (currentPage > 0) {
            navButtons.add(new Button(PREV_PAGE_BUTTON, PREV_PAGE_ACTION));
        }
        navButtons.add(new Button(BACK_TO_MENU_BUTTON, BACK_TO_MENU_ACTION));
        if (catPage.hasNext()) {
            navButtons.add(new Button(NEXT_PAGE_BUTTON, NEXT_PAGE_ACTION));
        }
        rows.add(new KeyboardRow(navButtons));

        return new Keyboard(rows);
    }
    
    public Keyboard createCatDetailsKeyboard(Long catId) {
        return new Keyboard(List.of(
                new KeyboardRow(List.of(
                        new Button(DELETE_BUTTON, DELETE_CAT_PREFIX + catId)
                )),
                new KeyboardRow(List.of(
                        new Button(PREV_PAGE_BUTTON, BACK_TO_MY_CATS_ACTION)
                ))
        ));
    }

    public Keyboard createReactionKeyboard(Long catId, int likeCount, int dislikeCount) {
        List<KeyboardRow> rows = new ArrayList<>();

        List<Button> reactionButtons = new ArrayList<>();
        reactionButtons.add(new Button(
                String.format(BotConstants.LIKE_BUTTON, likeCount),
                BotConstants.LIKE_ACTION_PREFIX + catId
        ));
        reactionButtons.add(new Button(
                String.format(BotConstants.DISLIKE_BUTTON, dislikeCount),
                BotConstants.DISLIKE_ACTION_PREFIX + catId
        ));
        rows.add(new KeyboardRow(reactionButtons));

        List<Button> navButtons = new ArrayList<>();
        navButtons.add(new Button(BotConstants.NEXT_CAT_BUTTON, BotConstants.NEXT_CAT_ACTION));
        navButtons.add(new Button(BotConstants.BACK_TO_MENU_BUTTON, BotConstants.BACK_TO_MENU_ACTION));
        rows.add(new KeyboardRow(navButtons));

        return new Keyboard(rows);
    }
}