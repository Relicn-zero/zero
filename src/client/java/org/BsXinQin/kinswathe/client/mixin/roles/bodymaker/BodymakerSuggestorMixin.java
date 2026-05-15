package org.BsXinQin.kinswathe.client.mixin.roles.bodymaker;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import dev.doctor4t.wathe.api.WatheRoles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerRoleWidget;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatInputSuggestor.class)
public abstract class BodymakerSuggestorMixin {

    @Shadow @Final TextFieldWidget textField;
    @Shadow public abstract void show(boolean narrateFirstSuggestion);
    @Shadow @Nullable private ChatInputSuggestor.SuggestionWindow window;
    @Shadow @Final private List<OrderedText> messages;
    @Shadow private int x;
    @Shadow private int width;
    @Shadow private boolean windowActive;
    @Shadow @Final MinecraftClient client;

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    void refresh(@NotNull CallbackInfo ci) {
        if (textField instanceof @NotNull BodymakerRoleWidget) {
            messages.clear();
            WatheRoles.ROLES.forEach((role) -> {
                if (HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())) return;
                if (Harpymodloader.SPECIAL_ROLES.contains(role)) return;
                if (role != WatheRoles.KILLER && role.identifier().getPath().equals("killer")) return;
                if (role.identifier().getPath().startsWith(textField.getText()) || textField.getText().isEmpty()) {
                    MutableText s = Text.literal(role.identifier().getPath());
                    if (!MinecraftClient.getInstance().getLanguageManager().getLanguage().startsWith("en_")) {
                        s.append(Text.literal(" (").append((Harpymodloader.getRoleName(role)).append(")")));
                    }
                    messages.add(s.withColor(role.color()).asOrderedText());
                }
            });
            x = textField.getX();
            width = this.textField.getWidth();
            window = null;
            if (windowActive && client.options.getAutoSuggestions().getValue()) {
                show(false);
            }
            ci.cancel();
        }
    }
    @Inject(method = "show", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;sortSuggestions(Lcom/mojang/brigadier/suggestion/Suggestions;)Ljava/util/List;"))
    void doNotCloseInventory(boolean narrateFirstSuggestion, CallbackInfo ci, @NotNull @Local(ordinal = 2) LocalIntRef intRef) {
        if (textField instanceof @NotNull BodymakerRoleWidget) {
            intRef.set(textField.getY() + textField.getHeight());
        }
    }
    @Inject(method = "renderMessages", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    void doNotCloseInventory(DrawContext context, CallbackInfo ci, @NotNull @Local(ordinal = 1) LocalIntRef intRef, @Local(ordinal = 0) int i) {
        if (textField instanceof @NotNull BodymakerRoleWidget) {
            intRef.set(textField.getY() + textField.getHeight() + 12 * i);
        }
    }
}
