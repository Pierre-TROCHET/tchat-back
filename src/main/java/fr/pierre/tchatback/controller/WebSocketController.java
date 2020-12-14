package fr.pierre.tchatback.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fr.pierre.tchatback.dto.MessageDto;
import fr.pierre.tchatback.entity.Conversation;
import fr.pierre.tchatback.entity.Message;
import fr.pierre.tchatback.entity.User;
import fr.pierre.tchatback.service.ConversationService;
import fr.pierre.tchatback.service.MessageService;
import fr.pierre.tchatback.service.UserService;

@Controller
public class WebSocketController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	ConversationService conversationService;
	
	@Autowired
	MessageService messageService;
	
    @Autowired
    private SimpMessagingTemplate messageSender;
	
	
	@MessageMapping("/conversationroom/{conversationid}/userid/{userid}")
	@SendTo("/topic/conversationroom/{conversationid}")
	public MessageDto conversationRoom(@DestinationVariable int conversationid, @DestinationVariable int userid, String message) {
		
		//On va chercher le user du message
		User chatUser = userService.getById(userid);
		
		//On va chercher la conversation du message
		Conversation chatConversation = conversationService.findConversationById(conversationid);
		
		//On crée le nouveau message et on le sauvegarde
		Message newChatMessage = new Message();
		newChatMessage.setConversation(chatConversation);
		newChatMessage.setUser(chatUser);
		newChatMessage.setText(message);
		messageService.save(newChatMessage);
		
		//on mappe le message 
		MessageDto messageRetour = mapMessageToMessageDto(newChatMessage);
		
		//On envoie le message dans le composant my-conversations
		this.messageSender.convertAndSend("/topic/conversationLastMessage/"+conversationid, messageRetour);
		
		//et on le renvoie 
		return messageRetour;
	}
    
    private MessageDto mapMessageToMessageDto(Message message) {
    	ModelMapper mapper = new ModelMapper();
    	MessageDto messageDto = mapper.map(message, MessageDto.class);
    	return messageDto;
    }
}
