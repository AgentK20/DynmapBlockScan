package org.dynmap.blockscan.statehandlers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.util.IStringSerializable;

public class ForgeStateContainer extends StateContainer {

	public ForgeStateContainer(Block blk, Set<String> renderprops, Map<String, List<String>> propMap) {
		List<IBlockState> bsl = blk.getStateContainer().getValidStates();
		IBlockState defstate = blk.getDefaultState();
		if (renderprops == null) {
			renderprops = new HashSet<String>();
			for (String pn : propMap.keySet()) {
				renderprops.add(pn);
			}
		}
		// Build table of render properties and valid values
		for (String pn : propMap.keySet()) {
			if (renderprops.contains(pn) == false) {
				continue;
			}
			this.renderProperties.put(pn, propMap.get(pn));
		}
		
		this.defStateIndex = 0;
		int idx = 0;
		for (IBlockState bs : bsl) {
			ImmutableMap.Builder<String,String> bld = ImmutableMap.builder();
			for (IProperty<?> ent : bs.getProperties()) {
				String pn = ent.getName();
				if (renderprops.contains(pn)) {	// If valid render property
					Comparable<?> v = bs.get(ent);
					if (v instanceof IStringSerializable) {
						v = ((IStringSerializable)v).getName();
					}
					bld.put(pn, v.toString());
				}
			}
			StateRec sr = new StateRec(idx, bld.build());
			int prev_sr = records.indexOf(sr);
			if (prev_sr < 0) {
				if (bs.equals(defstate)) {
					this.defStateIndex = records.size();
				}
				records.add(sr);
			}
			else {
				StateRec prev = records.get(prev_sr);
				if (prev.hasMeta(idx) == false) {
					sr = new StateRec(prev, idx);
					records.set(prev_sr, sr);
					if (bs.equals(defstate)) {
						this.defStateIndex = prev_sr;
					}
				}
			}
			idx++;
		}
		// Check for well-known block types
		if (blk instanceof BlockLeaves) {
		    type = WellKnownBlockClasses.LEAVES;
		}
		else if (blk instanceof BlockCrops) {
            type = WellKnownBlockClasses.CROPS;
		}
		else if (blk instanceof BlockFlower) {
		    type = WellKnownBlockClasses.FLOWER;
		}
		else if (blk instanceof BlockTallGrass) {
            type = WellKnownBlockClasses.TALLGRASS;
		}
		else if (blk instanceof BlockVine) {
		    type = WellKnownBlockClasses.VINES;
		}
        else if (blk instanceof BlockBush) {
            type = WellKnownBlockClasses.BUSH;
        }
        else if (blk instanceof BlockGrass) {
            type = WellKnownBlockClasses.GRASS;
        }
        else if (blk instanceof BlockFlowingFluid) {
            type = WellKnownBlockClasses.LIQUID;
        }
	}
}
