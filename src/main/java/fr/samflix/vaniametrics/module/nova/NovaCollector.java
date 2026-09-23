package fr.samflix.vaniametrics.module.nova;

import org.bukkit.Bukkit;
import org.bukkit.World;

import xyz.xenondevs.nova.api.Nova;

import fr.samflix.vaniametrics.api.Collector;
import fr.samflix.vaniametrics.api.Gauge;
import fr.samflix.vaniametrics.api.MetricRegistry;

/**
 * Nova — the machine census.
 *
 * <p>What its API allows, and what it doesn't: Nova's registries
 * ({@code NovaBlockRegistry}, {@code NovaItemRegistry}) only do lookup by identifier — they
 * enumerate nothing, so there's no "how many custom blocks exist" to pull from them. The
 * {@code TileEntityManager}, though, enumerates. It's the only door, and it gives what actually
 * matters: the number of machines placed, the ones consuming tick time.
 *
 * <p>Background mode, for good reason: {@code getTileEntities(World)} builds a list. On a server
 * covered in machines, doing that on every scrape would make the exporter responsible for the
 * lag it measures.
 */
public final class NovaCollector implements Collector {

	private Gauge machines;

	@Override
	public String name() {
		return "nova";
	}

	@Override
	public String source() {
		return "Nova";
	}

	@Override
	public boolean isBackground() {
		return true;
	}

	@Override
	public long intervalSeconds() {
		return 30;
	}

	@Override
	public boolean needsMainThread() {
		// Enumeration walks loaded worlds: that's server state, and it's read on its thread.
		return true;
	}

	@Override
	public void declare(MetricRegistry r) {
		machines = r.gauge("nova_tile_entities",
				"Nova machines placed, per world. These are what consume tick time — "
						+ "a rising number explains a rising MSPT.",
				"world");
	}

	@Override
	public void collect(MetricRegistry r) {
		var manager = Nova.getNova().getTileEntityManager();
		// An unloaded world must stop being published rather than freeze its last count.
		machines.clear();
		for (World world : Bukkit.getWorlds()) {
			machines.set(manager.getTileEntities(world).size(), world.getName());
		}
	}
}
