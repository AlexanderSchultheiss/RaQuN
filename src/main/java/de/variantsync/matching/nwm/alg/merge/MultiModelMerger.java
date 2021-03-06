package de.variantsync.matching.nwm.alg.merge;

import java.math.BigDecimal;
import java.util.*;

import de.variantsync.matching.experiments.common.Stopped;
import de.variantsync.matching.nwm.alg.Matchable;
import de.variantsync.matching.nwm.common.AlgoUtil;
import de.variantsync.matching.nwm.common.N_WAY;
import de.variantsync.matching.nwm.domain.Element;
import de.variantsync.matching.nwm.domain.Model;
import de.variantsync.matching.nwm.domain.Tuple;
import de.variantsync.matching.nwm.execution.RunResult;

/**
 * Undocumented code by Rubin and Chechik
 */
public abstract class MultiModelMerger extends Merger implements Matchable {
	
	protected ArrayList<Tuple> solution;
	private RunResult res ;

	private HashMap<Element, String> elemToLexMemo = new HashMap<Element, String>();

	public MultiModelMerger(ArrayList<Model> models, Stopped stopped){
		super(stopped);
		this.models = models;
	}

	public List<Tuple> run(){
		ArrayList<Element> curElems = joinAllModels();
		if (stopped()) {
			return null;
		}
		long startTime = System.currentTimeMillis();
		solution = execute(curElems);
		if (stopped()) {
			return null;
		}
		long endTime = System.currentTimeMillis();

		BigDecimal weight = AlgoUtil.calcGroupWeight(solution);
		if(AlgoUtil.areThereDuplicates(solution)){
			weight = new BigDecimal(-500);
		}
		long execTime = endTime - startTime;
		if(solution.size() == 0)
			return solution;
		BigDecimal avgTupleWeight = weight.divide(new BigDecimal(solution.size()), N_WAY.MATH_CTX);
		res = new RunResult(execTime, weight, avgTupleWeight, solution);
		res.setTitle("New Hungarian");
		//AlgoUtil.printTuples(solution);
		//System.out.println(res.toString());
		return solution;
	}

	private ArrayList<Element> joinAllModels() {
		ArrayList<Element> elems = new ArrayList<Element>();
		for(Model m:models){
			elems.addAll(m.getElements());
			if (stopped()) {
				return null;
			}
		}
		return elems;
	}
	
	protected void removeDuplicates(ArrayList<Tuple> tpls){
		Collections.sort(tpls, new AlgoUtil.TupleComparator(false));
		HashSet<Tuple> usedTuples = new HashSet<Tuple>();
		for(int i=tpls.size()-1;i>=0;i--){
			Tuple t = tpls.get(i);
			if(usedTuples.contains(t)){
				tpls.remove(t);
			}
			else
				usedTuples.add(t);
		}
	}

	private ArrayList<Tuple> execute(ArrayList<Element> elems){
		Model m = new Model("1000",elems);
		int rawModelCnt = 0;
		for(Model mm:models){
			rawModelCnt += mm.getMergedFrom();
			if (stopped()) {
				return null;
			}
		}
		m.setMergedFrom(rawModelCnt);
		MultiModelHungarian mmh = new MultiModelHungarian(m, rawModelCnt, stopped);
		if (stopped()) {
			return null;
		}
		mmh.runPairing();
		if (stopped()) {
			return null;
		}
		ArrayList<Tuple> allPairs = mmh.getTuplesInMatch();
		removeDuplicates(allPairs);
			//System.out.println("\n Found pairs:");
			//AlgoUtil.printTuples(allPairs);
		if (stopped()) {
			return null;
		}
		Collections.sort(allPairs, new AlgoUtil.TupleComparator(false));
		// at this point - in order

		if (stopped()) {
			return null;
		}
		ArrayList<Tuple> chainedTuples = optimizePairs(allPairs);
		

		
		return chainedTuples;
	}
	
	protected abstract ArrayList<Tuple> optimizePairs(ArrayList<Tuple> pairs);
	
