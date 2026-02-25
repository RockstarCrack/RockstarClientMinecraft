/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.helpers.game.Chat;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.PremiumModule;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.inventory.container.Slot;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.potion.Potion;
/*     */ import net.minecraft.potion.PotionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoBrew", desc = "Автоматически варит зелья", type = Category.OTHER)
/*     */ public class AutoBrew
/*     */   extends PremiumModule
/*     */ {
/*  28 */   private final Mode mode = new Mode((Bindable)this, "Варить...");
/*     */   
/*  30 */   private final Mode.Element speed = new Mode.Element(this.mode, "Зелье скорости");
/*  31 */   private final Mode.Element strength = new Mode.Element(this.mode, "Зелье силы");
/*  32 */   private final Mode.Element fire = new Mode.Element(this.mode, "Зелье огнестойкости");
/*     */   
/*  34 */   private final Slider delay = (new Slider((Bindable)this, "Задержка")).min(100.0F).max(1000.0F).inc(10.0F).set(100.0F);
/*  35 */   private final CheckBox loot = (new CheckBox((Bindable)this, "Забирать в инвентарь")).set(true);
/*  36 */   private final CheckBox addGunpowder = new CheckBox((Bindable)this, "Добавлять порох");
/*     */   
/*  38 */   private final TimerUtility timer = new TimerUtility();
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  42 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && 
/*  43 */       mc.player.openContainer instanceof net.minecraft.inventory.container.BrewingStandContainer) {
/*  44 */       if (isFuelEmpty()) {
/*  45 */         if (findItemInBrewingStand(Items.BLAZE_POWDER) == -1) {
/*  46 */           Chat.msg("Нет огненного порошка для заправки!");
/*  47 */           toggle();
/*     */         } 
/*  49 */         swapOneItem(Items.BLAZE_POWDER, 4);
/*     */       } 
/*     */       
/*  52 */       for (int i = 0; i < 3; i++) {
/*  53 */         if (getItem(i) instanceof net.minecraft.item.AirItem) {
/*  54 */           int waterPotionSlot = findPotionInBrewingStand("water");
/*  55 */           if (waterPotionSlot == -1) {
/*  56 */             toggle();
/*  57 */             Chat.msg("Нет бутылок с водой!");
/*     */           } 
/*  59 */           if (this.timer.passed((long)(this.delay.get() * 3.0F))) {
/*  60 */             swapOneItem(waterPotionSlot, i);
/*  61 */             this.timer.reset();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/*  66 */       if (getItem(3) instanceof net.minecraft.item.AirItem) {
/*  67 */         if (identicalPotionsCheck("water")) {
/*  68 */           if (findItemInBrewingStand(Items.NETHER_WART) == -1) {
/*  69 */             toggle();
/*  70 */             Chat.msg("Нет нароста!");
/*     */           } 
/*  72 */           swapOneItem(Items.NETHER_WART, 3);
/*     */         } 
/*     */         
/*  75 */         if (this.mode.is(this.speed)) {
/*  76 */           if (identicalPotionsCheck("awkward")) {
/*  77 */             if (findItemInBrewingStand(Items.SUGAR) == -1) {
/*  78 */               Chat.msg("Нет сахара для зелья скорости!");
/*  79 */               toggle();
/*     */             } 
/*  81 */             swapOneItem(Items.SUGAR, 3);
/*     */           } 
/*  83 */         } else if (this.mode.is(this.strength)) {
/*  84 */           if (identicalPotionsCheck("awkward")) {
/*  85 */             if (findItemInBrewingStand(Items.BLAZE_POWDER) == -1) {
/*  86 */               Chat.msg("Нет огненного порошка для зелья силы!");
/*  87 */               toggle();
/*     */             } 
/*  89 */             swapOneItem(Items.BLAZE_POWDER, 3);
/*     */           } 
/*  91 */         } else if (this.mode.is(this.fire) && 
/*  92 */           identicalPotionsCheck("awkward")) {
/*  93 */           if (findItemInBrewingStand(Items.MAGMA_CREAM) == -1) {
/*  94 */             Chat.msg("Нет слизи магмы для зелья огнестойкости!");
/*  95 */             toggle();
/*     */           } 
/*  97 */           swapOneItem(Items.MAGMA_CREAM, 3);
/*     */         } 
/*     */ 
/*     */         
/* 101 */         if (identicalPotionsCheck("strength") || identicalPotionsCheck("swiftness")) {
/* 102 */           if (findItemInBrewingStand(Items.GLOWSTONE_DUST) == -1) {
/* 103 */             Chat.msg("Нет светокамня для усиления зелья!");
/* 104 */             toggle();
/*     */           } 
/* 106 */           swapOneItem(Items.GLOWSTONE_DUST, 3);
/*     */         } 
/*     */         
/* 109 */         if (identicalPotionsCheck("fire_resistance")) {
/* 110 */           if (findItemInBrewingStand(Items.REDSTONE) == -1) {
/* 111 */             Chat.msg("Нет редстоуна для увеличения длительности зелья!");
/* 112 */             toggle();
/*     */           } 
/* 114 */           swapOneItem(Items.REDSTONE, 3);
/*     */         } 
/*     */         
/* 117 */         if (identicalPotionsCheck("strong_strength") || identicalPotionsCheck("strong_swiftness") || identicalPotionsCheck("long_fire_resistance")) {
/* 118 */           if (this.addGunpowder.get()) {
/* 119 */             if (findItemInBrewingStand(Items.GUNPOWDER) == -1) {
/* 120 */               Chat.msg("Нет пороха для создания взрывных зелий!");
/* 121 */               toggle();
/*     */             } 
/* 123 */             if (identicalSplashPotionsCheck()) {
/* 124 */               if (this.loot.get()) {
/* 125 */                 loot();
/*     */               }
/*     */             } else {
/* 128 */               swapOneItem(Items.GUNPOWDER, 3);
/*     */             }
/*     */           
/* 131 */           } else if (this.loot.get()) {
/* 132 */             loot();
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void loot() {
/* 142 */     for (int i = 0; i < 3; i++) {
/* 143 */       if (this.timer.passed((long)(this.delay.get() * 2.0F))) {
/* 144 */         mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.QUICK_MOVE, (PlayerEntity)mc.player);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean identicalSplashPotionsCheck() {
/* 150 */     boolean needIng = true;
/* 151 */     for (int i = 0; i < 3; i++) {
/* 152 */       if (((Slot)mc.player.openContainer.inventorySlots.get(i)).getStack().getItem() != Items.SPLASH_POTION) {
/* 153 */         needIng = false;
/*     */       }
/*     */     } 
/* 156 */     return needIng;
/*     */   }
/*     */   
/*     */   private boolean identicalPotionsCheck(String potionType) {
/* 160 */     boolean needIng = true;
/* 161 */     for (int i = 0; i < 3; i++) {
/* 162 */       if (PotionUtils.getPotionFromItem(((Slot)mc.player.openContainer.inventorySlots.get(i)).getStack()) != Potion.getPotionTypeForName(potionType)) {
/* 163 */         needIng = false;
/*     */       }
/*     */     } 
/* 166 */     return needIng;
/*     */   }
/*     */   
/*     */   public void swapOneItem(Item item, int to) {
/* 170 */     if (this.timer.passed((long)(this.delay.get() * 2.0F))) {
/*     */       int slot;
/* 172 */       if ((slot = findItemInBrewingStand(item)) != -1) {
/* 173 */         swapOneItem(slot, to);
/* 174 */         this.timer.reset();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void swapOneItem(int from, int to) {
/* 180 */     mc.playerController.windowClick(mc.player.openContainer.windowId, from, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/* 181 */     mc.playerController.windowClick(mc.player.openContainer.windowId, to, 1, ClickType.PICKUP, (PlayerEntity)mc.player);
/* 182 */     mc.playerController.windowClick(mc.player.openContainer.windowId, from, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/*     */   }
/*     */   
/*     */   private Item getItem(int slotId) {
/* 186 */     return ((Slot)mc.player.openContainer.inventorySlots.get(slotId)).getStack().getItem();
/*     */   }
/*     */   
/*     */   private boolean isFuelEmpty() {
/* 190 */     return getItem(4) instanceof net.minecraft.item.AirItem;
/*     */   }
/*     */   
/*     */   private int findPotionInBrewingStand(String potionType) {
/* 194 */     for (int i = 5; i < 41; i++) {
/* 195 */       if (PotionUtils.getPotionFromItem(((Slot)mc.player.openContainer.inventorySlots.get(i)).getStack()) == Potion.getPotionTypeForName(potionType)) return i; 
/*     */     } 
/* 197 */     return -1;
/*     */   }
/*     */   
/*     */   private int findItemInBrewingStand(Item item) {
/* 201 */     for (int i = 5; i < 41; i++) {
/* 202 */       if (getItem(i) == item) return i; 
/*     */     } 
/* 204 */     return -1;
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoBrew.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */