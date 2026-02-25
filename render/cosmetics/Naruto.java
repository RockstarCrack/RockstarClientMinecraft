/*    */ package fun.rockstarity.client.modules.render.cosmetics;
/*    */ 
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.render.player.EventModels;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.client.modules.render.Cosmetics;
/*    */ import net.minecraft.util.math.MathHelper;
/*    */ 
/*    */ 
/*    */ public class Naruto
/*    */   extends Cosmetics.Cosmetic
/*    */ {
/*    */   public Naruto(Cosmetics ui, Select select) {
/* 14 */     super(select, "Наруто");
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 19 */     if (event instanceof EventModels) { EventModels model = (EventModels)event; if (model.getOwner() == mc.player) {
/* 20 */         model.bipedRightArm.rotateAngleX = MathHelper.cos(0.6662F) * 2.0F * model.limbSwingAmount;
/* 21 */         model.bipedLeftArm.rotateAngleX = MathHelper.cos(0.6662F) * 2.0F * model.limbSwingAmount;
/* 22 */         model.bipedRightArm.rotateAngleZ = (MathHelper.cos(0.2812F) - 1.0F) * 1.0F * model.limbSwingAmount;
/* 23 */         model.bipedLeftArm.rotateAngleZ = (MathHelper.cos(0.2812F) - 1.0F) * 1.0F * model.limbSwingAmount;
/*    */       }  }
/*    */   
/*    */   }
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\cosmetics\Naruto.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */