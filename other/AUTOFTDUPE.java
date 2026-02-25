/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*     */ import fun.rockstarity.api.helpers.game.Chat;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.PremiumModule;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CPlayerDiggingPacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.text.IFormattableTextComponent;
/*     */ import net.minecraft.util.text.StringTextComponent;
/*     */ import net.minecraft.util.text.TextFormatting;
/*     */ import net.minecraft.world.World;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AUTOFTDUPE", desc = "СОСАЛ ДА СОСАЛ", type = Category.OTHER)
/*     */ public class AUTOFTDUPE
/*     */   extends PremiumModule
/*     */ {
/*  49 */   private final CheckBox dropTrident = new CheckBox((Bindable)this, "Выбрасывать трезубец");
/*  50 */   final Slider delay = (new Slider((Bindable)this, "Задержка")).min(0.0F).max(10.0F).inc(0.1F).set(5.0F);
/*     */   
/*  52 */   private int delayCounter = 0;
/*  53 */   private int bestSlot = -1;
/*     */   
/*     */   private boolean cancel;
/*  56 */   ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
/*  57 */   ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  62 */     if (event instanceof EventSendPacket) { EventSendPacket e = (EventSendPacket)event;
/*  63 */       if (e.getPacket() instanceof net.minecraft.network.play.client.CPlayerPacket || e
/*  64 */         .getPacket() instanceof net.minecraft.network.play.client.CCloseWindowPacket) {
/*     */         return;
/*     */       }
/*  67 */       if (!(e.getPacket() instanceof net.minecraft.network.play.client.CClickWindowPacket) && 
/*  68 */         !(e.getPacket() instanceof CPlayerDiggingPacket)) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/*  73 */       if (!this.cancel) {
/*     */         return;
/*     */       }
/*     */       
/*  77 */       IFormattableTextComponent iFormattableTextComponent = (new StringTextComponent(e.getPacket().toString())).mergeStyle(TextFormatting.WHITE);
/*     */       
/*  79 */       System.out.println(iFormattableTextComponent.getString());
/*     */       
/*  81 */       event.cancel(); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   private void findBestTrident() {
/*  87 */     this.bestSlot = -1;
/*  88 */     int bestDurability = -1;
/*     */     
/*  90 */     for (int i = 0; i < 9; i++) {
/*  91 */       ItemStack stack = mc.player.inventory.getStackInSlot(i);
/*  92 */       if (stack.getItem() == Items.TRIDENT) {
/*  93 */         int durability = stack.getMaxDamage() - stack.getDamage();
/*  94 */         if (durability > bestDurability) {
/*  95 */           bestDurability = durability;
/*  96 */           this.bestSlot = i;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 101 */     if (this.bestSlot == -1) {
/* 102 */       Chat.debug("No trident found in hotbar!");
/* 103 */       toggle();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   private void dupe() {
/* 110 */     int delayInt = (int)this.delay.get() * 100;
/*     */     
/* 112 */     int lowestHotbarSlot = 0;
/* 113 */     int lowestHotbarDamage = 1000;
/* 114 */     for (int i = 0; i < 9; i++) {
/*     */       
/* 116 */       if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TRIDENT) {
/*     */         
/* 118 */         Integer currentHotbarDamage = Integer.valueOf(mc.player.inventory.getStackInSlot(i).getDamage());
/* 119 */         if (lowestHotbarDamage > currentHotbarDamage.intValue()) { lowestHotbarSlot = i; lowestHotbarDamage = currentHotbarDamage.intValue(); }
/*     */       
/*     */       } 
/*     */     } 
/*     */     
/* 124 */     mc.playerController.processRightClick((PlayerEntity)mc.player, (World)mc.world, Hand.MAIN_HAND);
/* 125 */     this.cancel = true;
/*     */     
/* 127 */     int finalLowestHotbarSlot = lowestHotbarSlot;
/* 128 */     this.scheduler.schedule(() -> { this.cancel = false; if (!get()) return;  mc.playerController.windowClick(0, 45, 0, ClickType.SWAP, (PlayerEntity)mc.player); mc.player.connection.sendPacketSilent((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN)); if (this.dropTrident.get()) mc.playerController.windowClick(0, 45, 0, ClickType.THROW, (PlayerEntity)mc.player);  this.cancel = true; this.scheduler.schedule(this::dupe, delayInt, TimeUnit.MILLISECONDS); }delayInt, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onEnable() {
/* 152 */     if (mc.player == null)
/*     */       return; 
/* 154 */     for (int i = 0; i < 9; i++) {
/*     */       
/* 156 */       if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TRIDENT)
/*     */       {
/* 158 */         Integer integer = Integer.valueOf(mc.player.inventory.getStackInSlot(i).getDamage());
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 163 */     mc.player.connection.sendPacketSilent((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/*     */     
/* 165 */     Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
/*     */     
/* 167 */     int2ObjectOpenHashMap.put(3, mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem));
/* 168 */     int2ObjectOpenHashMap.put(36, mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem));
/*     */ 
/*     */     
/* 171 */     dupe();
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AUTOFTDUPE.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */