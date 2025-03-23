package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Transaction {
	
	    int amount;
	    long timestamp; // Store the time in milliseconds

	    public Transaction(int amount, long timestamp) {
	        this.amount = amount;
	        this.timestamp = timestamp;
	    }
	
	    static private final Map<CharacterData, List<Transaction>> userIncome = new HashMap<>();
  static private Integer INCOME_LIMIT;
 static private final long TIME_LIMIT = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

   static public void addIncome(CharacterData characterData, int amount, ServerData serverData, MessageReceivedEvent event) {
    	
    	if (serverData.economicData.dailyLimit!=null) {
    		INCOME_LIMIT = serverData.economicData.dailyLimit;
    	} else {
    		characterData.currency += amount;
    		Misc.sm(amount + " has been added", event);
    	}
        long currentTime = System.currentTimeMillis();

        // Get user's transactions or create a new list
        userIncome.putIfAbsent(characterData, new ArrayList<>());
        List<Transaction> transactions = userIncome.get(characterData);

        // Remove expired transactions
        transactions.removeIf(t -> currentTime - t.timestamp > TIME_LIMIT);

        // Calculate total income in the last 24 hours
        int totalIncome = transactions.stream().mapToInt(t -> t.amount).sum();

        // Check if adding the new income exceeds the limit
        if (totalIncome + amount > INCOME_LIMIT) {
        	int remainingAllowance = INCOME_LIMIT - totalIncome;
        	if (remainingAllowance > 0) {
        		Misc.sm(remainingAllowance + " has been deposited. You have reached the daily limit", event);
        		 characterData.currency += remainingAllowance;
        		 return;
        	}else 
            Misc.sm("This interaction has failed because you would exceed the daily limit, please try again later", event);
            return;// Reject the transaction
        }
        transactions.add(new Transaction(amount, currentTime));
        Misc.sm("Your transaction has been processed", event);
        characterData.currency += amount;
        return; // Accept the transaction
    }
}


