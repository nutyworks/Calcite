package works.nuty.calcite;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalciteModClient implements ClientModInitializer {
	public static Logger LOGGER = LogManager.getLogger("calcite");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}