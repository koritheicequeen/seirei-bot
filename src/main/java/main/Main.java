package main;


import javax.imageio.spi.IIORegistry;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends ListenerAdapter {
	 static final String token = "";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws LoginException {
       
        System.out.println(token);
       
        if (token == null || token.isEmpty()) {
            logger.error("Token is not set. Please set the DISCORD_BOT_TOKEN environment variable.");
            return;
        }
        IIORegistry.getDefaultInstance().registerServiceProvider(new WebPImageReaderSpi());

        
        logger.info("Token successfully retrieved");
      startBot();

    }
    @Override
    public void onStatusChange(StatusChangeEvent event) {
        if (event.getNewStatus() == Status.DISCONNECTED) {
        	  while (true) {
              	try {
              		 startBot(); 
              		 return;
              	} catch (Exception e) {
              		
              		System.err.println("Bot crashed! Restarting in 10 seconds...");
              		try {
              			Thread.sleep(10000);}
              		catch (InterruptedException ignored) {}
              		
              	}
              }
        }
    }
    public static void startBot() {
    	@SuppressWarnings("unused")
		JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(new MessageListener())
                .addEventListeners(new ButtonListener())
    			.addEventListeners( new Main())
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setAutoReconnect(false)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();
    
    
    }
}

 