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
    public static final String CAT_CONFIRMATION_PROMPT = "%s, подтвердите добавление котика:\nИмя: %s\nАвтор: @%s";
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
    public static final String NEXT_PAGE_ACTION = "NEXT_PAGE";
    public static final String PREV_PAGE_ACTION = "PREV_PAGE";
    public static final String BACK_TO_MENU_ACTION = "BACK_TO_MENU";
    public static final String CAT_DETAILS_PREFIX = "CAT_";
    public static final String DELETE_CAT_PREFIX = "DELETE_CAT_";
    public static final String BACK_TO_MY_CATS_ACTION = "BACK_TO_MY_CATS";
    public static final String PREV_PAGE_BUTTON = "⬅️ Назад";
    public static final String NEXT_PAGE_BUTTON = "Далее ➡️";
    public static final String BACK_TO_MENU_BUTTON = "В меню";
    public static final String NO_CAT_FUNDED = "Котик не найден или недоступен";
    public static final String DELETE_BUTTON="❌ Удалить";
    public static final String LIKE_ACTION_PREFIX = "LIKE_";
    public static final String DISLIKE_ACTION_PREFIX = "DISLIKE_";
    public static final String NEXT_CAT_ACTION = "NEXT_CAT";
    public static final String LIKE_BUTTON = "👍 %d";
    public static final String DISLIKE_BUTTON = "👎 %d";
    public static final String NEXT_CAT_BUTTON = "Следующий котик ➡️";
    public static final String NO_CATS_MESSAGE="Вы посмотрели всех котиков! 🐾\nПопробуйте позже, когда добавят новых.";
    public static final String CAT_CARD_MESSAGE="🐱 Имя: %s\nАвтор: @%s";
    public static final String CANCEL_ADD_CAT_MESSAGE="Добавление котика отменено";
    public static final String CAT_NAME_ERROR_MESSAGE="Имя котика должно быть от 2 до 30 символов. Попробуйте еще раз:";
    public static final String DEFAULT_MESSAGE="Пожалуйста, выберите действие";
    public static final String NAME_ERROR_MESSAGE="Имя должно быть от 2 до 30 символов. Попробуйте еще раз:";
    public static final String CAT_SUCCESS_DELETE_MESSAGE="Котик успешно удален ✅";



    private BotConstants() {
        throw new IllegalStateException("Utility class");
    }
}
