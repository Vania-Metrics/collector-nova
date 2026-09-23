package fr.samflix.vaniametrics.module.nova;

import org.bukkit.plugin.java.JavaPlugin;

import fr.samflix.vaniametrics.api.VaniaMetrics;
import fr.samflix.vaniametrics.api.VaniaMetricsProvider;

/**
 * Métriques des machines Nova.
 *
 * <p>Les registres de Nova ne savent que chercher, pas énumérer. Le TileEntityManager, lui, énumère — et c'est ce qui compte.
 */
public final class NovaPaper extends JavaPlugin {

	private NovaCollector collecteur;

	@Override
	public void onEnable() {
		VaniaMetrics metriques = VaniaMetricsProvider.get();
		collecteur = new NovaCollector();
		metriques.enregistrer(collecteur);
	}

	@Override
	public void onDisable() {
		if (collecteur != null) {
			VaniaMetricsProvider.chercher().ifPresent(m -> m.retirer(collecteur));
		}
	}
}
