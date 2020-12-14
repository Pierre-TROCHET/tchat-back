package fr.pierre.tchatback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pierre.tchatback.entity.Conversation;
import fr.pierre.tchatback.respository.ConversationRepository;

@Service
@Transactional
public class ConversationService {

	@Autowired
	ConversationRepository conversationRepository;
	
	public Conversation save(Conversation conversation) {
		return conversationRepository.save(conversation);
	}
	
	public Conversation findConversationById(int conversationId) {
		return conversationRepository.findById(conversationId);
	}
}
