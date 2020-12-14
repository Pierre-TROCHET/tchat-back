package fr.pierre.tchatback.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.pierre.tchatback.dto.ConversationDto;
import fr.pierre.tchatback.dto.MessageDto;
import fr.pierre.tchatback.dto.UserDto;
import fr.pierre.tchatback.dto.UserFullDto;
import fr.pierre.tchatback.entity.Conversation;
import fr.pierre.tchatback.entity.Message;
import fr.pierre.tchatback.entity.User;
import fr.pierre.tchatback.security.jwt.JwtProvider;
import fr.pierre.tchatback.service.ConversationService;
import fr.pierre.tchatback.service.MessageService;
import fr.pierre.tchatback.service.UserService;

import org.modelmapper.ModelMapper;

@RestController
@CrossOrigin
public class TchatController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	ConversationService conversationService;   
	
	@Autowired
	MessageService messageService;   
	
	@Autowired
    JwtProvider jwtProvider;
	
    @GetMapping("/getallusers")
    public ResponseEntity<List<UserDto>> getAllUsers(){
    	List<User> allUsers = userService.findAllUsers();
    	if(allUsers != null) {
			List<UserDto> allUsersDTO = allUsers.stream().map(user -> {
				return mapUserToUserDto(user);
			}).collect(Collectors.toList());
			return new ResponseEntity<List<UserDto>>(allUsersDTO, HttpStatus.OK);

    	}
        return new ResponseEntity<List<UserDto>>(HttpStatus.NO_CONTENT);
    }
	//TODO: enlever tous les println
    @GetMapping("/getallusersbutnotme")
    public ResponseEntity<List<UserDto>> getAllUsersButNotMe(){
    	//On prend tous les users
    	List<User> allUsers = userService.findAllUsers();
    	//On prend le user courant
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String providerIdCurrentUser = (String) auth.getPrincipal();
    	User currentUser = userService.getByProviderId(providerIdCurrentUser).get();
    	
    	//on enlève le user courant de tous les users
    	allUsers.remove(currentUser);
    	
    	List<UserDto> allUsersDTO = allUsers.stream().map(user -> {
			return mapUserToUserDto(user);
		}).collect(Collectors.toList());
		return new ResponseEntity<List<UserDto>>(allUsersDTO, HttpStatus.OK);
    }
    
    @GetMapping("/getmyinformations")
    public ResponseEntity<UserFullDto> getMyInformations(){
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String providerIdCurrentUser = (String) auth.getPrincipal();
    	User currentUser = userService.getByProviderId(providerIdCurrentUser).get();
    	UserFullDto currentUserFullDto = mapUserToUserFullDto(currentUser);
    	return new ResponseEntity<UserFullDto>(currentUserFullDto, HttpStatus.OK);
    }
    
    /*
     * example of ConversationDto
     * {
		"name" : "Conversation test 3",
		"users": [
			{
				"id" : 1
			},
			{
				"id" : 2
			}
			]
		}
     * 
     * */
    @PostMapping("/addconversation")
    public ResponseEntity<ConversationDto> addConversation(@RequestBody ConversationDto conversationDto){
    	//On transforme la conversation en entité
    	Conversation conversation = mapConversationDtoToConversation(conversationDto);
    	//On prend le user courant et on l'ajoute dans la conversation
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String providerIdCurrentUser = (String) auth.getPrincipal();
    	User currentUser = userService.getByProviderId(providerIdCurrentUser).get();
    	conversation.addUser(currentUser);
    	//On sauvegarde la conversation et on retourne l'objet enregistré
    	conversation = conversationService.save(conversation);
    	conversationDto = mapConversationToConversationDto(conversation);
    	return new ResponseEntity<ConversationDto>(conversationDto, HttpStatus.OK);
    }
    
    @GetMapping("/adduserinconversation")
    public ResponseEntity<ConversationDto> addUserInConversation(@RequestParam("conversationid") int conversationId, @RequestParam("userid") int userId){
    	
    	//Si la conversation n'existe pas déjà => erreur
    	Conversation conversation = conversationService.findConversationById(conversationId);
    	if(conversation == null) {
        	System.out.println("la conversation n'existe pas déjà => erreur");
    		return new ResponseEntity<ConversationDto>(HttpStatus.BAD_REQUEST);
    	}
    	
    	//Si le demandeur de la requete n'est pas dans la conversation => erreur
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String providerIdCurrentUser = (String) auth.getPrincipal();
    	int currentUserId = userService.getByProviderId(providerIdCurrentUser).get().getId();
    	System.out.println("currentUserId: " + currentUserId);
    	boolean currentUserHere = false;
    	for(User user : conversation.getUsers()){
            if(user.getId() == currentUserId) {
            	 currentUserHere = true;
            }
        }
    	System.out.println("currentUserHere: " + currentUserHere);
    	if(currentUserHere == false) {
    		return new ResponseEntity<ConversationDto>(HttpStatus.BAD_REQUEST);
    	}
    	
    	//Si le user est déjà dans la conversation => erreur
    	for(User user : conversation.getUsers()){
            if(user.getId() == userId) {
            	System.out.println("user est déjà dans la conversation => erreur");
            	return new ResponseEntity<ConversationDto>(HttpStatus.BAD_REQUEST);
            }
        }
    	
    	//Si le user n'existe pas => erreur
    	User newUser = userService.getById(userId);
    	if(newUser == null) {
    		System.out.println("le user n'existe pas => erreur");
    		return new ResponseEntity<ConversationDto>(HttpStatus.BAD_REQUEST);
    	}
    	
    	//Sinon tout est OK on peut ajouter le user
    	conversation.addUser(newUser);
    	conversationService.save(conversation);
    	
    	ConversationDto conversationDto = mapConversationToConversationDto(conversation);
    	System.out.println("OK");
    	return new ResponseEntity<ConversationDto>(conversationDto, HttpStatus.OK);
    }
    
	@GetMapping("/getmyconversations")
    public ResponseEntity<List<ConversationDto>> getMyConversations(){
    	//On récupère le user courant
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String providerIdCurrentUser = (String) auth.getPrincipal();
    	User currentUser = userService.getByProviderId(providerIdCurrentUser).get();
    	//On récupère les conversations du user courant
    	Set<Conversation> myConversations = currentUser.getConversations();
    	//Si elles existent on les mappent et on récupère le dernier message
    	if(myConversations != null) {
			List<ConversationDto> myConversationsDTO = myConversations.stream().map(myConversation -> {
				return mapConversationToConversationDto(myConversation);
			}).collect(Collectors.toList());
			myConversationsDTO.forEach(conversationDTO -> {
    			Message lastMessage = messageService.findLastMessageByConversation(conversationDTO.getId());
    			if(lastMessage != null) {
	    			MessageDto lastMessageDto =  mapMessageToMessageDto(lastMessage);
	    			if(lastMessageDto.getUser().getId() == currentUser.getId()) {
	    				lastMessageDto.setCurrentUser(true);
	    			}
	    			List<MessageDto> messages = new ArrayList<MessageDto>();
	    			messages.add(lastMessageDto);
	    			conversationDTO.setMessages(messages);
    			}
    			
    		});
			return new ResponseEntity<List<ConversationDto>>(myConversationsDTO, HttpStatus.OK);

    	}
		return new ResponseEntity<List<ConversationDto>>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/removemefromconversation")
    public ResponseEntity<ConversationDto> removeMeFromConversation(@RequestParam("conversationid") int conversationId){
    	//Si la conversation n'existe pas déjà => erreur
    	Conversation conversation = conversationService.findConversationById(conversationId);
    	if(conversation == null) {
        	System.out.println("la conversation n'existe pas déjà => erreur");
    		return new ResponseEntity<ConversationDto>(HttpStatus.BAD_REQUEST);
    	}
    	//On récupère le user courant
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String providerIdCurrentUser = (String) auth.getPrincipal();
    	User currentUser = userService.getByProviderId(providerIdCurrentUser).get();
    	
    	//Si l'utilisateur n'est pas présent dans la conversation => erreur
    	if(!conversation.isUserPresent(currentUser)) {
    		return new ResponseEntity<ConversationDto>(HttpStatus.BAD_REQUEST);
    	}
    	
    	//On enlève le user de la conversation et on sauvegarde
    	conversation.removeUser(currentUser);
    	conversation = conversationService.save(conversation);
    	
    	//On renvoie conversationDto
    	ConversationDto conversationDto = mapConversationToConversationDto(conversation);
		return new ResponseEntity<ConversationDto>(conversationDto, HttpStatus.OK);
    }
    
    @GetMapping("getallusersbutconversation")
    public ResponseEntity<List<UserDto>> getAllUsersButConversation(@RequestParam("conversationid") int conversationId){
    	//Si la conversation n'existe pas déjà => erreur
    	Conversation conversation = conversationService.findConversationById(conversationId);
    	if(conversation == null) {
        	System.out.println("la conversation n'existe pas déjà => erreur");
    		return new ResponseEntity<List<UserDto>>(HttpStatus.BAD_REQUEST);
    	}
    	List<User> users = userService.findAllUsers();
    	
    	Set<User> usersToRemove = conversation.getUsers();
    	
    	users.removeAll(usersToRemove);
    	List<UserDto> usersDTO = users.stream().map(user -> {
			return mapUserToUserDto(user);
		}).collect(Collectors.toList());
		return new ResponseEntity<List<UserDto>>(usersDTO, HttpStatus.OK);
    }
    
    @GetMapping("getfullconversation")
    public ResponseEntity<ConversationDto> getFullConversation(@RequestParam("conversationid") int conversationId){
    	//Si la conversation n'existe pas déjà => erreur
    	Conversation conversation = conversationService.findConversationById(conversationId);
    	if(conversation == null) {
        	System.out.println("la conversation n'existe pas déjà => erreur");
    		return new ResponseEntity<ConversationDto>(HttpStatus.BAD_REQUEST);
    	}
    	//Si l'utilisateur courant n'est pas dedans => erreur
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String providerIdCurrentUser = (String) auth.getPrincipal();
    	User currentUser = userService.getByProviderId(providerIdCurrentUser).get();
    	if(!conversation.isUserPresent(currentUser)) {
    		return new ResponseEntity<ConversationDto>(HttpStatus.BAD_REQUEST);
    	}
    	
    	//Sinon on récupère les messages
    	List<Message> messages = messageService.findMessageByConversation(conversation);
		List<MessageDto> messagesDto = messages.stream().map(message -> {
			return mapMessageToMessageDto(message);
		}).collect(Collectors.toList());
		messagesDto.forEach(messageDto -> {if(messageDto.getUser().getId() == currentUser.getId())messageDto.setCurrentUser(true); });
    	ConversationDto conversationDto = mapConversationToConversationDto(conversation);
    	conversationDto.setMessages(messagesDto);
    	
    	return new ResponseEntity<ConversationDto>(conversationDto, HttpStatus.OK);
    }
    
    private UserDto mapUserToUserDto(User user) {
    	ModelMapper mapper = new ModelMapper();
    	UserDto userDto = mapper.map(user, UserDto.class);
    	return userDto;
    }
    
    private UserFullDto mapUserToUserFullDto(User user) {
    	ModelMapper mapper = new ModelMapper();
    	UserFullDto userFullDto = mapper.map(user, UserFullDto.class);
    	return userFullDto;
    }
    
    private Conversation mapConversationDtoToConversation(ConversationDto conversationDto) {
    	ModelMapper mapper = new ModelMapper();
    	Conversation conversation = mapper.map(conversationDto, Conversation.class);
    	return conversation;
    }
    
    private ConversationDto mapConversationToConversationDto(Conversation conversation) {
    	ModelMapper mapper = new ModelMapper();
    	ConversationDto conversationDto = mapper.map(conversation, ConversationDto.class);
    	return conversationDto;
    }
    
    private MessageDto mapMessageToMessageDto(Message message) {
    	ModelMapper mapper = new ModelMapper();
    	MessageDto messageDto = mapper.map(message, MessageDto.class);
    	return messageDto;
    }

}
