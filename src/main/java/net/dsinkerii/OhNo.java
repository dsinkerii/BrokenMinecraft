package net.dsinkerii;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.GameRules.Category;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.core.config.builder.api.Component;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;

public class OhNo implements ModInitializer  {
	public float chaos = 1;
	public static final String MOD_ID = "dsinkerii_ohno";
    public static final Logger LOGGER = LoggerFactory.getLogger("what");
	public static final Identifier JAVASPRITE = new Identifier(MOD_ID,"textures/gui/java.png");
	public static final Identifier INSERTDISK = new Identifier(MOD_ID,"textures/gui/insertdisk.png");
	public static final Block CSS_BLOCK  = new Block(FabricBlockSettings.create().strength(4.0f));
	private static KeyBinding keyBinding;

	@Override
    public void onInitialize() {
		Registry.register(Registries.BLOCK, new Identifier("css", "css_block"), CSS_BLOCK);
		PlayerBlockBreakEvents.AFTER.register(this::BeforeBlockBreak);
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            renderGui(drawContext, tickDelta);
        });
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.ohno.diskenter", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_ENTER, // The keycode of the key
				"category.ohno.disk" // The translation key of the keybinding's category.
		));

        ServerTickEvents.START_SERVER_TICK.register(server -> {
			
			//remove chunks

            int removeChunk = new Random().nextInt((int)(1000/chaos));
			if(removeChunk == 2){
				MinecraftClient mc = MinecraftClient.getInstance();
				PlayerEntity player = mc.player;
				if(player != null){	
					int randomX = new Random().nextInt(10)*16;
					int randomZ = new Random().nextInt(10)*16;
					if(randomZ - 80 >= 32 || randomZ - 80 <= -32)
					if(randomX - 80 >= 32 || randomX - 80 <= -32){
						for (int x = 0; x <= 15; x++)
						for (int z = 0; z <= 15; z++)
						for (int y = -64; y <= 320; y++){
								BlockPos pos = new BlockPos((int)(player.getX()/16)*16+x+randomX, y, (int)(player.getZ()/16)*16+z+randomZ);
								server.getWorld(player.getWorld().getRegistryKey()).setBlockState(pos, Blocks.AIR.getDefaultState());
							}
						}
        	    	System.out.println("player pos (x:" + player.getX() + ", y:" + player.getY() + ", z:" + player.getZ());
				}
				else
					System.out.println("cant get player :(");
			}

			// entity-firework
			World world = server.getOverworld();
			MinecraftClient mc = MinecraftClient.getInstance();
			int EntityWork = new Random().nextInt((int)(750/chaos));
			if(mc.player != null && EntityWork == 2){
				LivingEntity entities = world.getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, mc.player, mc.player.getX()+new Random().nextInt(10)-5, mc.player.getY(), mc.player.getZ()+new Random().nextInt(10)-5, new Box(mc.player.getX()-10, mc.player.getY()-10, mc.player.getZ()-10,mc.player.getX()+10, mc.player.getY()+10, mc.player.getZ()+10));
				if(entities != null && entities.getEntityName() != mc.player.getEntityName())
					HorseWork(entities,server);
			}

			// ip sign
			int ipSign = new Random().nextInt((int)(2930/chaos));
			if(ipSign == 2 && mc.player != null){
				int randomX = new Random().nextInt(5);
				int randomZ = new Random().nextInt(5);
				BlockPos sign = new BlockPos((int)(mc.player.getX())+randomX, (int)(mc.player.getY()), (int)(mc.player.getZ())+randomZ);
				world.setBlockState(sign, Blocks.SPRUCE_SIGN.getDefaultState());
				BlockEntity blockEntity = world.getBlockEntity(sign);
				if (blockEntity instanceof SignBlockEntity) {
					SignText text = new SignText();
					text = text.withMessage(0, Text.literal("get doxed idiot"));
					text = text.withMessage(1, Text.literal(String.format("%d.%d.%d.%d",new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255))));
					text = text.withMessage(2, Text.literal(" "));
					text = text.withMessage(3, Text.literal(" "));
					text = text.withColor(DyeColor.WHITE);
					text = text.withGlowing(true);
					SignBlockEntity signEnt = (SignBlockEntity) blockEntity;
					signEnt.setText(text,false);
					signEnt.markDirty();
				}
			}
			// fling player by chance
			int flingChance = new Random().nextInt((int)(2000/chaos));
			if(flingChance == 2 && mc.player != null){
				try {
					flingPlayer(mc.player,25.0/15.0,server);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			// change block graphics
			int blockgraphic = new Random().nextInt((int)(1000/chaos));
			if(blockgraphic == 2 && mc.player != null){
				changeBlockGraphic(mc);
			}

			// deplete hunger in an instant
			int hungerDeplete = new Random().nextInt((int)(4000/chaos));
			if(hungerDeplete == 2 && mc.player != null){
				System.out.println("hungry");
				for(World i : server.getWorlds()){
					if(i.getPlayers().size() != 0)
						for(PlayerEntity p : i.getPlayers()){
							if(p == mc.player){
								p.getHungerManager().add(-12, 1);
							}
						}
				}
				//mc.player.getHungerManager().add(-500,0);
			}
			//kill player for no reason
			int KillPlayer = new Random().nextInt((int)(14000/chaos));
			if(KillPlayer == 2 && mc.player != null){
				for(World i : server.getWorlds()){
					if(i.getPlayers().size() != 0)
						for(PlayerEntity p : i.getPlayers()){
							if(p == mc.player){
								p.kill();
							}
						}
				}
			}
			//let a random fake player join 
			int joinPlayer = new Random().nextInt((int)(6000/chaos));
			if(joinPlayer == 2 && mc.player != null){
				PlayerJoined(mc);
			}
			//heroin
			int herobrineRandom = new Random().nextInt((int)(5000/chaos));
			if(herobrineRandom == 2 && mc.player != null){
				//calculate position.
				int randomX = new Random().nextInt(2)*2-3;
				int randomZ = new Random().nextInt(2)*2-3;
				Vec3d start = new Vec3d(mc.player.getX(), world.getHeight(), mc.player.getZ());
				Vec3d end = new Vec3d(mc.player.getX(), -64, mc.player.getZ());
				
				BlockHitResult result = world.raycast(new RaycastContext(start, end,RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,mc.player));
				ZombieEntity zombieEntity = new ZombieEntity(EntityType.ZOMBIE,server.getOverworld());
				zombieEntity.setPos(mc.player.getX()+randomX*10,result.getPos().getY()+1,mc.player.getZ()+randomZ*10);
				//zombieEntity.setAiDisabled(true);
				zombieEntity.setSilent(true);
				ItemStack helmet = new ItemStack(Items.PLAYER_HEAD,1);
				NbtCompound tag = helmet.getOrCreateNbt();
        		tag.putString("SkullOwner", "Flyua");
				zombieEntity.equipStack(EquipmentSlot.HEAD, helmet);
				server.getOverworld().spawnEntity(zombieEntity);
			}
			//unmount randomly
			if(mc.player != null)
				if(mc.player.isRiding()){
					int unmount = new Random().nextInt((int)(300/chaos));
					if(unmount == 2){
						mc.player.stopRiding();
					}
				}
			//summon night time monsters at day
			if(world.getTimeOfDay() >= 1000 && world.getTimeOfDay() <= 13000){
				int summonEv = new Random().nextInt((int)(1000/chaos));
				if(summonEv == 2 && mc.player!=null){
					int randomX = new Random().nextInt(25)*2;
					int randomZ = new Random().nextInt(25)*2;
					Vec3d start = new Vec3d(mc.player.getX(), world.getHeight(), mc.player.getZ());
					Vec3d end = new Vec3d(mc.player.getX(), -64, mc.player.getZ());
					BlockHitResult result = world.raycast(new RaycastContext(start, end,RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,mc.player));
					ZombieEntity zombieEntity = new ZombieEntity(EntityType.ZOMBIE,server.getOverworld());
					zombieEntity.setPos(mc.player.getX()+randomX,result.getPos().getY()+1,mc.player.getZ()+randomZ);
					server.getOverworld().spawnEntity(zombieEntity);

					SkeletonEntity skeletonEntity = new SkeletonEntity(EntityType.SKELETON,server.getOverworld());
					skeletonEntity.setPos(mc.player.getX()+randomX,result.getPos().getY()+1,mc.player.getZ()+randomZ);
					server.getOverworld().spawnEntity(skeletonEntity);
				}
			}
			//let a bunch of players join
			int joinBunchPlayer = new Random().nextInt((int)(10000/chaos));
			if(joinBunchPlayer == 2 && mc.player != null){
				for(int i = 0; i < 21; i++)
					PlayerJoined(mc);
			}
			// random effect 
			int effectRandom = new Random().nextInt((int)(2500/chaos));
			if(effectRandom == 2 && mc.player != null){
				List<StatusEffect> effects = Arrays.asList(StatusEffects.LEVITATION,StatusEffects.ABSORPTION,StatusEffects.BLINDNESS,StatusEffects.HEALTH_BOOST,StatusEffects.HUNGER,StatusEffects.JUMP_BOOST,StatusEffects.POISON,StatusEffects.SLOWNESS);
				StatusEffectInstance randomEffect = new StatusEffectInstance(effects.get(new Random().nextInt(effects.size()-1)), new Random().nextInt((int)2500), 2, true, true);
				mc.player.addStatusEffect(randomEffect);
				reseteff(mc.player);
			}
			//chaos effect
			if(mc.player!=null)
				chaos += (0.1f/20/60);
			// change language
			int languageRan = new Random().nextInt((int)(5000/chaos));
			if(languageRan == 2 && mc.player != null){
				
				//!!!
				///this code only changes your language **only**. please do not call this a spyware nor any kind of other *ware for this ty
				//!!!

				String path = String.valueOf(FabricLoader.getInstance().getGameDir());
				Path pathOptions = Path.of(path+"/options.txt");
				try {
					String file2 = Files.readString(pathOptions);
					List<String> randLang = Arrays.asList("ru_ru","fr_fr","jp_jp","en_pt","enws","es_cl");
					int lineNumber = -1;
					for (String line : file2.split("\n")) {
						if (line.contains("lang")) {
							file2 = file2.replace("lang:"+file2.split("\n")[lineNumber+1].split(":")[1], "lang:" + new Random().nextInt(randLang.size()-1));
							FileOutputStream fileOut = new FileOutputStream(path+"/options.txt");
							fileOut.write(file2.getBytes());
							fileOut.close();
							break;
						}
						lineNumber++;
					}
					mc.options.load();
					backtoeng(mc, pathOptions, path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//butterfingers
			int butterfingers = new Random().nextInt((int)(750/chaos));
			if(butterfingers == 2 && mc.player != null){
				mc.player.dropSelectedItem(true);
			}
			//css textures
			int cssgraphic = new Random().nextInt((int)(100/chaos));
			if(cssgraphic == 2 && mc.player != null){
				int randomX = new Random().nextInt(5);
				int randomZ = new Random().nextInt(5);
				BlockPos randomBlock = new BlockPos((int)(mc.player.getX())+randomX, (int)(mc.player.getY()-1), (int)(mc.player.getZ())+randomZ);
				if(mc.player.getWorld().getBlockState(randomBlock) != Blocks.AIR.getDefaultState()){
					BlockState CssTexture = CSS_BLOCK.getDefaultState();
					mc.player.getWorld().setBlockState(randomBlock, CssTexture);
				}
			}
			//minigun-skeletons
			int minigunskeletons = new Random().nextInt((int)(100/chaos));
            if(minigunskeletons == 2 && mc.player!= null){
				List<SkeletonEntity> skeleton = world.getEntitiesByType(EntityType.SKELETON, new Box(mc.player.getX()-10,mc.player.getY()-10,mc.player.getZ()-10,mc.player.getX()+10,mc.player.getY()+10,mc.player.getZ()+10),EntityPredicates.VALID_ENTITY);
				if(skeleton.size() != 0){
					System.out.println(skeleton.get(0).speed);
					ArrowEntity arrowEntity = new ArrowEntity(EntityType.ARROW,server.getOverworld());
					arrowEntity.setPos(skeleton.get(0).getX(),skeleton.get(0).getY()+1.5f,skeleton.get(0).getZ());
					arrowEntity.setVelocity(skeleton.get(0),skeleton.get(0).getPitch(),skeleton.get(0).headYaw,skeleton.get(0).getRoll(),3,1);
					server.getOverworld().spawnEntity(arrowEntity);
				}
            }
			//
        }); 
		
	}











	void reseteff(ClientPlayerEntity player){
		new Thread(){public void run(){
			try {
				Thread.sleep(5000);
				for(StatusEffectInstance StatusEff : player.getStatusEffects()){
					player.removeStatusEffect(StatusEff.getEffectType());
				}
			}catch (InterruptedException e){}
		}}.start();
	}
	void backtoeng(MinecraftClient mc,Path pathOptions,String path){
		new Thread(){public void run(){
			try {
				Thread.sleep(30000);
				String file2 = Files.readString(pathOptions);
					int lineNumber = -1;
					for (String line : file2.split("\n")) {
						if (line.contains("lang")) {
							file2 = file2.replace("lang:"+file2.split("\n")[lineNumber+1].split(":")[1], "lang:en_us");
							FileOutputStream fileOut = new FileOutputStream(path+"/options.txt");
							fileOut.write(file2.getBytes());
							fileOut.close();
							break;
						}
						lineNumber++;
					}
					mc.options.load();
			}catch (InterruptedException e) {} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}}.start();
	}

	void PlayerJoined(MinecraftClient mc){
		new Thread(){public void run(){
			try {
				Thread.sleep(new Random().nextInt(500));
				int playerID = new Random().nextInt(255);
				mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("§ePlayer%d has joined the game",playerID)));
				Thread.sleep(3461 + new Random().nextInt(500));

				List<String> randomMSGBeforeLeave = Arrays.asList("sorr ywrong ip", "fuck", "hello yahi", "ok", "imm over here strokin my dick i got lotion on my dick-");
				mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("<Player%d> " + randomMSGBeforeLeave.get(new Random().nextInt(randomMSGBeforeLeave.size()-1)),playerID)));
				Thread.sleep(1921 + new Random().nextInt(500));
				mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("§ePlayer%d has left the game",playerID)));
			} catch (InterruptedException e) {}}}.start();
	}
	void BeforeBlockBreak(
        final World world,
        final PlayerEntity player,
        final BlockPos blockPos,
        final BlockState blockState,
        final BlockEntity blockEntity
    ){
		int CanHit = new Random().nextInt(5);
		if(CanHit == 2){
			world.setBlockState(blockPos, blockState);
			BlockPos blockPosBreak = blockPos;
			blockPosBreak = blockPosBreak.add(new Random().nextInt(10)-5, 0, new Random().nextInt(10)-5);
			world.setBlockState(blockPosBreak, Blocks.AIR.getDefaultState());
		}
	}
	
	void changeBlockGraphic(MinecraftClient mc){
		int randomX = new Random().nextInt(5);
		int randomZ = new Random().nextInt(5);
		BlockPos randomBlock = new BlockPos((int)(mc.player.getX())+randomX, (int)(mc.player.getY()-1), (int)(mc.player.getZ())+randomZ);
		if(mc.player.getWorld().getBlockState(randomBlock) != Blocks.AIR.getDefaultState()){
			BlockState OldState = mc.player.getWorld().getBlockState(randomBlock);
			List<BlockState> BSpossible = Arrays.asList(Blocks.GOLD_ORE.getDefaultState(),Blocks.DIAMOND_ORE.getDefaultState(),Blocks.EMERALD_ORE.getDefaultState(),Blocks.FIRE.getDefaultState());
			mc.player.getWorld().setBlockState(randomBlock, BSpossible.get(new Random().nextInt(3)));
			new Thread()
			{
				public void run()
				{
					//some code here.
					try {
						Thread.sleep(10000);
						mc.player.getWorld().setBlockState(randomBlock, OldState);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//more code here.
				}
			}.start();
		}
	}
	void flingPlayer(PlayerEntity player, double height, MinecraftServer server) throws InterruptedException {
		player.setVelocity(player.getVelocity().x, height, player.getVelocity().z);
		GameRules.Key<GameRules.BooleanRule> key = GameRules.FALL_DAMAGE;
		server.getGameRules().get(key).set(false,server);
		new Thread()
			{
				public void run()
				{
					//some code here.
					try {
						Thread.sleep(10000);
						server.getGameRules().get(key).set(true,server);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//more code here.
				}
			}.start();
	}
	void HorseWork(Entity horse, MinecraftServer server){ // apply a firework like effect to any entity
		StatusEffectInstance levitationEffect = new StatusEffectInstance(StatusEffects.LEVITATION, 100000, 10);
		((LivingEntity) horse).addStatusEffect(levitationEffect);
		new Thread()
			{
				public void run()
				{
					//some code here.
					try {
						Thread.sleep(2000);
						TntEntity tntEntity = new TntEntity(EntityType.TNT,server.getOverworld());
						tntEntity.setPos(horse.getX(),horse.getY(),horse.getZ());
						tntEntity.setFuse(0);
						server.getOverworld().spawnEntity(tntEntity);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//more code here.
				}
			}.start();
	}
	boolean isrendering = false;
	boolean isrenderingDisk = false;
	boolean isDownloading = false;
	public void renderGui(DrawContext drawContext, float tickDelta) {
		int javaScreen = new Random().nextInt((int)(10000/chaos));
		if(javaScreen == 2){
			isrendering = true;
		}
		int InsertDisk = new Random().nextInt((int)(10000/chaos));
		if(InsertDisk == 2){
			isDownloading=false;
			isrenderingDisk = true;
		}
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer renderer = mc.textRenderer;
		if(mc.player != null){
			drawContext.drawText(renderer, String.format("§lCHAOS: " + Float.toString(chaos)), 10, 10, 0x7c0618,false);
			drawContext.drawText(renderer, String.format("§lCHAOS: " + Float.toString(chaos)), 10+(int)(Math.sin(mc.world.getTime()/3)*2), 10+(int)(Math.cos(mc.world.getTime()/3)*2), 0xff0000,false);
		}
		//get minecraft full version
		int DemoMC = new Random().nextInt((int)(10000/chaos));
		if(DemoMC == 2){
			List<String> Texts = Arrays.asList("§l(System): please buy our game we have 12 dollars in our budg et","§l(System): Please buy the full version of the game....... or else", "§l(System): stop playing the demo and buy the full game ffs", "§l(System): buy the game already, you are playing demo", "§l(System): Please buy the full version of the game to continue playing");
			int textToSend = new Random().nextInt(Texts.size());
			mc.inGameHud.getChatHud().addMessage(Text.literal(Texts.get(textToSend)));

		}
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding.wasPressed()) {
				new Thread()
				{
					public void run()
					{
						//some code here.
						try {
							isDownloading=true;
							Thread.sleep(3000);
							isrenderingDisk=false;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//more code here.
					}
				}.start();
			}
		});

		//Insert disk
		if(isrenderingDisk && mc.player != null){
			int xJava, yJava = 0;
			xJava = mc.getWindow().getScaledWidth()/2-276/2;
			yJava = mc.getWindow().getScaledHeight()/2-233/2;
			drawContext.drawTexture(INSERTDISK,xJava,yJava,0,0,276,233,276,233);
			if(!isDownloading)
				drawContext.drawText(renderer, String.format("Please insert missing files"),          xJava+142/2, yJava+35, 0x181818,false);
			else
				drawContext.drawText(renderer, String.format("        Downloading...        "),          xJava+142/2, yJava+35, 0x24ea2f,true);
			drawContext.drawText(renderer, String.format("Your Copy of Minecraft"),               xJava+153/2, yJava+60, 0xdedede,false);
			drawContext.drawText(renderer, String.format("Has been flagged as"),                  xJava+170/2, yJava+70, 0xdedede,false);
			drawContext.drawText(renderer, String.format("\"Absolutely fucked.\""),               xJava+177/2, yJava+80, 0xdedede,false);
			drawContext.drawText(renderer, String.format("To fix this, press enter to download"), xJava+96/2, yJava+90, 0xdedede,false);
			drawContext.drawText(renderer, String.format("missing files."),                       xJava+200/2, yJava+100, 0xdedede,false);
		}
		if( isrendering && mc.player != null){
			int xJava, yJava = 0;
			xJava = mc.getWindow().getScaledWidth()/2-1920/2;
			yJava = mc.getWindow().getScaledHeight()/2-1080/2;
			drawContext.drawTexture(JAVASPRITE,xJava,yJava,0,0,1920,1080,1920,1080);
			new Thread()
			{
				public void run()
				{
					//some code here.
					try {
						Thread.sleep(5000);
						isrendering = false;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//more code here.
				}
			}.start();
		}
	}

}