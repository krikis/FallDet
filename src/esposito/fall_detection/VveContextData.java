package esposito.fall_detection;

import java.util.HashSet;
import java.util.Set;

import org.istmusic.mw.context.model.api.IContextData;
import org.istmusic.mw.context.model.api.IContextValue;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.api.IValue;

public class VveContextData implements IContextData {

	private static final long serialVersionUID = 7635317906562634345L;

	private final VveContextValue vveValContextValue;
	private final CreateTimeContextValue vveTimeContextValue;

	public VveContextData(final float vveVal, final long time) {
		vveValContextValue = new VveContextValue(vveVal);
		vveTimeContextValue = new CreateTimeContextValue(time);
	}

	@Override
	public IContextValue getContextValue(IScope scopeKey) {
		if (scopeKey.equals(VveContextValue.SCOPE)) {
			return vveValContextValue;
		} else if (scopeKey.equals(CreateTimeContextValue.SCOPE)) {
			return vveTimeContextValue;
		}

		return null;
	}

	@Override
	public IValue getValue(IScope scopeKey) {
		IContextValue contextValue = getContextValue(scopeKey);

		return contextValue == null ? null : contextValue.getValue();
	}

	@Override
	public Set<IScope> keySet() {
		final Set<IScope> keySet = new HashSet<IScope>();

		keySet.add(VveContextValue.SCOPE);
		keySet.add(CreateTimeContextValue.SCOPE);

		return keySet;
	}
}
