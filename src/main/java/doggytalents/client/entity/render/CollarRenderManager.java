package doggytalents.client.entity.render;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import doggytalents.api.client.render.IAccessoryRenderer;
import doggytalents.api.client.render.ITalentRenderer;
import doggytalents.api.registry.Accessory;
import doggytalents.api.registry.Talent;

public class CollarRenderManager {

    private static Map<Accessory, IAccessoryRenderer<?>> accessoryRendererMap = Maps.newConcurrentMap();
    private static Map<Talent, ITalentRenderer<?>> talentRendererMap = Maps.newConcurrentMap();

    /**
     * Register a renderer for a collar type
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static void registerRenderer(Accessory entityClass, IAccessoryRenderer<?> shader) {
        accessoryRendererMap.put(entityClass, shader);
    }

    public static void registerRenderer(Talent entityClass, ITalentRenderer<?> shader) {
        talentRendererMap.put(entityClass, shader);
    }


    public static boolean hasRenderer(Accessory type) {
        return accessoryRendererMap.containsKey(type);
    }

    @Nullable
    public static IAccessoryRenderer<?> getRendererFor(Accessory type) {
        return accessoryRendererMap.get(type);
    }

    @Nullable
    public static ITalentRenderer<?> getRendererFor(Talent talent) {
        return talentRendererMap.get(talent);
    }
}
