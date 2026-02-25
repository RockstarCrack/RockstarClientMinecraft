/*    */ package fun.rockstarity.client.modules.render;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.EventType;
/*    */ import fun.rockstarity.api.events.list.render.world.EventRenderWorld;
/*    */ import fun.rockstarity.api.helpers.render.Render;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.render.color.themes.Style;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.math.vector.Vector3d;
/*    */ import org.lwjgl.opengl.GL11;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "BlockESP", desc = "Подсвечивает выбранные блоки", type = Category.RENDER)
/*    */ public class BlockESP
/*    */   extends Module
/*    */ {
/* 30 */   private final Select blocks = (new Select((Bindable)this, "Блоки")).desc("Блоки которые будут подсвечиваться").min(1);
/* 31 */   private final Select.Element chest = (new Select.Element(this.blocks, "Сундук")).set(true);
/* 32 */   private final Select.Element enderChest = (new Select.Element(this.blocks, "Ендер Сундуки")).set(true);
/* 33 */   private final Select.Element spawner = (new Select.Element(this.blocks, "Спавнер")).set(false);
/* 34 */   private final Select.Element shulker = (new Select.Element(this.blocks, "Шалкер")).set(false);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @EventType({EventRenderWorld.class})
/*    */   public void onEvent(Event event) {
/* 50 */     if (event instanceof EventRenderWorld) {
/* 51 */       Vector3d renderPos = (mc.getRenderManager()).info.getProjectedView();
/*    */       
/* 53 */       GL11.glPushMatrix();
/* 54 */       GL11.glTranslated(-renderPos.x, -renderPos.y, -renderPos.z);
/* 55 */       this; for (TileEntity entity : mc.world.loadedTileEntityList) {
/*    */         
/* 57 */         BlockPos pos = entity.getPos();
/* 58 */         if (this.chest.isEnabled() && entity instanceof net.minecraft.tileentity.ChestTileEntity) Render.blockEsp(pos, Style.getMain().getRGB()); 
/* 59 */         if (this.enderChest.isEnabled() && entity instanceof net.minecraft.tileentity.EnderChestTileEntity) Render.blockEsp(pos, Style.getMain().getRGB()); 
/* 60 */         if (this.spawner.isEnabled() && entity instanceof net.minecraft.tileentity.MobSpawnerTileEntity) Render.blockEsp(pos, Style.getMain().getRGB()); 
/* 61 */         if (this.shulker.isEnabled() && entity instanceof net.minecraft.tileentity.ShulkerBoxTileEntity) Render.blockEsp(pos, Style.getMain().getRGB()); 
/*    */       } 
/* 63 */       GL11.glPopMatrix();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\BlockESP.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */