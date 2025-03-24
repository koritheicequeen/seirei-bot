package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ServerData {
EconomicData economicData;
Map<String, PlayerData> playerDatas;
List<String> stats;
List<String> cData;
boolean dice;
boolean econ;
boolean character;
String levelingStrat;
Integer levelUpPoints;
Integer CharacterCap;
String serverID;
List<String> lockedCommands;
String prefix;
Map<String, String> abbrev;
String healthDerivitive;
Integer healthMultiplier;
List<CharacterData> delectedCharacters;
String roleName;
Map<String, Integer> levelUpStats;
List<String>abilityName;
Integer cdataSplit;
Integer levelstatmulti;
PlayerData deletedCharacterHolder;

	public ServerData (String serverID) {
		this.roleName = "";
		this.serverID=serverID;
		this.economicData = new EconomicData();
		this.playerDatas = new HashMap<>();
		this.stats = new ArrayList<>();
		this.cData = new ArrayList<>();
		this.dice = false;
		this.econ = false;
		this.character = false;
		this.levelingStrat=null;
		this.levelUpPoints=null;
		this.CharacterCap = 9;
		this.prefix = "!";
		this.lockedCommands = new ArrayList<>();
		this.abbrev = new HashMap<>();
		this.healthDerivitive = "Vitality";
		this.healthMultiplier = 1;
		this.delectedCharacters = new ArrayList<>();
		this.levelUpStats = new LinkedHashMap<>();
		this.levelstatmulti = 1;
	
		this.abilityName = new ArrayList<>();
		this.cdataSplit = 0;
		this.deletedCharacterHolder = new PlayerData("0");

	}
	void viewDeleted () {
		
	}
	void abilityNameChange (String name, MessageReceivedEvent event) {
		this.abilityName.add((name));
	}
	void changeRole (String rolename, MessageReceivedEvent event, ServerData serverData, Guild server) {
		List<Role> roles = server.getRolesByName(rolename, true);
		if (!roles.isEmpty()) {
			this.roleName=rolename;
			event.getChannel().sendMessage(roles.get(0) + " has been selected").queue();
		} else event.getChannel().sendMessage("Could not find the specified role").queue();
	}

	void modifyStats(String part1, String part2, MessageReceivedEvent event, ServerData serverData) {
		if (part1.toLowerCase().equals("remove")) {
			if (stats.contains(Misc.capitalize(part2))) {
			stats.remove(Misc.capitalize(part2));
			for (Entry<String, PlayerData> entry : serverData.playerDatas.entrySet()) {
				PlayerData playerData = entry.getValue();
			    for(CharacterData characterData : playerData.characterData) {
			    	characterData.stats.remove(Misc.capitalize(part2));
			    }
		}
		event.getChannel().sendMessage(Misc.capitalize(part2) + " has been removed").queue();
	} else event.getChannel().sendMessage("No such stat is found").queue();
	}
		if (part1.toLowerCase().equals("add")) {
			stats.add(Misc.capitalize(part2));
			for (Entry<String, PlayerData> entry : serverData.playerDatas.entrySet()) {
				PlayerData playerData = entry.getValue();
			    for(CharacterData characterData : playerData.characterData) {
			    	characterData.stats.put(Misc.capitalize(part2),1);
			    }}
			event.getChannel().sendMessage(Misc.capitalize(part2) + " has been added").queue();}}
	void viewStats(ServerData serverData, MessageReceivedEvent event) {
		StringBuilder message = new StringBuilder();
		for (String stat : serverData.stats) {
			message.append(stat + "\n");
		}
		Guild guild = event.getJDA().getGuildById(serverData.serverID);
		String serverName = guild.getName();
		event.getChannel().sendMessageEmbeds(Misc.createEmbed(serverName + "'s stats", message.toString())).queue();
	}
	void modifyCData(String part1, String part2, MessageReceivedEvent event, ServerData serverData) {
		if (part1.toLowerCase().equals("remove")) {
			if (cData.contains((part2))) {
			cData.remove((part2));
			for (Entry<String, PlayerData> entry : serverData.playerDatas.entrySet()) {
				PlayerData playerData = entry.getValue();
			    for(CharacterData characterData : playerData.characterData) {
			    	characterData.Cdata.remove((part2));
			    }
		}
		event.getChannel().sendMessage((part2) + " has been removed").queue();
	} else event.getChannel().sendMessage("No such character trait was found").queue();
	}
		if (part1.toLowerCase().equals("add")) {
			cData.add((part2));
			for (Entry<String, PlayerData> entry : serverData.playerDatas.entrySet()) {
				PlayerData playerData = entry.getValue();
			    for(CharacterData characterData : playerData.characterData) {
			    	characterData.Cdata.put((part2),null);
			    }}
			event.getChannel().sendMessage((part2) + " has been added").queue();}}
	void viewCData(ServerData serverData, MessageReceivedEvent event) {
		StringBuilder message = new StringBuilder();
		for (String cdata : serverData.cData) {
			message.append(cdata + "\n");
		}
		Guild guild = event.getJDA().getGuildById(serverData.serverID);
		String serverName = guild.getName();
		event.getChannel().sendMessageEmbeds(Misc.createEmbed(serverName + "'s character data", message.toString())).queue();
	}
	static void enable (ServerData serverData, String enable, MessageReceivedEvent event) {
		
		enable = enable.toLowerCase();
		boolean triggered = false;
		if (enable.equals("dice") ||enable.equals("roll") ) {
			serverData.dice = true;
			triggered = true;
		}else 
		if (enable.contains("econ")) {
			serverData.econ = true;
			triggered = true;
		}else 
		if (enable.equals("char") ||enable.equals("character")) {
			serverData.character = true;
			triggered = true;
		}
		if (triggered) {
			event.getChannel().sendMessage(enable + " has been enabled").queue();
		} else event.getChannel().sendMessage("no such feature exists").queue();	
	}
	void disable (String disable, MessageReceivedEvent event) {
		disable = disable.toLowerCase();
		boolean triggered = false;
		if (disable.equals("dice")) {
			this.dice = false;
			triggered = true;
		}
		if (disable.equals("econ")) {
			this.econ = false;
			triggered = true;
		}
		if (disable.equals("character")) {
			this.character = false;
			triggered = true;
		}
		if (triggered) {
			event.getChannel().sendMessage(disable + " has been disabled").queue();
		} else event.getChannel().sendMessage("no such feature exists").queue();	
	}
	void levelStrat(String formula, MessageReceivedEvent event) {

		this.levelingStrat = formula;
		event.getChannel().sendMessage(formula + " has been recorded").queue();
	}
	void handleNumberData(String modify, String number, MessageReceivedEvent event) {
		modify = modify.toLowerCase();
		if (modify.equals("leveluppoints")) {
			this.levelUpPoints = Integer.valueOf(number);
		}
		else if (modify.equals("charactercap")) {
			this.CharacterCap = Integer.valueOf(number);
		} else if (modify.equals("prefix")) {
			this.prefix = number;
		}
	}
	void lockCommands (String command, MessageReceivedEvent event) {
		command = command.replace(prefix, "").toLowerCase();
		lockedCommands.add(command);
		event.getChannel().sendMessage(command + " has been added!").queue();
	}
	void addAbbrev (String stat, String abbrev, MessageReceivedEvent event) {
		stat = Misc.capitalize(stat);
		abbrev = abbrev.toUpperCase();
		this.abbrev.put(abbrev, stat);
		event.getChannel().sendMessage(abbrev + "has been added for " + stat).queue();
	}
	void changeHealthDerivitive(String healthstat, MessageReceivedEvent event) {
		healthstat = Misc.capitalize(healthstat);
		this.healthDerivitive = healthstat;
		
		event.getChannel().sendMessage("Healthstat has been changed");
		globalUpdate();
	}
	void changeMultiplier(Integer modifier, MessageReceivedEvent event) {
		healthMultiplier = modifier;
		
		event.getChannel().sendMessage("The modifier has been updated!").queue();
		globalUpdate();
	}
	void globalUpdate() {
		for (Entry<String, PlayerData> entry  : this.playerDatas.entrySet()) {
			for (CharacterData characterData: entry.getValue().characterData) {
				try {
					CharacterData.calculateLevel(this, characterData, entry.getValue());
				} catch (ScriptException e) {
					e.printStackTrace();
				}
				CharacterData.calculateHP(characterData, this);
				levelupstatcalculator(characterData);
			}
		}
	}
	void levelupstatcalculator(CharacterData characterData) {
		for (Entry<String, Integer> entry : this.levelUpStats.entrySet()) {
		characterData.stats.put(entry.getKey(), entry.getValue()*characterData.Level);
		}
	}
	
	void levelUpStats(String stat, Integer num, MessageReceivedEvent event) {
		if (this.stats.contains(Misc.capitalize(stat))) {
			this.levelUpStats.put(Misc.capitalize(stat), num);
			event.getChannel().sendMessage(stat + " has been updated").queue();;
			globalUpdate();
		}else event.getChannel().sendMessage(stat + " could not be found").queue();;
	}
	void cdataSplit(Integer num, MessageReceivedEvent event) {
		this.cdataSplit=num;
		Misc.sm("Split has been updated", event);
	}
	static void clear (ServerData serverData, MessageReceivedEvent event) {
		MessageListener.serverDataMap.remove(serverData.serverID);
		Misc.sm("Server Data has been cleared", event);
	}
	
}
