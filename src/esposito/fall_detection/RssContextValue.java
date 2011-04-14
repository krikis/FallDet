package esposito.fall_detection;

import org.istmusic.mw.context.model.api.IContextValue;
import org.istmusic.mw.context.model.api.IMetadata;
import org.istmusic.mw.context.model.api.IRepresentation;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.api.IValue;
import org.istmusic.mw.context.model.impl.Factory;
import org.istmusic.mw.context.model.impl.values.FloatValue;

public class RssContextValue implements IContextValue {

	private static final long serialVersionUID = 2256687594000310873L;

	public static final IScope SCOPE = Factory
			.createScope("#concepts.scopes.features.impact.value");

	public static final IRepresentation REPRESENTATION = FallOntology.REPRESENTATION_BASIC_FLOAT;

	private final FloatValue rssValue;

	public RssContextValue(final float rssValue) {

		this.rssValue = new FloatValue(rssValue);
	}

	@Override
	public IScope getScope() {
		return SCOPE;
	}

	@Override
	public IRepresentation getRepresentation() {
		return REPRESENTATION;
	}

	@Override
	public IValue getValue() {
		return this.rssValue;
	}

	@Override
	public IMetadata getMetadata() {
		return IMetadata.EMPTY_METADATA;
	}

	public String toString() {
		return "RssValue = " + this.rssValue;
	}
}
