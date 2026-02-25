/*    */ package fun.rockstarity.client.modules.other;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.connection.globals.ClientAPI;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.server.SChatPacket;
/*    */ import net.minecraft.util.text.TextFormatting;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "AutoAccept", desc = "Автоматически принимает дуэли или тп", type = Category.OTHER)
/*    */ public class AutoAccept
/*    */   extends Module
/*    */ {
/*    */   private boolean duel;
/*    */   private String name;
/* 25 */   private final Select utils = new Select((Bindable)this, "Выбор");
/* 26 */   private final Select.Element duelsAccept = new Select.Element(this.utils, "Дуэли");
/* 27 */   private final Select.Element tpaAccept = (new Select.Element(this.utils, "Телепорт")).set(true);
/*    */   
/* 29 */   private final Select kits = (new Select((Bindable)this, "Киты")).hide(() -> Boolean.valueOf(!this.duelsAccept.get()));
/* 30 */   private final Select.Element classicKits = new Select.Element(this.kits, "Классик");
/* 31 */   private final Select.Element totemsKits = (new Select.Element(this.kits, "Тотемы")).set(true);
/* 32 */   private final Select.Element noDeBuffKits = new Select.Element(this.kits, "НоДебафф");
/* 33 */   private final Select.Element cheatsParadiseKits = new Select.Element(this.kits, "Читерский рай");
/* 34 */   private final Select.Element nezerKits = new Select.Element(this.kits, "Незеритка");
/* 35 */   private final Select.Element shipiKits = new Select.Element(this.kits, "Шипы");
/* 36 */   private final Select.Element shieldKits = new Select.Element(this.kits, "Щит");
/* 37 */   private final Select.Element bowKits = new Select.Element(this.kits, "Лук");
/* 38 */   private final Select.Element sharKits = new Select.Element(this.kits, "Шары");
/*    */   
/* 40 */   private final Select accept = new Select((Bindable)this, "Принимать");
/* 41 */   private final Select.Element all = new Select.Element(this.accept, "Всех");
/* 42 */   private final Select.Element friends = (new Select.Element(this.accept, "Друзей")).hide(() -> Boolean.valueOf(this.all.get()));
/* 43 */   private final Select.Element users = (new Select.Element(this.accept, "Пользователей Rockstar")).hide(() -> Boolean.valueOf((this.all.get() || !((Globals)rock.getModules().get(Globals.class)).get())));
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 47 */     if (event instanceof EventReceivePacket) { EventReceivePacket receive = (EventReceivePacket)event; IPacket iPacket = receive.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/* 48 */         String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());
/* 49 */         if (message.contains("Набор: ") && this.duel && this.duelsAccept.get()) {
/* 50 */           this.duel = false;
/*    */ 
/*    */           
/* 53 */           boolean accept = (this.classicKits.isEnabled() || this.totemsKits.isEnabled() || this.noDeBuffKits.isEnabled() || this.cheatsParadiseKits.isEnabled() || this.shipiKits.isEnabled() || this.shieldKits.isEnabled() || this.bowKits.isEnabled() || this.nezerKits.isEnabled() || this.sharKits.isEnabled());
/* 54 */           mc.player.sendChatMessage("/duel " + (accept ? "accept" : "deny") + " " + this.name);
/*    */         } 
/* 56 */         if (message.contains("телепортироваться") && this.tpaAccept.get() && 
/* 57 */           canAccept(message)) {
/* 58 */           mc.player.sendChatMessage("/tpaccept");
/*    */         }
/*    */         
/* 61 */         if (message.contains("Ник: ") && this.duelsAccept.get() && 
/* 62 */           canAccept(message)) {
/* 63 */           this.duel = true;
/* 64 */           this.name = message.replace("➝ Ник: ", "");
/*    */         }  }
/*    */        }
/*    */   
/*    */   }
/*    */   
/*    */   private boolean canAccept(String message) {
/* 71 */     if (this.all.get()) return true;
/*    */     
/* 73 */     if (this.friends.get()) {
/* 74 */       if (rock.getFriendsHandler().isFriend(message.split(" ")[1]) || rock
/* 75 */         .getFriendsHandler().isFriend(message.replace("੷ просит телепортироваться к Вам.੷§l [ੲ§l✔੷§l]੷§l [੼§l✗੷§l]", "").replace("੶", "")) || rock
/* 76 */         .getFriendsHandler().isFriend(message.replace("➝ Ник: ", ""))) {
/* 77 */         return true;
/*    */       }
/* 79 */       if (message.contains("телепортироваться")) {
/* 80 */         String[] parts = message.split(" ");
/* 81 */         if (parts.length >= 2 && rock.getFriendsHandler().isFriend(parts[2])) {
/* 82 */           return true;
/*    */         }
/*    */       } 
/*    */     } 
/* 86 */     if (this.users.get() && (
/* 87 */       ClientAPI.getClient(message.split(" ")[1]) != null || 
/* 88 */       ClientAPI.getClient(message.replace("੷ просит телепортироваться к Вам.੷§l [ੲ§l✔੷§l]੷§l [੼§l✗੷§l]", "").replace("੶", "")) != null || 
/* 89 */       ClientAPI.getClient(message.replace("➝ Ник: ", "")) != null)) {
/* 90 */       return true;
/*    */     }
/* 92 */     return false;
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoAccept.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */