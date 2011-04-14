package esposito.fall_detection;

import java.util.HashSet;
import java.util.Set;

import org.istmusic.mw.context.model.api.IContextData;
import org.istmusic.mw.context.model.api.IContextValue;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.api.IValue;

public class RssContextData implements IContextData {

	private static final long serialVersionUID = 4297940123618965633L;

	private final RssContextValue rssValContextValue;
	private final CreateTimeContextValue rssTimeContextValue;

	public RssContextData(final float rssVal, final long time) {
		rssValContextValue = new RssContextValue(rssVal);
		rssTimeContextValue = new CreateTimeContextValue(time);
	}

	@Override
	public IContextValue getContextValue(IScope scopeKey) {
		if (scopeKey.equals(RssContextValue.SCOPE)) {
			return rssValContextValue;
		} else if (scopeKey.equals(CreateTimeContextValue.SCOPE)) {
			return rssTimeContextValue;
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

		keySet.add(RssContextValue.SCOPE);
		keySet.add(CreateTimeContextValue.SCOPE);

		return keySet;
	}

}
