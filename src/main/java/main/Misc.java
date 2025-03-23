package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Misc {

	static List<String> splitByQuotes(String message) {

	    List<String> segments = new ArrayList<>();

	    Pattern pattern = Pattern.compile("\"([^\"]*)\"");
	    Matcher matcher = pattern.matcher(message);

	    int lastIndex = 0;
	    while (matcher.find()) {
	        // Add text before the quoted segment
	        if (matcher.start() > lastIndex) {
	            String textBeforeQuote = message.substring(lastIndex, matcher.start()).trim();
	            if (!textBeforeQuote.isEmpty()) {
	                segments.add(textBeforeQuote);
	            }
	        }
	        // Add the quoted segment itself (without quotes)
	        segments.add(matcher.group(1));
	        lastIndex = matcher.end();
	    }

	    // Add any text after the last quoted segment
	    if (lastIndex < message.length()) {
	        String textAfterQuote = message.substring(lastIndex).trim();
	        if (!textAfterQuote.isEmpty()) {
	            segments.add(textAfterQuote);
	        }
	    }

	    return segments;
	}
	static boolean isModerator(String id, Member member, ServerData serverData) {

		if (id.equals("992246009147162624") || id.equals("0")) {
			
			return true;
		}
        return member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase(serverData.roleName));
    }
	 static Member extractMemberFromMention(String mention, MessageReceivedEvent event) {
	        String id = mention.replaceAll("[^0-9]", "");
	        if (id == null || id == "" || id.length()<5) {
	        	return null;
	        }
	        return event.getGuild().getMemberById(id);
	
}
	 static MessageEmbed createEmbed(String title, String description) {
		    return new EmbedBuilder()
		            .setTitle(title)
		            .setDescription(description)
		            .setColor(Color.BLUE)
		            .build();
		}
	 public static String capitalize(String str) {
		    if (str == null || str.isEmpty()) {
		        return str;
		    }
		    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
		}


	static void sm (String message, MessageReceivedEvent event) {
		event.getChannel().sendMessage(message).queue();
	}
	static CharacterData characterCheck (String content, Member member, MessageReceivedEvent event, ServerData serverData, MessageListener messageListener) {
		PlayerData playerData = serverData.playerDatas.get(member.getId());
		if (playerData == null) {
			playerData = new PlayerData(member.getId());
		}
		CharacterData characterData = null;
		if (playerData.characterData.size()>0) {
			// Ensure selectedChar is within bounds
			if (playerData.characterData.size() <= playerData.selectedChar) {
			    playerData.selectedChar = 0; // Set it to the last valid index
			}

			if (playerData.selectedChar < 0) {
			    playerData.selectedChar = playerData.characterData.size()-1; // If negative, reset to 0
			}
		}
		if (playerData.characterData.size()<1) {
			characterData = new CharacterData(playerData.UserId, serverData);
			playerData.characterData.add(characterData);
			return characterData;
		}
		characterData = playerData.characterData.get(playerData.selectedChar);
        if (characterData == null) {
        	characterData = new CharacterData("no name" + playerData.characterData.size()+1, serverData);
        	playerData.characterData.add(characterData);
        }  
		 if (content.toLowerCase().contains("--select")){
	        	Pattern pattern = Pattern.compile("--select\\s*(\\d)");
	            Matcher matcher = pattern.matcher(content);
	            String num = "";
	            
	            if (matcher.find()) {  // Find the first match
	                // Extract the number from the capturing group
	                num = matcher.group(1);
	                messageListener.content.replace("--select", "").replace(matcher.group(1), "");
	                characterData = playerData.characterData.get(Integer.valueOf(num.trim())-1);
	               
	                if (characterData== null) {
	                	Misc.sm("Please select a valid character", event);
	                	return null;
	                }
	            System.out.println(characterData + "correct");
	        }}return characterData;
	}
	public static Integer calculateLevel(ServerData serverData, Integer levelcap) {
	    // Check if the leveling strategy is provided
	    if (serverData.levelingStrat == null)
	        return null;

	    int level = 0;
	    int ExpNeeded = 0;

	    // Loop to determine the experience required to reach the level cap
	    for (Integer i = 0; i < levelcap; i++) {
	        String formula = serverData.levelingStrat;
	      
	        // Manually compute the formula (basic example handling x^2 + x formula)
	        ExpNeeded = evaluateFormula(formula, i+1)-evaluateFormula(formula, i);

	       
	        // If we reach the level cap, return the total experience used
	        if (level == levelcap) {
	            return ExpNeeded;  
	        }level++;
	       
	    }
	    
	    return ExpNeeded; // This should not be reached as the loop breaks when level == levelcap
	}

	public static Integer currentLevel(ServerData serverData, CharacterData characterData, PlayerData playerData) {
	    // Check if the leveling strategy is provided
	    if (serverData.levelingStrat == null)
	        return null;
	    if (characterData==null) {
	    	characterData = new CharacterData(playerData.UserId, serverData);
	    	playerData.characterData.add(characterData);
	    }
	    int totalEXP = characterData.EXP;
	    int level = 1;
	    int ExpNeeded = 0;
	    // Loop to determine the experience required to reach the level cap
	    for (Integer i = 1; true; i++) {
	        String formula = serverData.levelingStrat;
	      
	        // Manually compute the formula (basic example handling x^2 + x formula)
	        ExpNeeded = evaluateFormula(formula, i)-evaluateFormula(formula, i-1);
	     
	       
	        // If we reach the level cap, return the total experience used
	        if (ExpNeeded>totalEXP) {
	        	
	        	characterData.remainingExp=totalEXP;
	            return level; 
	        }else totalEXP -=ExpNeeded;
	        level++;
	    }

	    
	}
	// This function will evaluate basic expressions like x^2 + x
	public static int evaluateFormula(String formula, int x) {
	  
		boolean positive = true;
	

	    // Split the formula into terms (e.g., "25x^2" and "25x")
	    String[] terms = formula.split("\\s+");
	   
	    int result = 0;

	    // For each term, calculate its value (basic handling for terms like 25x^2 and 25x)
	    for (String term : terms) {
	        term = term.trim();

	        // If term contains x^2
	        if (term.contains("x^")) {
	        	String[] parts = term.split("x\\^");
	        	Integer exponent =Integer.parseInt( parts[1].replace("^", ""));
	                	 String coefficient = parts[0].replace("x", "").trim();
	                
	            double coefficientValue = coefficient.isEmpty() ? 1.0 : Double.parseDouble(coefficient);
	            double termResult =coefficientValue * Math.pow(x, exponent);
	           
	            int roundedTermResult = (int) Math.floor(termResult);
	            if(positive) {
	            	 result += roundedTermResult;
	            }
	           if (!positive) {
	        	   result -= roundedTermResult;
	           }
	              
	        }

	        // If term contains x
	        else if (term.contains("x")) {
	        	String coefficient = term.replace("x", "").trim();
	        	double coefficientValue = coefficient.isEmpty() ? 1.0 : Double.parseDouble(coefficient);

	        	// Calculate the result for this term
	        	double termResult = coefficientValue * x;

	        	// Round down and cast to int
	        	
	        	int roundedTermResult = (int) Math.floor(termResult);
	            if (positive) {
	            result += roundedTermResult; }
	            if (!positive) {
	            result -= roundedTermResult;
	            
	            }
	        }
	        else if (term.contains("+")) {
	        	positive = true;
	        }
	        else if (term.contains("-")) {
	        	positive = false;
	        }else {
	        	 if(positive) {
	            	 result += Integer.parseInt(term);
	            }
	           if (!positive) {
	        	   result -= Integer.parseInt(term);
	        }
	        }
	    }
	    return result;
	}


		
	
	
}


