package esposito.fall_detection;

import java.util.HashSet;
import java.util.Set;

import org.istmusic.mw.context.model.api.IContextData;
import org.istmusic.mw.context.model.api.IContextValue;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.api.IValue;

public class FallContextData implements IContextData {

	private static final long serialVersionUID = -3775530819922307210L;

	private final VveContextValue vveContextValue;
	private final RssContextValue rssContextValue;
	private final CreateTimeContextValue timeContextValue;

	public FallContextData(final float vveVal, final float rssVal,
			final long time) {
		vveContextValue = new VveContextValue(vveVal);
		rssContextValue = new RssContextValue(rssVal);
		timeContextValue = new CreateTimeContextValue(time);
	}

	@Override
	public IContextValue getContextValue(IScope scopeKey) {
		if (scopeKey.equals(VveContextValue.SCOPE)) {
			return vveContextValue;
		} else if (scopeKey.equals(RssContextValue.SCOPE)) {
			return rssContextValue;
		} else if (scopeKey.equals(CreateTimeContextValue.SCOPE)) {
			return timeContextValue;
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
		keySet.add(RssContextValue.SCOPE);
		keySet.add(CreateTimeContextValue.SCOPE);

		return keySet;
	}
}
