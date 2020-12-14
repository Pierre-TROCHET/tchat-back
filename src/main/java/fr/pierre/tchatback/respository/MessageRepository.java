package fr.pierre.tchatback.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pierre.tchatback.entity.Conversation;
import fr.pierre.tchatback.entity.Message;

@Repository
public interface MessageRepository  extends JpaRepository<Message, Integer> {
	List<Message> findTop10ByConversationOrderByIdDesc(Conversation Id);
	Message findTop1ByConversationIdOrderByIdDesc(int conversationId);
}
