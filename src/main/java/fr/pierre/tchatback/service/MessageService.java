package fr.pierre.tchatback.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pierre.tchatback.entity.Conversation;
import fr.pierre.tchatback.entity.Message;
import fr.pierre.tchatback.respository.MessageRepository;

@Service
@Transactional
public class MessageService {
	
	@Autowired
	MessageRepository messageRepository;
	

    public List<Message> findMessageByConversation(Conversation conversation){
        return messageRepository.findTop10ByConversationOrderByIdDesc(conversation);
    }
    
    public Message findLastMessageByConversation(int conversationId){
        return messageRepository.findTop1ByConversationIdOrderByIdDesc(conversationId);
    }

    public Message save(Message message){
        return messageRepository.save(message);
    }

}
