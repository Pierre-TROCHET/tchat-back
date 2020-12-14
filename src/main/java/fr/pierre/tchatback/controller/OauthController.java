package fr.pierre.tchatback.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import fr.pierre.tchatback.dto.TokenDto;
import fr.pierre.tchatback.entity.Conversation;
import fr.pierre.tchatback.entity.Role;
import fr.pierre.tchatback.entity.User;
import fr.pierre.tchatback.enums.RoleName;
import fr.pierre.tchatback.security.jwt.JwtProvider;
import fr.pierre.tchatback.service.ConversationService;
import fr.pierre.tchatback.service.RoleService;
import fr.pierre.tchatback.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/oauth")
@CrossOrigin
public class OauthController {

    @Value("${google.clientId}")
    String googleClientId;

    @Value("${secretPsw}")
    String secretPsw;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserService userService;

    @Autowired
    RoleService rolService;
    
    @Autowired
    ConversationService conversationService;



    @PostMapping("/google")
    public ResponseEntity<TokenDto> google(@RequestBody TokenDto tokenDto) throws IOException {
        final NetHttpTransport transport = new NetHttpTransport();
        final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
        GoogleIdTokenVerifier.Builder verifier =
                new GoogleIdTokenVerifier.Builder(transport, jacksonFactory)
                .setAudience(Collections.singletonList(googleClientId));
        final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), tokenDto.getValue());
        final GoogleIdToken.Payload payload = googleIdToken.getPayload();
        
        System.out.println("payload.get('name'): "  + payload.get("name"));
        System.out.println("payload.get('picture'): "  + payload.get("picture"));
        System.out.println("payload.getSubject(): "  + payload.getSubject());
        System.out.println("payload.getEmail(): " + payload.getEmail());

        User user = new User();
        if(userService.existsProviderId(payload.getSubject())) {
        	//Si le user existe déjà on le met à jour
        	user = updateUser(payload.getSubject(), payload.getEmail(), payload.get("name").toString(), payload.get("picture").toString(), "GOOGLE");	
        }	
        else {
        	//Sinon on le sauvegarde
        	user = saveUser(payload.getSubject(), payload.getEmail(), payload.get("name").toString(), payload.get("picture").toString(), "GOOGLE");
        	//On ajoute le nouveau user au tchat général
        	addTchatGeneral(user);
        }
        TokenDto tokenRes = login(user);
        return new ResponseEntity<TokenDto>(tokenRes, HttpStatus.OK);
    }

    @PostMapping("/facebook")
    public ResponseEntity<?> facebook(@RequestBody TokenDto tokenDto) throws IOException {
        Facebook facebook = new FacebookTemplate(tokenDto.getValue());
        final String [] fields = {"email", "link", "name", "picture"};
        org.springframework.social.facebook.api.User userFacebook = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);
        

        System.out.println("userFacebook.getName(): "  + userFacebook.getName());
        System.out.println("userFacebook.getCover(): "  + "https://graph.facebook.com/"+userFacebook.getId()+"/picture?type=normal");
        System.out.println("userFacebook.getId(): "  + userFacebook.getId());
        System.out.println("userFacebook.getEmail(): " + userFacebook.getEmail());

        User user = new User();
        if(userService.existsProviderId(userFacebook.getId())) {
        	//Si le user existe déjà on le met à jour
        	user = updateUser(userFacebook.getId(), userFacebook.getEmail(), userFacebook.getName(), "https://graph.facebook.com/"+userFacebook.getId()+"/picture?type=normal", "FACEBOOK");	
        }
        else {
        	//Sinon on le sauvegarde
        	user = saveUser(userFacebook.getId(), userFacebook.getEmail(), userFacebook.getName(), "https://graph.facebook.com/"+userFacebook.getId()+"/picture?type=normal", "FACEBOOK");
        	//On ajoute le nouveau user au tchat général
        	addTchatGeneral(user);
        }
        TokenDto tokenRes = login(user);
        return new ResponseEntity<TokenDto>(tokenRes, HttpStatus.OK);
    }

    private TokenDto login(User user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getProviderId(), secretPsw)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        TokenDto tokenDto = new TokenDto();
        tokenDto.setValue(jwt);
        tokenDto.setUserId(user.getId());
        return tokenDto;
    }

    private User saveUser(String providerId, String email, String name, String pictureUrl, String provider){
        User user = new User(providerId, email, name, pictureUrl, passwordEncoder.encode(secretPsw), provider);
        Role rolUser = rolService.getByRoleName(RoleName.ROLE_USER).get();
        Set<Role> roles = new HashSet<>();
        roles.add(rolUser);
        user.setRoles(roles);
        return userService.save(user);
    }

    private User updateUser(String providerId, String email, String name, String pictureUrl, String provider){
    	User user = userService.getByProviderId(providerId).get();
        user.setEmail(email);
        user.setName(name);
        user.setPictureUrl(pictureUrl);
        user.setProvider(provider);
        return userService.save(user);
    }
    
    private void addTchatGeneral(User user) {
    	Conversation conversationGenerale =  conversationService.findConversationById(1);
    	conversationGenerale.addUser(user);
    	conversationService.save(conversationGenerale);
    }

}
