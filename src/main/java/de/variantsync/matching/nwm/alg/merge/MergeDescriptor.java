package de.variantsync.matching.nwm.alg.merge;

import de.variantsync.matching.nwm.common.N_WAY;
import de.variantsync.matching.nwm.common.N_WAY.ALG_POLICY;

/**
 * Undocumented code by Rubin and Chechik
 */
public class MergeDescriptor {

	public ALG_POLICY algPolicy;
	public boolean asc;
	public N_WAY.ORDER_BY orderBy;

	public MergeDescriptor(N_WAY.ALG_POLICY algPolicy, boolean asc, N_WAY.ORDER_BY orderBy) {
		this.algPolicy = algPolicy;
		this.asc = asc;
		this.orderBy = orderBy;
	}
	
	public static MergeDescriptor EMPTY = new MergeDescriptor(N_WAY.ALG_POLICY.PAIR_WISE, true, N_WAY.ORDER_BY.MODEL_ID);

}