package de.variantsync.matching.nwm.alg.merge;

import java.util.ArrayList;
import java.util.HashSet;

import de.variantsync.matching.experiments.common.Stopped;
import de.variantsync.matching.nwm.common.AlgoUtil;
import de.variantsync.matching.nwm.domain.Element;
import de.variantsync.matching.nwm.domain.Model;
import de.variantsync.matching.nwm.domain.Tuple;

/**
 * Undocumented code by Rubin and Chechik
 */
public class MultiModelHungarianOptimizingMerger extends MultiModelMerger {

	public MultiModelHungarianOptimizingMerger(ArrayList<Model> models, Stopped stopped) {
		super(models, stopped);
	}

	@Override
	protected ArrayList<Tuple> optimizePairs(ArrayList<Tuple> pairs) {
		
		ArrayList<Tuple> nextPairs = extractNextPairs(pairs);
		ArrayList<Tuple> currPairs = getValidPairs(pairs);
		while(AlgoUtil.calcGroupWeight(nextPairs).compareTo(AlgoUtil.calcGroupWeight(currPairs)) > 0){
			currPairs = nextPairs;
			nextPairs = extractNextPairs(nextPairs);
		}
		
		return currPairs;
	}
	
	private ArrayList<Tuple> extractNextPairs(ArrayList<Tuple> pairs){
		ArrayList<Tuple> validPairs = getValidPairs(pairs);
		ArrayList<Element> elems = new ArrayList<Element>();
		for(Tuple t:validPairs){
			elems.add(new Element(t));
		}
		elems.addAll(getFreeElements(validPairs));
		Model m = new Model("2000", elems);
		MultiModelHungarian mmh = new MultiModelHungarian(m, models.size(), stopped);
		mmh.runPairing();
		ArrayList<Tuple> nextPairs = mmh.extractMerge();
		return getValidPairs(nextPairs);
	}

	private ArrayList<Tuple> getValidPairs(ArrayList<Tuple> pairs) {
		ArrayList<Tuple> validTuples = new ArrayList<Tuple>();
		//Collections.sort(pairs, new AlgoUtil.TupleComparator(true));
		HashSet<Element> usedElems = new HashSet<Element>();
		for(Tuple t:pairs){
			boolean usefulTuple = true;
			for(Element e:t.getRealElements()){
				if(usedElems.contains(e)){
					usefulTuple = false;
					break;
				}
			}
			if(usefulTuple){
				validTuples.add(t);
				usedElems.addAll(t.getRealElements());
			}
		}
		return validTuples;
	}

}