package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class CharacterData {
	 int levelUpPoints;
	   HashMap<String, Integer> stats;
	   String userId;
	   String Avatar;
	   String name;
	   Integer currency;
	   String race;
	   HashMap<String, String> Cdata;
	   Integer EXP;
	   Integer Level;
	   Integer remainingExp;
	   HashMap<String, String> abilities;
	   boolean terminated;
	   PlayerData creatorData;
	   List<Entry<String,String>> pages;
	   int pagecount;
	   List<ItemData> itemDatas;
	   
	   public CharacterData(String playerID, ServerData serverData) {
		   this.itemDatas = new ArrayList<>();
		   this.pagecount = 0;
		   this.pages = new ArrayList<>();
		   this.name = null;
		   this.userId=playerID;
		   terminated = false;
		   this.stats = new LinkedHashMap<>();  
		   this.stats.put("Total HP", 0);
		   this.stats.put("Current HP", 0);
		   for (String stat : serverData.stats) {
			   this.stats.put(stat, 0);
		   }
		   this.creatorData=null;
		   this.Avatar = null;
		   this.EXP = 0;
		   this.Level=1;
		   this.remainingExp = 0;
		   this.currency = serverData.economicData.startingCurrency;
		   this.Cdata = new LinkedHashMap<>();
		   
		   for (String cdata: serverData.cData) {
			   Cdata.put(cdata, null);
		   }
		   this.abilities= new LinkedHashMap<>();
		   
	   }
	   static void charUpdate(ServerData serverData, CharacterData characterData, PlayerData playerData) {
		   try {
				CharacterData.calculateLevel(serverData, characterData, playerData);
			} catch (ScriptException e) {
				e.printStackTrace();
			}
			CharacterData.calculateHP(characterData, serverData);
			serverData.levelupstatcalculator(characterData);
	   }
	   static void work (String jobName, CharacterData characterData, ServerData serverData, MessageReceivedEvent event) {
		   if (jobName!=null) {
			  if ( serverData.economicData.jobs.containsKey(Misc.capitalize(jobName))){
				   Transaction.addIncome(characterData,serverData.economicData.jobs.get(Misc.capitalize(jobName)), serverData, null);
			  } else if (serverData.economicData.incomeMin!=null && serverData.economicData.incomeCap!=null) {
				  String regex = ("(\\d+)\\s*h");
			         Pattern pattern = Pattern.compile(regex);
			         Matcher matcher = pattern.matcher(jobName);
			         
			        int total = 0;
			         if (matcher.find()) {  // Find the first match
			          for (int i = 0; i <Integer.valueOf( matcher.group(1)); i++) {
			        	  int roll = diceroller.rollDie(serverData.economicData.incomeCap-serverData.economicData.incomeMin) + serverData.economicData.incomeMin;
			        	  total+=roll;
			          }
			        
			          
				 } else total = diceroller.rollDie(serverData.economicData.incomeCap-serverData.economicData.incomeMin) + serverData.economicData.incomeMin;
				  Transaction.addIncome(characterData, total, serverData, event);
				
			  }else Misc.sm("Could not find the specified job", event);
			
		   }Misc.sm("Please specify the chosen job", event);
	   }
	   static void crime (String crimeName, CharacterData characterData, ServerData serverData, MessageReceivedEvent event) {
		   if (crimeName!=null) {
			  if ( serverData.economicData.crimes.containsKey(Misc.capitalize(crimeName))){
				   Transaction.addIncome(characterData,serverData.economicData.crimes.get(Misc.capitalize(crimeName)), serverData, null);
			  } else if (serverData.economicData.incomeMin!=null && serverData.economicData.incomeCap!=null) {
				  String regex = ("(\\d+)\\s*h");
			         Pattern pattern = Pattern.compile(regex);
			         Matcher matcher = pattern.matcher(crimeName);
			         
			        int total = 0;
			         if (matcher.find()) {  // Find the first match
			          for (int i = 0; i <Integer.valueOf( matcher.group(1)); i++) {
			        	  int roll = diceroller.rollDie(serverData.economicData.incomeCap-serverData.economicData.incomeMin) + serverData.economicData.incomeMin;
			        	  total+=roll;
			          }
			        
			          
				 } else total = diceroller.rollDie(serverData.economicData.incomeCap-serverData.economicData.incomeMin) + serverData.economicData.incomeMin;
				  Transaction.addIncome(characterData, total, serverData, event);
				
			  }else Misc.sm("Could not find the specified crime", event);
			
		   }Misc.sm("Please specify the chosen crime", event);
	   }
	   static void view (CharacterData characterData, MessageReceivedEvent event, ServerData serverData) {
		   event.getChannel().sendMessageEmbeds(Misc.createEmbed(characterData.name+ "'s Balance", characterData.currency + " "+serverData.economicData.currencyName)).queue();;
	   }
	  static void give (CharacterData characterData, MessageReceivedEvent event, ServerData serverData,int amount) {
		CharacterData self = serverData.playerDatas.get(event.getMember().getId()).characterData.get(serverData.playerDatas.get(event.getMember().getId()).selectedChar);
		if (self.currency>=amount) {
			self.currency -= amount;
			characterData.currency +=amount;
			Misc.sm(amount + " has been sent", event);
		} else Misc.sm("You do not have enough to complete this transaction", event);
	  }
	  static void select (int num, PlayerData playerData, MessageReceivedEvent event) {
		 playerData.selectedChar = num-1; 
		 Misc.sm(num + " has been selected", event);
		 
	  }
	  static void addLevelup (CharacterData characterData, String num, MessageReceivedEvent event) {
		  characterData.levelUpPoints+=Integer.valueOf(num.trim());
		  Misc.sm(num + " has been added", event);
	  }
	  static void allocate (CharacterData characterData, ServerData serverData, String text, PlayerData playerData, MessageReceivedEvent event) {
		  Pattern pattern = Pattern.compile("([A-Za-z]+)\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
		  Matcher matcher = pattern.matcher(text);

		  while (matcher.find()) {
		      String statName =Misc.capitalize(matcher.group(1).trim());
		      int value = Integer.parseInt(matcher.group(2).trim());

		      if (serverData.stats.contains(statName) || serverData.abbrev.containsKey(statName.toUpperCase())) {
		    	  if (serverData.abbrev.containsKey(statName.toUpperCase())) {
		    		  statName = serverData.abbrev.get(statName.toUpperCase());
		    	  }
		          if (value <= characterData.levelUpPoints) {
		              String lowerStat = statName.toLowerCase(); // or adjust as needed
		              characterData.stats.put(lowerStat, value + characterData.stats.get(lowerStat));
		              characterData.levelUpPoints -= value;
		              Misc.sm(statName + " has been updated", event);
		          } else {
		              Misc.sm(statName + " failed to update", event);
		          }

		         
		      }else   Misc.sm(statName + " failed to update", event);
		  }
		  try {
              calculateLevel(serverData, characterData, playerData);
          } catch (ScriptException e) {
              e.printStackTrace();
          }
         
		  calculateHP(characterData, serverData);
	  }
	 static void calculateLevel(ServerData serverData, CharacterData characterData,PlayerData playerData)  throws ScriptException  {
	int level = characterData.Level;
	characterData.Level= Misc.currentLevel(serverData, characterData, playerData );
	if (level != characterData.Level) {
		characterData.levelUpPoints= (characterData.Level-level)*serverData.levelUpPoints;
	}
		    }
	static void calculateHP (CharacterData characterData, ServerData serverData) {
		 if (characterData.stats.get("Total HP")!=characterData.stats.get(serverData.healthDerivitive)*serverData.healthMultiplier) {
			 characterData.stats.put("Current HP", characterData.stats.get(serverData.healthDerivitive)*serverData.healthMultiplier);
		 }
		 characterData.stats.put("Total HP",characterData.stats.get(serverData.healthDerivitive)*serverData.healthMultiplier);
		 //characterData.stats
	 }
	 static void modifystats (CharacterData characterData, String content, ServerData serverData, MessageReceivedEvent event, PlayerData playerData) {
		
		 content = content.toLowerCase();
		 for (String stat : serverData.stats) {
	    	 String regex = (stat.toLowerCase()) + ".*?:\\s*(\\d+)";
	         Pattern pattern = Pattern.compile(regex);
	         Matcher matcher = pattern.matcher(content);
	         if (matcher.find()) {  // Find the first match
	             // Extract the number from the capturing group
	        	
	             characterData.stats.put(stat, Integer.valueOf(matcher.group(1)));
	    }
	      
	}
		 String regex = "current hp" + ".*?:\\s*(\\d+)";
         Pattern pattern = Pattern.compile(regex);
         Matcher matcher = pattern.matcher(content);
         if (matcher.find()) {  // Find the first match
             // Extract the number from the capturing group
             characterData.stats.put("Current HP", Integer.valueOf(matcher.group(1)));}
         
		 String regex1 = "exp" + ".*?:\\s*(\\d+)";
         Pattern pattern1 = Pattern.compile(regex1);
         Matcher matcher1 = pattern1.matcher(content);
         if (matcher1.find()) {  // Find the first match
             // Extract the number from the capturing group
             characterData.EXP = Integer.valueOf(matcher1.group(1));}
         String regex11 = "level" + ".*?:\\s*(\\d+)";
         Pattern pattern11 = Pattern.compile(regex11);
         Matcher matcher11 = pattern11.matcher(content);
         if (matcher11.find()) {  // Find the first match
             // Extract the number from the capturing group
             characterData.Level = Integer.valueOf(matcher11.group(1));}
         
		 Misc.sm("Stats have been updated", event);
		 calculateHP(characterData, serverData);
		 try {
			calculateLevel(serverData, characterData, playerData);
		} catch (ScriptException e) {
		
			e.printStackTrace();
		}
	 }
	 void viewAbilities ( MessageReceivedEvent event, PlayerData playerData) {
			CharacterData characterData = this;
				for (Entry<String, String> entry : characterData.abilities.entrySet()) {
					pages.add(entry);
				}
					
			List<Button> buttons = ButtonListener.turnPageAbility(playerData);
			event.getChannel().sendMessageEmbeds(Misc.createEmbed(pages.get(0).getKey(), pages.get(0).getValue())).setActionRow(buttons).queue();
		             
		        
			 
	   }
	 static void characterModification(CharacterData characterData, MessageReceivedEvent event, String message, ServerData serverData) {
		   for (String cdata : serverData.cData) {
		    	 String regex = (cdata).toLowerCase() + ".*?:\\s*(.*)";
		         Pattern pattern = Pattern.compile(regex);
		         Matcher matcher = pattern.matcher(message);
		         if (matcher.find()) {  // Find the first match
		             // Extract the number from the capturing group
		             characterData.Cdata.put(cdata,matcher.group(1));
		    }
		   Misc.sm("Character info has been updated", event);
		  
		    }
	   }
	 void rename (CharacterData characterData, String text, MessageReceivedEvent event, ServerData serverData) {
		
		 characterData.name = text;
	 }
	  static String getAllStats(CharacterData characterData, ServerData serverData, PlayerData playerData) throws ScriptException {
		
		   StringBuilder message = new StringBuilder();
		  message.append("Character #: " + (playerData.characterData.indexOf(characterData)+1) + "\n");
		   int i = 0;
		   for (String cdata : serverData.cData) {
			   if (i >= serverData.cdataSplit) {
				   break;
			   }else i++;
			   message.append(cdata + ": " + characterData.Cdata.get(cdata) + "\n");
			   
		   }
		   message.append("\n \n Health: " + characterData.stats.get("Current HP") + "/" + characterData.stats.get("Total HP")+ "\n");
		   message.append("Level: "+characterData.Level + " - " + "(" + characterData.remainingExp + "/" + Misc.calculateLevel(serverData, characterData.Level)+") \n \n");
		  
		   for (int num = 0; num < serverData.stats.size(); num++) {
			   String stat = serverData.stats.get(num);
			   message.append(stat + ": " + characterData.stats.get(stat) + "\n");
		   } 
		   message.append(" __Remaining Points__: " + characterData.levelUpPoints + "\n" + "\n");
		   message.append("__Abilities__ \n");
		   for (Entry<String, String> entry : characterData.abilities.entrySet()) {
			   String ability = entry.getKey();
			   message.append(ability+ "\n");
		   }
		   charUpdate(serverData, characterData, playerData);
		return message.toString();
}
	  static void avatar(CharacterData characterData, MessageReceivedEvent event) {
		  if (ImageClass.avatarRetrieval(event.getMessage(), event, characterData)) {
			   Misc.sm("Avatar has been saved!", event);
		   } else Misc.sm("Failure to add Avatar", event);
	  }
	  static void addexp (MessageReceivedEvent event, Integer amount, ServerData serverData) {
		  Message message = event.getMessage();
			Message repliedMessage = null;
			String content = null;
			 if (message.getReferencedMessage() != null) {
			        repliedMessage = message.getReferencedMessage();
			       content = repliedMessage.getContentRaw();
			    } else {Misc.sm("Please reply to a message",event); return;}
			 PlayerData playerData = serverData.playerDatas.get(repliedMessage.getId());
			 content = content.replaceAll("\\[.*?\\]", "");
		  CharacterData characterData = null;
			 if (content.toLowerCase().contains("character #:")) {
				 String regex = ("Character #" + ".*?:\\s*(\\d+)");
		         Pattern pattern = Pattern.compile(regex);
		         Matcher matcher = pattern.matcher(content);
		        
		         if (matcher.find()) {  // Find the first match
		            content.replace("Character #:" + matcher.group(1), ""); // Extract the number from the capturing group
		        characterData = playerData.characterData.get(Integer.valueOf(matcher.group(1).trim())-1);
		       
		          
			 } else {Misc.sm("Please specify a character number", event); return;} }
			 Integer reqAmount = null;
			 if (content.toLowerCase().contains("amount:")) {
				 String regex = ("amount" + ".*?:\\s*(\\d+)");
		         Pattern pattern = Pattern.compile(regex);
		         Matcher matcher = pattern.matcher(content.toLowerCase());
		        
		         if (matcher.find()) {  // Find the first match
		            content.replace("Amount:" + matcher.group(1), ""); // Extract the number from the capturing group
		       reqAmount =Integer.valueOf(matcher.group(1));
			 } } if (amount!=null) {
				 reqAmount = amount;
			 }
			 if (reqAmount == null) {
				 Misc.sm("Could not find a value", event);
				 return;
			 }
			 characterData.EXP += reqAmount;
			 CharacterData.charUpdate(serverData, characterData, playerData);
			 Misc.sm(reqAmount.toString() + " has been added!", event);
			 
	  }
	  static void retrieveCharData(String cdata,CharacterData characterData, MessageReceivedEvent event) {
		  StringBuilder sb = new StringBuilder(
		           characterData.Cdata.get(cdata)
		        );
		  		if (event.getChannel().getName().toLowerCase().contains("spam")) {
		  		
		        List<StringBuilder> chunks = splitStringBuilder(sb, 2000);

		        // Print the split chunks
		        for (int i = 0; i < chunks.size(); i++) {
		          Misc.sm(chunks.get(i).toString(), event);
		        }
		    }else Misc.sm("Lets move this to spam instead!", event);
	  }
	  static void addCharData(String cdata, String text, CharacterData characterData, MessageReceivedEvent event) {
		  StringBuilder sb = new StringBuilder();
		 sb.append(characterData.Cdata.get(cdata));
		 sb.append(" " + text);
		 characterData.Cdata.put(cdata, sb.toString());
		 Misc.sm(cdata + " has been updated", event);
	  }

		    public static List<StringBuilder> splitStringBuilder(StringBuilder sb, int chunkSize) {
		        List<StringBuilder> result = new ArrayList<>();
		        int start = 0;

		        while (start < sb.length()) {
		            int end = Math.min(start + chunkSize, sb.length());

		            // Look for the last period before the cutoff
		            int lastPeriod = sb.lastIndexOf(".", end);
		            if (lastPeriod > start && lastPeriod != -1) {
		                end = lastPeriod + 1; // Include the period in the chunk
		            }

		            // Add the chunk to the list
		            result.add(new StringBuilder(sb.substring(start, end).trim()));

		            // Move the start index forward
		            start = end;
		        }

		        return result;
		    }
		

}
	   