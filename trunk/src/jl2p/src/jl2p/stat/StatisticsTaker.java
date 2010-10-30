package jl2p.stat;
import java.util.List;
import java.util.Vector;
import jpcap.packet.Packet;

public abstract class StatisticsTaker
{
	public abstract String getName();

	public abstract void analyze(List<Packet> packets);
	public abstract void addPacket(Packet p);
	
	public abstract String[] getLabels();
	public abstract String[] getStatTypes();
	public abstract long[] getValues(int index);
	
	public abstract void clear();
	
	public StatisticsTaker newInstance(){
		try{
			return (StatisticsTaker)this.getClass().newInstance();
		}catch(Exception e){
			return null;
		}
	}
}