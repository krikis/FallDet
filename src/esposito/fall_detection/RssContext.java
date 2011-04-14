package esposito.fall_detection;

import org.istmusic.mw.context.model.api.IContextData;
import org.istmusic.mw.context.model.api.IContextElement;
import org.istmusic.mw.context.model.api.IEntity;
import org.istmusic.mw.context.model.api.IMetadata;
import org.istmusic.mw.context.model.api.IRepresentation;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.api.IValue;
import org.istmusic.mw.context.model.impl.Factory;

public class RssContext implements IContextElement {

	private static final long serialVersionUID = 3933388187301036433L;

	public static final IEntity ENTITY = FallOntology.ENTITY_FALL;
	public static final IScope SCOPE = FallOntology.SCOPE_FEATURE_IMPACT;

	public static final long MILLISECONDS_BEFORE_EXPIRY = 40000L; // 40 seconds

	private final String source;

	private final IMetadata metadata;

	private final RssContextData contextData;

	public RssContext(final String source, final float rssVal,
			final long rssTime) {
		this.source = source;
		this.metadata = Factory
				.createDefaultMetadata(MILLISECONDS_BEFORE_EXPIRY);
		this.contextData = new RssContextData(rssVal, rssTime);
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
		final IValue rssValue = contextData.getValue(RssContextValue.SCOPE);
		final IValue rssTime = contextData
				.getValue(CreateTimeContextValue.SCOPE);
		return "Fall Impact { RssValue: " + rssValue + ", CreateTime: "
				+ rssTime + " }";
	}

}
