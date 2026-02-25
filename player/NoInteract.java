/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import java.util.Arrays;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "NoInteract", desc = "Позволяет не нажимать ПКМ по печкам, дверям и т.д", type = Category.PLAYER)
/*    */ public class NoInteract
/*    */   extends Module
/*    */ {
/* 25 */   private final CheckBox all = new CheckBox((Bindable)this, "Все блоки"); public CheckBox getAll() { return this.all; }
/*    */   
/* 27 */   private final Select utils = (new Select((Bindable)this, "Убирать..")).hide(() -> Boolean.valueOf(this.all.get())).desc("Убрать взаимодействие с объектами из списка"); public Select getUtils() { return this.utils; }
/*    */   
/* 29 */   private final Select.Element chest = (new Select.Element(this.utils, "Сундуки")).set(true); public Select.Element getChest() { return this.chest; }
/* 30 */    private final Select.Element doors = (new Select.Element(this.utils, "Двери")).set(true); public Select.Element getDoors() { return this.doors; }
/* 31 */    private final Select.Element buttons = new Select.Element(this.utils, "Кнопки"); public Select.Element getButtons() { return this.buttons; }
/* 32 */    private final Select.Element craftingTable = (new Select.Element(this.utils, "Верстак")).set(true); public Select.Element getCraftingTable() { return this.craftingTable; }
/* 33 */    private final Select.Element trapDoor = (new Select.Element(this.utils, "Люки")).set(true); public Select.Element getTrapDoor() { return this.trapDoor; }
/* 34 */    private final Select.Element lever = new Select.Element(this.utils, "Рычаг"); public Select.Element getLever() { return this.lever; }
/*    */   
/*    */   public Set<Integer> getBlocks() {
/* 37 */     HashSet<Integer> blocks = new HashSet<>();
/* 38 */     addBlocksForInteractionType(blocks, 0, new Integer[] { Integer.valueOf(147), Integer.valueOf(329), Integer.valueOf(270) });
/* 39 */     addBlocksForInteractionType(blocks, 1, new Integer[] { Integer.valueOf(173), Integer.valueOf(161), Integer.valueOf(485), Integer.valueOf(486), Integer.valueOf(487), Integer.valueOf(488), Integer.valueOf(489), Integer.valueOf(720), Integer.valueOf(721) });
/* 40 */     addBlocksForInteractionType(blocks, 3, new Integer[] { Integer.valueOf(151) });
/* 41 */     addBlocksForInteractionType(blocks, 2, new Integer[] { Integer.valueOf(183), Integer.valueOf(308), Integer.valueOf(309), Integer.valueOf(310), Integer.valueOf(311), Integer.valueOf(312), Integer.valueOf(313), Integer.valueOf(718), Integer.valueOf(719), Integer.valueOf(758) });
/* 42 */     addBlocksForInteractionType(blocks, 4, new Integer[] { Integer.valueOf(222), Integer.valueOf(223), Integer.valueOf(224), Integer.valueOf(225), Integer.valueOf(226), Integer.valueOf(227), Integer.valueOf(712), Integer.valueOf(713), Integer.valueOf(379) });
/* 43 */     addBlocksForInteractionType(blocks, 5, new Integer[] { Integer.valueOf(171) });
/* 44 */     return blocks;
/*    */   }
/*    */   
/*    */   private void addBlocksForInteractionType(Set<Integer> blocks, int interactionType, Integer... blockIds) {
/* 48 */     if (((Select.Element)this.utils.getElements().stream().toList().get(interactionType)).get())
/* 49 */       blocks.addAll(Arrays.asList(blockIds)); 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */   
/*    */   public void onEvent(Event event) {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\NoInteract.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */