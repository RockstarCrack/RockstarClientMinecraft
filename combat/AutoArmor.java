/*     */ package fun.rockstarity.client.modules.combat;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.helpers.game.Server;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import net.minecraft.enchantment.Enchantment;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
/*     */ import net.minecraft.enchantment.Enchantments;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.inventory.EquipmentSlotType;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.item.ArmorItem;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.util.DamageSource;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ @NativeInclude
/*     */ @Info(name = "AutoArmor", desc = "Автоматически надевает на вас броню", type = Category.COMBAT)
/*     */ public class AutoArmor
/*     */   extends Module
/*     */ {
/*  33 */   private final Slider passed = (new Slider((Bindable)this, "Задержка")).min(1.0F).max(1000.0F).inc(1.0F).set(10.0F).desc("Задержка с которой будет надеваться броня");
/*     */   
/*  35 */   private final CheckBox onlyCt = new CheckBox((Bindable)this, "Только в PVP");
/*  36 */   private final CheckBox ignoreElytra = (new CheckBox((Bindable)this, "Игнор элитры")).desc("Не надевать нагрудник вместо элитры");
/*     */   
/*  38 */   private final TimerUtility timerUtils = new TimerUtility();
/*     */   
/*     */   private boolean isNullOrEmpty(ItemStack stack) {
/*  41 */     return (stack == null || stack.isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  46 */     if (event instanceof fun.rockstarity.api.events.list.player.EventMotion) {
/*  47 */       PlayerInventory inventory = mc.player.inventory;
/*     */       
/*  49 */       int[] bestArmorSlots = new int[4];
/*  50 */       int[] bestArmorValues = new int[4];
/*  51 */       if (Server.hasCT() || !this.onlyCt.get()) {
/*  52 */         evaluateCurrentArmorValues(inventory, bestArmorSlots, bestArmorValues);
/*     */         
/*  54 */         ArrayList<Integer> types = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) }));
/*  55 */         Collections.shuffle(types);
/*     */         
/*  57 */         for (Iterator<Integer> iterator = types.iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
/*  58 */           int bestSlot = bestArmorSlots[i];
/*  59 */           if (bestSlot == -1) {
/*     */             continue;
/*     */           }
/*  62 */           ItemStack oldArmor = inventory.armorItemInSlot(i);
/*  63 */           if (!oldArmor.isEmpty() && inventory.getFirstEmptyStack() == -1) {
/*     */             continue;
/*     */           }
/*  66 */           if (this.ignoreElytra.get() && mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof net.minecraft.item.ElytraItem && i == 2)
/*  67 */             continue;  transferArmorItem(inventory, bestSlot, i); }
/*     */       
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void evaluateCurrentArmorValues(PlayerInventory inventory, int[] bestArmorSlots, int[] bestArmorValues) {
/*  75 */     for (int type = 0; type < 4; type++) {
/*  76 */       bestArmorSlots[type] = -1;
/*  77 */       ItemStack stack = inventory.armorItemInSlot(type);
/*  78 */       if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem) {
/*  79 */         ArmorItem item = (ArmorItem)stack.getItem();
/*  80 */         bestArmorValues[type] = getArmorValue(item, stack);
/*     */       } 
/*     */     } 
/*     */     
/*  84 */     for (int slot = 0; slot < 36; slot++) {
/*  85 */       ItemStack stack = inventory.getStackInSlot(slot);
/*  86 */       if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem) {
/*  87 */         ArmorItem item = (ArmorItem)stack.getItem();
/*  88 */         int armorType = item.getEquipmentSlot().getIndex();
/*  89 */         int armorValue = getArmorValue(item, stack);
/*  90 */         if (armorValue > bestArmorValues[armorType]) {
/*  91 */           bestArmorSlots[armorType] = slot;
/*  92 */           bestArmorValues[armorType] = armorValue;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void transferArmorItem(PlayerInventory inventory, int bestSlot, int armorType) {
/* 100 */     if (bestSlot < 9) {
/* 101 */       bestSlot += 36;
/*     */     }
/* 103 */     if (this.timerUtils.passed((long)this.passed.get())) {
/* 104 */       ItemStack oldArmor = inventory.armorItemInSlot(armorType);
/* 105 */       if (!oldArmor.isEmpty()) {
/* 106 */         mc.playerController.windowClick(0, 8 - armorType, 0, ClickType.QUICK_MOVE, (PlayerEntity)mc.player);
/*     */       }
/* 108 */       mc.playerController.windowClick(0, bestSlot, 0, ClickType.QUICK_MOVE, (PlayerEntity)mc.player);
/* 109 */       this.timerUtils.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   private int getArmorValue(ArmorItem item, ItemStack stack) {
/* 114 */     int armorPoints = item.getDamageReduceAmount();
/* 115 */     int prtPoints = 0;
/* 116 */     int armorToughness = (int)item.getToughness();
/* 117 */     int armorType = item.getArmorMaterial().getDamageReductionAmount(EquipmentSlotType.LEGS);
/* 118 */     Enchantment protection = Enchantments.PROTECTION;
/* 119 */     int prtLvl = EnchantmentHelper.getEnchantmentLevel(protection, stack);
/* 120 */     DamageSource dmgSource = DamageSource.causePlayerDamage((PlayerEntity)mc.player);
/* 121 */     prtPoints = protection.calcModifierDamage(prtLvl, dmgSource);
/* 122 */     return armorPoints * 5 + prtPoints * 3 + armorToughness + armorType;
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\AutoArmor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */