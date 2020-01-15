package edu.virginia.cs.Synthesizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.virginia.cs.Synthesizer.SmartBridge.Bounds;
import edu.virginia.cs.Synthesizer.SmartBridge.SolutionInfo;
import weka.attributeSelection.PrincipalComponents;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class Classifier {

	private static final int NUM_CLUSTERS = 5;

	List<List<Double>> observations = new ArrayList<List<Double>>();

	public List<Double> computeFeatureVector(SolutionInfo solutionInfo) {
		Evaluator e = new Evaluator(solutionInfo.root, solutionInfo.solution);
		List<String> components = e.queryNames("Class + Association");
		components.sort(null);
		List<String> strategies = e.queryNames("Strategy");
		strategies.sort(null);
		List<Double> features = new ArrayList<Double>(components.size());
		for (String c : components) {
			List<String> strategy = e.queryNames("assignees." + c);
			if (strategy.size() > 0) {
				assert strategy.size() == 1;
				features.add(strategies.indexOf(strategy.get(0)) + 1.0d);
			} else {
				features.add(0.0d);
			}
		}
		return features;
	}

	public List<Map<String, Bounds>> partition(int[] clusters, List<SolutionInfo> solutionList) {
		List<Map<String, Bounds>> bounds = new ArrayList<>(NUM_CLUSTERS);
		for (int i = 0; i < NUM_CLUSTERS; ++i)
			bounds.add(new HashMap<>());

		for (int i = 0; i < clusters.length; i++) {
			SolutionInfo sol = solutionList.get(i);
			for (String strategy : sol.assignees.keySet()) {
				Map<String, Bounds> partition = bounds.get(clusters[i]);
				if (!partition.containsKey(strategy))
					partition.put(strategy, new Bounds(sol.assignees.get(strategy)));
				else {
					Bounds b = partition.get(strategy);
					b.lower.retainAll(sol.assignees.get(strategy));
					b.upper.addAll(sol.assignees.get(strategy));
				}
			}
		}
		return bounds;
	}

	public int[] cluster() throws Exception {
		if (observations.size() == 0)
			return new int[0];

		System.out.print("clustering ... ");
		long beg = System.currentTimeMillis();
		// set up the dataset based on the extant observation vectors
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i = 0; i < observations.get(0).size(); i++)
			attributes.add(new Attribute("a" + i, i));
		Instances instances = new Instances("Dataset", attributes, observations.size());
		for (List<Double> f : observations) {
			instances.add(new DenseInstance(1, f.stream().mapToDouble(x -> x).toArray()));
		}

		PrincipalComponents pc = new PrincipalComponents();
		pc.buildEvaluator(instances);
		Instances converted = pc.transformedData(instances);

		// generate the clusters and get the centroids
		SimpleKMeans km = new SimpleKMeans();
		km.setPreserveInstancesOrder(true);
		km.setNumClusters(NUM_CLUSTERS);
		km.buildClusterer(converted);
		System.out.printf("done (%10.2f secs)%n", (System.currentTimeMillis() - beg) / 1000.0);

		double[] sizes = km.getClusterSizes();
		System.out.println(
				"[" + String.join(",", Arrays.stream(sizes).boxed().map(x -> x.toString()).collect(Collectors.toList()))
						+ "]");

		return km.getAssignments();
	}
}
