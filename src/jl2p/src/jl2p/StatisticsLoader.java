package jl2p;
import java.util.ArrayList;
import java.util.List;

import jl2p.statistics.ApplicationProtocolStatistics;
import jl2p.statistics.NetworkProtocolStatistics;
import jl2p.statistics.DataLinkStatistics;
import jl2p.statistics.Statistics;
import jl2p.statistics.TransportProtocolStatistics;

public class StatisticsLoader
{
	static ArrayList<Statistics> statistics=new ArrayList<Statistics>();
	
	static void loadDefaultStatistics(){
		statistics.add(new DataLinkStatistics());
		statistics.add(new NetworkProtocolStatistics());
		statistics.add(new TransportProtocolStatistics());
		statistics.add(new ApplicationProtocolStatistics());
	}
	
	public static List<Statistics> getStatistics(){
		return statistics;
	}
	
	public static Statistics getStatisticAt(int index){
		return statistics.get(index);
	}
}
