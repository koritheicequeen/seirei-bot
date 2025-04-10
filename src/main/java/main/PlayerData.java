package main;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

public class PlayerData {
	String UserId;
	List<CharacterData> characterData;
	int selectedChar;
	List<String> pages;
	String imageId;
	
	public PlayerData(String userId) {
		this.UserId = userId;
		this.characterData = new ArrayList<>();
		this.selectedChar = 0;
		
	}

void viewAllStats(MessageReceivedEvent event, PlayerData playerData, ServerData serverData, CharacterData characterData) {
	this.pages=new ArrayList<>();
	for (CharacterData characterDatas : playerData.characterData) {
		try {
			pages.add( CharacterData.getAllStats(characterDatas, serverData, playerData));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	
	List<Button> buttons = ButtonListener.turnPage(playerData);
	 if (characterData.Avatar!=null && !characterData.Avatar.isEmpty()) {
     	try (InputStream is = ImageClass.downloadImage(characterData.Avatar)) {
             if (is != null) {
            	 event.getChannel().sendMessageEmbeds(Misc.createEmbed(characterData.name + "'s Stats", pages.get(playerData.characterData.indexOf(characterData))))
            	    .setActionRow(buttons)
            	    .queue(); // Delete the first message (embed-only)
            	 Message message = event.getChannel().sendMessage("")
            			    .addFiles(FileUpload.fromData(is, "image.png"))
            			    .complete();
            	 String messageId = message.getId();
            	 this.imageId=messageId;
            	


             
             }
         } catch (IOException e) {
             e.printStackTrace();
	
     	   }
	 } else event.getChannel().sendMessageEmbeds(Misc.createEmbed(characterData.name +"'s Stats", pages.get(playerData.characterData.indexOf(characterData)))).setActionRow(buttons).queue();
	 
}
static void characterApproval( MessageReceivedEvent event, ServerData serverData) {
	
	Message message = event.getMessage();
	Message repliedMessage = null;
	String text = null;
	 if (message.getReferencedMessage() != null) {
	        repliedMessage = message.getReferencedMessage();
	       text = repliedMessage.getContentRaw();
	    } else {Misc.sm("Please reply to a message",event); return;}
	if (text.toLowerCase().contains("item template")){
		EconomicData.setItem(text, event, serverData);
		return;
	}
	 
	PlayerData playerData = serverData.playerDatas.get( repliedMessage.getAuthor().getId());
	if (playerData == null) {
		playerData = new PlayerData(repliedMessage.getAuthor().getId());
		serverData.playerDatas.put(repliedMessage.getAuthor().getId(), playerData);
	}
	

	playerData.characterData.removeIf(characterData -> {
	    // Checks for invalid names or default state, can add more checks here
	    return characterData.name == null || 
	           characterData.name.contains("no name")|| 
	           characterData.name.contains("main.CharacterData@");
	});
	if (playerData.characterData.size()>= serverData.CharacterCap &&!Misc.isModerator(repliedMessage.getMember().getId(), repliedMessage.getMember(), serverData)) {
		Misc.sm("Please delete a character first", event);
	}
	
	CharacterData characterData = null;
	 if (text.toLowerCase().contains("character #:")) {
		 String regex = ("Character #" + ".*?:\\s*(\\d+)");
         Pattern pattern = Pattern.compile(regex);
         Matcher matcher = pattern.matcher(text);
        
         if (matcher.find()) {  // Find the first match
           text = text.replace("Character #:" + matcher.group(1), ""); // Extract the number from the capturing group
        characterData = playerData.characterData.get(Integer.valueOf(matcher.group(1).trim())-1);
       
          
	 }else if (!text.contains("Basic Information") && (!text.contains("Character Name")||!text.contains("Real Name"))&&playerData.characterData.size()>0){ characterData = playerData.characterData.get(playerData.selectedChar-1);}}else if (!text.contains("Basic Information") && (!text.contains("Character Name")||!text.contains("Real Name"))&&playerData.characterData.size()>0){ characterData = playerData.characterData.get(playerData.selectedChar-1);}
		boolean New = false;
	 if (characterData==null) {
        	characterData = new CharacterData(playerData.UserId, serverData);
        	New = true;
        }
		
	    // Check if the message is a reply
	   
	    
	    for (String stat : serverData.stats) {
	    	 String regex = (stat) + ".*?:\\s*(\\d+)";
	         Pattern pattern = Pattern.compile(regex);
	         Matcher matcher = pattern.matcher(text);
	         if (matcher.find()) {  // Find the first match
	             // Extract the number from the capturing group
	        	
	             characterData.stats.put(stat, Integer.valueOf(matcher.group(1)));
	    }
	}
	    for (String cdata : serverData.cData) {
	    	
	    	 String regex = (cdata) + ".*?:\\s*(.*)";
	         Pattern pattern = Pattern.compile(regex);
	         Matcher matcher = pattern.matcher(text);
	         if (matcher.find()) {  // Find the first match
	             if (cdata.toLowerCase().contains("name")) {
	    		characterData.name=matcher.group(1).trim();
	    	}
	             characterData.Cdata.put(cdata,matcher.group(1).trim());
	    }
	    }
	    if (serverData.abilityName!=null) {
	    for (String abilityType : serverData.abilityName) {
	    	String abilityName = null;
	    	 String regex = (abilityType) + ".*?:\\s*(.*)";
	         Pattern pattern = Pattern.compile(regex);
	         Matcher matcher = pattern.matcher(text);
	         if (matcher.find()) {  // Find the first match
	             // Extract the number from the capturing group
	           abilityName = matcher.group(1);
	        text = text.replace(abilityType + ":", "");
	        text = text.replace(abilityName, "");
	       
	           characterData.abilities.put(abilityName, text); 
	          
	           
	         }
	    }}
	    boolean found = false;
	    for (int i = 0; i < playerData.characterData.size(); i++) {
	        CharacterData characterdata = playerData.characterData.get(i);
	        if (characterdata.name.equals(characterData.name)) {
	            // Update existing character data instead of replacing the reference
	            for (Entry<String, String> entry : characterData.Cdata.entrySet()) {
	                if (entry.getValue() != null)
	                    characterdata.Cdata.put(entry.getKey(), entry.getValue());
	            }
	            for (Entry<String, Integer> entry : characterData.stats.entrySet()) {
	                if (entry.getValue() != null && entry.getValue() != 0) {
	                    characterdata.stats.put(entry.getKey(), entry.getValue());
	                }
	            }
	            characterdata.currency = characterData.currency;
	            characterdata.EXP = characterData.EXP;

	            // Set the updated character back in the list
	            playerData.characterData.set(i, characterdata);

	            found = true;  // Mark that the character was found and updated
	            break;
	        }
	    }

	    if (!found) { // If no character was found, add a new one
	        playerData.characterData.add(characterData);
	    } else if (New) {
	        playerData.characterData.add(characterData); // Ensure it's appended properly
	    } 
	    StringBuilder send = new StringBuilder();
	    send.append("Approval has been confirmed");
	   if (ImageClass.avatarRetrieval(repliedMessage, event, characterData)) {
		   send.append("\n Avatar has been saved");
	   }
 Misc.sm(send.toString(), event);
	CharacterData.charUpdate(serverData, characterData, playerData);
	   
}
static void characterDelete(CharacterData characterData, MessageReceivedEvent event, ServerData serverData, PlayerData playerData, boolean terminate) {
characterData.creatorData=playerData;
serverData.deletedCharacterHolder.characterData.add(characterData);
playerData.characterData.remove(characterData);
		
if (terminate) {
	characterData.terminated=true;
}
	Misc.sm("Character has been deleted", event);
}
static void characterRecovery(String name, PlayerData playerData, ServerData serverData, MessageReceivedEvent event) {
	for (CharacterData characterData : serverData.deletedCharacterHolder.characterData) {
		if (characterData.name.toLowerCase().equals(name.toLowerCase())&& playerData==characterData.creatorData) {
			
			if (playerData.characterData.size()<serverData.CharacterCap || Misc.isModerator(event.getMember().getId(),event.getMember(), serverData)) {
				playerData.characterData.add(characterData);
				Misc.sm("Character has been retrieved", event);
				return;
			}else Misc.sm("You have too many characters", event);
		}
	} Misc.sm("Character could not be found", event);
}
static void DelCharView(ServerData serverData, MessageReceivedEvent event) {
	if (serverData.deletedCharacterHolder.characterData.size()>0) {
	serverData.deletedCharacterHolder.viewAllStats(event, serverData.deletedCharacterHolder, serverData, serverData.deletedCharacterHolder.characterData.get(0));}
	else Misc.sm("No character has been deleted", event);
}
static void clear(PlayerData playerData, ServerData serverData, MessageReceivedEvent event) {
	if (serverData.playerDatas.containsKey(playerData.UserId)) {	
		serverData.playerDatas.put(playerData.UserId, new PlayerData(playerData.UserId));
		Misc.sm("player's data has been cleared", event);
	} else Misc.sm("could not find data", event);
}
}
