package fr.pierre.tchatback.respository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pierre.tchatback.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
	
	Conversation findById(int id);

}
