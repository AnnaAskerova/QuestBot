package ru.coffeecoders.questbot.keyboards.admins.creators;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ru.coffeecoders.questbot.entities.Question;

import java.util.*;

public class ViewQuestionsUpdateCreator {

    // для клавиатуры нужно передать в updateParameters(int userId, int firstNumber, int numberOfButton)
    // int userId чтобы можно было правильно отработать нажатие другим админом (заигнорить)

    private static Map<Integer, List<Integer>> parameters = new HashMap<>();
    private static List<Question> questionsList = new ArrayList<>();

    private static InlineKeyboardMarkup createKeyboard(int firstNumber, int numberOfButton, boolean isFirst, boolean isLast) {
        InlineKeyboardButton[] buttonArrows = makeButtonArrows(isFirst, isLast);
        InlineKeyboardButton[] buttonNumbers = createButtonNumber(firstNumber, numberOfButton);
        InlineKeyboardButton[][] buttonRows = makeRows(buttonNumbers, buttonArrows);
        return makeNumbersKeyboard(buttonRows);
    }

    public static InlineKeyboardMarkup viewKeyboardCreate(int userId) {
        List<Integer> userParams = parameters.getOrDefault(userId, Arrays.asList(0, 1));
        int firstNumber = userParams.get(0);
        int numberOfButton = userParams.get(1);
        boolean isFirst = (firstNumber == 0);
        boolean isLast = (firstNumber + numberOfButton == questionsList.size());
        return createKeyboard(firstNumber, numberOfButton, isFirst, isLast);
    }

    private static InlineKeyboardButton[] makeButtonArrows(boolean isLast, boolean isFirst) {

        InlineKeyboardButton left = null;
        InlineKeyboardButton right = null;

        if (isFirst && !isLast) {
            right = new InlineKeyboardButton("\t→");
        }
        if (!isFirst && isLast) {
            left = new InlineKeyboardButton("\t←");
        }
        if (!isFirst && !isLast) {
            left = new InlineKeyboardButton("\t←");
            right = new InlineKeyboardButton("\t→");
        }
        return new InlineKeyboardButton[]{left, right};
    }

    private static InlineKeyboardButton[] createButtonNumber(int firstNumber, int numberOfButton) {
        InlineKeyboardButton[] buttons = new InlineKeyboardButton[numberOfButton];
        for (int i = 0; i < numberOfButton; i++) {
            buttons[i] = new InlineKeyboardButton(String.valueOf(i + 1));
            buttons[i].callbackData("question_" + (firstNumber + i));
        }
        return buttons;
    }

    private static InlineKeyboardButton[][] makeRows(InlineKeyboardButton[] buttonArrowArray,
                                                     InlineKeyboardButton[] buttonNumbers) {
        return new InlineKeyboardButton[][]{buttonNumbers, buttonArrowArray};
    }

    private static InlineKeyboardMarkup makeNumbersKeyboard(InlineKeyboardButton[][] buttonRows) {
        return new InlineKeyboardMarkup(buttonRows);
    }

    public static void updateQuestionsList(List<Question> questionsList) {
        ViewQuestionsUpdateCreator.questionsList = questionsList;
    }

    public static void updateParameters(int userId, int firstNumber, int numberOfButton) {
        parameters.put(userId, Arrays.asList(firstNumber, numberOfButton));
    }
}

