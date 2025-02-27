package ru.coffeecoders.questbot.managers.commands;

import static ru.coffeecoders.questbot.managers.commands.Command.Attribute.*;

public enum Command {
    STOPBOT(GLOBALADMIN),
    PREPAREGAME(GLOBALADMIN),
    DROPPREPARE(GLOBALADMIN),
    STARTGAME(GLOBALADMIN),
    DROPGAME(GLOBALADMIN),
    DELETECHAT(GLOBALADMIN),

    SHOWGAMES(ADMIN),
    SHOWQUESTIONS(ADMIN),
    NEWGAME(ADMIN),

    START(OWNER),
    ADMINON(OWNER),
    ADMINOFF(OWNER),
    PROMOTE(OWNER),
    DEMOTE(OWNER),

    INFO(PLAYER),
    REGTEAM(PLAYER),
    JOINTEAM(PLAYER),
    QUESTIONS(PLAYER);

    public enum Attribute {
        OWNER,
        GLOBALADMIN,
        ADMIN,
        PLAYER,
    }

    private final Attribute attribute;

    Command(Attribute attribute) {
        this.attribute = attribute;
    }
    public Attribute getAttribute() {
        return attribute;
    }
}
