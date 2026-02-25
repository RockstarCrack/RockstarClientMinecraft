/*    */ package fun.rockstarity.client.modules.other;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.player.EventFinishEat;
/*    */ import fun.rockstarity.api.helpers.game.Server;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.Items;
/*    */ import org.lwjgl.glfw.GLFW;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
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
/*    */ @Info(name = "ItemTimer", desc = "Визуализация задержки на предметы", type = Category.OTHER)
/*    */ public class ItemTimer
/*    */   extends Module
/*    */ {
/* 31 */   private final Select select = (new Select((Bindable)this, "Предметы")).desc("Предметы на которые можно установить визуальную задержку");
/*    */   
/* 33 */   private final Select.Element gapple = (new Select.Element(this.select, "Золотое яблоко")).set(true);
/* 34 */   private final Select.Element horus = (new Select.Element(this.select, "Хорус")).set(true);
/* 35 */   private final Select.Element pearl = (new Select.Element(this.select, "Эндер перл")).set(true);
/* 36 */   private final Select.Element charka = new Select.Element(this.select, "Чарка");
/* 37 */   private final Select.Element dezorent = new Select.Element(this.select, "Дезориентация");
/* 38 */   private final Select.Element trapka = new Select.Element(this.select, "Трапка");
/*    */   
/* 40 */   private final Slider gappleCool = (new Slider((Bindable)this, "Кд золотого яблока")).min(1.0F).max(200.0F).inc(1.0F).set(100.0F).hide(() -> Boolean.valueOf(!this.gapple.get()));
/* 41 */   private final Slider horusCool = (new Slider((Bindable)this, "Кд хоруса")).min(1.0F).max(200.0F).inc(1.0F).set(100.0F).hide(() -> Boolean.valueOf(!this.horus.get()));
/* 42 */   private final Slider charkaCool = (new Slider((Bindable)this, "Кд чарки")).min(1.0F).max(200.0F).inc(1.0F).set(200.0F).hide(() -> Boolean.valueOf(!this.charka.get()));
/* 43 */   private final Slider pearlCool = (new Slider((Bindable)this, "Кд эндер перла")).min(1.0F).max(300.0F).inc(1.0F).set(300.0F).hide(() -> Boolean.valueOf(!this.pearl.get()));
/* 44 */   private final Slider trapkaCool = (new Slider((Bindable)this, "Кд трапки")).min(1.0F).max(20000.0F).inc(1.0F).set(20000.0F).hide(() -> Boolean.valueOf(!this.trapka.get()));
/* 45 */   private final Slider dezorentCool = (new Slider((Bindable)this, "Кд дезориентации")).min(1.0F).max(20000.0F).inc(1.0F).set(20000.0F).hide(() -> Boolean.valueOf(!this.dezorent.get()));
/*    */   
/* 47 */   private final CheckBox onlyPvp = (new CheckBox((Bindable)this, "Только в PVP")).desc("Накладывает задержку только в PVP режиме");
/*    */ 
/*    */   
/*    */   @NativeInclude
/*    */   public void onEvent(Event event) {
/* 52 */     Item[] items = { Items.GOLDEN_APPLE, Items.CHORUS_FRUIT, Items.ENCHANTED_GOLDEN_APPLE, Items.ENDER_PEARL, Items.ENDER_EYE, Items.NETHERITE_SCRAP };
/* 53 */     if (event instanceof EventFinishEat) { EventFinishEat e = (EventFinishEat)event; if (e.getEntity() == mc.player && (Server.hasCT() || !this.onlyPvp.get())) {
/* 54 */         double[] cooldowns = { this.gappleCool.get(), this.horusCool.get(), this.charkaCool.get(), this.pearlCool.get(), this.dezorentCool.get(), this.trapkaCool.get() };
/*    */         
/* 56 */         for (int i = 0; i < items.length; ) {
/* 57 */           if (e.getItem() != items[i]) { i++; continue; }
/* 58 */            mc.player.getCooldownTracker().setCooldown(items[i], (int)cooldowns[i]);
/*    */         } 
/*    */       }  }
/*    */ 
/*    */     
/* 63 */     if (event instanceof fun.rockstarity.api.events.list.render.EventRender2D)
/*    */     {
/* 65 */       for (Item item : items) {
/* 66 */         if (item.isCooldowned() && mc.player.getActiveItemStack().getItem() == item) {
/* 67 */           (mc.getGameSettings()).keyBindUseItem.setPressed(false);
/*    */         }
/*    */         
/* 70 */         if ((mc.player.getHeldItemMainhand().getItem() == item || mc.player.getHeldItemOffhand().getItem() == item) && !item.isCooldowned() && GLFW.glfwGetMouseButton(mc.getMainWindow().getHandle(), (mc.getGameSettings()).keyBindUseItem.getDefault().getKeyCode()) == 1 && !(mc.currentScreen instanceof net.minecraft.client.gui.screen.inventory.ContainerScreen)) {
/* 71 */           (mc.getGameSettings()).keyBindUseItem.setPressed((GLFW.glfwGetMouseButton(mc.getMainWindow().getHandle(), (mc.getGameSettings()).keyBindUseItem.getDefault().getKeyCode()) == 1));
/*    */         }
/*    */       } 
/*    */     }
/*    */     
/* 76 */     if (event instanceof fun.rockstarity.api.events.list.game.EventTick) {
/* 77 */       Item[] itemsDon = { Items.ENDER_EYE, Items.NETHERITE_SCRAP };
/*    */       
/* 79 */       for (Item item : itemsDon) {
/* 80 */         if ((mc.player.getHeldItemMainhand().getItem() == item || mc.player.getHeldItemOffhand().getItem() == item) && 
/* 81 */           (mc.getGameSettings()).keyBindUseItem.isPressed() && !mc.player.getCooldownTracker().hasCooldown(item) && (Server.hasCT() || !this.onlyPvp.get()))
/* 82 */           mc.player.getCooldownTracker().setCooldown(item, 400); 
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\ItemTimer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */