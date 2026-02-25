/*    */ package fun.rockstarity.client.modules.render;
/*    */ 
/*    */ import fun.rockstarity.api.constuctor.ConstructorScreen;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import net.minecraft.client.gui.screen.Screen;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "Constructor", desc = "Позволяет редактировать скрипты", type = Category.RENDER)
/*    */ public class Constructor
/*    */   extends Module
/*    */ {
/*    */   public void onAllEvent(Event event) {
/* 22 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && rock.getConstructor() == null) {
/* 23 */       rock.setConstructor(new ConstructorScreen());
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {}
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 33 */     mc.displayGuiScreen((Screen)rock.getScriptConstructor().getScreen());
/* 34 */     ConstructorScreen.opening.setForward(true);
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\Constructor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */