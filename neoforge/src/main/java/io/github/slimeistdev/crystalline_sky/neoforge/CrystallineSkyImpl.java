package io.github.slimeistdev.crystalline_sky.neoforge;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.foundation.registration.CatnipRegistry;
import io.github.slimeistdev.crystalline_sky.foundation.registration.Registration;
import io.github.slimeistdev.crystalline_sky.multiloader.Env;
import io.github.slimeistdev.crystalline_sky.multiloader.PlatformHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.URI;

@EventBusSubscriber(modid = CrystallineSky.ID)
@Mod(CrystallineSky.ID)
public class CrystallineSkyImpl {
	static IEventBus bus;

	public CrystallineSkyImpl(IEventBus bus) {
		restoreLoggers();
		CrystallineSkyImpl.bus = bus;

		CrystallineSky.init();
		//noinspection Convert2MethodRef
		Env.CLIENT.runIfCurrent(() -> () -> CrystallineSkyClientImpl.init());
	}

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		for (Registration<?, ?, ?> registration : CatnipRegistry.REGISTRATIONS_VIEW.get(event.getRegistry())) {
			if (registration != null) {
				registration.register();
			}
		}
	}

	private static void restoreLoggers() {
		if (PlatformHelper.isDevEnv()) {
			// restore our logging config, since forge likes to nuke it for fun
			for (String prop : new String[] {"log4j.configurationFile", "log4j2.configurationFile"}) {
				String file = System.getProperty(prop);
				if (file != null) {
					Configurator.reconfigure(ConfigurationFactory.getInstance().getConfiguration(
						LoggerContext.getContext(),
						ConfigurationSource.fromUri(URI.create(file))
					));
					break;
				}
			}
		}
	}
}
