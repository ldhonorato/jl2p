package jl2p.statistics;

import java.util.List;

import jpcap.packet.Packet;

public abstract class Statistics {
	public abstract String getName();

	public abstract void analyze(List<Packet> packets);

	public abstract void addPacket(Packet p);

	public abstract String[] getLabels();

	public abstract String[] getStatTypes();

	public abstract long[] getValues(int index);

	public abstract void clear();

	public Statistics newInstance() {
		try {
			return (Statistics) this.getClass().newInstance();
		} catch (Exception e) {
			return null;
		}
	}
}