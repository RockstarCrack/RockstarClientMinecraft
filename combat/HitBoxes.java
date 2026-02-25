/*    */ package fun.rockstarity.client.modules.combat;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.render.world.EventRenderWorld;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.util.math.AxisAlignedBB;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*    */ 
/*    */ 
/*    */ @Info(name = "HitBoxes", desc = "Увеличивает хитбоксы сущностей", type = Category.COMBAT)
/*    */ public class HitBoxes
/*    */   extends Module
/*    */ {
/* 21 */   private final Select targets = (new Select((Bindable)this, "Цели")).desc("Сущность у которых будет увеличиваться хитбокс");
/*    */   
/* 23 */   private final Select.Element players = (new Select.Element(this.targets, "Игрок")).set(true);
/* 24 */   private final Select.Element mobs = (new Select.Element(this.targets, "Мобы")).set(true);
/*    */   
/* 26 */   private final Slider size = (new Slider((Bindable)this, "Размер хитбоксов")).min(0.0F).max(1.0F).inc(0.1F).set(0.3F).desc("Размер хитбоксов по которым можно будет ударить");
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 30 */     if (event instanceof EventRenderWorld) { EventRenderWorld e = (EventRenderWorld)event;
/* 31 */       float size = this.size.get();
/*    */       
/* 33 */       for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity ent = objectIterator.next();
/* 34 */         if (((!(ent instanceof net.minecraft.entity.player.PlayerEntity) || !this.players.get()) && ((!(ent instanceof net.minecraft.entity.MobEntity) && !(ent instanceof net.minecraft.entity.passive.AnimalEntity)) || !this.mobs.get())) || 
/* 35 */           ent == null || ent == mc.player)
/*    */           continue; 
/* 37 */         ent.setBoundingBox(new AxisAlignedBB(ent
/* 38 */               .getPosX() - size, 
/* 39 */               (ent.getBoundingBox()).minY, ent
/* 40 */               .getPosZ() - size, ent
/* 41 */               .getPosX() + size, 
/* 42 */               (ent.getBoundingBox()).maxY, ent
/* 43 */               .getPosZ() + size)); }
/*    */        }
/*    */   
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @NativeInclude
/*    */   public void onDisable() {
/* 52 */     for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity ent = objectIterator.next();
/* 53 */       if (((!(ent instanceof net.minecraft.entity.player.PlayerEntity) || !this.players.get()) && ((!(ent instanceof net.minecraft.entity.MobEntity) && !(ent instanceof net.minecraft.entity.passive.AnimalEntity)) || !this.mobs.get())) || 
/* 54 */         ent == null || ent == mc.player)
/* 55 */         continue;  ent.setBoundingBox(new AxisAlignedBB(ent
/* 56 */             .getPosX() - 0.30000001192092896D, 
/* 57 */             (ent.getBoundingBox()).minY, ent
/* 58 */             .getPosZ() - 0.30000001192092896D, ent
/* 59 */             .getPosX() + 0.30000001192092896D, 
/* 60 */             (ent.getBoundingBox()).maxY, ent
/* 61 */             .getPosZ() + 0.30000001192092896D)); }
/*    */   
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\HitBoxes.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */