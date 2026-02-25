/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.helpers.math.InventoryUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.potion.EffectInstance;
/*     */ import net.minecraft.potion.Effects;
/*     */ import net.minecraft.potion.PotionUtils;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoInvisible", desc = "Автоматически пьет зелье невидимости", type = Category.OTHER)
/*     */ public class AutoInvisible
/*     */   extends Module
/*     */ {
/*     */   private boolean isUsingPotion;
/*     */   private boolean hasThrownBottle;
/*  32 */   private final Map<String, EffectInstance> effects = new TreeMap<>(); public Map<String, EffectInstance> getEffects() { return this.effects; }
/*     */ 
/*     */   
/*  35 */   public boolean isUsingPotion() { return this.isUsingPotion; } public boolean isHasThrownBottle() {
/*  36 */     return this.hasThrownBottle;
/*     */   }
/*     */   
/*     */   public void onEvent(Event event) {
/*  40 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*  41 */       boolean hasInvisibility = mc.player.isPotionActive(Effects.INVISIBILITY);
/*     */       
/*  43 */       if (!hasInvisibility) {
/*  44 */         ItemStack offhandItem = mc.player.getHeldItemOffhand();
/*  45 */         boolean hasPotionInOffhand = isInvisibilityPotion(offhandItem);
/*     */         
/*  47 */         if (!hasPotionInOffhand) {
/*  48 */           int potionSlot = findPotion();
/*  49 */           if (potionSlot != -1) {
/*  50 */             InventoryUtility.moveItem(potionSlot, 45, true);
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/*  55 */         if (hasPotionInOffhand) {
/*  56 */           this.isUsingPotion = true;
/*  57 */           (mc.getGameSettings()).keyBindUseItem.setPressed(true);
/*  58 */           this.hasThrownBottle = false;
/*     */         } 
/*  60 */       } else if (this.isUsingPotion) {
/*  61 */         (mc.getGameSettings()).keyBindUseItem.setPressed(false);
/*  62 */         this.isUsingPotion = false;
/*  63 */         ItemStack offhandItem = mc.player.getHeldItemOffhand();
/*  64 */         if (offhandItem.getItem() == Items.GLASS_BOTTLE) {
/*  65 */           mc.playerController.windowClick(0, 45, 1, ClickType.THROW, (PlayerEntity)mc.player);
/*  66 */           this.hasThrownBottle = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   @NativeInclude
/*     */   private int findPotion() {
/*  73 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/*  74 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/*  75 */       List<EffectInstance> effects = PotionUtils.getEffectsFromStack(stack);
/*  76 */       for (EffectInstance effect : effects) {
/*  77 */         if (effect.getPotion() == Effects.INVISIBILITY) {
/*  78 */           return (i < 9) ? (36 + i) : i;
/*     */         }
/*     */       } 
/*     */     } 
/*  82 */     return -1;
/*     */   }
/*     */   
/*     */   private boolean isInvisibilityPotion(ItemStack stack) {
/*  86 */     if (stack == null || stack.isEmpty()) return false;
/*     */     
/*  88 */     List<EffectInstance> effects = PotionUtils.getEffectsFromStack(stack);
/*  89 */     for (EffectInstance effect : effects) {
/*  90 */       if (effect.getPotion() == Effects.INVISIBILITY) {
/*  91 */         return true;
/*     */       }
/*     */     } 
/*  94 */     return false;
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   public void onEnable() {
/*  99 */     this.isUsingPotion = false;
/* 100 */     this.hasThrownBottle = false;
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   public void onDisable() {
/* 105 */     if (this.isUsingPotion) {
/* 106 */       (mc.getGameSettings()).keyBindUseItem.setPressed(false);
/*     */     }
/* 108 */     this.isUsingPotion = false;
/* 109 */     this.hasThrownBottle = false;
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoInvisible.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */