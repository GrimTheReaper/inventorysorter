/*
 *     Copyright © 2016 cpw
 *     This file is part of Inventorysorter.
 *
 *     Inventorysorter is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inventorysorter is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Inventorysorter.  If not, see <http://www.gnu.org/licenses/>.
 */

package cpw.mods.inventorysorter;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Properties;

/**
 * Created by cpw on 08/01/16.
 */

@Mod(modid="inventorysorter",name="Inventory Sorter", guiFactory = "cpw.mods.inventorysorter.GuiConfigFactory", acceptedMinecraftVersions = "[1.9.4,1.11)")
public class InventorySorter
{
    @Mod.Instance("inventorysorter")
    public static InventorySorter INSTANCE;

    public Logger log;
    public SimpleNetworkWrapper channel;
    final List slotblacklist = Lists.newArrayList();

    @Mod.EventHandler
    public void handleimc(FMLInterModComms.IMCEvent evt)
    {
        for (FMLInterModComms.IMCMessage msg : evt.getMessages())
        {
            if ("slotblacklist".equals(msg.key) && msg.isStringMessage()) {
                slotblacklist.add(msg.getStringValue());
            }
        }
    }

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        final Properties versionProperties = evt.getVersionProperties();
        if (versionProperties != null)
        {
            evt.getModMetadata().version = versionProperties.getProperty("inventorysorter.version");
        }
        else
        {
            evt.getModMetadata().version = "1.0";
        }
        SideProxy.INSTANCE.loadConfiguration(evt.getSuggestedConfigurationFile());
        log = evt.getModLog();
        channel = NetworkRegistry.INSTANCE.newSimpleChannel("inventorysorter");
        channel.registerMessage(ServerHandler.class, Network.ActionMessage.class, 1, Side.SERVER);
        SideProxy.INSTANCE.bindKeys();
        // blacklist codechickencore because
        FMLInterModComms.sendMessage("inventorysorter", "slotblacklist", "codechicken.core.inventory.SlotDummy");
    }
}
