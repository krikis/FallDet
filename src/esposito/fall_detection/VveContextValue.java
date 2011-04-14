package esposito.fall_detection;

import org.istmusic.mw.context.model.api.IContextValue;
import org.istmusic.mw.context.model.api.IMetadata;
import org.istmusic.mw.context.model.api.IRepresentation;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.api.IValue;
import org.istmusic.mw.context.model.impl.Factory;
import org.istmusic.mw.context.model.impl.values.FloatValue;

public class VveContextValue implements IContextValue {

	private static final long serialVersionUID = 4874633204809464085L;

	public static final IScope SCOPE = Factory
			.createScope("#concepts.scopes.features.vertical_velocity.value");

	public static final IRepresentation REPRESENTATION = FallOntology.REPRESENTATION_BASIC_FLOAT;

	private final FloatValue vveValue;

	public VveContextValue(final float vveValue) {

		this.vveValue = new FloatValue(vveValue);
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
		return this.vveValue;
	}

	@Override
	public IMetadata getMetadata() {
		return IMetadata.EMPTY_METADATA;
	}

	public String toString() {
		return "VveValue = " + this.vveValue;
	}

}
