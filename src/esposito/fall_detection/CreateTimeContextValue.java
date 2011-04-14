package esposito.fall_detection;

import org.istmusic.mw.context.model.api.IContextValue;
import org.istmusic.mw.context.model.api.IMetadata;
import org.istmusic.mw.context.model.api.IRepresentation;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.api.IValue;
import org.istmusic.mw.context.model.impl.values.LongValue;

public class CreateTimeContextValue implements IContextValue {

	private static final long serialVersionUID = 4281439080482780009L;

	public static final IScope SCOPE = FallOntology.SCOPE_METADATA_TIMESTAMP_CREATION;

	public static final IRepresentation REPRESENTATION = FallOntology.REPRESENTATION_BASIC_LONG;

	private final LongValue time;

	public CreateTimeContextValue(final long time) {
		this.time = new LongValue(time);
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
		return this.time;
	}

	@Override
	public IMetadata getMetadata() {
		return IMetadata.EMPTY_METADATA;
	}

	public String toString() {
		return "Create Time = " + this.time;
	}
}
