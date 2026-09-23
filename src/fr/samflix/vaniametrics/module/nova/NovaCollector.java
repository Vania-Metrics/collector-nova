package fr.samflix.vaniametrics.module.nova;

import org.bukkit.Bukkit;
import org.bukkit.World;

import xyz.xenondevs.nova.api.Nova;

import fr.samflix.vaniametrics.api.Collector;
import fr.samflix.vaniametrics.api.Gauge;
import fr.samflix.vaniametrics.api.MetricRegistry;

/**
 * Nova — le recensement des machines.
 *
 * <p>CE QUE SON API PERMET, ET CE QU'ELLE NE PERMET PAS. Les registres de Nova
 * ({@code NovaBlockRegistry}, {@code NovaItemRegistry}) ne font que de la RECHERCHE par
 * identifiant : ils n'énumèrent rien, il n'y a donc aucun « combien de blocs personnalisés
 * existent » à en tirer. Le {@code TileEntityManager}, lui, énumère — c'est la seule porte, et
 * elle donne ce qui compte vraiment : le nombre de MACHINES posées, celles qui consomment du temps
 * de tick.
 *
 * <p>EN FOND, ET POUR UNE BONNE RAISON : {@code getTileEntities(World)} construit une liste. Sur
 * un serveur couvert de machines, la faire à chaque scrape rendrait l'exportateur responsable du
 * lag qu'il mesure.
 */
public final class NovaCollector implements Collector {

	private Gauge machines;

	@Override
	public String nom() {
		return "nova";
	}

	@Override
	public String origine() {
		return "Nova";
	}

	@Override
	public boolean enFond() {
		return true;
	}

	@Override
	public long intervalleSecondes() {
		return 30;
	}

	@Override
	public boolean filPrincipal() {
		// L'énumération traverse les mondes chargés : c'est de l'état du serveur, et il se lit
		// sur son fil.
		return true;
	}

	@Override
	public void declarer(MetricRegistry r) {
		machines = r.gauge("nova_tile_entities",
				"Machines Nova posées, par monde. Ce sont elles qui consomment du temps de tick — "
						+ "un nombre qui grimpe explique un MSPT qui grimpe.",
				"world");
	}

	@Override
	public void relever(MetricRegistry r) {
		var gestionnaire = Nova.getNova().getTileEntityManager();
		// Un monde déchargé doit cesser d'être publié plutôt que de figer son dernier compte.
		machines.clear();
		for (World monde : Bukkit.getWorlds()) {
			machines.set(gestionnaire.getTileEntities(monde).size(), monde.getName());
		}
	}
}
