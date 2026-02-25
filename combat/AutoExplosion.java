/*     */ package fun.rockstarity.client.modules.combat;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.events.list.player.EventPlace;
/*     */ import fun.rockstarity.api.helpers.math.MathUtility;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.Blocks;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CHeldItemChangePacket;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoExplosion", desc = "Автоматически ставит и взрывает якоря/кристаллы", type = Category.COMBAT)
/*     */ public class AutoExplosion
/*     */   extends Module
/*     */ {
/*  42 */   private final Select utils = new Select((Bindable)this, "Выбор");
/*     */   
/*  44 */   private final Select.Element crystals = (new Select.Element(this.utils, "Кристалл")).set(true);
/*  45 */   private final Select.Element anchor = new Select.Element(this.utils, "Якорь");
/*     */   
/*  47 */   private final CheckBox self = (new CheckBox((Bindable)this, "Не взрывать себя")).set(true);
/*  48 */   private final Slider delay = (new Slider((Bindable)this, "Задержка")).max(100.0F).min(5.0F).inc(5.0F).set(50.0F).hide(() -> Boolean.valueOf(!this.crystals.get())).desc("Задержка с которой будет ставиться кристал на обсидиан");
/*     */   
/*  50 */   private final List<EventPlace> blocks = new ArrayList<>();
/*  51 */   private final TimerUtility timer = new TimerUtility();
/*     */   
/*     */   private float[] rot;
/*     */   
/*     */   public void onEvent(Event event) {
/*  56 */     if (event instanceof EventPlace) { EventPlace place = (EventPlace)event;
/*  57 */       handleBlockPlaceEvent(place);
/*     */       
/*     */       return; }
/*     */     
/*  61 */     if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/*  62 */       handleMotionEvent(e); }
/*     */ 
/*     */     
/*  65 */     if (this.rot != null) Player.look(event, this.rot[0], this.rot[1], false); 
/*     */   }
/*     */   @NativeInclude
/*     */   private void handleBlockPlaceEvent(EventPlace place) {
/*  69 */     if (isBlockValid(place.getBlock(), place)) {
/*  70 */       this.blocks.add(place);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isBlockValid(Block block, EventPlace place) {
/*  75 */     if (block == Blocks.OBSIDIAN && this.self.get() && mc.player.getPosY() > place.getPos().getY()) return false; 
/*  76 */     return (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK || (this.anchor.isEnabled() && block == Blocks.RESPAWN_ANCHOR));
/*     */   }
/*     */   
/*     */   private void handleMotionEvent(EventMotion e) {
/*  80 */     if (!this.blocks.isEmpty()) {
/*  81 */       EventPlace block = this.blocks.get(0);
/*  82 */       this.rot = MathUtility.getRotVec(mc.player.getPositionVec(), new Vector3d(block.getPos().getX(), block.getPos().getY(), block.getPos().getZ()));
/*     */       
/*  84 */       int slot = Player.findItem(9, (block.getBlock() == Blocks.RESPAWN_ANCHOR) ? Items.GLOWSTONE : Items.END_CRYSTAL);
/*  85 */       if (slot != -1 && this.timer.passed(this.delay.get() * 9.0F)) {
/*  86 */         placeAndBreakBlock(block, slot);
/*  87 */         this.rot = null;
/*  88 */         this.timer.reset();
/*     */       } 
/*     */     } 
/*     */     
/*  92 */     attackNearbyCrystals(e);
/*     */   }
/*     */   
/*     */   private void placeAndBreakBlock(EventPlace block, int slot) {
/*  96 */     if (mc.world.getBlockState(block.getPos()).getBlock() != Blocks.AIR) {
/*  97 */       mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(slot - 36));
/*  98 */       Direction facing = Direction.UP;
/*  99 */       mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(block.getPos().getVec().add(0.5D, 0.5D, 0.5D), facing, block.getPos(), false));
/* 100 */       mc.player.swingArm(Hand.MAIN_HAND);
/* 101 */       mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(mc.player.inventory.currentItem));
/*     */     } 
/*     */     
/* 104 */     if (mc.world.getBlockState(block.getPos()).getBlock() != Blocks.RESPAWN_ANCHOR) {
/* 105 */       this.blocks.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   private void attackNearbyCrystals(EventMotion e) {
/* 110 */     for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity ent = objectIterator.next();
/* 111 */       if (ent instanceof net.minecraft.entity.item.EnderCrystalEntity && mc.player.getDistance(ent) < 5.0F && 
/* 112 */         mc.player.getCooledAttackStrength(0.0F) > 0.5D) {
/* 113 */         mc.playerController.attackEntity((PlayerEntity)mc.player, ent);
/* 114 */         mc.player.swingArm(Hand.MAIN_HAND);
/* 115 */         this.rot = null;
/*     */       }  }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 123 */     this.blocks.clear();
/* 124 */     this.rot = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 129 */     this.timer.reset();
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\AutoExplosion.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */