package ru.coffeecoders.questbot.commands.admins.keybords;

import com.pengrad.telegrambot.model.request.KeyboardButton;
import org.springframework.stereotype.Component;

@Component
public class AdminsCommandActions {

    /**
     * Методы
     * newGameCommand(long chatId, int userId)
     * alreadyRunningGamesCommand(long chatId, int userId)
     * questionsCommand(long chatId, int userId)
     * defaultKeyboardNotAdmin(long chatId)
     */
    private final KeyboardButton visitChatButton = new KeyboardButton("Посетить чат сообщества");
    //логика кнопки Игра по станциям
    private final KeyboardButton returnKeyboardButton = new KeyboardButton("Вернуть клавиатуру");
    //логика кнопки Игра по станциям
    private final KeyboardButton returnToMain = new KeyboardButton("Вернуться в главное меню");
    //логика кнопки возвратить в главное меню
    private String replyText;
    private final AdminCommandsMsgSender msgSender;
    private final DefaultAdminKeyboard defaultAmKb;
    private final NewGameAdminKeyboard newGameAmKb;

    private final KeyboardFactory keyboardFactory;
    public AdminsCommandActions(AdminCommandsMsgSender msgSender, DefaultAdminKeyboard defaultAdminKeyboard, NewGameAdminKeyboard newGameAmKb, KeyboardFactory keyboardFactory) {

        this.msgSender = msgSender;
        this.defaultAmKb = defaultAdminKeyboard;
        this.newGameAmKb = newGameAmKb;
        this.keyboardFactory = keyboardFactory;
    }


    //TODO msgSender.getAllGames();
    //TODO msgSender.send(request);
    //TODO ??.isAdmin(userId)





    private void defaultNotAdmin(long chatId) {
        defaultAmKb.defaultKeyboardNotAdmin(chatId);

        keyboardFactory.createKeyboard(KeyboardFactory.KeyboardType.NEW_GAME);
    }
    public void newGameCommand(long chatId, int userId) {

    }




}
