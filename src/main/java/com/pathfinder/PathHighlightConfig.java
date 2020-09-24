package com.pathfinder;

import net.runelite.client.config.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("pathfinder")
public interface PathHighlightConfig extends Config
{
	@Alpha
	@ConfigItem(
			keyName = "highlightPathColor",
			name = "Path Color",
			description = "Configures the color of the path"
	)
	default Color highlightPathColor()
	{
		return new Color(0, 127, 0, 127);
	}
	@ConfigItem(
			keyName = "displaySetting",
			name = "Display Setting",
			description = "Configures when the path should be displayed"
	)
	default PathDisplaySetting displaySetting() { return PathDisplaySetting.ALWAYS_DISPLAY; }
	@ConfigItem(
			keyName = "displayKeybind",
			name = "Keybind",
			description = "Sets the keybind if configured to display the path on toggle or while a key is pressed.\nCan be combined with Shift, Ctrl and Alt as well as Command on Mac."
	)
	default Keybind displayKeybind() { return new Keybind(KeyEvent.VK_P, 0); }
}
