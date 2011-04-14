package esposito.fall_detection;

import org.istmusic.mw.context.IContextAccess;
import org.istmusic.mw.context.events.ContextChangedEvent;
import org.istmusic.mw.context.events.IContextListener;
import org.istmusic.mw.context.exceptions.ContextException;
import org.istmusic.mw.context.model.api.IContextElement;

public class FeatureListener implements IContextListener {

	// method binds client to the provider of the context access service
	public void setContextAccessService(final IContextAccess contextAccess) {
		try {
			contextAccess.addContextListener(FallOntology.ENTITY_FALL,
					FallOntology.SCOPE_FEATURE_VERTICAL_VELOCITY, this);
			contextAccess.addContextListener(FallOntology.ENTITY_FALL,
					FallOntology.SCOPE_FEATURE_IMPACT, this);
		} catch (Exception ce) {
		}
	}

	// method unbinds client from the provider of the context access service
	public void unsetContextAccessService(final IContextAccess contextAccess) {
		try {
			contextAccess.removeContextListener(FallOntology.ENTITY_FALL,
					FallOntology.SCOPE_FEATURE_VERTICAL_VELOCITY, this);
			contextAccess.removeContextListener(FallOntology.ENTITY_FALL,
					FallOntology.SCOPE_FEATURE_IMPACT, this);
		} catch (ContextException ce) {
		}
	}

	@Override
	public void contextChanged(ContextChangedEvent event) {
		IContextElement[] contextElements = event.getContextDataset()
				.getContextElements();

		if (FallDetector.pendingContext == null) {
			FallDetector.pendingContext = contextElements[0];
		}
	}

}
