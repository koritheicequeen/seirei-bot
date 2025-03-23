package main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class diceroller {
	public static void rollprocessorNew(String content, MessageReceivedEvent event, CharacterData characterData, ServerData serverData, Member member) {
		String[] Words = content.replace("r ", "").replace("roll ", "").replace(" ", "").split("(?=[+-])|(?<=[+-])");
    	Boolean areThereDice = false;
    	Boolean Negative = false;
    	String dividend = "0";
    	String multiplier = "0";
    	int dice = 0;
    	String diceType = "";
    	List<Integer> results = new ArrayList<>();
    	double result = 0;
    	StringBuilder message = new StringBuilder();
    	StringBuilder response = new StringBuilder();
    	Double finalTotal = 0.0;
    	int bonus = 0;
    	
    	double Bonus2 = 0.0;
    	// 1 magic (/)5 (*)2 + 1 agility (/)2 - 15 + 1 5
    	for (int i = 0; i < Words.length; i++) {
    		
    		Words[i].toLowerCase();
    				if (i >0) {
    				if (Words[i-1].contains("-")) {
    					Negative = true;
    				}
    				if (Words[i-1].contains("+")) {
    					Negative = false;
    				}}
    		if (Words[i].contains("d")) {
    			areThereDice=true;
    			// 1 magic 5 3
    			Pattern pattern = Pattern.compile("(\\d*)d(.*)$");
    	        Matcher matcher = pattern.matcher(Words[i]);
    	        if (matcher.find()) {
    	        dice = numberOfDiceRetrieval(matcher.group(1).isEmpty() ? "" : (matcher.group(1)));
    	        diceType = matcher.group(2);}
    	        
    	        if (diceType.contains("/")) {
    	        Pattern pattern2 = Pattern.compile("^(.*?)/(.*)$");
    	        Matcher matcher2 = pattern2.matcher(diceType);
    	        if (matcher2.find()) {
    	        diceType = matcher2.group(1);
    	        dividend = matcher2.group(2);}
    	        
    	        if (dividend.contains("*")) {
        	        Pattern pattern3 = Pattern.compile("^(.*?)\\*(.*)$");
        	        Matcher matcher3 = pattern3.matcher(dividend);
        	        if (matcher3.find()) {
        	        dividend = matcher3.group(1);
        	        multiplier = matcher3.group(2);}
        	        }
    	      
    	        }
    	        if (diceType.contains("*")) {
        	        Pattern pattern2 = Pattern.compile("^(.*?)\\*(.*)$");
        	        Matcher matcher2 = pattern2.matcher(diceType);
        	        if (matcher2.find()) {
        	        diceType = matcher2.group(1);
        	        multiplier = matcher2.group(2);
        	        }}
    	        
    	       if (serverData.abbrev.containsKey(Misc.capitalize(diceType))) {
    	    	   diceType = serverData.abbrev.get(Misc.capitalize(diceType));
    	       }
    	       // if (diceType.contains("mag")) diceType = "magic";
    	         if (characterData.stats.containsKey(Misc.capitalize(diceType))) {
    	    	 result = Integer.valueOf(characterData.stats.get(Misc.capitalize(diceType)));
    	      } else if (Integer.valueOf(diceType)>0)  result = Integer.valueOf(diceType);
    	      else {event.getChannel().sendMessage("an error has occured").queue(); return;}
    	         
    	       
      	      if (Integer.valueOf(dividend) !=0) {
      	    	  result= result/Integer.valueOf(dividend);
      	      }
      	      if (Integer.valueOf(multiplier) !=0) {
      	    	  result= result*Integer.valueOf(multiplier);
      	      }
      	       double decimalResponse = new BigDecimal(result).setScale(1, RoundingMode.HALF_UP).doubleValue(); 
      	      int intResponse = (int)decimalResponse;
      	      for(int i2 =0; i2 < Integer.valueOf(dice); i2++) {
      	    	  int intResponse2 = rollDie(intResponse);
      	    	   if (Negative) {
        	    	  intResponse2 = -1*(intResponse2);
        	    
        	      } 
      	     finalTotal += intResponse2;
    	      response.append(" (" +intResponse2+") +" );
    	     results.add(intResponse2);
      	      }
      	      dividend = "0";
      	     multiplier = "0";
    		}else if (!Words[i].contains("-")&& !Words[i].contains("+") && !Words[i].contains("!")) {
    			String value = Words[i];
    			 if (value.contains("*")) {
         	        Pattern pattern2 = Pattern.compile("^(.*?)\\*(.*)$");
         	        Matcher matcher2 = pattern2.matcher(value);
         	       if (matcher2.find()) {
         	    	  
         	        value = matcher2.group(1);
         	     
         	        multiplier = matcher2.group(2);}
         	        }
    			 if (value.contains("/")) {
         	        Pattern pattern3 = Pattern.compile("^(.*?)/(.*)$");
         	        Matcher matcher3 = pattern3.matcher(value);
         	       if (matcher3.find()) {
         	        value = matcher3.group(1);
         	        dividend = matcher3.group(2);}
         	        }
    			 if (multiplier.contains("/")) {
          	        Pattern pattern3 = Pattern.compile("^(.*?)/(.*)$");
          	        Matcher matcher3 = pattern3.matcher(multiplier);
          	      if (matcher3.find()) {
          	        multiplier = matcher3.group(1);
          	        dividend = matcher3.group(2);}
          	        }
    			 double Bonus = Integer.valueOf(value);
    			 if (multiplier != "0") {
    				 Bonus = Bonus*Integer.valueOf(multiplier);
    			 }
    			 if (dividend != "0") {
    				 Bonus = Bonus/Integer.valueOf(dividend);
    			 }
    			 if (Negative) {
    				 Bonus = -(Bonus);
    			 }
    			 if (areThereDice) {
    				 
    			  double Bonus3 = new BigDecimal(Bonus).setScale(1, RoundingMode.HALF_UP).doubleValue(); 
    		
    			  Bonus2 = Bonus3;
    			 bonus = (int) Bonus2;
    			 finalTotal += bonus;
    			}
    			 else { Bonus2 = Bonus;
    				 finalTotal += Bonus;
    			 }
        	     results.add(bonus);
        	     response.append(" (" + Bonus2+") +" );
    			 dividend = "0";
          	     multiplier = "0";
    		}
    		
    		}
    	
    for (String word : Words) {
    	
    			message.append(word + " ");	
    	}
   StringBuilder resultish = new StringBuilder();
      for(int i3 = 0; i3 < String.valueOf(finalTotal).length(); i3++) {
    	  if (String.valueOf(finalTotal).charAt(i3)=='.') {
    		  break;
    	  }
    	  resultish.append(String.valueOf(finalTotal).charAt(i3));
      }
 	       // = String.valueOf();
    
    response.delete(response.length()-1, response.length());
    response.append("= "+ "__" +resultish + "__"); 
    String name = "";
    if (characterData.name.contains("no name")){
    	name = member.getEffectiveName();
    } else name = characterData.name;
    event.getChannel().sendMessageEmbeds(Misc.createEmbed( name + "'s roll", message + " = " + response)).queue();
    
	}public static int rollDie(int sides) {
        return (int) (Math.random() * sides) + 1;  // Random roll between 1 and sides
    }
	private static int numberOfDiceRetrieval(String times) {
		 
		return times.isEmpty() ? 1 : Integer.valueOf(times);
		}
}
