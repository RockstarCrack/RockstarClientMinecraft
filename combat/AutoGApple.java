/*     */ package fun.rockstarity.client.modules.combat;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.helpers.math.InventoryUtility;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.potion.Effects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoGApple", desc = "Автоматически ест геплы", type = Category.COMBAT)
/*     */ public class AutoGApple
/*     */   extends Module
/*     */ {
/*  29 */   private final Slider health = (new Slider((Bindable)this, "Здоровье")).min(1.0F).max(20.0F).inc(0.5F).set(15.0F).desc("Здоровье при котором будет кушаться гепл");
/*  30 */   private final CheckBox eatBegining = new CheckBox((Bindable)this, "Есть в начале");
/*  31 */   private final TimerUtility waitTimer = new TimerUtility();
/*     */   
/*     */   private boolean isEating;
/*     */   
/*     */   public void onEvent(Event event) {
/*  36 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*  37 */       if (shouldToTakeGApple() && this.eatBegining.get()) {
/*  38 */         takeGappleInOffHand();
/*     */       }
/*     */       
/*  41 */       eatGapple();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void eatGapple() {
/*  46 */     if (conditionToEat()) {
/*  47 */       startEating();
/*  48 */     } else if (this.isEating) {
/*  49 */       stopEating();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean shouldToTakeGApple() {
/*  54 */     boolean isTicksExisted = (mc.player.ticksExisted == 15);
/*  55 */     boolean appleNotEaten = (mc.player.getAbsorptionAmount() == 0.0F || !mc.player.isPotionActive(Effects.REGENERATION));
/*  56 */     boolean appleIsNotOffHand = (mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE);
/*  57 */     boolean timeHasPassed = this.waitTimer.passed(200L);
/*  58 */     boolean settingIsEnalbed = this.eatBegining.get();
/*     */     
/*  60 */     return (isTicksExisted && appleNotEaten && appleIsNotOffHand & timeHasPassed && settingIsEnalbed);
/*     */   }
/*     */   
/*     */   private void takeGappleInOffHand() {
/*  64 */     InventoryUtility.getInstance(); int gappleSlot = InventoryUtility.getSlotInInventory(Items.GOLDEN_APPLE);
/*     */     
/*  66 */     if (gappleSlot >= 0) {
/*  67 */       moveGappleToOffhand(gappleSlot);
/*     */     }
/*     */   }
/*     */   
/*     */   private void moveGappleToOffhand(int gappleSlot) {
/*  72 */     if (gappleSlot < 9 && gappleSlot != -1) {
/*  73 */       gappleSlot += 36;
/*     */     }
/*  75 */     mc.playerController.windowClick(0, gappleSlot, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/*  76 */     mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/*  77 */     if (!(mc.player.getHeldItemOffhand().getItem() instanceof net.minecraft.item.AirItem)) {
/*  78 */       mc.playerController.windowClick(0, gappleSlot, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/*     */     }
/*  80 */     this.waitTimer.reset();
/*     */   }
/*     */   
/*     */   private void startEating() {
/*  84 */     if (mc.currentScreen != null) {
/*  85 */       mc.currentScreen.passEvents = true;
/*     */     }
/*  87 */     if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
/*  88 */       mc.gameSettings.keyBindUseItem.setPressed(true);
/*  89 */       this.isEating = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void stopEating() {
/*  94 */     mc.gameSettings.keyBindUseItem.setPressed(false);
/*  95 */     this.isEating = false;
/*     */   }
/*     */   
/*     */   private boolean conditionToEat() {
/*  99 */     float myHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
/*     */     
/* 101 */     boolean appleNotEaten = (mc.player.getAbsorptionAmount() == 0.0F || !mc.player.isPotionActive(Effects.REGENERATION));
/*     */     
/* 103 */     return ((isHealthLow(myHealth) || (mc.player.ticksExisted < 100 && appleNotEaten)) && 
/* 104 */       hasGappleInHand() && 
/* 105 */       !isGappleOnCooldown());
/*     */   }
/*     */   
/*     */   private boolean isGappleOnCooldown() {
/* 109 */     return mc.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE);
/*     */   }
/*     */   
/*     */   private boolean isHealthLow(float health) {
/* 113 */     return (health <= this.health.get());
/*     */   }
/*     */   
/*     */   private boolean hasGappleInHand() {
/* 117 */     return (mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE || mc.player
/* 118 */       .getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
/*     */   }
/*     */   
/*     */   private void reset() {
/* 122 */     this.waitTimer.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 127 */     reset();
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\AutoGApple.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */