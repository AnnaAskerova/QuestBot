package ru.coffeecoders.questbot.actions;

import com.pengrad.telegrambot.model.ChatPermissions;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.coffeecoders.questbot.entities.AdminChat;
import ru.coffeecoders.questbot.entities.Game;
import ru.coffeecoders.questbot.entities.NewGameCreatingState;
import ru.coffeecoders.questbot.entities.QuestionGroup;
import ru.coffeecoders.questbot.keyboards.QuestionsGroupsKeyboard;
import ru.coffeecoders.questbot.senders.MessageSender;
import ru.coffeecoders.questbot.services.*;
import ru.coffeecoders.questbot.validators.QuestionsValidator;

import java.util.Arrays;
import java.util.List;

/**
 * @author ezuykow
 */
@Component
public class NewGameActions {

    private final NewGameCreatingStateService newGameCreatingStateService;
    private final QuestionGroupService questionGroupService;
    private final QuestionsValidator questionsValidator;
    private final GameService gameService;
    private final AdminChatService adminChatService;
    private final AdminChatMembersService adminChatMembersService;
    private final MessageSender msgSender;
    private final Environment env;

    public NewGameActions(NewGameCreatingStateService newGameCreatingStateService,
                          QuestionGroupService questionGroupService, QuestionsValidator questionsValidator, GameService gameService, AdminChatService adminChatService, AdminChatMembersService adminChatMembersService, MessageSender msgSender, Environment env) {
        this.newGameCreatingStateService = newGameCreatingStateService;
        this.questionGroupService = questionGroupService;
        this.questionsValidator = questionsValidator;
        this.gameService = gameService;
        this.adminChatService = adminChatService;
        this.adminChatMembersService = adminChatMembersService;
        this.msgSender = msgSender;
        this.env = env;
    }

    public void createNewGameCreatingState(long chatId) {
        newGameCreatingStateService.save(
                new NewGameCreatingState(chatId)
        );
        requestNewGameName(chatId);
    }

    public void addGameNameToStateAndRequestNextPart(long chatId, NewGameCreatingState state,
                                                     String gameName, int answerMsgId) {
        state.setGameName(gameName);
        newGameCreatingStateService.save(state);
        int requestMsgId = getRequestMsgIdAndDeleteAnswerMsg(chatId, answerMsgId);
        requestQuestionGroups(gameName, chatId, requestMsgId);
    }

    public void addSelectedQuestionGroupAndRefreshMsg(long chatId, int msgId, int questionGroupId) {
        int[] allStateGroupsIds = addQuestionGroupIdToState(chatId, questionGroupId);
        String stateGroupsNames = getGroupsNames(allStateGroupsIds);
        msgSender.edit(chatId, msgId,
                String.format(env.getProperty("messages.game.addedQuestionGroup", "Error"),
                        stateGroupsNames),
                QuestionsGroupsKeyboard.createKeyboard(questionGroupService.findAll()
                        .stream()
                        .filter(g -> !ArrayUtils.contains(allStateGroupsIds, g.getGroupId()))
                        .toList()
                )
        );
    }

    public void stopSelectingQuestionsGroupsAndRequestNextPart(long chatId, int msgId) {
        requestMaxQuestionsCount(chatId, msgId);
    }

    public void addMaxQuestionsCountToStateAndRequestNextPart(long chatId, NewGameCreatingState state,
                                                              String text, int msgId) {
        Integer maxQuestionCount = parseTextToInteger(text);
        int requestMsgId = getRequestMsgIdAndDeleteAnswerMsg(chatId, msgId);
        if (maxQuestionCount != null && maxQuestionCount > 0) {
            if (questionsValidator.isRequestedQuestionCountNotMoreThanWeHaveByGroups(
                    maxQuestionCount, state.getGroupsIds())) {

                state.setMaxQuestionsCount(maxQuestionCount);
                newGameCreatingStateService.save(state);
                requestStartCountTasks(chatId, requestMsgId, maxQuestionCount);
            } else {
                msgSender.edit(chatId, requestMsgId,
                        env.getProperty("messages.game.invalidQuestionCount")
                                + env.getProperty("messages.game.requestMaxQuestionsCountSimple"),
                        null
                );
            }
        } else {
            msgSender.edit(chatId, requestMsgId,
                    env.getProperty("messages.game.invalidNumber")
                            + env.getProperty("messages.game.requestMaxQuestionsCountSimple"),
                    null
            );
        }
    }

    public void addStartCountTaskToStateAndRequestNextPart(long chatId, NewGameCreatingState state,
                                                           String text, int msgId) {
        Integer startCountTask = parseTextToInteger(text);
        int requestMsgId = getRequestMsgIdAndDeleteAnswerMsg(chatId, msgId);
        int maxQuestionCount = state.getMaxQuestionsCount();
        if (startCountTask != null && startCountTask > 0) {
            if (startCountTask <= maxQuestionCount) {
                state.setStartCountTasks(startCountTask);
                newGameCreatingStateService.save(state);
                requestMaxPerformedQuestionCount(chatId, requestMsgId, startCountTask);
            } else {
                msgSender.edit(chatId, requestMsgId,
                        env.getProperty("messages.game.startQMoreMaxQ")
                                + String.format(
                                env.getProperty("messages.game.requestStartCountTasks", "Error"),
                                maxQuestionCount),
                        null
                );
            }
        } else {
            msgSender.edit(chatId, requestMsgId,
                    env.getProperty("messages.game.invalidNumber")
                            + String.format(
                                env.getProperty("messages.game.requestStartCountTasks", "Error"),
                                maxQuestionCount),
                    null
            );
        }
    }

    public void addMaxPerformedQuestionsCountToStateAndRequestNextPart(long chatId, NewGameCreatingState state,
                                                                       String text, int msgId) {
        Integer maxPerformedQuestionsCount = parseTextToInteger(text);
        int requestMsgId = getRequestMsgIdAndDeleteAnswerMsg(chatId, msgId);
        int startQuestions = state.getStartCountTasks();
        if (maxPerformedQuestionsCount != null && maxPerformedQuestionsCount > 0) {
            if (maxPerformedQuestionsCount <= state.getMaxQuestionsCount()) {
                state.setMaxPerformedQuestionsCount(maxPerformedQuestionsCount);
                newGameCreatingStateService.save(state);
                requestMinQuestionsCountInGame(chatId, requestMsgId, maxPerformedQuestionsCount);
            } else {
                msgSender.edit(chatId, requestMsgId,
                        env.getProperty("messages.game.maxPerformedQMoreMaxQ")
                                + String.format(
                                    env.getProperty("messages.game.requestMaxPerformedQuestionCount", "Error"),
                                    startQuestions),
                        null
                );
            }
        } else {
            msgSender.edit(chatId, requestMsgId,
                    env.getProperty("messages.game.invalidNumber")
                            + String.format(
                                env.getProperty("messages.game.requestMaxPerformedQuestionCount", "Error"),
                                startQuestions),
                    null
            );
        }
    }

    public void addMinQuestionsCountInGameAndRequestNextPart(long chatId, NewGameCreatingState state,
                                                             String text, int msgId) {
        Integer minQuestionsInGame = parseTextToInteger(text);
        int requestMsgId = getRequestMsgIdAndDeleteAnswerMsg(chatId, msgId);
        int maxPerformed = state.getMaxPerformedQuestionsCount();
        if (minQuestionsInGame != null && minQuestionsInGame >= 0) {
            state.setMinQuestionsCountInGame(minQuestionsInGame);
            newGameCreatingStateService.save(state);
            requestQuestionsCountToAdd(chatId, requestMsgId, minQuestionsInGame);
        } else {
            msgSender.edit(chatId, requestMsgId,
                    env.getProperty("messages.game.invalidNumber")
                    + String.format(
                            env.getProperty("messages.game.requestMinQuestionsCountInGame", "Error"),
                            maxPerformed),
                    null);
        }
    }

    public void addQuestionsCountToAddAndRequestNextPart(long chatId, NewGameCreatingState state,
                                                         String text, int msgId) {
        Integer questionsToAdd = parseTextToInteger(text);
        int requestMsgId = getRequestMsgIdAndDeleteAnswerMsg(chatId, msgId);
        int minInGame = state.getMinQuestionsCountInGame();
        if (questionsToAdd != null && questionsToAdd >= 0) {
            state.setQuestionsCountToAdd(questionsToAdd);
            newGameCreatingStateService.save(state);
            requestMaxTimeMinutes(chatId, requestMsgId, questionsToAdd);
        } else {
            msgSender.edit(chatId, requestMsgId,
                    env.getProperty("messages.game.invalidNumber")
                            + String.format(
                            env.getProperty("messages.game.requestQuestionsCountToAdd", "Error"),
                            minInGame),
                    null);
        }
    }

    public void addMaxTimeMinutesToStateAmdSaveNewGame(long chatId, NewGameCreatingState state,
                                                       String text, int msgId) {
        Integer minutes = parseTextToInteger(text);
        int requestMsgId = getRequestMsgIdAndDeleteAnswerMsg(chatId, msgId);
        int toAdd = state.getQuestionsCountToAdd();
        if (minutes != null && minutes > 0) {
            msgSender.sendDelete(chatId, requestMsgId);
            saveNewGame(state);
            newGameCreatingStateService.delete(state);
            removeBlockedByAdminOnAdminChat(chatId);
            unRestrictAllMembers(chatId);
        } else {
            msgSender.edit(chatId, requestMsgId,
                    env.getProperty("messages.game.invalidNumber")
                            + String.format(
                            env.getProperty("messages.game.requestMaxTimeMinutes", "Error"),
                            toAdd),
                    null);
        }
    }

