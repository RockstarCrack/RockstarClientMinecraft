/*    */ package fun.rockstarity.client.modules.other;
/*    */ 
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.game.Chat;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import net.minecraft.util.text.TextFormatting;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "DeathCoordinates", desc = "Отпраяляет в чат место смерти", type = Category.OTHER)
/*    */ public class DeathCoordinates
/*    */   extends Module
/*    */ {
/*    */   public void onEvent(Event event) {
/* 30 */     if (event instanceof fun.rockstarity.api.events.list.player.EventDeath) {
/*    */       
/* 32 */       int xCord = mc.player.getPosition().getX(), yCord = mc.player.getPosition().getY(), zCord = mc.player.getPosition().getZ();
/*    */       
/* 34 */       Chat.msg(String.valueOf(TextFormatting.AQUA) + "[Rockstar] " + String.valueOf(TextFormatting.AQUA) + "Координаты смерти: " + String.valueOf(TextFormatting.RESET) + ", " + xCord + ", " + yCord, "Создать метку", () -> rock.getCommands().execute("way add dead " + xCord + " " + yCord + " " + zCord));
/*    */     } 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\DeathCoordinates.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */