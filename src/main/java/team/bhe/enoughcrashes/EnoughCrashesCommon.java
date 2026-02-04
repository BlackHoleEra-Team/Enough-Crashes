package team.bhe.enoughcrashes;

import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnoughCrashesCommon {
    public static final String MOD_ID = "enough-crashes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Random RANDOM = Random.create();
    
    // 崩溃消息池 - 客户端和服务器都可以访问
    public static final String[] CRASH_MESSAGES = {
        "enoughcrashes.crash_message.0",
        "enoughcrashes.crash_message.1",
        "enoughcrashes.crash_message.2",
        "enoughcrashes.crash_message.3",
        "enoughcrashes.crash_message.4",
        "enoughcrashes.crash_message.5",
        "enoughcrashes.crash_message.6",
        "enoughcrashes.crash_message.7",
        "enoughcrashes.crash_message.8",
        "enoughcrashes.crash_message.9",
        "enoughcrashes.crash_message.10",
        "enoughcrashes.crash_message.11",
        "enoughcrashes.crash_message.12",
        "enoughcrashes.crash_message.13",
        "enoughcrashes.crash_message.14",
        "enoughcrashes.crash_message.15",
        "enoughcrashes.crash_message.16",
        "enoughcrashes.crash_message.17",
        "enoughcrashes.crash_message.18",
        "enoughcrashes.crash_message.19",
        "enoughcrashes.crash_message.20",
        "enoughcrashes.crash_message.21",
        "enoughcrashes.crash_message.22",
        "enoughcrashes.crash_message.23",
        "enoughcrashes.crash_message.24",
        "enoughcrashes.crash_message.25",
        "enoughcrashes.crash_message.26",
        "enoughcrashes.crash_message.27",
        "enoughcrashes.crash_message.28",
        "enoughcrashes.crash_message.29",
        "enoughcrashes.crash_message.30",
        "enoughcrashes.crash_message.31",
        "enoughcrashes.crash_message.32",
        "enoughcrashes.crash_message.33",
        "enoughcrashes.crash_message.34",
        "enoughcrashes.crash_message.35",
        "enoughcrashes.crash_message.36",
        "enoughcrashes.crash_message.37",
        "enoughcrashes.crash_message.38",
        "enoughcrashes.crash_message.39",
        "enoughcrashes.crash_message.40",
        "enoughcrashes.crash_message.41",
        "enoughcrashes.crash_message.42",
        "enoughcrashes.crash_message.43",
        "enoughcrashes.crash_message.44",
        "enoughcrashes.crash_message.45",
        "enoughcrashes.crash_message.46",
        "enoughcrashes.crash_message.47",
        "enoughcrashes.crash_message.48",
        "enoughcrashes.crash_message.49"
    };
    
    public static String getRandomCrashMessage() {
        return CRASH_MESSAGES[RANDOM.nextInt(CRASH_MESSAGES.length)];
    }
}