    public NewGameCreatingState getNewGameCreatingState(long chatId) {
        return newGameCreatingStateService.findById(chatId)
                .orElseThrow(() ->
                {
                    msgSender.send(chatId, env.getProperty("messages.somethingWrong"));
                    return new RuntimeException("Этого, конечно, никогда не будет, нооо... пиздец, короче");
                });
    }

    private void requestNewGameName(long chatId) {
        msgSender.send(chatId, env.getProperty("messages.game.requestNewGameName"));
    }

    private void requestQuestionGroups(String gameName, long chatId, int requestMsgId) {
        msgSender.edit(chatId, requestMsgId,
                String.format(
                        env.getProperty("messages.game.requestQuestionsGroups", "Error"), gameName),
                QuestionsGroupsKeyboard.createKeyboard(questionGroupService.findAll())
        );
    }

    private void requestMaxQuestionsCount(long chatId, int msgIdToEdit) {
        msgSender.edit(chatId, msgIdToEdit,
                String.format(
                        env.getProperty("messages.game.requestMaxQuestionsCount", "Error"),
                        getGroupsNames(getNewGameCreatingState(chatId).getGroupsIds())),
                null);
    }

    private void requestStartCountTasks(long chatId, int msgIdToEdit, int maxQuestionCount) {
        msgSender.edit(chatId, msgIdToEdit,
                String.format(
                        env.getProperty("messages.game.requestStartCountTasks", "Error"),
                        maxQuestionCount),
                null);
    }

    private void requestMaxPerformedQuestionCount(long chatId, int msgIdToEdit, int startCountTask) {
        msgSender.edit(chatId, msgIdToEdit,
                String.format(
                        env.getProperty("messages.game.requestMaxPerformedQuestionCount", "Error"),
                        startCountTask),
                null);
    }

    private void requestMinQuestionsCountInGame(long chatId, int msgIdToEdit, Integer maxPerformedQuestionsCount) {
        msgSender.edit(chatId, msgIdToEdit,
                String.format(
                        env.getProperty("messages.game.requestMinQuestionsCountInGame", "Error"),
                        maxPerformedQuestionsCount),
                null);
    }

    private void requestQuestionsCountToAdd(long chatId, int msgIdToEdit, Integer minQuestionsInGame) {
        msgSender.edit(chatId, msgIdToEdit,
                String.format(
                        env.getProperty("messages.game.requestQuestionsCountToAdd", "Error"),
                        minQuestionsInGame),
                null);
    }

    private void requestMaxTimeMinutes(long chatId, int msgIdToEdit, Integer questionsToAdd) {
        msgSender.edit(chatId, msgIdToEdit,
                String.format(
                        env.getProperty("messages.game.requestMaxTimeMinutes", "Error"),
                        questionsToAdd),
                null);
    }

    private int getRequestMsgIdAndDeleteAnswerMsg(long chatId, int answerMsgId) {
        msgSender.sendDelete(chatId, answerMsgId);
        return answerMsgId - 1;
    }

    private String getGroupsNames(int[] allStateGroupsIds) {
        StringBuilder sb = new StringBuilder();
        List<QuestionGroup> groups = questionGroupService.findAll();
        for (int i = 0; i < allStateGroupsIds.length; i++) {
            final int id = allStateGroupsIds[i];
            groups.stream().filter(g -> g.getGroupId() == id).findAny()
                    .ifPresent(g -> sb.append(g.getGroupName()));
            if (i < allStateGroupsIds.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private int[] addQuestionGroupIdToState(long chatId, int questionGroupId) {
        NewGameCreatingState state = getNewGameCreatingState(chatId);
        int[] groupsIds = ArrayUtils.add(state.getGroupsIds(), questionGroupId);
        state.setGroupsIds(groupsIds);
        newGameCreatingStateService.save(state);
        return groupsIds;
    }

    private Integer parseTextToInteger(String text) {
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void saveNewGame(NewGameCreatingState state) {
        gameService.save(
                new Game(
                        state.getGameName(),
                        state.getGroupsIds(),
                        state.getMaxTimeMinutes(),
                        state.getMaxQuestionsCount(),
                        state.getMaxPerformedQuestionsCount(),
                        state.getMinQuestionsCountInGame(),
                        state.getQuestionsCountToAdd(),
                        state.getStartCountTasks()
                )
        );
    }

    private void removeBlockedByAdminOnAdminChat(long chatId) {
        AdminChat currentAdminChat = adminChatService.findById(chatId).get();
        currentAdminChat.setBlockedByAdminId(0);
        adminChatService.save(currentAdminChat);
    }

    private void unRestrictAllMembers(long chatId) {
        ChatPermissions permissions = new ChatPermissions()
                .canSendMessages(true)
                .canSendOtherMessages(true);

        Arrays.stream(adminChatMembersService.findByChatId(chatId).get().getMembers())
                .forEach(id -> msgSender.sendRestrictChatMember(chatId, id, permissions));
    }
}
