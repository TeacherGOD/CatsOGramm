package com.example.catphototg.constants;

public final class BotConstants {
    public static final String CONFIRM_BUTTON = "✅ Подтвердить";
    public static final String CANCEL_BUTTON = "❌ Отмена";
    public static final String ADD_CAT_COMMAND = "➕ Добавить котика";
    public static final String SHOW_CATS_COMMAND="🐱 Смотреть котиков";
    public static final String CHANGE_NAME_COMMAND="✏️ Сменить имя";
    public static final String MY_CATS_COMMAND = "❤️ Мои котики";
    public static final String NAME_REGISTRATION_PROMPT = "Привет! Как тебя зовут?";
    public static final String CAT_NAME_PROMPT = "введите имя котика:";
    public static final String CAT_PHOTO_PROMPT = "отправьте фото котика:";
    public static final String CAT_CONFIRMATION_PROMPT = "подтвердите добавление котика:\nИмя: ";
    public static final String PHOTO_SENDING_ERROR = "Ошибка отправки фото";
    public static final String ERROR_MESSAGE = "Ошибка: ";
    public static final String CANCEL_ACTION = "CANCEL_ACTION";
    public static final String CONFIRM_CAT_ACTION = "CONFIRM_CAT";
    public static final String CANCEL_CAT_ACTION = "CANCEL_CAT";
    public static final String VIEW_CATS_ACTION = "VIEW_CATS";
    public static final String ADD_CAT_ACTION = "ADD_CAT";
    public static final String MY_CATS_ACTION = "MY_CATS";
    public static final String CHANGE_NAME_ACTION = "CHANGE_NAME";
    public static final String NAME_CHANGED = "✅ Имя успешно изменено на: %s";
    public static final String WELCOME_MESSAGE ="""
        Привет, %s! Добро пожаловать в мир котиков! 😺

        Здесь ты можешь:
        - Добавлять фото своих котиков
        - Смотреть котиков других пользователей
        - Оценивать милых котиков

        Начни прямо сейчас!""";
    public static final String BROWSING_MY_CATS_TEXT = "Продолжаем просмотр ваших котиков...";
    //для будущих вещей
    public static final String NEXT_PAGE_ACTION = "NEXT_PAGE";
    public static final String PREV_PAGE_ACTION = "PREV_PAGE";
    public static final String BACK_TO_MENU_ACTION = "BACK_TO_MENU";
    public static final String CAT_DETAILS_PREFIX = "CAT_";
    public static final String REACTION_DETAILS_PREFIX = "REACTION_";
    public static final String PREV_PAGE_BUTTON = "⬅️ Назад";
    public static final String NEXT_PAGE_BUTTON = "Далее ➡️";
    public static final String BACK_TO_MENU_BUTTON = "В меню";


    private BotConstants() {
        throw new IllegalStateException("Utility class");
    }
}
