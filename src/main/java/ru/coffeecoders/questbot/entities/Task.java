package ru.coffeecoders.questbot.entities;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * @author ezuykow
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskId;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "question_id")
    private int questionId;

    @Column(name = "performed_team_name")
    private String performedTeamName;

    public Task() {
    }

    public Task(String gameName, int questionId) {
        this(gameName, questionId, null);
    }

    public Task(String gameName, int questionId, String performedTeamName) {
        this.gameName = gameName;
        this.questionId = questionId;
        this.performedTeamName = performedTeamName;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getPerformedTeamName() {
        return performedTeamName;
    }

    public void setPerformedTeamName(String performedTeamName) {
        this.performedTeamName = performedTeamName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", gameName='" + gameName + '\'' +
                ", questionId=" + questionId +
                ", performedTeamName='" + performedTeamName + '\'' +
                '}';
    }
}
