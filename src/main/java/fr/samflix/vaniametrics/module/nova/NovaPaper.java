package fr.samflix.vaniametrics.module.nova;

import org.bukkit.plugin.java.JavaPlugin;

import fr.samflix.vaniametrics.api.VaniaMetrics;
import fr.samflix.vaniametrics.api.VaniaMetricsProvider;

/**
 * Nova machine metrics.
 *
 * <p>Nova's registries can only look up, not enumerate. The TileEntityManager, though, enumerates — and that's what matters.
 */
public final class NovaPaper extends JavaPlugin {

	private NovaCollector collector;

	@Override
	public void onEnable() {
		VaniaMetrics metrics = VaniaMetricsProvider.get();
		collector = new NovaCollector();
		metrics.register(collector);
	}

	@Override
	public void onDisable() {
		if (collector != null) {
			VaniaMetricsProvider.find().ifPresent(m -> m.unregister(collector));
		}
	}
}