	protected Element getElementWithSmallerId(Tuple t){
		ArrayList<Element> elems = new ArrayList<Element>(t.getOrderedElements());
		Element e1  = elems.get(0);
		if(elems.size() == 1)
			return e1;
		Element e2  = elems.get(1);
		if(MultiModelHungarian.areInOrder(e1, e2))
			return e1;
		return e2;
//		if(elem1Str.compareTo(elem2Str) < 0)
//			return elems.get(0);
//		return elems.get(1);
	}
	
	private String getLexOfElement(Element e) {
		String retVal = elemToLexMemo.get(e);
		if(retVal == null){
			retVal = AlgoUtil.getLexicographicRepresentation(e);
			elemToLexMemo.put(e, retVal);
		}
		return retVal;
	}

	protected Element getElementWithHigherId(Tuple t){
		ArrayList<Element> elems = new ArrayList<Element>(t.getOrderedElements());
		Element e1  = elems.get(0);
		if(elems.size() == 1)
			return e1;
		Element e2  = elems.get(1);
		if(MultiModelHungarian.areInOrder(e1, e2))
			return e2;
		return e1;
//		String elem1Str = getLexOfElement(e1);
//		String elem2Str = getLexOfElement(e2);
//		
//		if(elem1Str.compareTo(elem2Str) < 0)
//			return elems.get(1);
//		return elems.get(0);
	}
	
	protected void cleanFreeElements(ArrayList<Tuple> resultTuples){
		ArrayList<Tuple> deficientTuples = new ArrayList<Tuple>();
		for(Tuple t:resultTuples){
			if(t.getWeight().compareTo(BigDecimal.ZERO) == 0)
				deficientTuples.add(t);
		}
		
		for(Tuple t:deficientTuples){
			resultTuples.remove(t);
		}
	}
	
	protected ArrayList<Element> getFreeElements(ArrayList<Tuple> resultTuples){
		cleanFreeElements(resultTuples);
		HashSet<Element> allElems = new HashSet<Element>();
		for(Model m:models){
			allElems.addAll(m.getElements());
		}
		HashSet<Element> usedElements = new HashSet<Element>();
		
		ArrayList<Element> freeElems = new ArrayList<Element>();
		ArrayList<Tuple> deficientTuples = new ArrayList<Tuple>();
		for(Tuple t:resultTuples){
				usedElements.addAll(t.getRealElements());
		}
		
		for(Tuple t:deficientTuples){
			resultTuples.remove(t);
		}
		
		for(Element e:allElems){
			if(!usedElements.contains(e)){
				freeElems.add(e);
			}
		}
		return freeElems;
	}
	
	@Override
	public ArrayList<Tuple> getTuplesInMatch() {
		return solution;
	}

	@Override
	public ArrayList<Model> getModels() {
		return models;
	}

	@Override
	protected Matchable getMatch() {
		return this;
	}

	@Override
	public RunResult getRunResult(int numOfModels) {
		return res;
	}
	
//	private boolean isElementContainingPartOfTuple(Element e, Tuple t){
//	for(Element bue:e.getBasedUponElements()){
//		for(Element te:t.getRealElements()){
//			if(te == bue){
//				return true;
//			}
//		}
//	}
//	return false;
//}

//private void removeTupleElements(ArrayList<Element> elems, Tuple t) {
//	HashSet<Element> elemsToRemove = new HashSet<Element>();
//	for(Element e:elems){
//		if(isElementContainingPartOfTuple(e, t))
//			elemsToRemove.add(e);
//	}
//	
//	for(Element etr:elemsToRemove){
//		elems.remove(etr);
//	}
//}

//private void removeExpandedTuple(ArrayList<Tuple> tuples, Tuple expanded){
//HashSet<Tuple> tuplesToRemove = new HashSet<Tuple>();
//for(Tuple t:tuples){
//	for(Element re:t.getRealElements()){
//		if(isElementContainingPartOfTuple(re, expanded))
//			tuplesToRemove.add(t);
//	}
//}
//
//for(Tuple ttr:tuplesToRemove){
//	tuples.remove(ttr);
//	//System.out.println("Removed: "+ttr);
//}
//}

}