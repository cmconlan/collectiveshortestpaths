package uk.ac.warwick.heuristics;

import java.util.Collection;
import java.util.List;

import uk.ac.warwick.queries.Query;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Pair;

public interface BaseSolution {
	List<Pair<Query, Path>> process(Collection<Query> queries, boolean capacityAware);
	Path processQuery(Query query, boolean capacityAware);
	void showLoad();
	Pair<Integer, Integer> evaluate(List<Path> paths);
}
