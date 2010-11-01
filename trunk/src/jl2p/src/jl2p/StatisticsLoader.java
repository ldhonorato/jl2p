package jl2p;
import java.util.ArrayList;
import java.util.List;

import jl2p.statistics.ApplicationProtocolStatistics;
import jl2p.statistics.NetworkProtocolStatistics;
import jl2p.statistics.PacketStatistics;
import jl2p.statistics.Statistics;
import jl2p.statistics.TransportProtocolStatistics;

public class StatisticsLoader
{
	static ArrayList<Statistics> stakers=new ArrayList<Statistics>();
	
	static void loadStatisticsTaker(){
		stakers.add(new PacketStatistics());
		stakers.add(new NetworkProtocolStatistics());
		stakers.add(new TransportProtocolStatistics());
		stakers.add(new ApplicationProtocolStatistics());
	}
	
	public static List<Statistics> getStatisticsTakers(){
		return stakers;
	}
	
	public static Statistics getStatisticsTakerAt(int index){
		return stakers.get(index);
	}
}
