package main;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EconomicData {
	String currencyName = null;
	boolean work = false;
	Integer incomeCap = null;
	Integer startingCurrency;
	Integer incomeMin = null;
	boolean crime = false;
	boolean job = false;
	Integer dailyLimit;
	Map<String, Integer> crimes;
	Map<String, Integer> jobs;
	Map<String, Integer> itemsPrice;
	Map<String, String> itemsDesc;
	public EconomicData () {
		this.currencyName = null;
		this.work = false;
		this.crime = false;
		this. job = false;
		
		this.dailyLimit = null;
		this.incomeCap = null;
		this.incomeMin = null;
		this.startingCurrency = 0;
		
		this. crimes = new HashMap<>();
		this. jobs = new HashMap<>();
		
		this. itemsPrice = new LinkedHashMap<>();
		this. itemsDesc = new LinkedHashMap<>();
		
	}
	static void changeCurrency (String currency, MessageReceivedEvent event, ServerData serverData) {
		serverData.economicData.currencyName = Misc.capitalize(currency);
		Misc.sm("currency had been updated", event);
	}
	static void enable (String type, String yesOrNo, MessageReceivedEvent event, ServerData serverData) {
		type =type.toLowerCase();
		yesOrNo = yesOrNo.toLowerCase();
		Boolean check = null;
		if (yesOrNo.equals("yes") || yesOrNo.equals("on") || yesOrNo.equals("enable")) {
			check = true;
		} else if (yesOrNo.equals("no") || yesOrNo.equals("off") || yesOrNo.equals("disable")) {
			check = false;
		}
		if (check == null) {
			event.getChannel().sendMessage("please specify on or off").queue();
			return;
		}
		if (type.equals("work")) {
			serverData.economicData.work = check;
		} else
		if (type.equals("crime")) {
			serverData.economicData.work = check;
		} else
		if (type.equals("job")) {
			serverData.economicData.work = check;
		} else { event.getChannel().sendMessage("Please specify an existing feature").queue(); return;}
		event.getChannel().sendMessage(type + " has been changed");
	}
	static void setValues (String type, String num, MessageReceivedEvent event, ServerData serverData) {
		Integer Num = Integer.valueOf(num);
		if (Num == null) {
			event.getChannel().sendMessage("Please add numbers correctly").queue();
			return;
		}
		type = type.toLowerCase();
		if (type.contains("limit")) {
			serverData.economicData.dailyLimit = Num;
		} else
		if (type.contains("cap")) {
			serverData.economicData.incomeCap = Num;
		} else
		if (type.contains("min")) {
			serverData.economicData.incomeMin = Num;
		} else
		if (type.contains("start")) {
			serverData.economicData.startingCurrency = Num;
		} else {
			event.getChannel().sendMessage("Please specify a proper target").queue();
		}
	}
	static void setjob (String addDelete, String crime, String Num, MessageReceivedEvent event, ServerData serverData) {
		
		if (crime.toLowerCase().equals("crime")) {
		addDelete.toLowerCase();
				if (addDelete.equals("add")) {
					serverData.economicData.crimes.put(Misc.capitalize(crime), Integer.valueOf(Num));
				} else if (addDelete.equals("remove") ||addDelete.equals("delete")||addDelete.equals("null")) {
					serverData.economicData.crimes.remove(crime);
				}
	}
		if (crime.toLowerCase().equals("job")) {
			addDelete.toLowerCase();
					if (addDelete.equals("add")) {
						serverData.economicData.jobs.put(Misc.capitalize(crime), Integer.valueOf(Num));
					} else if (addDelete.equals("remove") ||addDelete.equals("delete")||addDelete.equals("null")) {
						serverData.economicData.jobs.remove(crime);
					}
		}
	}
	static void setItem (String text, MessageReceivedEvent event, ServerData serverData) {
		Pattern pattern = Pattern.compile("Name.*?:\\s*(.*)");
        Matcher matcher = pattern.matcher(text);
        String name = "";
        
        if (matcher.find()) {  // Find the first match
            // Extract the number from the capturing group
            name = matcher.group(1);
	name = Misc.capitalize(name);} else event.getChannel().sendMessage("Please send the appropriate template").queue();;
        pattern = Pattern.compile("Description:.*?:\\s*(.*)");
        matcher = pattern.matcher(text);
        String desc = "";
        if (matcher.find()) {  // Find the first match
            // Extract the number from the capturing group
            desc = matcher.group(1);}
        pattern = Pattern.compile("price:.*?:\\s*(.*)");
        matcher = pattern.matcher(text);
        String price = "";
        if (matcher.find()) {  // Find the first match
            // Extract the number from the capturing group
            price = matcher.group(1);}
	serverData.economicData.itemsPrice.put(name, Integer.valueOf(price));
	serverData.economicData.itemsDesc.put(name, desc);
	event.getChannel().sendMessage(name + " has been added").queue();;
	}
	static void removeItem (String name, MessageReceivedEvent event, ServerData serverData) {
		if (serverData.economicData.itemsPrice.containsKey(Misc.capitalize(name))) {
			serverData.economicData.itemsPrice.remove(Misc.capitalize(name));
			serverData.economicData.itemsDesc.remove(Misc.capitalize(name));
			event.getChannel().sendMessage(name + " has been removed").queue();
		}
		else event.getChannel().sendMessage("Could not find " + name).queue();
	}
	static void adjustCurrency (String addRemove, String amount, MessageReceivedEvent event, ServerData serverData, CharacterData characterData) {
		addRemove = addRemove.toLowerCase();
	if (addRemove.equals("add")) {
		characterData.currency +=Integer.valueOf(amount);
		Misc.sm(amount + "has been added", event);
	} else if (addRemove.equals("remove")){
		characterData.currency -=Integer.valueOf(amount);
		Misc.sm(amount + "has been removed", event);
	}else if (addRemove.equals("set")) {
		characterData.currency =Integer.valueOf(amount);
		Misc.sm(amount + "has been set", event);
	}else { Misc.sm("please specify add or remove", event); return;}
	
	}



}
