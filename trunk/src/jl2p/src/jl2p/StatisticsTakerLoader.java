package jl2p;
import java.util.*;

import jl2p.stat.ApplicationProtocolStat;
import jl2p.stat.FreeMemStat;
import jl2p.stat.StatisticsTaker;
import jl2p.stat.NetworkProtocolStat;
import jl2p.stat.PacketStat;
import jl2p.stat.TransportProtocolStat;

public class StatisticsTakerLoader
{
	static ArrayList<StatisticsTaker> stakers=new ArrayList<StatisticsTaker>();
	
	static void loadStatisticsTaker(){
		stakers.add(new PacketStat());
		stakers.add(new NetworkProtocolStat());
		stakers.add(new TransportProtocolStat());
		stakers.add(new ApplicationProtocolStat());
		stakers.add(new FreeMemStat());
	}
	
	public static List<StatisticsTaker> getStatisticsTakers(){
		return stakers;
	}
	
	public static StatisticsTaker getStatisticsTakerAt(int index){
		return stakers.get(index);
	}
}
