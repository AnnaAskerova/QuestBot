package ru.coffeecoders.questbot.managers.commands;

import org.springframework.stereotype.Component;
import ru.coffeecoders.questbot.actions.commands.PlayersCommandsActions;
import ru.coffeecoders.questbot.models.ExtendedUpdate;

/**
 * @author anna
 */
@Component
public class PlayersCommandsManager {
    private final PlayersCommandsActions playersCommandsActions;

    public PlayersCommandsManager(PlayersCommandsActions playersCommandsActions) {
        this.playersCommandsActions = playersCommandsActions;
    }

    /**
     * Вызывает определенный метод {@link PlayersCommandsActions} в зависимости от того,
     * какая команда была передана в параметрах
     * @param update - апдейт
     * @param cmd - команда (enum)
     */
    public void manageCommand(ExtendedUpdate update, Command cmd) {
        long chatId = update.getMessageChatId();
        switch (cmd) {
            case INFO -> playersCommandsActions.showInfo(chatId);
            case REGTEAM -> playersCommandsActions.regTeam(update);
            case JOINTEAM -> playersCommandsActions.joinTeam(update);
            case QUESTIONS -> playersCommandsActions.showQuestions(chatId);
        }
    }
}