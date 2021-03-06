package hello;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class Application {
    @Bean
    JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory();
    }
    
    @Bean
    RedisMessageListenerContainer container(final JedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer() {{
            setConnectionFactory(connectionFactory);
        }};
        container.addMessageListener(listenerAdapter(), new PatternTopic("chat"));
        return container;
    }
    
    @Bean
    MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(new Receiver(), "receiveMessage");
    }
    
    @Bean
    StringRedisTemplate template(JedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
    
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Application.class);
        StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
        System.out.println("Sending message...");
        template.convertAndSend("chat", "Hello from Redis!");
        ctx.close();
    }
}
