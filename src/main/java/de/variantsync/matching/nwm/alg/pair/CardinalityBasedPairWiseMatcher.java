package de.variantsync.matching.nwm.alg.pair;

import java.util.ArrayList;
import java.util.Comparator;

import de.variantsync.matching.nwm.alg.merge.HungarianMerger;
import de.variantsync.matching.nwm.domain.Model;

/**
 * Undocumented code by Rubin and Chechik
 */
public class CardinalityBasedPairWiseMatcher extends PairWiseMatch {

	boolean ascending;
	public CardinalityBasedPairWiseMatcher(ArrayList<Model> lst, boolean asc) {
		super((asc?"Pairwise Most Sparse First":"Pairwise Less Sparse First"),lst,asc);
		ascending = asc;
	}

	@Override
	public Comparator<HungarianMerger> getPolicyComperator(final boolean largerFrst) {
		// TODO Auto-generated method stub
		
		return new Comparator<HungarianMerger>() {
			@Override
			public int compare(HungarianMerger mp1, HungarianMerger mp2) {
				int cardinality1 = mp1.getTuplesInMatch().size();
				int cardinality2 = mp1.getTuplesInMatch().size();
				if(!largerFrst)
					return (cardinality1 > cardinality2)?1:-1;
				else
					return (cardinality1 > cardinality2)?-1:1;
			}
		};
	}

}