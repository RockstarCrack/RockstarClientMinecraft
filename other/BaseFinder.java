/*    */ package fun.rockstarity.client.modules.other;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.game.Chat;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.client.modules.player.FreeCam;
/*    */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*    */ import java.util.ArrayList;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.item.ArmorStandEntity;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ 
/*    */ @Info(name = "BaseFinder", type = Category.OTHER, desc = "Находит базы игроков.")
/*    */ public class BaseFinder
/*    */   extends Module
/*    */ {
/* 21 */   private final CheckBox preview = new CheckBox((Bindable)this, "Предпоказ базы");
/*    */   
/*    */   private BlockPos pos;
/* 24 */   private ArrayList<BlockPos> baseCache = new ArrayList<>();
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 28 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 29 */       for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity entity = objectIterator.next();
/* 30 */         if (entity instanceof ArmorStandEntity) {
/* 31 */           ArmorStandEntity armorStand = (ArmorStandEntity)entity;
/* 32 */           this.pos = entity.getPosition();
/* 33 */           if (!this.baseCache.contains(this.pos) && isArmorStandValid(armorStand)) {
/* 34 */             Chat.msg(String.format("Мы нашли базу на координатах (%s)", new Object[] { this.pos.getCoordinatesAsString() }));
/*    */             
/* 36 */             if (this.preview.get()) {
/* 37 */               ((FreeCam)rock.getModules().get(FreeCam.class)).set(true);
/* 38 */               ((FreeCam)rock.getModules().get(FreeCam.class)).onEnable();
/* 39 */               mc.player.setPosition((armorStand.getPositionVec()).x, (armorStand.getPositionVec()).y, (armorStand.getPositionVec()).z);
/*    */             } 
/*    */           } 
/* 42 */           this.baseCache.add(this.pos);
/*    */         }  }
/*    */     
/*    */     }
/*    */     
/* 47 */     if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange || (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && mc.gameSettings.keyBindSneak.isPressed())) {
/* 48 */       mc.setRenderViewEntity((Entity)mc.player);
/* 49 */       if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange) this.baseCache.clear(); 
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean isArmorStandValid(ArmorStandEntity armorStand) {
/* 54 */     String armorStandName = armorStand.getDisplayName().getString();
/* 55 */     return (armorStand.isInvisible() && armorStandName.toLowerCase().contains("владелец"));
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 60 */     mc.setRenderViewEntity((Entity)mc.player);
/* 61 */     if (this.preview.get()) ((FreeCam)rock.getModules().get(FreeCam.class)).set(false); 
/* 62 */     this.baseCache.clear();
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\BaseFinder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */