package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
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
	
	private List<Actor> intervistati;
	private int numIntervistati;
	private int numGiornateRiposo;
	private int giorniConsecutivi;
	private int giornoX;
	
	public void init() {
		
		intervistati = new ArrayList<>();
		this.numGiornateRiposo = 0;
		this.numIntervistati = 0;
		this.giorniConsecutivi = 0;
		this.giornoX = 0;
		
	}
	
	public void run(int N_giorni) {
		
		while(giornoX <= N_giorni) {
			giornoX++;
			
			if(giorniConsecutivi >= 2 && Math.random() > 0.1) {
				
				this.numGiornateRiposo++;
				this.giorniConsecutivi = 0;
				
			}else if(Math.random() > 0.4 || giorniConsecutivi == 0) {
				
				Actor a = getAttore();
				intervistati.add(a);
				this.numIntervistati++;
				this.giorniConsecutivi++;
				
			}else if(Math.random() > 0.6) {
				
				Actor migliore = null;
				int gradoMigliore = 0;
				
				for(Actor a: Graphs.neighborListOf(grafo, intervistati.get(intervistati.size()-1))) {
					if(grafo.degreeOf(a) > gradoMigliore) {
						migliore = a;
						gradoMigliore = grafo.degreeOf(a);
					}
				}
				
				if(gradoMigliore == 0) {
					Actor attore = getAttore();
						
					intervistati.add(attore);
					numIntervistati++;
					giorniConsecutivi++;
					
				}else {
					if(migliore != null && !intervistati.contains(migliore)) {
						intervistati.add(migliore);
						numIntervistati++;
						giorniConsecutivi++;
					}else {
						intervistati.add(getAttore());
						numIntervistati++;
						giorniConsecutivi++;
					}
					
				}
			}
		}
	}
	
	public Map<Integer, Actor> attori ;
	public int max = 0;
	
	public void setAttori() {
		
		attori = new HashMap<>();
		for(Actor a: this.getVertici()) {
			attori.put(a.getId(), a);
		}
		
		for(Integer i: attori.keySet()) {
			if(max < i) {
				max = i;
			}
		}
	}
	
	public Actor getAttore() {
		
		
		boolean trovato = false;
		Actor a = null;
		
		while(trovato == false) {
			a = attori.get((int)Math.random() *max);
			
			if(a != null && !intervistati.contains(a)) {
				trovato=true;
				break;
			}
		
		}
		
		return a;
		
	}
	public int getNumeroIntervistati() {
		return this.numIntervistati;
	}
	
	public int getGiornateRiposo() {
		return this.numGiornateRiposo;
	}

	public Graph<Actor, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
}
