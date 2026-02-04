package team.bhe.enoughcrashes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import team.bhe.enoughcrashes.config.ModConfig;

import com.mojang.brigadier.arguments.BoolArgumentType;

public class Enoughcrashes implements ModInitializer {

    @Override
    public void onInitialize() {
        // 初始化配置
        ModConfig.init();
        
        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("enoughcrashes")
                .then(CommandManager.literal("reload")
                    .executes(context -> {
                        ModConfig.loadConfig();
                        context.getSource().sendFeedback(() -> Text.translatable("command.enoughcrashes.reload.success"), false);
                        return 1;
                    })
                )
                .then(CommandManager.literal("debug")
                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            ModConfig.setDebug(enabled);
                            context.getSource().sendFeedback(() -> Text.translatable("command.enoughcrashes.debug.success", enabled), false);
                            return 1;
                        })
                    )
                )
                .then(CommandManager.literal("level")
                    .then(CommandManager.argument("levelName", StringArgumentType.string())
                        .suggests((context, builder) -> CommandSource.suggestMatching(ModConfig.getLevelNames(), builder))
                        .executes(context -> {
                            String levelName = StringArgumentType.getString(context, "levelName");
                            if (ModConfig.setLevel(levelName)) {
                                context.getSource().sendFeedback(() -> Text.translatable("command.enoughcrashes.level.success", levelName), false);
                                return 1;
                            } else {
                                context.getSource().sendError(Text.translatable("command.enoughcrashes.level.not_found", levelName));
                                return 0;
                            }
                        })
                    )
                )
            );
        });
    }
}
