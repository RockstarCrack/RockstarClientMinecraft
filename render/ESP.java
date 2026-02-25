/*     */ package fun.rockstarity.client.modules.render;
/*     */ 
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.EventType;
/*     */ import fun.rockstarity.api.events.list.render.ui.EventRenderPreUI;
/*     */ import fun.rockstarity.api.helpers.render.PositionTracker;
/*     */ import fun.rockstarity.api.helpers.render.Render;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.render.ui.clickgui.esp.ESPElement;
/*     */ import fun.rockstarity.api.render.ui.clickgui.esp.ESPEventable;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import net.minecraft.client.settings.PointOfView;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.vector.Vector2f;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ import net.minecraft.util.math.vector.Vector4f;
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
/*     */ @Info(name = "ESP", desc = "Информация о игроках через стены", type = Category.RENDER)
/*     */ public class ESP
/*     */   extends Module
/*     */ {
/*  41 */   private final HashMap<Entity, Vector4f> positions = new HashMap<>();
/*     */ 
/*     */   
/*     */   @EventType({EventRenderPreUI.class})
/*     */   public void onEvent(Event event) {
/*  46 */     if (event instanceof EventRenderPreUI) { EventRenderPreUI e = (EventRenderPreUI)event;
/*  47 */       collect(e);
/*  48 */       render(e); }
/*     */ 
/*     */     
/*     */     try {
/*  52 */       for (ESPElement elmt : rock.getEspSettingsHandler().getEspElements()) {
/*  53 */         if (elmt instanceof ESPEventable) { ESPEventable eventable = (ESPEventable)elmt; if (elmt.isActive())
/*  54 */             eventable.onEvent(event);  } 
/*     */       } 
/*  56 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void render(EventRenderPreUI e) {
/*  61 */     MatrixStack ms = e.getMatrixStack();
/*  62 */     NameTags nameTags = (NameTags)rock.getModules().get(NameTags.class);
/*     */     
/*  64 */     for (Map.Entry<Entity, Vector4f> entry : this.positions.entrySet()) {
/*  65 */       Vector4f position = entry.getValue();
/*     */       
/*  67 */       LivingEntity livingEntity = (LivingEntity)entry.getKey(); if (livingEntity instanceof LivingEntity) { LivingEntity entity = livingEntity; if (!entity.getDeathAnim().finished(false)) {
/*  68 */           float x = position.x;
/*  69 */           float y = position.y;
/*  70 */           float width = position.z - position.x;
/*  71 */           float height = position.w - position.y;
/*     */           
/*  73 */           float anim = entity.getDeathAnim().get();
/*     */           
/*  75 */           for (ESPElement elmt : rock.getEspSettingsHandler().getEspElements()) {
/*  76 */             if (!entity.getUniqueID().equals(PlayerEntity.getOfflineUUID(entity.getName().getString())))
/*     */               continue; 
/*  78 */             if (!elmt.getActiveAnim().finished(false)) {
/*  79 */               Vector2f tag = nameTags.getPlayerTags().get(entity);
/*  80 */               if (elmt.getDirection() == 0 && tag != null && ((NameTags)rock.getModules().get(NameTags.class)).get() && !(elmt instanceof fun.rockstarity.api.render.ui.clickgui.esp.elmts.ESPBoxes)) {
/*  81 */                 elmt.drawOnEntity(ms, entity, tag.x - width / 2.0F, tag.y, width, height, anim); continue;
/*     */               } 
/*  83 */               elmt.drawOnEntity(ms, entity, x, y, width, height, anim);
/*     */             } 
/*     */           } 
/*     */         }  }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isAir(ItemStack stack) {
/*  92 */     return (stack.getItem() == Items.AIR);
/*     */   }
/*     */   
/*     */   private void collect(EventRenderPreUI e) {
/*  96 */     this.positions.clear();
/*  97 */     for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity entity = objectIterator.next();
/*  98 */       if (!PositionTracker.isInView(entity) || (
/*  99 */         !(entity instanceof PlayerEntity) && !(entity instanceof net.minecraft.entity.item.ItemEntity)) || (
/* 100 */         entity == mc.player && mc.getGameSettings().getPointOfView() == PointOfView.FIRST_PERSON))
/*     */         continue; 
/* 102 */       double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * e.getPartialTicks();
/* 103 */       double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * e.getPartialTicks();
/* 104 */       double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * e.getPartialTicks();
/*     */       
/* 106 */       Vector3d size = new Vector3d((entity.getBoundingBox()).maxX - (entity.getBoundingBox()).minX, (entity.getBoundingBox()).maxY - (entity.getBoundingBox()).minY, (entity.getBoundingBox()).maxZ - (entity.getBoundingBox()).minZ);
/*     */       
/* 108 */       AxisAlignedBB aabb = new AxisAlignedBB(x - size.x / 2.0D, y, z - size.z / 2.0D, x + size.x / 2.0D, y + size.y, z + size.z / 2.0D);
/*     */       
/* 110 */       Vector4f position = null;
/*     */       
/* 112 */       for (int i = 0; i < 8; i++) {
/* 113 */         Vector2f vector = Render.projectf((i % 2 == 0) ? aabb.minX : aabb.maxX, (i / 2 % 2 == 0) ? aabb.minY : aabb.maxY, (i / 4 % 2 == 0) ? aabb.minZ : aabb.maxZ);
/*     */         
/* 115 */         if (position == null) {
/* 116 */           position = new Vector4f(vector.x, vector.y, 1.0F, 1.0F);
/*     */         } else {
/* 118 */           position.x = Math.min(vector.x, position.x);
/* 119 */           position.y = Math.min(vector.y, position.y);
/* 120 */           position.z = Math.max(vector.x, position.z);
/* 121 */           position.w = Math.max(vector.y, position.w);
/*     */         } 
/*     */       } 
/*     */       
/* 125 */       this.positions.put(entity, position); }
/*     */   
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\ESP.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */