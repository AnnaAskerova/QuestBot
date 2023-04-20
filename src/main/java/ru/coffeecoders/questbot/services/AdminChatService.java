package ru.coffeecoders.questbot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.coffeecoders.questbot.entities.AdminChat;
import ru.coffeecoders.questbot.repositories.AdminChatRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AdminChatService {
    Logger logger = LoggerFactory.getLogger(AdminChatService.class);
    private final AdminChatRepository adminChatRepository;
    public AdminChatService(AdminChatRepository adminChatRepository) {
        this.adminChatRepository = adminChatRepository;
    }

    public List<AdminChat> findAll() {
        List<AdminChat> list = adminChatRepository.findAll();
        logger.info("AdminChats {} displaying", list.isEmpty() ? "are not" : "are");
        return list;
    }

    public Optional<AdminChat> findById(long id) {
        Optional<AdminChat> adminChat = adminChatRepository.findById(id);
        logger.info("AdminChat {} with id = {}", adminChat.isPresent() ? "found" : "not found", id);
        return adminChat;
    }

    public AdminChat save(AdminChat adminChat) {
        logger.info("AdminChat = {} has been saved", adminChat);
        return adminChatRepository.save(adminChat);
    }

    /**
     * Удаляет админский чат с БД
     * @param adminChat удаляемый чат
     * @author ezuykow
     */
    public void delete(AdminChat adminChat) {
        adminChatRepository.delete(adminChat);
    }

    /**
     *
     * @param chatId
     * @author ezuykow
     */
    public void deleteByChatId(long chatId) {
        adminChatRepository.deleteById(chatId);
    }
}