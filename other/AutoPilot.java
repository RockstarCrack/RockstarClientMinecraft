/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.events.list.player.EventTrace;
/*     */ import fun.rockstarity.api.helpers.game.Chat;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Inventory;
/*     */ import fun.rockstarity.api.helpers.player.Move;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.client.commands.WayCommand;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CHeldItemChangePacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ import net.minecraft.world.gen.Heightmap;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoPilot", desc = "Автоматически летит на ивент", type = Category.OTHER)
/*     */ public class AutoPilot
/*     */   extends Module
/*     */ {
/*  32 */   private Vector3d target = Vector3d.ZERO;
/*     */   private float yaw;
/*  34 */   private final TimerUtility timer = new TimerUtility();
/*     */   private float pitch;
/*  36 */   private final CheckBox swapChestplate = (new CheckBox((Bindable)this, "Брать нагрудник")).desc("Брать нагрудник по прилёту на координаты");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  42 */     if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/*  43 */       this.target = WayCommand.points.values().stream().findFirst().orElse(null);
/*  44 */       if (this.target != null && this.target != Vector3d.ZERO && mc.player.isElytraFlying()) {
/*  45 */         Player.look(event, this.yaw, this.pitch, true);
/*  46 */         e.setYaw(this.yaw);
/*  47 */         e.setPitch(this.pitch);
/*     */ 
/*     */         
/*  50 */         Vector3d vec = this.target.subtract(mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
/*  51 */         float rawYaw = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
/*  52 */         int highestY = (int)mc.player.getPosY();
/*  53 */         int highestX = (int)this.target.x;
/*  54 */         int highestZ = (int)this.target.z;
/*  55 */         int iterations = 60;
/*     */         
/*  57 */         for (int x = -iterations; x < iterations; x++) {
/*  58 */           for (int z = -iterations; z < iterations; z++) {
/*  59 */             int height = mc.world.getHeight(Heightmap.Type.WORLD_SURFACE, (int)(mc.player.getPosX() + x), (int)(mc.player.getPosZ() + z)) + 5;
/*     */             
/*  61 */             if (height > highestY && height > mc.player.getPosY()) {
/*  62 */               highestY = height;
/*  63 */               highestX = (int)(mc.player.getPosX() + x);
/*  64 */               highestZ = (int)(mc.player.getPosZ() + z);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/*  69 */         Vector3d vecHeight = (new Vector3d(highestX, (highestY + 23), highestZ)).subtract(mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
/*  70 */         float rawPitch = (float)MathHelper.clamp(Math.toDegrees(Math.asin(-vecHeight.y)), -89.0D, 89.0D);
/*     */         
/*  72 */         this.yaw = rawYaw;
/*  73 */         this.pitch = rawPitch + 13.0F;
/*     */         
/*  75 */         (mc.getGameSettings()).keyBindSprint.setPressed(true);
/*  76 */         (mc.getGameSettings()).keyBindForward.setPressed(true);
/*     */         
/*  78 */         if (Move.getSpeed() < 1.46D && this.timer.passed((long)(1000.0F / mc.timer.timerSpeed)) && Inventory.getFirework() != -1) {
/*  79 */           mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(Inventory.getFirework()));
/*  80 */           mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/*  81 */           mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(mc.player.inventory.currentItem));
/*  82 */           this.timer.reset();
/*     */         } 
/*     */         
/*  85 */         for (int i = mc.world.getHeight(Heightmap.Type.WORLD_SURFACE, (int)mc.player.getPosX(), (int)mc.player.getPosZ()) - 10; i < mc.player.getPosY(); i++) {
/*  86 */           if (!mc.world.getBlockState(new BlockPos(mc.player.getPosX(), i, mc.player.getPosZ())).getFluidState().isEmpty() && mc.player.getPosY() - i < 5.0D) {
/*  87 */             rawPitch -= 11.0F;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*  92 */         if (mc.player.getDistance(this.target) < 30.0F) {
/*  93 */           Chat.msg("Отличная поездка! Спасибо за использование сервиса \"Димамик\"");
/*  94 */           if (this.swapChestplate.get()) {
/*  95 */             int item = Inventory.getChestplate();
/*  96 */             Player.moveItemOld((item < 46) ? item : 6, 6, true);
/*     */           } 
/*  98 */           toggle();
/*     */         } 
/*     */       }  }
/*     */ 
/*     */     
/* 103 */     if (event instanceof EventTrace) { EventTrace e = (EventTrace)event;
/* 104 */       e.setYaw(this.yaw);
/* 105 */       e.setPitch(this.pitch);
/* 106 */       e.cancel(); }
/*     */   
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoPilot.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */