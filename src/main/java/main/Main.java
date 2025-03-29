package main;


import java.net.InetSocketAddress;
import java.net.Socket;

import javax.imageio.spi.IIORegistry;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main extends ListenerAdapter {
	 static final String token = System.getenv("DISCORD_BOT_TOKEN");;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    static JDA jda;
    static boolean reconnecting = false;

    public static void main(String[] args) throws LoginException {
       
       
        if (token == null || token.isEmpty()) {
            logger.error("Token is not set. Please set the DISCORD_BOT_TOKEN environment variable.");
            return;
        }
        IIORegistry.getDefaultInstance().registerServiceProvider(new WebPImageReaderSpi());

        
        logger.info("Token successfully retrieved");
      startBot();

  	MessageListener.loadUserData();

    }
    @Override
  
    	public void onStatusChange(StatusChangeEvent event) {
    	    if (event.getNewStatus() == Status.DISCONNECTED) {
    	        if (!reconnecting) {
    	            reconnecting = true;  // Prevent multiple loops
    	            new Thread(() -> {
    	                while (true) {
    	                    if (isInternetAvailable()) {
    	                        try {
    	                            startBot();
    	                            break;  // Exit the loop once the bot restarts
    	                        } catch (Exception e) {
    	                            System.err.println("Bot crashed! Restarting in 10 seconds...");
    	                            try {
										Thread.sleep(10000);
									} catch (InterruptedException e1) {
										
										e1.printStackTrace();
									}
    	                        }
    	                    }
    	                    try {
    	                    	System.err.println("Wifi not available, trying again in 10 seconds");
    	                        Thread.sleep(10000);
    	                    } catch (InterruptedException ignored) {}
    	                }
    	                reconnecting = false;  // Allow future reconnects
    	            }).start();
    	        }
    	    }
    	}
    public static boolean isInternetAvailable() {
      
       try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("8.8.8.8", 53), 3000);
                return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void startBot() {
    	if (jda != null) jda.shutdownNow();
       
		jda = JDABuilder.createDefault(token)
                .addEventListeners(new MessageListener())
                .addEventListeners(new ButtonListener())
    			.addEventListeners( new Main())
                .setChunkingFilter(ChunkingFilter.ALL) 
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setAutoReconnect(false)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();
    
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		    MessageListener.saveUserData();
		    try {
		        Thread.sleep(500); // Wait to ensure file writes complete
		    } catch (InterruptedException ignored) {}
		}));
    }
}

 