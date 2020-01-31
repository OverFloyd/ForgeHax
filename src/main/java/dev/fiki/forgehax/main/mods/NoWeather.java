package dev.fiki.forgehax.main.mods;

import dev.fiki.forgehax.common.events.packet.PacketInboundEvent;
import dev.fiki.forgehax.main.Globals;
import dev.fiki.forgehax.main.events.ClientWorldEvent;
import dev.fiki.forgehax.main.util.command.Setting;
import dev.fiki.forgehax.main.util.mod.Category;
import dev.fiki.forgehax.main.util.mod.ToggleMod;
import dev.fiki.forgehax.main.util.mod.loader.RegisterMod;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static dev.fiki.forgehax.main.Globals.*;

/**
 * Created on 5/16/2017 by fr1kin
 */
@RegisterMod
public class NoWeather extends ToggleMod {
  
  private boolean isRaining = false;
  private float rainStrength = 0.f;
  private float previousRainStrength = 0.f;
  
  public NoWeather() {
    super(Category.WORLD, "NoWeather", false, "Disables weather");
  }
  
  private final Setting<Boolean> showStatus =
      getCommandStub()
          .builders()
          .<Boolean>newSettingBuilder()
          .name("hudstatus")
          .description("show info about suppressed weather")
          .defaultTo(true)
          .build();
  
  private void saveState(World world) {
    if (world != null) {
      setState(world.getWorldInfo().isRaining(), world.rainingStrength, world.prevRainingStrength);
    } else {
      setState(false, 1.f, 1.f);
    }
  }
  
  private void setState(boolean raining, float rainStrength, float previousRainStrength) {
    this.isRaining = raining;
    setState(rainStrength, previousRainStrength);
  }
  
  private void setState(float rainStrength, float previousRainStrength) {
    this.rainStrength = rainStrength;
    this.previousRainStrength = previousRainStrength;
  }
  
  private void disableRain() {
    if (getWorld() != null) {
      getWorld().getWorldInfo().setRaining(false);
      getWorld().setRainStrength(0.f);
    }
  }
  
  public void resetState() {
    if (getWorld() != null) {
      getWorld().getWorldInfo().setRaining(isRaining);
      getWorld().rainingStrength = rainStrength;
      getWorld().prevRainingStrength = previousRainStrength;
    }
  }
  
  @Override
  public void onEnabled() {
    saveState(getWorld());
  }
  
  @Override
  public void onDisabled() {
    resetState();
  }
  
  @SubscribeEvent
  public void onWorldChange(ClientWorldEvent event) {
    saveState(event.getWorld());
  }
  
  @SubscribeEvent
  public void onWorldTick(TickEvent.ClientTickEvent event) {
    disableRain();
  }
  
  @SubscribeEvent
  public void onPacketIncoming(PacketInboundEvent event) {
    if (event.getPacket() instanceof SChangeGameStatePacket) {
      int state = ((SChangeGameStatePacket) event.getPacket()).getGameState();
      float strength = ((SChangeGameStatePacket) event.getPacket()).getValue();
      boolean isRainState = false;
      switch (state) {
        case 1: // end rain
          isRainState = false;
          setState(false, 0.f, 0.f);
          break;
        case 2: // start rain
          isRainState = true;
          setState(true, 1.f, 1.f);
          break;
        case 7: // fade value: sky brightness
          isRainState = true; // needs to be cancelled to avoid flicker
          break;
      }
      if (isRainState) {
        disableRain();
        event.setCanceled(true);
      }
    }
  }
  
  @Override
  public String getDisplayText() {
    if (isRaining
        && showStatus.getAsBoolean()
        && isInWorld()) {
      Biome biome = getWorld().getBiome(getLocalPlayer().getPosition());
      boolean canRain = Biome.RainType.RAIN.equals(biome.getPrecipitation());
      boolean canSnow = Biome.RainType.SNOW.equals(biome.getPrecipitation());
      
      String status;
      
      if (getWorld().isThundering()) {
        status = "[Thunder]";
      } else if (canSnow) {
        status = "[Snowing]";
      } else if (!canRain) {
        status = "[Cloudy]";
      } else {
        status = "[Raining]";
      }
      
      return super.getDisplayText() + status;
    } else {
      return super.getDisplayText();
    }
  }
}