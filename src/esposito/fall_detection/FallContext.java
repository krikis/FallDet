package esposito.fall_detection;

import org.istmusic.mw.context.model.api.IContextData;
import org.istmusic.mw.context.model.api.IContextElement;
import org.istmusic.mw.context.model.api.IEntity;
import org.istmusic.mw.context.model.api.IMetadata;
import org.istmusic.mw.context.model.api.IRepresentation;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.api.IValue;
import org.istmusic.mw.context.model.impl.Factory;

public class FallContext implements IContextElement {

	private static final long serialVersionUID = -168952437555877452L;

	public static final IEntity ENTITY = FallOntology.ENTITY_FALL;
	public static final IScope SCOPE = FallOntology.SCOPE_FEATURE_POSTURE;

	public static final long MILLISECONDS_BEFORE_EXPIRY = 40000L; // 40 seconds

	private final String source;

	private final IMetadata metadata;

	private final FallContextData contextData;

	public FallContext(final String source, final float vveVal,
			final float rssVal, final long time) {
		this.source = source;
		this.metadata = Factory
				.createDefaultMetadata(MILLISECONDS_BEFORE_EXPIRY);
		this.contextData = new FallContextData(vveVal, rssVal, time);
	}

	@Override
	public IEntity getEntity() {
		return ENTITY;
	}

	@Override
	public IScope getScope() {
		return SCOPE;
	}

	@Override
	public IRepresentation getRepresentation() {
		return null;
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public IContextData getContextData() {
		return this.contextData;
	}

	@Override
	public IMetadata getMetadata() {
		return this.metadata;
	}

	public String toString() {
		final IValue VveValue = contextData.getValue(VveContextValue.SCOPE);
		final IValue RssValue = contextData.getValue(RssContextValue.SCOPE);
		final IValue FallTime = contextData
				.getValue(CreateTimeContextValue.SCOPE);
		return "Fall { Vertical Valocity: " + VveValue + ", Impact: "
				+ RssValue + ", TimeStamp: " + FallTime + " }";
	}

}
