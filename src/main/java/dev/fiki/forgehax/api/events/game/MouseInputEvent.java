package dev.fiki.forgehax.api.events.game;

import dev.fiki.forgehax.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MouseInputEvent extends Event {
  private final int button;
  private final int action;
  private final int mods;
}
