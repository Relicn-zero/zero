package org.BsXinQin.kinswathe.client.roles.bodymaker;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.packet.roles.BodymakerC2SPacket;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class BodymakerRoleWidget extends TextFieldWidget {
    public final LimitedInventoryScreen screen;
    public final ChatInputSuggestor suggestor;
    public static boolean stopClosing = false;
    private final UUID targetPlayerUuid;
    private final String deathReason;

    public BodymakerRoleWidget(@NotNull LimitedInventoryScreen screen, @NotNull TextRenderer textRenderer, int x, int y, @NotNull UUID targetPlayerUuid, @NotNull String deathReason) {
        super(textRenderer, x, y, 200, 16, Text.literal(""));
        this.screen = screen;
        this.targetPlayerUuid = targetPlayerUuid;
        this.deathReason = deathReason;
        this.suggestor = new ChatInputSuggestor(MinecraftClient.getInstance(), screen, this, textRenderer, true, true, -1, 10, false, Color.TRANSLUCENT);
        this.suggestor.refresh();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        boolean original = super.charTyped(chr, modifiers);
        suggestor.refresh();
        return original;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 258) {
            if (!getText().isEmpty()) {
                String roleName = getText();
                var matchingRoles = WatheRoles.ROLES.stream().filter(role -> !HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())).filter(role -> !Harpymodloader.SPECIAL_ROLES.contains(role)).filter(role -> !(role != WatheRoles.KILLER && role.identifier().getPath().equals("killer"))).filter(role -> role.identifier().getPath().startsWith(roleName)).map(role -> role.identifier().getPath()).toList();
                if (!matchingRoles.isEmpty()) {
                    setText(matchingRoles.getFirst());
                    this.setCursorToEnd(false);
                    suggestor.refresh();
                    return true;
                }
            }
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            if (!getText().isEmpty()) {
                String roleName = getText();
                var matchedRole = WatheRoles.ROLES.stream().filter(role -> !HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())).filter(role -> !Harpymodloader.SPECIAL_ROLES.contains(role)).filter(role -> !(role != WatheRoles.KILLER && role.identifier().getPath().equals("killer"))).filter(role -> role.identifier().getPath().equalsIgnoreCase(roleName)).findFirst();
                if (matchedRole.isPresent()) {
                    ClientPlayNetworking.send(new BodymakerC2SPacket(targetPlayerUuid, deathReason, matchedRole.get().identifier().toString()));
                } else {
                    ClientPlayNetworking.send(new BodymakerC2SPacket(targetPlayerUuid, deathReason, roleName));
                }
                screen.close();
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void eraseCharacters(int characterOffset) {
        super.eraseCharacters(characterOffset);
        suggestor.refresh();
    }

    @Override
    public void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        stopClosing = isSelected();
        ArrayList<String> suggestions = new ArrayList<>();
        WatheRoles.ROLES.forEach((role) -> {
            if (HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())) return;
            if (Harpymodloader.SPECIAL_ROLES.contains(role)) return;
            if (role != WatheRoles.KILLER && role.identifier().getPath().equals("killer")) return;
            String rolePath = role.identifier().getPath();
            if (rolePath.startsWith(getText()) || getText().isEmpty()) {
                suggestions.add(rolePath);
            }
        });
        if (!suggestions.isEmpty()) {
            setSuggestion(suggestions.getFirst().substring(getText().length()));
        } else {
            setSuggestion("");
        }
        super.renderWidget(context, mouseX, mouseY, delta);
        suggestor.render(context, mouseX, mouseY);
    }
}