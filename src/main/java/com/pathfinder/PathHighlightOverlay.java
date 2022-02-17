package com.pathfinder;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.*;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.awt.*;

public class PathHighlightOverlay extends Overlay {
    private final Client client;
    private final PathHighlightConfig config;
    private final PathHighlightPlugin plugin;

    @Inject
    private PathHighlightOverlay(Client client, PathHighlightConfig config, PathHighlightPlugin plugin)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isDisplay()) {
            return null;
        }
        WorldPoint currPoint = client.getLocalPlayer().getWorldLocation();
        Tile selectedTile = client.getSelectedSceneTile();
        if (selectedTile != null && currPoint != null)
        {
            WorldPoint selectedPoint = selectedTile.getWorldLocation();
            int xDist = selectedPoint.getX() - currPoint.getX();
            int yDist = selectedPoint.getY() - currPoint.getY();
            int dx = 0, dy = 0;
            //Figure out which axis the character will walk along,
            //According to whether the x or y-distance to the destination is longer.
            if (Math.abs(xDist) > Math.abs(yDist))
            {
                dx = (int) Math.signum((float) xDist);
            }
            else
            {
                dy = (int) Math.signum((float) yDist);
            }

            boolean isRunning;
            Widget runOrb = client.getWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB);

            if (runOrb == null)
                isRunning = true;
            else
                isRunning = runOrb.isFilled();

            boolean jumpedOver = isRunning;
            //Character will first walk on an axis until along a diagonal with the destination...
            while (Math.abs(xDist) != Math.abs(yDist))
            {
                currPoint = currPoint.dx(dx).dy(dy);
                LocalPoint pt = LocalPoint.fromWorld(client, currPoint.getX(), currPoint.getY());
                if (!jumpedOver || !config.skipJumpedTiles())
                    renderTile(graphics, pt, config.highlightPathColor());
                jumpedOver ^= isRunning; // Flip only if running;
                xDist = selectedPoint.getX() - currPoint.getX();
                yDist = selectedPoint.getY() - currPoint.getY();
            }
            //...Then walk diagonally to the destination.
            dx = (int) Math.signum((float) xDist);
            dy = (int) Math.signum((float) yDist);
            while (xDist != 0 && yDist != 0)
            {
                currPoint = currPoint.dx(dx).dy(dy);
                LocalPoint pt = LocalPoint.fromWorld(client, currPoint.getX(), currPoint.getY());
                if (!config.skipJumpedTiles() || !jumpedOver)
                    renderTile(graphics, pt, config.highlightPathColor());
                jumpedOver ^= isRunning; // Flip only if running;
                xDist = selectedPoint.getX() - currPoint.getX();
                yDist = selectedPoint.getY() - currPoint.getY();
            }
        }
        return null;
    }

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color)
    {
        if (dest == null)
        {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

        if (poly == null)
        {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, color);
    }
}
