package esposito.fall_detection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.istmusic.mw.context.model.api.IConcept;
import org.istmusic.mw.context.model.api.IEntity;
import org.istmusic.mw.context.model.api.IRepresentation;
import org.istmusic.mw.context.model.api.IScope;
import org.istmusic.mw.context.model.impl.Factory;
import org.istmusic.mw.context.ontologies.IContextOntology;

public class FallOntology implements IContextOntology {

	private static final long serialVersionUID = 8566061462114806301L;

	public static final String FALL_ONTOLOGY = "fall_ontology";

	public static final IEntity ENTITY_FALL = Factory
			.createEntity("#concepts.entities.fall");

	public static final String[] ENTITY_FALL_GROUNDINGS = { "this" };

	public static final IScope SCOPE_FEATURE_IMPACT = Factory
			.createScope("#concepts.scopes.features.impact");
	public static final IScope SCOPE_FEATURE_VERTICAL_VELOCITY = Factory
			.createScope("#concepts.scopes.features.vertical_velocity");
	public static final IScope SCOPE_FEATURE_POSTURE = Factory
			.createScope("#concepts.scopes.features.posture");
	public static final IScope SCOPE_LOCATION = Factory
			.createScope("#concepts.scopes.location");
	public static final IScope SCOPE_LOCATION_LONGITUDE = Factory
			.createScope("#concepts.scopes.location.longitude");
	public static final IScope SCOPE_LOCATION_LATITUDE = Factory
			.createScope("#concepts.scopes.location.latitude");
	public static final IScope SCOPE_METADATA_TIMESTAMP_CREATION = Factory
			.createScope("#concepts.scopes.metadata.timestamp.creation");

	public static final IRepresentation REPRESENTATION_LOCATION_PAIR_OF_COORDINATES = Factory
			.createRepresentation("#concepts.representations.location.pair_of_coordinates");
	public static final IRepresentation REPRESENTATION_ABSTRACT_TIMESTAMP_AS_LONG = Factory
			.createRepresentation("#concepts.representations.abstract.timestamp_as_long");
	public static final IRepresentation REPRESENTATION_BASIC_BOOLEAN = Factory
			.createRepresentation("#concepts.representations.basic.boolean");
	public static final IRepresentation REPRESENTATION_BASIC_FLOAT = Factory
			.createRepresentation("#concepts.representations.basic.float");
	public static final IRepresentation REPRESENTATION_BASIC_DOUBLE = Factory
			.createRepresentation("#concepts.representations.basic.double");
	public static final IRepresentation REPRESENTATION_BASIC_INTEGER = Factory
			.createRepresentation("#concepts.representations.basic.integer");
	public static final IRepresentation REPRESENTATION_BASIC_LONG = Factory
			.createRepresentation("#concepts.representations.basic.long");
	public static final IRepresentation REPRESENTATION_BASIC_STRING = Factory
			.createRepresentation("#concepts.representations.basic.string");

	private static IContextOntology instance;

	public static IContextOntology getInstance() {
		synchronized (FallOntology.class) {
			if (instance == null) {
				instance = new FallOntology();
			}
			return instance;
		}
	}

	private final Map<String, IEntity> allEntities = new HashMap<String, IEntity>();
	private final Map<String, IScope> allScopes = new HashMap<String, IScope>();

	private final Map<IConcept, Set<IRepresentation>> scopesToRepresentations = new HashMap<IConcept, Set<IRepresentation>>();

	private final Map<IScope, Set<IScope>> scopesToChildScopes = new HashMap<IScope, Set<IScope>>();

	private FallOntology() {
		// use reflection to instantiate the list of entities, scopes and
		// representations
		Class<FallOntology> clazz = FallOntology.class;
		final Field[] allDeclaredFields = clazz.getDeclaredFields();
		final int numOfDeclaredFields = allDeclaredFields.length;
		for (int i = 0; i < numOfDeclaredFields; i++) {
			final Field field = allDeclaredFields[i];
			try {
				final Class<?> fieldType = field.getType();
				if (fieldType.isAssignableFrom(IEntity.class)) {
					final IEntity entity = (IEntity) field.get(null);
					allEntities.put(entity.getEntityAsString(), entity);
				} else if (fieldType.isAssignableFrom(IScope.class)) {
					final IScope scope = (IScope) field.get(null);
					allScopes.put(scope.getScopeAsString(), scope);
				}
			} catch (IllegalAccessException iae) {
				throw new RuntimeException(iae);
			}
		}

		// initialize the scopesToRepresentations map
		final Set<IRepresentation> locationRepresentationsSet = new HashSet<IRepresentation>();
		locationRepresentationsSet
				.add(REPRESENTATION_LOCATION_PAIR_OF_COORDINATES);
		scopesToRepresentations.put(SCOPE_LOCATION, locationRepresentationsSet);

		final Set<IRepresentation> timestampRepresentationsSet = new HashSet<IRepresentation>();
		timestampRepresentationsSet
				.add(REPRESENTATION_ABSTRACT_TIMESTAMP_AS_LONG);
		scopesToRepresentations.put(SCOPE_METADATA_TIMESTAMP_CREATION,
				timestampRepresentationsSet);

		final Set<IRepresentation> impactRepresentationsSet = new HashSet<IRepresentation>();
		impactRepresentationsSet.add(REPRESENTATION_BASIC_FLOAT);
		scopesToRepresentations.put(SCOPE_FEATURE_IMPACT,
				impactRepresentationsSet);

		final Set<IRepresentation> verticalVelocityRepresentationsSet = new HashSet<IRepresentation>();
		verticalVelocityRepresentationsSet.add(REPRESENTATION_BASIC_FLOAT);
		scopesToRepresentations.put(SCOPE_FEATURE_IMPACT,
				verticalVelocityRepresentationsSet);

		// initialize the scopesToChildScopes map
		for (Iterator<IScope> scopesIterator = allScopes.values().iterator(); scopesIterator
				.hasNext();) {
			final IScope childScope = (IScope) scopesIterator.next();
			final IScope parentScope = childScope.getParentScope();

			if (parentScope != null) {
				// add the pair
				Set<IScope> children = (Set<IScope>) scopesToChildScopes
						.get(parentScope);
				if (children == null) {
					children = new HashSet<IScope>();
					scopesToChildScopes.put(parentScope, children);
				}
				children.add(childScope);
			}
		}
	}

	@Override
	public String getID() {
		return FALL_ONTOLOGY;
	}

	@Override
	public Set<IEntity> getAvailableEntities() {
		return new HashSet<IEntity>(allEntities.values());
	}

	@Override
	public Set<IScope> getAvailableScopes() {
		return new HashSet<IScope>(allScopes.values());
	}

	@Override
	public Set<String> getEntityGroundings(IEntity entity) {
		if (ENTITY_FALL.equals(entity)) {
			return new HashSet<String>(Arrays.asList(ENTITY_FALL_GROUNDINGS));
		}

		return null;
	}

	@Override
	public Set<?> getChildScopes(IScope scope) {
		final Set<?> childScopes = (Set<?>) scopesToChildScopes.get(scope);

		return childScopes == null ? new HashSet<Object>() : childScopes;
	}

	@Override
	public IScope getParentScope(IScope scope) {
		return scope.getParentScope();
	}

	@Override
	public Set<?> getRepresentationsFor(IScope scope) {
		final Set<?> representationsSet = (Set<?>) scopesToRepresentations
				.get(scope);
		if (representationsSet == null) {
			return new HashSet<Object>();
		} else {
			return new HashSet<Object>(representationsSet);
		}
	}

}
