package ru.coffeecoders.questbot.managers.commands;

import org.springframework.stereotype.Component;
import ru.coffeecoders.questbot.actions.commands.AdminsCommandsActions;
import ru.coffeecoders.questbot.managers.game.NewGameManager;
import ru.coffeecoders.questbot.models.ExtendedUpdate;

@Component
public class AdminsCommandsManager {

    private final AdminsCommandsActions cmdActions;
    private final NewGameManager newGameManager;

    public AdminsCommandsManager(AdminsCommandsActions cmdActions, NewGameManager newGameManager) {
        this.cmdActions = cmdActions;
        this.newGameManager = newGameManager;
    }

    /**
     * @author anna
     * <br>Redact ezuykow
     */
    public void manageCommand(ExtendedUpdate update, Command cmd) {
        long senderAdminId = update.getMessageFromUserId();
        long chatId = update.getMessageChatId();
        switch (cmd) {
            case SHOWQUESTIONS -> cmdActions.performShowQuestionsCmd(senderAdminId, chatId);
            case NEWGAME -> newGameManager.startCreatingGame(senderAdminId, chatId);
            case STOPBOT -> cmdActions.performStopBotCmd();
        }
    }
}

