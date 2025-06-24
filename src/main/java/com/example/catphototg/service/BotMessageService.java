package com.example.catphototg.service;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.springframework.stereotype.Service;
import com.example.catphototg.entity.User;

@Service
@RequiredArgsConstructor
public class BotMessageService {
    private final KeyboardService keyboardService;

    public void sendCatsPage(CatBot bot, Long chatId, Page<Cat> catPage, int currentPage) {
        String message = "Ваши котики (страница " + (currentPage + 1) + "):";
        InlineKeyboardMarkup keyboard = keyboardService.createCatsKeyboard(catPage, currentPage);
        bot.sendTextWithKeyboard(chatId, message, keyboard);
    }

    public void sendMainMenu(CatBot bot, Long chatId, User user) {
        String message = user.getDisplayName() + ", выбери действие:";
        bot.sendTextWithKeyboard(chatId, message, keyboardService.createMainMenuKeyboard());
    }
}
