package esposito.fall_detection;

import org.istmusic.mw.context.model.api.*;
import org.istmusic.mw.context.model.impl.Factory;

public class VveContext implements IContextElement {

	private static final long serialVersionUID = -7570163190997286016L;

	public static final IEntity ENTITY = FallOntology.ENTITY_FALL;
	public static final IScope SCOPE = FallOntology.SCOPE_FEATURE_VERTICAL_VELOCITY;

	public static final long MILLISECONDS_BEFORE_EXPIRY = 40000L; // 40 seconds

	private final String source;

	private final IMetadata metadata;

	private final VveContextData contextData;

	public VveContext(final String source, final float vveVal,
			final long vveTime) {
		this.source = source;
		this.metadata = Factory
				.createDefaultMetadata(MILLISECONDS_BEFORE_EXPIRY);
		this.contextData = new VveContextData(vveVal, vveTime);
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
		final IValue vveValue = contextData.getValue(VveContextValue.SCOPE);
		final IValue vveTime = contextData
				.getValue(CreateTimeContextValue.SCOPE);
		return "Fall Vertical Velocity { VveValue: " + vveValue
				+ ", CreateTime: " + vveTime + " }";
	}

}
