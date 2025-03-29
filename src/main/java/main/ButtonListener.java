package main;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

public class ButtonListener extends ListenerAdapter{
	
	public static List<Button> turnPage (PlayerData playerData) {
        List<Button> buttons = new ArrayList<>();
        
        buttons.add(Button.secondary(playerData.UserId + " leftpage", "◁"));
        buttons.add(Button.secondary( playerData.UserId + " rightpage", "▷"));
        return buttons;
    }
	public static List<Button> turnPageAbility (PlayerData playerData) {
        List<Button> buttons = new ArrayList<>();
        
        buttons.add(Button.secondary(playerData.UserId + " leftpageability", "◁"));
        buttons.add(Button.secondary( playerData.UserId + " rightpageability", "▷"));
        return buttons;
    }
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		
	    String buttonId = event.getComponentId(); // Get button ID
	    String userID = event.getUser().getId();
	    
	    ServerData serverdata = MessageListener.serverDataMap.get(event.getGuild().getId());
	    if (!buttonId.equals("0")&&!buttonId.contains(userID)&&!Misc.isModerator(userID, event.getMember(), serverdata)){
	    	return;
	    }
	    String[] parts = buttonId.trim().split("\\s+");
	    PlayerData playerData = serverdata.playerDatas.get(parts[0]);
	
	    
	    switch (parts[1]) {
	    
	        case "leftpage":
	        
	        	playerData.selectedChar -= 1;
	        	if (playerData.selectedChar<0) {
	        		playerData.selectedChar = playerData.characterData.size()-1;
	        	}
	        	playerData.pages.get(playerData.selectedChar);
	        	CharacterData characterData = playerData.characterData.get(playerData.selectedChar);
	        	 EmbedBuilder updatedEmbed = new EmbedBuilder()
                .setTitle(characterData.name + "'s stats")
                .setDescription(playerData.pages.get(playerData.selectedChar))
                .setColor(Color.BLUE);
	        	
	        	 if (!characterData.Avatar.equals(null) && !characterData.Avatar.isEmpty()) {
	        	     	try (InputStream is = ImageClass.downloadImage(characterData.Avatar)) {  
	        	     		
	        	     		
	        	     		event.editMessageEmbeds(updatedEmbed.build()).queue();
	        	     		event.getChannel().retrieveMessageById(playerData.imageId)
	        	     	    .queue(message -> {
	        	     	    message.editMessageAttachments(FileUpload.fromData(is, "image.png")).queue();
	        	     	    }, failure -> {
	        	     	        
	        	     	    });
	        	         } catch (IOException e) {
	        	             e.printStackTrace();
	        	     	   }
	        	 
	        		  }
	            break;
	        case "rightpage":
	        	playerData.selectedChar +=1;
	        	if (playerData.selectedChar>playerData.characterData.size()-1) {
	        		playerData.selectedChar = 0;
	        	}
	        	playerData.pages.get(playerData.selectedChar);
	        	CharacterData characterData1 = playerData.characterData.get(playerData.selectedChar);
	        	 EmbedBuilder updatedEmbed1 = new EmbedBuilder()
                .setTitle(characterData1.name + "'s stats")
                .setDescription(playerData.pages.get(playerData.selectedChar))
                .setColor(Color.BLUE);
	        	
	        	 if (characterData1.Avatar!=null && !characterData1.Avatar.isEmpty()) {
	        	     	try (InputStream is = ImageClass.downloadImage(characterData1.Avatar)) {  
	        	     		
	        	     		
	        	     		event.editMessageEmbeds(updatedEmbed1.build()).queue();
	        	     		event.getChannel().retrieveMessageById(playerData.imageId)
	        	     	    .queue(message -> {
	        	     	    message.editMessageAttachments(FileUpload.fromData(is, "image.png")).queue();
	        	     	    }, failure -> {
	        	     	        
	        	     	    });
	        	         } catch (IOException e) {
	        	             e.printStackTrace();
	        	     	   }
	        	 
	        		  }
	        	break;
	        case "leftpageability":
	        	CharacterData characterData2 = playerData.characterData.get( playerData.selectedChar);
	        	characterData2.pagecount -= 1;
	        	if (characterData2.pagecount<0) {
	        		characterData2.pagecount = characterData2.pages.size()-1;
	        	}
	        	 EmbedBuilder updatedEmbed2 = new EmbedBuilder()
	                     .setTitle(characterData2.pages.get(characterData2.pagecount).getKey())
	                     .setDescription(characterData2.pages.get(characterData2.pagecount).getValue())
	                     .setColor(Color.BLACK);
	        	 event.editMessageEmbeds(updatedEmbed2.build()).queue();;
	        	 
	        	break;
	        case "rightpageability":
	        	CharacterData characterData3 = playerData.characterData.get( playerData.selectedChar);
	        	characterData3.pagecount += 1;
	        	if (characterData3.pagecount>characterData3.pages.size()-1) {
	        		characterData3.pagecount = 0;
	        	}
	        	 EmbedBuilder updatedEmbed3 = new EmbedBuilder()
	                     .setTitle(characterData3.pages.get(characterData3.pagecount).getKey())
	                     .setDescription(characterData3.pages.get(characterData3.pagecount).getValue())
	                     .setColor(Color.BLACK);
	        	 event.editMessageEmbeds(updatedEmbed3.build()).queue();;
	        	break;
	        
	        	
	    }
	   MessageListener.saveUserData();
	}
}
