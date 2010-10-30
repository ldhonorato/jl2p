package jl2p;
import java.util.*;

import jl2p.analyzer.*;

public class PacketAnalyzerLoader
{
	static List<Analyzer> analyzers=new ArrayList<Analyzer>();
	static List<List<Analyzer>> layerAnalyzers=new ArrayList<List<Analyzer>>();
	
	static void loadDefaultAnalyzer(){
		analyzers.add(new PacketAnalyzer());
		analyzers.add(new EthernetAnalyzer());
		analyzers.add(new IPv4Analyzer());
		analyzers.add(new IPv6Analyzer());
		analyzers.add(new TCPAnalyzer());
		analyzers.add(new UDPAnalyzer());
		analyzers.add(new ICMPAnalyzer());
		analyzers.add(new HTTPAnalyzer());
		analyzers.add(new FTPAnalyzer());
		analyzers.add(new TelnetAnalyzer());
		analyzers.add(new SSHAnalyzer());
		analyzers.add(new SMTPAnalyzer());
		analyzers.add(new POP3Analyzer());
		analyzers.add(new ARPAnalyzer());
		
		for(int i=0;i<10;i++)
			layerAnalyzers.add(new ArrayList<Analyzer>());
		
		for(Analyzer a:analyzers)
			layerAnalyzers.get(a.layer).add(a);
	}
	
	public static List<Analyzer> getAnalyzers(){
		return analyzers;
	}
	
	public static List<Analyzer> getAnalyzersOf(int layer){
		return layerAnalyzers.get(layer);
	}
}
