/*     */ package fun.rockstarity.client.modules.render;
/*     */ 
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.Setting;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.animation.Animation;
/*     */ import fun.rockstarity.api.render.animation.Easing;
/*     */ import fun.rockstarity.api.render.menufilter.MenuFilter;
/*     */ import fun.rockstarity.api.render.ui.clickgui.ClickGuiRenderer;
/*     */ import fun.rockstarity.api.render.ui.clickgui.ClickGuiScreen;
/*     */ import fun.rockstarity.api.render.ui.clickgui.GlyphType;
/*     */ import fun.rockstarity.api.render.ui.clickgui.SettingRect;
/*     */ import fun.rockstarity.api.scripts.Script;
/*     */ import fun.rockstarity.api.sounds.Sound;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import net.minecraft.client.gui.screen.Screen;
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
/*     */ @Info(name = "ClickGui", desc = "Меню чита", type = Category.RENDER)
/*     */ public class ClickGui
/*     */   extends Module
/*     */ {
/*  42 */   private final Mode mode = new Mode((Bindable)this, "Анимация закрытия"); public Mode getMode() { return this.mode; }
/*     */   
/*  44 */   private final Mode.Element classic = new Mode.Element(this.mode, "Классическая"); public Mode.Element getClassic() { return this.classic; }
/*  45 */    private final Mode.Element filter = new Mode.Element(this.mode, "Фильтр"); public Mode.Element getFilter() { return this.filter; }
/*  46 */    private final Mode.Element tech = (new Mode.Element(this.mode, "Трёхмерная")).set(); public Mode.Element getTech() { return this.tech; }
/*     */   
/*  48 */   private final CheckBox shadow = (new CheckBox((Bindable)this, "Затемнение")).set(true).hide(() -> Boolean.valueOf(!this.classic.get())); public CheckBox getShadow() { return this.shadow; }
/*  49 */    private final CheckBox sound = (new CheckBox((Bindable)this, "Звук")).set(true).hide(() -> Boolean.valueOf(!this.filter.get())); public CheckBox getSound() { return this.sound; }
/*  50 */   public Slider getZoom() { return this.zoom; } private final Slider zoom = (new Slider((Bindable)this, "Приближение")).min(0.0F).max(100.0F).inc(5.0F).set(50.0F).text(0.0F, "Нет")
/*  51 */     .hide(() -> Boolean.valueOf(!this.filter.get()));
/*     */   
/*  53 */   private final Animation darkness = (new Animation()).setEasing(Easing.BOTH_SINE).setSpeed(200); public Animation getDarkness() { return this.darkness; }
/*     */ 
/*     */   
/*     */   public void onAllEvent(Event event) {
/*  57 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && rock.getClickGui() == null) {
/*  58 */       rock.setClickGui(new ClickGuiScreen());
/*  59 */       rock.getClickGui().getWindow().getEspSettings().renderPage(new MatrixStack(), 0, 0, 0.0F);
/*     */     } 
/*     */     
/*  62 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*  63 */       if (rock.getClickGui() == null) {
/*  64 */         rock.setClickGui(new ClickGuiScreen());
/*  65 */         rock.getClickGui().getWindow().getEspSettings().renderPage(new MatrixStack(), 0, 0, 0.0F);
/*     */       } 
/*     */       
/*  68 */       if (!(mc.currentScreen instanceof ClickGuiScreen) && MenuFilter.active()) {
/*  69 */         rock.getClickGui().getWindow().close();
/*     */       }
/*     */     } 
/*     */     
/*  73 */     super.onAllEvent(event);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {}
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  82 */     if (this.filter.get()) {
/*  83 */       MenuFilter.show(true);
/*  84 */       if (this.sound.get()) {
/*  85 */         (new Sound("openmenu")).play();
/*     */       }
/*     */     } 
/*     */     
/*  89 */     mc.displayGuiScreen((Screen)rock.getClickGui());
/*     */     
/*  91 */     ClickGuiRenderer.opening.setForward(true);
/*  92 */     ClickGuiRenderer.rotationESP.setForward(true);
/*     */     
/*  94 */     rock.getClickGui().getWindow().getRenderer(); if (ClickGuiRenderer.getSettings().isEmpty()) {
/*  95 */       for (Category cat : Category.values()) {
/*  96 */         int i = cat.getIndex();
/*  97 */         rock.getClickGui().getWindow().getRenderer(); ClickGuiRenderer.getGlyphes()[i] = new GlyphType();
/*     */         
/*  99 */         List<Module> modules = new ArrayList<>();
/*     */         
/* 101 */         rock.getModules().values().forEach(mod -> modules.add(mod));
/* 102 */         for (Script script : rock.getScriptHandler().getEnabledScripts()) {
/* 103 */           script.getScriptModules().forEach(mod -> modules.add(mod));
/*     */         }
/*     */         
/* 106 */         modules.sort((rock.getScriptHandler()).SORT_METHOD);
/*     */         
/* 108 */         for (Module module : modules) {
/* 109 */           if (module.getInfo().type() == cat) {
/* 110 */             rock.getClickGui().getWindow().getRenderer(); if (!ClickGuiRenderer.getGlyphes()[i].containsKey(Character.valueOf(module.getInfo().name().charAt(0)))) {
/* 111 */               rock.getClickGui().getWindow().getRenderer(); ClickGuiRenderer.getGlyphes()[i].put(Character.valueOf(module.getInfo().name().charAt(0)), new ArrayList());
/*     */             } 
/* 113 */             rock.getClickGui().getWindow().getRenderer(); ((List<Module>)ClickGuiRenderer.getGlyphes()[i].get(Character.valueOf(module.getInfo().name().charAt(0)))).add(module);
/*     */             
/* 115 */             for (Setting setting : module.getSettings()) {
/* 116 */               rock.getClickGui().getWindow().getRenderer(); ClickGuiRenderer.getSettings().add(new SettingRect(setting));
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 123 */     toggle(false, true);
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\ClickGui.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */