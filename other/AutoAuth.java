/*    */ package fun.rockstarity.client.modules.other;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*    */ import fun.rockstarity.api.helpers.system.TextUtility;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.api.modules.settings.list.Input;
/*    */ import java.util.Objects;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.server.SChatPacket;
/*    */ 
/*    */ @Info(name = "AutoAuth", desc = "Автоматически входит на сервер", type = Category.OTHER)
/*    */ public class AutoAuth extends Module {
/*    */   private final CheckBox random;
/*    */   private final Input passwords;
/*    */   
/*    */   public AutoAuth() {
/* 22 */     this.random = (new CheckBox((Bindable)this, "Рандом пароль")).desc("Случайный пароль для аккаунта на сервере");
/* 23 */     Objects.requireNonNull(this.random); this.passwords = (new Input((Bindable)this, "Пароль..")).hide(this.random::get).desc("Введите пароль, который будет использоваться для автоматического входа");
/*    */   }
/*    */   
/*    */   public void onEvent(Event event) {
/* 27 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/* 28 */         String message = packet.getChatComponent().getString().toLowerCase();
/* 29 */         String password = this.random.get() ? TextUtility.getRandomNick() : this.passwords.get();
/*    */         
/* 31 */         if (message.contains("Зарегистрируйтесь") || message.contains("/reg")) {
/* 32 */           mc.player.sendChatMessage(String.format("/reg %s %s", new Object[] { password, password }));
/* 33 */         } else if (message.contains("Авторизуйтесь") || message.contains("/login") || (message.contains("/l") && message.matches("/l(\\s|$)"))) {
/* 34 */           mc.player.sendChatMessage(String.format("/l %s", new Object[] { password }));
/*    */         }  }
/*    */        }
/*    */   
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoAuth.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */