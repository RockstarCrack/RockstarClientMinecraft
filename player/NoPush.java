/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "NoPush", desc = "Не дает игроку оттолкнуться", type = Category.PLAYER)
/*    */ public class NoPush
/*    */   extends Module
/*    */ {
/* 20 */   private final Select utils = (new Select((Bindable)this, "Убирать...")).desc("Убрать столкновение с объектами из списка"); public Select getUtils() { return this.utils; }
/* 21 */    private final Select.Element players = (new Select.Element(this.utils, "Игроки")).set(true); public Select.Element getPlayers() { return this.players; }
/* 22 */    private final Select.Element water = (new Select.Element(this.utils, "Вода")).set(true); public Select.Element getWater() { return this.water; }
/* 23 */    private final Select.Element blocs = (new Select.Element(this.utils, "Блоки")).set(true); public Select.Element getBlocs() { return this.blocs; }
/*    */ 
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */   
/*    */   public void onEvent(Event event) {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\NoPush.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */