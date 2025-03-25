package main;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter{
	
	public static Map<String, ServerData> serverDataMap;
	ServerData serverData;
	private static final String DATA_FILE = "ServerDatas.json";
	Member targetMember;
		String content;
		String[] par;
		List<String> parts = new ArrayList<>();
		String targetMemberId;
		PlayerData playerData;
		CharacterData characterData;
		Member self;
	
	@Override
    public void onMessageReceived(MessageReceivedEvent event) {
		loadUserData();
		  if (serverDataMap == null) {
              serverDataMap = new LinkedHashMap<>();
          }
		  
		
		if (event.getAuthor().isBot()) return;
		
		content = event.getMessage().getContentRaw();
		
		if (!event.isFromGuild()) return;
		
		
		String serverId = event.getGuild().getId();
		this.serverData = serverDataMap.get(serverId);
		if (serverData == null) {
		serverDataMap.putIfAbsent(serverId, new ServerData(serverId));
		this.serverData = serverDataMap.get(serverId);}
		par = content.split("\\s+");
		
		
		boolean A = false;
        targetMember = par.length > 1 ? Misc.extractMemberFromMention(par[1], event) : null;
        if (targetMember == null) {
        	targetMember = event.getMember();
        } else A = true;
        self = event.getMember();
        targetMemberId = targetMember.getId();
        playerData = serverData.playerDatas.get(targetMemberId);
        if (playerData == null) {
        	playerData = new PlayerData(targetMemberId);
        	serverData.playerDatas.put(targetMemberId, playerData);
        
        }
      ;
      String command = par[0]; 
      if (!command.contains(serverData.prefix))
    	   return;
        characterData = Misc.characterCheck(content,targetMember, event, serverData, this);
        if (characterData == null) {
        	Misc.sm("could not find character", event);
        	return;
        }
       for (String part : par) {
    	   if (!part.contains("@")&&!part.contains(serverData.prefix)) {
    		  parts.add(part);
    	   }
       }
		List<String> segments = Misc.splitByQuotes(content);
       
       
        command = command.toLowerCase().replace(serverData.prefix, "");
       
        content = content.replace(serverData.prefix, "");
        boolean moderator = false;
       
        if (Misc.isModerator(event.getMember().getId(),event.getMember(), serverData)) {
        	moderator = true;
        }
        String trueCommand = command.replace(serverData.prefix, "");
      
    if (moderator ||!serverData.lockedCommands.contains(trueCommand)) {
        if (serverData.dice) {
        	if ((command.equals("roll") || command.equals("r"))) {//&& serverData.dice
        		diceroller.rollprocessorNew(content.toLowerCase(), event, characterData, serverData, targetMember);
        	}
        }
        
        if (serverData.character) {
        	if (command.equals("damage") && !A) {
        		CharacterData.modifystats(characterData, "Current HP: "+Integer.valueOf(characterData.stats.get("Current HP") - Integer.valueOf(parts.get(0))).toString(), serverData, event, playerData);
        	}
        	if (command.equals("heal")) {
        		CharacterData.modifystats(characterData, "Current HP: "+Integer.valueOf(characterData.stats.get("Current HP") + Integer.valueOf(parts.get(0))).toString(), serverData, event, playerData);
        	}
        	if (command.equals("avatar")) {
        		CharacterData.avatar(characterData, event);
        	}
        	if (command.equals("adddata")) {
        		content = content.toLowerCase();
        		boolean pass = false;
        		for (String cdata : serverData.cData) {
        			if (content.replace("adddata", "").trim().startsWith(cdata.toLowerCase())) {
        				
        		CharacterData.addCharData(cdata, content.replace("adddata", "").replace(cdata.toLowerCase(), "").trim(), characterData, event);
        			pass = true;
        		break;}}
        		 if (!pass) Misc.sm("Failed to update data", event);
        	}
        	if (command.equals("approve") && Misc.isModerator(self.getId(), self, serverData)) {
        		PlayerData.characterApproval(event, serverData);
        	}
           	if (command.equals("stats") &&(!A ||Misc.isModerator(self.getId(), self, serverData))) {
        		playerData.viewAllStats(event, playerData, serverData, characterData);
           	}
           	if (command.equals("delete")&&(!A ||Misc.isModerator(self.getId(), self, serverData))) {
           		PlayerData.characterDelete(characterData, event, serverData, playerData, false);
           	}
           	if (command.equals("terminate")&& Misc.isModerator(self.getId(), self, serverData)) {
           		if (!A) {
           			Misc.sm("Please mention a user", event);
           			return;
           		}
           		PlayerData.characterDelete(characterData, event, serverData, playerData, true);
           	}
           	if (command.equals("recover")&&(!A ||Misc.isModerator(self.getId(), self, serverData))) {
           		PlayerData.characterRecovery(segments.get(1), playerData, serverData, event);
           	}
           	if (command.equals("viewdeleted")) {
           		PlayerData.DelCharView(serverData, event);
           	}
           if (command.equals("select")&&(!A)) {
        	   CharacterData.select(Integer.valueOf(parts.get(0)), playerData);
           }
           if (command.equals("addLevelUp")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	   if (!A) {
          			Misc.sm("Please mention a user", event);
          			return;
          		}
        	   CharacterData.select(Integer.valueOf(parts.get(0)), playerData);
           }
           if (command.equals("allocate")&&(!A)) {
        	   CharacterData.allocate(characterData, serverData, content, playerData);
           }
           if (command.equals("setStats")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	   if (!A) {
          			Misc.sm("Please mention a user", event);
          			return;
          		}
        	   CharacterData.modifystats(characterData, content, serverData, event, playerData);
           }
           if (command.equals("viewabilities")&&(!A ||Misc.isModerator(self.getId(), self, serverData))) {
        	   characterData.viewAbilities(event, playerData);
           }
           if (command.equals("addexp")) {
        	   if (parts.size()>0) {
        	   CharacterData.addexp(playerData, event,Integer.valueOf(parts.get(0)), serverData);}
        	   else CharacterData.addexp(playerData, event,null, serverData);
           }
           if (command.equals("changecharacter")&&(!A ||Misc.isModerator(self.getId(), self, serverData))) {
        	   CharacterData.characterModification(characterData, event, content, serverData);
           }
           if (command.equals("get")) {
        	   boolean pass = false;
        	   for (String cdata : serverData.cData) {
        		  if( content.replace("get", "").trim().equals(cdata.toLowerCase())){
        			  CharacterData.retrieveCharData(cdata, characterData, event);
        			  pass = true;
        		  }
        	   } if (!pass) Misc.sm("Couldnt find the requested data", event);
           }
          if (command.equals("id")) {
        	  if (parts.get(0).equals("1")){
        	ImageClass.IdCard1(event);
        	  }if (parts.get(0).equals("2")) {
        		  ImageClass.IdCard2(event);
        	  }
        	  if (parts.get(0).equals("3")) {
        	  ImageClass.IdCard3(event);
        	  }else if (parts.get(0).equals("4")) {
        	  ImageClass.IDcard4(event, characterData);
          }}
        }
        if (serverData.econ) {
        	if (command.equals("work")&&(!A ||Misc.isModerator(self.getId(), self, serverData))) {
        		if (segments.size()>1) {
        			CharacterData.work(segments.get(1), characterData, serverData, event);
        		}
        		CharacterData.work(parts.get(0), characterData, serverData, event);
        	}
           	if (command.equals("crime")&&(!A ||Misc.isModerator(self.getId(), self, serverData))) {
           		if (segments.size()>1) {
        			CharacterData.crime(segments.get(1), characterData, serverData, event);
        		}
        		CharacterData.crime(parts.get(0), characterData, serverData, event);
           	}
           	
        	if (command.equals("econenable")&&(Misc.isModerator(self.getId(), self, serverData))) {
        		EconomicData.enable(parts.get(0), true, event, serverData);
        	}
        	if (command.equals("currency")&&(Misc.isModerator(self.getId(), self, serverData))) {
        		EconomicData.changeCurrency(parts.get(0), event, serverData);
        	}
        	if (command.equals("econvalue")&&(Misc.isModerator(self.getId(), self, serverData))) {
        		EconomicData.setValues(parts.get(0), parts.get(1), event, serverData);
        	}
           	if (command.equals("setjob")&&(Misc.isModerator(self.getId(), self, serverData))) {
           		EconomicData.setjob(parts.get(0), parts.get(1), parts.get(3), event, serverData);
           	}
           	if (command.equals("removeitem")&&(Misc.isModerator(self.getId(), self, serverData))) {
           		if (segments.size()>1) {
           			EconomicData.removeItem(segments.get(1), event, serverData);
           		}else EconomicData.removeItem(parts.get(0), event, serverData);
           	}
           	if (command.equals("adjustcurrency")&&(Misc.isModerator(self.getId(), self, serverData))) {
           		if (!A) {
           			Misc.sm("Please mention a user", event);
           			return;
           		}
        		EconomicData.adjustCurrency(parts.get(0), parts.get(1), event, serverData, characterData);
           	}
        	if ((command.equals("bal")||command.equals("balance"))&&(!A ||Misc.isModerator(self.getId(), self, serverData))) {
        		
        		CharacterData.view(characterData, event, serverData);
           	}
           	if (command.equals("give")) {
           		if (!A) {
           			Misc.sm("Please mention a user", event);
           			return;
           		}
           		CharacterData.give(characterData, event, serverData,Integer.valueOf( parts.get(0)));
           	}
        }
  
       if (command.equals("serverstats")&& Misc.isModerator(self.getId(), self, serverData)) {
    	  if (segments.size()>1) {
    		  serverData.modifyStats(parts.get(0),segments.get(1) , event, serverData);
    	  }else serverData.modifyStats(parts.get(0), parts.get(1), event, serverData);
        		
        }
        if (command.equals("serverdata")&& Misc.isModerator(self.getId(), self, serverData)) {
        	  if (segments.size()>1) {
        		  serverData.modifyCData(parts.get(0),segments.get(1) , event, serverData);
        	  }else serverData.modifyStats(parts.get(0), parts.get(1), event, serverData);
        	  
        } if (command.equals("abilityname")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	 if (segments.size()>1) {
        		 serverData.abilityNameChange(segments.get(1), event);
        	 }else 
        	serverData.abilityNameChange(parts.get(0), event);
        }
        if (command.equals("role")&& Misc.isModerator(self.getId(), self, serverData)) {
        	serverData.changeRole(segments.get(1), event, serverData, event.getGuild());
        }
        if (command.equals("viewstats")&& Misc.isModerator(self.getId(), self, serverData)) {
        	serverData.viewStats(serverData, event);
        }
        if (command.equals("viewdata")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	serverData.viewCData(serverData, event);
        }
        if (command.equals("enable")&&(Misc.isModerator(self.getId(), self, serverData))) {
      
        	ServerData.enable(serverData, parts.get(0), event);
        }
        if (command.equals("disable")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	serverData.disable(parts.get(0), event);
        }
        if (command.equals("levelstrat")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	serverData.levelStrat(segments.get(1), event);
        }
        if (command.equals("modifydata")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	serverData.handleNumberData(parts.get(0), parts.get(1), event);
        }
        if (command.equals("lock")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	serverData.lockCommands(parts.get(0), event);
        }
        if (command.equals("abbrev")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	serverData.addAbbrev(parts.get(0), parts.get(1), event);
        }
        if (command.equals("hpstat")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	serverData.changeHealthDerivitive(parts.get(0), event);
        }
        if (command.equals("hpmulti")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	serverData.changeMultiplier(Integer.valueOf(parts.get(0)), event);
        }
        if (command.equals("levelstat")&&(Misc.isModerator(self.getId(), self, serverData))) {
        	
        	serverData.levelUpStats(Misc.capitalize( parts.get(0)), Integer.valueOf(parts.get(1)), event);
        }
       if (command.equals("cdatasplit")&&(Misc.isModerator(self.getId(), self, serverData))) {
    	   serverData.cdataSplit(Integer.valueOf(parts.get(0)), event);
       }
       if (command.equals("playerclear")&&(Misc.isModerator(self.getId(), self, serverData))&&!A) {
    	   PlayerData.clear(playerData, serverData, event);
       }
       if (command.equals("serverclear")&&(Misc.isModerator(self.getId(), self, serverData))&&!A) {
    	   ServerData.clear(serverData, event);
       }
       if (command.equals("update")) {
    	   serverData.globalUpdate();
       }
       if (command.equals("image")) {            
    	   System.out.println(ImageClass.imageRetrieval(event.getMessage(), event, characterData));
       }
       
        	}
        
        	//saveUserData();
        
        
	
	}

	

	 static void saveUserData() {
	        try (FileWriter writer = new FileWriter(DATA_FILE)) {
	            new Gson().toJson(serverDataMap, writer);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	 }
	  static void loadUserData() {
	        try (FileReader reader = new FileReader(DATA_FILE)) {
	            Type userDataType = new TypeToken<Map<String, ServerData>>() {}.getType();
	            serverDataMap = new Gson().fromJson(reader, userDataType);
	            
	            if (serverDataMap == null) {
	                serverDataMap = new LinkedHashMap<>();
	            }
	        } catch (IOException e) {
	            serverDataMap = new LinkedHashMap<>();
	            
	            e.printStackTrace();
	        }
	  }
}

