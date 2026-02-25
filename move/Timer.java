/*     */ package fun.rockstarity.client.modules.move;
/*     */ 
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*     */ import fun.rockstarity.api.events.list.render.EventRender2D;
/*     */ import fun.rockstarity.api.helpers.player.Move;
/*     */ import fun.rockstarity.api.helpers.render.Render;
/*     */ import fun.rockstarity.api.helpers.system.TextUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.animation.Animation;
/*     */ import fun.rockstarity.api.render.animation.Easing;
/*     */ import fun.rockstarity.api.render.color.FixColor;
/*     */ import fun.rockstarity.api.render.shaders.list.Round;
/*     */ import fun.rockstarity.api.render.ui.draggables.Draggable;
/*     */ import fun.rockstarity.api.render.ui.rect.Rect;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.world.Difficulty;
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
/*     */ @Info(name = "Timer", desc = "Ускоряет мир для вас", type = Category.MOVE)
/*     */ public class Timer
/*     */   extends Module
/*     */ {
/*  41 */   Mode mode = new Mode((Bindable)this, "Режим");
/*     */   
/*  43 */   Mode.Element classic = new Mode.Element(this.mode, "Обычный");
/*  44 */   Mode.Element funtime = new Mode.Element(this.mode, "FunTime");
/*     */   
/*  46 */   private final CheckBox smart = new CheckBox((Bindable)this, "Умный");
/*     */   
/*  48 */   private final Slider speed = (new Slider((Bindable)this, "Скорость")).min(0.1F).max(10.0F).inc(0.1F).set(5.0F).hide(() -> Boolean.valueOf(this.funtime.get()));
/*     */   
/*  50 */   private float charge = 100.0F; private float prevCharge = 100.0F;
/*     */   
/*     */   private long last;
/*  53 */   protected final Draggable draggable = new Draggable("Таймер", new Rect(0.0F, 0.0F, 0.0F, 0.0F));
/*  54 */   protected final Animation showing = (new Animation()).setEasing(Easing.BOTH_SINE).setSpeed(300);
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  58 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*  59 */       if (this.funtime.get()) {
/*  60 */         if (!this.smart.get() || this.charge > 10.0F) {
/*  61 */           (mc.player.getMotion()).x *= 1.05D;
/*  62 */           (mc.player.getMotion()).z *= 1.05D;
/*     */           
/*  64 */           if (mc.player.fallDistance > 0.0F)
/*  65 */             (mc.player.getMotion()).y *= 1.05D; 
/*     */         } 
/*  67 */       } else if (this.classic.get()) {
/*  68 */         if (this.smart.get()) {
/*  69 */           mc.timer.timerSpeed = (this.charge > 10.0F) ? this.speed.get() : 1.0F;
/*     */         } else {
/*  71 */           mc.timer.timerSpeed = this.speed.get();
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onAllEvent(Event event) {
/*  79 */     smartCalculation(event);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   private void smartCalculation(Event event) {
/*  87 */     if (this.smart.get()) {
/*  88 */       if (this.classic.get()) {
/*  89 */         if (event instanceof EventSendPacket) { EventSendPacket e = (EventSendPacket)event; if (e.getPacket() instanceof net.minecraft.network.play.client.CPlayerPacket)
/*  90 */           { if (System.currentTimeMillis() - this.last < 1000L) {
/*  91 */               float value = (float)(0.05D - ((float)(System.currentTimeMillis() - this.last) / 1000.0F)) * 400.0F;
/*  92 */               this.charge -= Math.max(0.0F, value);
/*     */             } 
/*  94 */             if (Move.isMoving()) this.charge += 0.5F; 
/*  95 */             this.charge = Math.max(0.0F, Math.min(100.0F, this.charge));
/*  96 */             this.last = System.currentTimeMillis(); }  }
/*     */       
/*  98 */       } else if (this.funtime.get()) {
/*  99 */         if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange) {
/* 100 */           this.charge += 7.0F;
/*     */         }
/*     */         
/* 103 */         if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 104 */           if (mc.world.getDifficulty() == Difficulty.EASY) {
/* 105 */             this.charge = 100.0F;
/*     */           }
/*     */           
/* 108 */           this.charge += 0.006F;
/*     */           
/* 110 */           if (get()) {
/* 111 */             this.charge -= 2.5F;
/*     */           }
/*     */           
/* 114 */           this.charge = MathHelper.clamp(this.charge, 0.0F, 100.0F);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 119 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 120 */       for (Module module : rock.getModules().values());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 125 */     if (event instanceof EventRender2D) { EventRender2D e = (EventRender2D)event;
/* 126 */       if (!this.smart.get()) {
/* 127 */         this.draggable.setWidth(0.0F);
/* 128 */         this.draggable.setHeight(0.0F);
/*     */         
/*     */         return;
/*     */       } 
/* 132 */       this.showing.setForward(((this.charge < 100.0F && this.charge > 0.0F) || mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen));
/*     */       
/* 134 */       if (this.showing.finished(false))
/*     */         return; 
/* 136 */       FixColor bgColor = rock.getThemes().getFirstColor().alpha(this.showing.get());
/* 137 */       FixColor textColor = rock.getThemes().getTextFirstColor().alpha(this.showing.get());
/*     */       
/* 139 */       String type = "Timer";
/* 140 */       MatrixStack ms = e.getMatrixStack();
/* 141 */       String remained = String.format("Заряд: %s%%", new Object[] { TextUtility.formatNumber(this.charge) });
/*     */       
/* 143 */       float width = 40.0F + semibold.get(10).getWidth(remained);
/* 144 */       float height = 20.0F;
/*     */       
/* 146 */       if (this.draggable.getX() == 0.0F && this.draggable.getY() == 0.0F) {
/* 147 */         this.draggable.setX(sr.getScaledWidth() / 2.0F - width / 2.0F);
/* 148 */         this.draggable.setY((sr.getScaledHeight() - 100) - height / 2.0F);
/*     */       } 
/*     */       
/* 151 */       float x = this.draggable.getX();
/* 152 */       float y = this.draggable.getY();
/*     */       
/* 154 */       this.draggable.setWidth(width);
/* 155 */       this.draggable.setHeight(height);
/*     */       
/* 157 */       Round.draw(ms, new Rect(x, y, width, height), 2.0F, bgColor);
/* 158 */       bold.get(12).draw(ms, type, x + 5.0F, y + 3.0F, textColor);
/*     */       
/* 160 */       semibold.get(10).draw(ms, remained, x + 5.0F, y + 10.0F, textColor);
/* 161 */       float size = 12.0F;
/* 162 */       int from = 270;
/* 163 */       Round.draw(ms, new Rect(x + width - height / 2.0F - size / 2.0F, y + height / 2.0F - size / 2.0F, size, size), size / 2.0F - 0.1F, rock.getThemes().getSecondColor().alpha(this.showing.get()));
/* 164 */       Render.drawClientCircle(x + width - height / 2.0F, y + height / 2.0F, size / 2.0F - 1.0F, from, from + (int)(361.0F * this.charge / 100.0F), this.showing.get()); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onDisable() {
/* 174 */     mc.timer.reset();
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\Timer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */