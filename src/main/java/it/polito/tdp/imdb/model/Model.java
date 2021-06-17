package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private ImdbDAO dao;
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private Map<Integer, Actor> idMap;
	
	public Model() {
		dao = new ImdbDAO();
		idMap = new HashMap<>();
	}
	
	public List<String> getGeneri(){
		return dao.getGeneriFilm();
	}
	
	public void creaGrafo(String genere) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		this.dao.listAllActors(idMap, genere);
		for(Actor a : idMap.values()) {
			this.grafo.addVertex(a);
		}
		
		List<Adiacenza> archi = dao.getArchi(idMap, genere);
		for(Adiacenza a: archi) {
			Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
		}
	}
	
	public int getNVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}

	public Set<Actor> getVertici() {
		
		return grafo.vertexSet();
	}
	
	private Map<Actor, Actor> predecessore;
	public List<Actor> getAttoriSimili(Actor partenza) {
		List<Actor> result = new ArrayList<>();
		
		BreadthFirstIterator<Actor, DefaultWeightedEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza);
		
		predecessore = new HashMap<>();
		predecessore.put(partenza, null);
		
		bfv.addTraversalListener(new TraversalListener<Actor, DefaultWeightedEdge>(){

			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				DefaultWeightedEdge arco = e.getEdge();
				Actor a = grafo.getEdgeSource(arco);
				Actor b = grafo.getEdgeTarget(arco);
				
				if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {
					//ho scoperto 'a' arrivando da 'b' ( se 'b' lo conosco già ( è nella mappa) )
					predecessore.put(a, b);
				}else if(predecessore.containsKey(a) && !predecessore.containsKey(b)){
					//conosco 'b' arrivando da 'a'
					predecessore.put(b, a);
				}
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Actor> e) {
				Actor nuova = e.getVertex();
				//Fermata precedente ;
				//predecessore.put(nuova, precedente);
				
			}

			public void vertexFinished1(VertexTraversalEvent<Actor> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Actor> e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		while(bfv.hasNext()) {
			Actor f = bfv.next();
			if(!f.equals(partenza))
				result.add(f);
		}
		
		Collections.sort(result, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				
				return o1.getLastName().compareTo(o2.getLastName());
			}
			
		});
		
		return result;
	}
}
